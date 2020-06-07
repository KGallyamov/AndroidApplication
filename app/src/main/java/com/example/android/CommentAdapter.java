package com.example.android;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CommentAdapter extends ArrayAdapter<Comment> {
    // адаптер списка комментариев
    TextView author;
    String where, pos_path;
    ArrayList<String> comment_paths;
    final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];

    CommentAdapter(@NonNull Context context, int resource, Comment[] arr, String where, String post_path, ArrayList<String> comment_paths) {
        super(context, resource, arr);
        this.pos_path = post_path;
        this.where = where;
        this.comment_paths = comment_paths;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Comment db = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, null);

        }
        // в комментарии отображается аватарка пользователя
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
        // комментарии можно лайкать и дизлайкать
        DatabaseReference likes = FirebaseDatabase.getInstance().getReference();
        likes.child(where).child(pos_path).child("comments").child(comment_paths.get(position)).
                child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int result = 0;
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    if (!i.getKey().equals("zero")) {
                        if (i.getValue().equals("up")) {
                            ++result;
                        } else {
                            --result;
                        }
                        // пользователь уже оценил этот комментарий
                        if (login.equals(i.getKey())) {
                            if (i.getValue().equals("up")) {
                                ((TextView) finalConvertView.findViewById(R.id.up)).
                                        setBackground(finalConvertView.getResources().getDrawable(R.drawable.ic_thumb_up_activated_24dp));

                                ((TextView) finalConvertView.findViewById(R.id.down)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DatabaseReference vote = FirebaseDatabase.getInstance().getReference();
                                        vote.child(where).child(pos_path).child("comments").child(comment_paths.get(position)).
                                                child("likes").child(login).removeValue();
                                    }
                                });
                            } else {
                                ((TextView) finalConvertView.findViewById(R.id.down)).
                                        setBackground(finalConvertView.getResources().getDrawable(R.drawable.ic_thumb_down_activated_24dp));

                                ((TextView) finalConvertView.findViewById(R.id.up)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DatabaseReference vote = FirebaseDatabase.getInstance().getReference();
                                        vote.child(where).child(pos_path).child("comments").child(comment_paths.get(position)).
                                                child("likes").child(login).removeValue();
                                    }
                                });
                            }

                        }
                    }
                }
                ((TextView) finalConvertView.findViewById(R.id.result_likes)).setText(Integer.toString(result));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // пользователь еще не оценил комментарий
        ((TextView) finalConvertView.findViewById(R.id.up)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference vote = FirebaseDatabase.getInstance().getReference();
                vote.child(where).child(pos_path).child("comments").child(comment_paths.get(position)).
                        child("likes").child(login).setValue("up");
            }
        });
        ((TextView) finalConvertView.findViewById(R.id.down)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference vote = FirebaseDatabase.getInstance().getReference();
                vote.child(where).child(pos_path).child("comments").child(comment_paths.get(position)).
                        child("likes").child(login).setValue("down");
            }
        });


        author = (TextView) convertView.findViewById(R.id.author);

        author.setText(db.getAuthor());
        ((TextView) convertView.findViewById(R.id.main_text)).setText(db.getText());

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd MMMM yyyy");
        String now = dateformat.format(c.getTime());
        String time = db.getTime().split("/")[0];

        // если отправили не сегодня, а раньше
        if (now.equals(time)) {
            ((TextView) convertView.findViewById(R.id.time)).setText(db.getTime().split("/")[1]);
        } else {
            ((TextView) convertView.findViewById(R.id.time)).setText(db.getTime().split("/")[0]);
        }
        author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AnotherUserPage.class);
                intent.putExtra("author", author.getText().toString());
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
