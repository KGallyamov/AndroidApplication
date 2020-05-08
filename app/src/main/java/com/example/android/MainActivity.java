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

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ImageButton btn_add, btn_main, btn_profile, btn_message;
    SharedPreferences sPref;
    NewsFeed nf;
    Add_post add;
    Profile profile;
    Chat chat;
    String text_login, text_role, text_password, text_avatar;
    ArrayList<String> posts;
    FragmentTransaction fragmentTransaction;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        btn_add = (ImageButton) findViewById(R.id.add);
        btn_main = (ImageButton) findViewById(R.id.main);
        btn_profile = (ImageButton) findViewById(R.id.profile);
        btn_message = (ImageButton) findViewById(R.id.messages);
        Intent intent = getIntent();
        text_password = intent.getStringExtra("password");
        text_role = intent.getStringExtra("role");
        text_login = intent.getStringExtra("Login");
        posts = intent.getStringArrayListExtra("posts");
        text_avatar = intent.getStringExtra("avatar");

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        nf = new NewsFeed(text_role, text_login);
        add = new Add_post(text_role, text_login);
        profile = new Profile(text_role, text_login,
                text_password, text_avatar, posts);
        chat = new Chat(text_login);
        fragmentTransaction.add(R.id.frgmCont, nf);
        fragmentTransaction.commit();

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
}
