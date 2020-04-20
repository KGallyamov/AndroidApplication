package com.example.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CommentAdapter extends ArrayAdapter<Comment> {

    CommentAdapter(@NonNull Context context, int resource, Comment[] arr) {
        super(context, resource, arr);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Comment db = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, null);

        }

        ((TextView) convertView.findViewById(R.id.author)).setText(db.getAuthor());
        ((TextView) convertView.findViewById(R.id.main_text)).setText(db.getText());
        return convertView;
    }
}
