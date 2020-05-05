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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatListAdapter extends ArrayAdapter<String> {
    ChatListAdapter(@NonNull Context context, int resource, String[] arr) {
        super(context, resource, arr);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final String chat = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item, null);
        }
        ((TextView) convertView.findViewById(R.id.name)).setText(chat);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final View finalConvertView = convertView;
        reference.child("Users").child(chat).child("avatar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(getContext()).load(dataSnapshot.getValue().toString()).into((ImageView) finalConvertView.findViewById(R.id.avatar));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OneChatActivity.class);
                intent.putExtra("Another_person", chat);
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }
}
