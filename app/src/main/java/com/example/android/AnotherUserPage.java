package com.example.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AnotherUserPage extends AppCompatActivity {
    TextView login, posts, role, exit;
    ImageView avatar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.another_user_page);
        avatar = (ImageView) findViewById(R.id.avatar);
        login = (TextView) findViewById(R.id.login);
        role = (TextView) findViewById(R.id.role);
        posts = (TextView) findViewById(R.id.posts);
        exit = (TextView) findViewById(R.id.exit);
        Intent intent = getIntent();

        String author_login = intent.getStringExtra("author");
        login.setText(author_login);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(author_login).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot i: dataSnapshot.getChildren()){
                    if(i.getKey().equals("role")){
                        role.setText(i.getValue().toString());
                    }else if(i.getKey().equals("avatar")){
                        Glide.with(AnotherUserPage.this).load(i.getValue().toString()).into(avatar);
                    }else if(i.getKey().equals("posts")){
                        posts.setText(i.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
