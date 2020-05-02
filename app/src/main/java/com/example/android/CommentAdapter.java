package com.example.android;

import android.content.Context;
import android.util.Log;
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(db.getAuthor());
        final View finalConvertView = convertView;
        reference.child("avatar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String avatar = dataSnapshot.getValue().toString();
                Glide.with(getContext()).load(avatar).into((ImageView) finalConvertView.findViewById(R.id.avatar));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
            ((TextView) convertView.findViewById(R.id.time)).setText(db.getTime().split("/")[0]);
        }

        return convertView;
    }
}
