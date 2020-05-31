package com.example.android;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendsListAdapter extends ArrayAdapter<String> {
    private Context context;
    private String state;

    public FriendsListAdapter(@NonNull Context context, int resource, @NonNull String[] objects, String state) {
        super(context, resource, objects);
        this.context = context;
        this.state = state;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final String username = getItem(position);
        if(convertView == null){
            if(state.equals("friends")) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.friends_list_item, null);
            } else{
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend_request_list_item, null);
                TextView accept = (TextView) convertView.findViewById(R.id.accept);
                TextView cancel = (TextView) convertView.findViewById(R.id.cancel);
                final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];

                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO: в два узла прописать друга
                        DatabaseReference add_friend = FirebaseDatabase.getInstance().getReference();
                        add_friend.child("Users").child(username).child("friends").child(login).setValue("friend");
                        DatabaseReference add_to_current_user = FirebaseDatabase.getInstance().getReference();
                        add_to_current_user.child("Users").child(login).child("friends").child(username).setValue("friend");
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference add_to_current_user = FirebaseDatabase.getInstance().getReference();
                        add_to_current_user.child("Users").child(login).child("friends").child(username).removeValue();
                    }
                });
            }
        }
        TextView name = (TextView) convertView.findViewById(R.id.username);
        name.setText(username);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        final View finalConvertView = convertView;
        reference.child(username).child("avatar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String avatar_link = dataSnapshot.getValue().toString();
                Glide.with(context).load(avatar_link).into((ImageView) finalConvertView.findViewById(R.id.avatar));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AnotherUserPage.class);
                intent.putExtra("author", username);
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
