package com.example.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ActivityTwo extends AppCompatActivity{
    TextView text;
    int[] liked;
    Activity getActivity = this;
    BottomNavigationView bnv;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Intent intent = getIntent();
        liked = intent.getIntArrayExtra("liked");
        text = (TextView) findViewById(R.id.textView2);
        bnv = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bnv.setOnNavigationItemSelectedListener(getBottomNavigationListener());
    }

    @NonNull
    private BottomNavigationView.OnNavigationItemSelectedListener getBottomNavigationListener(){
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.action_posts:
                        finish();
                        break;
                    case R.id.action_1:
                        Intent intent = new Intent(getActivity, favorites.class);
                        intent.putExtra("liked", liked);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                return true;
            }
        };
    }
}
