package com.example.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsList extends AppCompatActivity {
    // активити со списком друзей пользователя
    ListView friends_list;
    TextView exit, tv_friends, tv_requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        String name = getIntent().getStringExtra("name");
        boolean show_requests = getIntent().getBooleanExtra("show_requests", true);
        friends_list = (ListView) findViewById(R.id.friends_list);
        tv_friends = (TextView) findViewById(R.id.friends);
        tv_requests = (TextView) findViewById(R.id.requests);
        exit = (TextView) findViewById(R.id.exit);
        // пользователь посматривает список чужих друзей,
        // ему не видны заявки в друзья
        if(!show_requests){
            tv_requests.setVisibility(View.GONE);
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(name).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<String> friends_names = new ArrayList<>();
                final ArrayList<String> friend_requests = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue().toString().equals("friend")) {
                        friends_names.add(ds.getKey());
                    } else if (ds.getValue().toString().equals("request")) {
                        friend_requests.add(ds.getKey());
                    }
                }
                FriendsListAdapter adapter = new FriendsListAdapter(FriendsList.this,
                        R.layout.friends_list_item, friends_names.toArray(new String[0]),
                        "friends");
                friends_list.setAdapter(adapter);

                tv_friends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FriendsListAdapter adapter = new FriendsListAdapter(FriendsList.this,
                                R.layout.friends_list_item, friends_names.toArray(new String[0]),
                                "friends");
                        friends_list.setAdapter(adapter);
                    }
                });
                // если пользователь просматривает список своих друзей,
                // ему доступен также список запросов в друзья
                tv_requests.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FriendsListAdapter adapter = new FriendsListAdapter(FriendsList.this,
                                R.layout.friends_list_item, friend_requests.toArray(new String[0]),
                                "requests");
                        friends_list.setAdapter(adapter);
                    }
                });

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
