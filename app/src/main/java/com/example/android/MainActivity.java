package com.example.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    ImageButton btn_add, btn_main, btn_profile;
    SharedPreferences sPref;
    NewsFeed nf;
    Add_post add;
    Profile profile;
    FragmentTransaction fragmentTransaction;


    @Override
    protected void onStart() {
        super.onStart();
        boolean isLarge =  (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        if(isLarge){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        btn_add = (ImageButton) findViewById(R.id.add);
        btn_main = (ImageButton) findViewById(R.id.main);
        btn_profile = (ImageButton) findViewById(R.id.profile);
        Intent intent = getIntent();
        String password = intent.getStringExtra("password");

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        nf = new NewsFeed(intent.getStringExtra("role"), intent.getStringExtra("Login"));
        add = new Add_post(intent.getStringExtra("role"), intent.getStringExtra("Login"));
        profile = new Profile(intent.getStringExtra("role"), intent.getStringExtra("Login"), password, intent.getStringExtra("avatar"));
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
    }

}
