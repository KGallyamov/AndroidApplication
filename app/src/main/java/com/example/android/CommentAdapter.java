package com.example.android;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CommentAdapter extends ArrayAdapter<Comment> {

    CommentAdapter(@NonNull Context context, int resource, Comment[] arr) {
        super(context, resource, arr);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Comment db = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, null);

        }

        ((TextView) convertView.findViewById(R.id.author)).setText(db.getAuthor());
        ((TextView) convertView.findViewById(R.id.main_text)).setText(db.getText());

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd MMMM yyyy");
        String now = dateformat.format(c.getTime());
        String time = db.getTime().split("/")[0];
        Log.d("Look", now);

        // если отправили не сегодня, а раньше
        if(now.equals(time)) {
            ((TextView) convertView.findViewById(R.id.time)).setText(db.getTime().split("/")[1]);
        }else{
            ((TextView) convertView.findViewById(R.id.time)).setText(db.getTime());
        }

        return convertView;
    }
}
