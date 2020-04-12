package com.example.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import android.view.MenuItem;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bnv;
    SharedPreferences sPref;
    NewsFeed nf;
    Add_post add;
    Profile profile;
    FragmentTransaction fragmentTransaction;

    Activity getActivity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String password = intent.getStringExtra("password");
        bnv = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        nf = new NewsFeed(intent.getStringExtra("role"), intent.getStringExtra("Login"));
        add = new Add_post(intent.getStringExtra("role"), intent.getStringExtra("Login"));
        profile = new Profile(intent.getStringExtra("role"), intent.getStringExtra("Login"), password);
        bnv.setItemBackground(getDrawable(R.drawable.toolbar));
        fragmentTransaction.add(R.id.frgmCont, nf);
        fragmentTransaction.commit();



        bnv.setOnNavigationItemSelectedListener(getBottomNavigationListener());
    }

    @NonNull
    private BottomNavigationView.OnNavigationItemSelectedListener getBottomNavigationListener(){
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();

                switch(menuItem.getItemId()){
                    case R.id.action_add:
                        fragmentTransaction.replace(R.id.frgmCont, add);
                        break;
                    case R.id.action_posts:
                        fragmentTransaction.replace(R.id.frgmCont, nf);
                        break;
                    case R.id.user_account:
                        fragmentTransaction.replace(R.id.frgmCont, profile);
                        break;
                }
                fragmentTransaction.commit();
                return true;
            }
        };
    }

}
