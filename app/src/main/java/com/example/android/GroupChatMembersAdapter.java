package com.example.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class GroupChatMembersAdapter extends ArrayAdapter<User> {
    User[] users;
    String[] names;
    String creator;
    String path;
    public GroupChatMembersAdapter(@NonNull Context context, int resource, @NonNull User[] objects, String[] names, String creator, String path) {
        super(context, resource, objects);
        this.users = objects;
        this.names = names;
        this.creator = creator;
        this.path = path;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);
        final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];

        if(convertView == null){
            if(login.equals(creator) && !names[position].equals(login)){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_chat_member_for_creator, null);
                TextView remove_user = (TextView) convertView.findViewById(R.id.remove_user);
                remove_user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder ask = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
                        ask.setMessage("Are you sure you want to remove " + names[position] + "?").setCancelable(false)
                                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseReference remove_user_reference = FirebaseDatabase.getInstance().getReference();
                                        remove_user_reference.child("GroupChats").child(path).child("members").child(names[position]).removeValue();
                                        DatabaseReference remove_chat_reference = FirebaseDatabase.getInstance().getReference();
                                        remove_chat_reference.child("Users").child(names[position]).child("chats").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot i:dataSnapshot.getChildren()){
                                                    if(i.getValue().toString().equals(path)){
                                                        DatabaseReference remove_form_user = FirebaseDatabase.getInstance().getReference();
                                                        remove_form_user.child("Users").child(names[position]).child("chats").child(i.getKey()).removeValue();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alertDialog = ask.create();
                        alertDialog.setTitle("Log out");
                        alertDialog.show();

                    }
                });
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_chat_member, null);
            }
        }
        final TextView lastSeen = convertView.findViewById(R.id.time);
        final ImageView avatar = convertView.findViewById(R.id.avatar);
        TextView name = convertView.findViewById(R.id.username);
        lastSeen.setText(user.getLastSeen());
        name.setText(names[position]);
        if(user.getLastSeen().equals("online")){
            lastSeen.setTextColor(getContext().getResources().getColor(R.color.active_blue));
        } else{
            lastSeen.setTextColor(getContext().getResources().getColor(R.color.grey));
        }

        Glide.with(getContext()).load(user.getAvatar()).into(avatar);

        return convertView;
    }
}
