package com.example.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

public class OneChatAdapter extends ArrayAdapter<Message> {
    String avatar;
    String another_user;
    OneChatAdapter(@NonNull Context context, int resource, Message[] arr) {
        super(context, resource, arr);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Message message = getItem(position);
        String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        if(message.getAuthor().equals(login)){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_out_item, null);
        }else{
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_in_item, null);
        }
        ((TextView) convertView.findViewById(R.id.text)).setText(message.getText());
        ((TextView) convertView.findViewById(R.id.time)).setText(message.getTime());
        return convertView;
    }
}
