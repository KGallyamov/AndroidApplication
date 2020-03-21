package com.example.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.nfc.Tag;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bnv;
    SharedPreferences sPref;
    TextView textView;
    TextView txtOption;
    String toRemove = "news_feed";
    NewsFeed nf;
    Add_post add;
    Profile profile;
    FragmentTransaction fragmentTransaction;
    private FirebaseAuth mAuth;
    final String SAVED = "favorite_posts";
    Activity getActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String login = intent.getStringExtra("Login");
        bnv = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        txtOption = (TextView) findViewById(R.id.txtOptionDigit);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        nf = new NewsFeed(intent.getStringExtra("role"), intent.getStringExtra("Login"));
        add = new Add_post(intent.getStringExtra("role"), intent.getStringExtra("Login"));
        profile = new Profile(intent.getStringExtra("role"), intent.getStringExtra("Login"));

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
                switch(toRemove){
                    case "add_post":
                        fragmentTransaction.remove(add);
                        break;
                    case "news_feed":
                        fragmentTransaction.remove(nf);
                        break;
                    case "profile":
                        fragmentTransaction.remove(profile);
                        break;
                }
                switch(menuItem.getItemId()){
                    case R.id.action_add:
                        fragmentTransaction.add(R.id.frgmCont, add);
                        toRemove = "add_post";
                        break;
                    case R.id.action_posts:
                        fragmentTransaction.add(R.id.frgmCont, nf);
                        toRemove = "news_feed";
                        break;
                    case R.id.user_account:
                        fragmentTransaction.add(R.id.frgmCont, profile);
                        toRemove = "profile";
                        break;
                }
                fragmentTransaction.commit();
                return true;
            }
        };
    }

}
