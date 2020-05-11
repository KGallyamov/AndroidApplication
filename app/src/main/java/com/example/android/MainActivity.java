package com.example.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ImageButton btn_add, btn_main, btn_profile, btn_message;
    SharedPreferences sPref;
    NewsFeed nf;
    Add_post add;
    Profile profile;
    Chat chat;
    String text_login;
    FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        btn_add = (ImageButton) findViewById(R.id.add);
        btn_main = (ImageButton) findViewById(R.id.main);
        btn_profile = (ImageButton) findViewById(R.id.profile);
        btn_message = (ImageButton) findViewById(R.id.messages);
        Intent intent = getIntent();
        text_login = intent.getStringExtra("Login");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(text_login);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                nf = new NewsFeed(user.getRole(), text_login);
                add = new Add_post(user.getRole(), text_login);
                profile = new Profile(user.getRole(), text_login,
                        user.getPassword(), user.getAvatar(), new ArrayList<String>(user.getPosts().values()));
                chat = new Chat(text_login);
                SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                boolean first = preferences.getBoolean("first", true);
                if(first) {
                    fragmentTransaction.add(R.id.frgmCont, nf);
                    fragmentTransaction.commit();
                    SharedPreferences.Editor ed = preferences.edit();
                    ed.putBoolean("first", false);
                    ed.apply();
                }

                //Переключение между экранами - фрагментами
                btn_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frgmCont, profile);
                        fragmentTransaction.commit();
                    }
                });
                btn_main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frgmCont, nf);
                        fragmentTransaction.commit();
                    }
                });
                btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frgmCont, add);
                        fragmentTransaction.commit();
                    }
                });
                btn_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frgmCont, chat);
                        fragmentTransaction.commit();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("lastSeen").setValue("online");

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("first");
        editor.apply();
    }
}
