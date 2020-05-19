package com.example.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class GroupChatMembersAdapter extends ArrayAdapter<User> {
    User[] users;
    String[] names;
    public GroupChatMembersAdapter(@NonNull Context context, int resource, @NonNull User[] objects, String[] names) {
        super(context, resource, objects);
        this.users = objects;
        this.names = names;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_chat_member, null);
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
