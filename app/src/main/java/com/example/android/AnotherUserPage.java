package com.example.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class AnotherUserPage extends AppCompatActivity {
    TextView login, posts, role, exit;
    ImageView avatar;
    RecyclerView user_posts;
    Context context = this;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.another_user_page);
        avatar = (ImageView) findViewById(R.id.avatar);
        login = (TextView) findViewById(R.id.login);
        role = (TextView) findViewById(R.id.role);
        posts = (TextView) findViewById(R.id.posts);
        exit = (TextView) findViewById(R.id.exit);
        user_posts = (RecyclerView) findViewById(R.id.users_posts);
        Intent intent = getIntent();

        String author_login = intent.getStringExtra("author");
        login.setText(author_login);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(author_login).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int k = 0;
                ArrayList<String> user_links = new ArrayList<>();
                for(DataSnapshot i: dataSnapshot.getChildren()) {
                    if (i.getKey().equals("role")) {
                        role.setText(i.getValue().toString());
                    } else if (i.getKey().equals("avatar")) {
                        Glide.with(AnotherUserPage.this).load(i.getValue().toString()).into(avatar);
                    } else if (i.getKey().equals("posts")) {
                        for (DataSnapshot j : i.getChildren()) {
                            if (!j.getKey().equals("zero")) {
                                k++;
                                user_links.add(j.getValue().toString());
                            }
                        }
                    } else if(i.getKey().equals("lastSeen")){
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss dd.MMMM.yyyy");
                        String[] now = dateformat.format(c.getTime()).split(" ");
                        String time = i.getValue().toString();
                        if(time.equals("online")){
                            ((TextView) findViewById(R.id.lastSeen)).setText(i.getValue().toString());
                        }else {
                            if (now[1].equals(i.getValue().toString().split(" ")[1])) {
                                String[] refactor = time.split(" ")[0].split(":");
                                ((TextView) findViewById(R.id.lastSeen)).setText(refactor[0] + ":" + refactor[1]);
                            } else {
                                ((TextView) findViewById(R.id.lastSeen)).setText(i.getValue().toString());
                            }
                        }
                    }
                }
                fill(user_links);
                posts.setText(Integer.toString(k));

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
    private void fill(final ArrayList<String> links){
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Data");
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<RecyclerItem> listItems = new ArrayList<>();
                ArrayList<String> pt = new ArrayList<>();
                LinearLayoutManager manager = new LinearLayoutManager(context);
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    if(links.contains(dataSnapshot1.getKey())) {
                        RecyclerItem p = dataSnapshot1.getValue(RecyclerItem.class);
                        listItems.add(p);
                        pt.add(dataSnapshot1.getKey());
                    }
                }
                Collections.reverse(listItems);
                Collections.reverse(pt);
                MyAdapter adapter = new MyAdapter(listItems, context, "Data", "user", pt, login.getText().toString());
                user_posts.setAdapter(adapter);

                user_posts.setLayoutManager(manager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
