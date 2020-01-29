package com.example.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bnv;
    SharedPreferences sPref;
    TextView textView;
    TextView txtOption;
    String toRemove = "news_feed";
    NewsFeed nf = new NewsFeed();
    User u = new User();
    Favorite liked = new Favorite();
    Add_post add = new Add_post();
    FragmentTransaction fragmentTransaction;
    private FirebaseAuth mAuth;
    final String SAVED = "favorite_posts";
    Activity getActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bnv = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        txtOption = (TextView) findViewById(R.id.txtOptionDigit);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
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
                    case "user":
                        fragmentTransaction.remove(u);
                        break;
                    case "add_post":
                        fragmentTransaction.remove(add);
                    case "news_feed":
                        fragmentTransaction.remove(nf);
                    case "liked":
                        fragmentTransaction.remove(liked);
                }
                switch(menuItem.getItemId()){
                    case R.id.action_profile:
                        fragmentTransaction.add(R.id.frgmCont, u);
                        toRemove = "user";
                        break;
                    case R.id.action_add:
                        fragmentTransaction.add(R.id.frgmCont, add);
                        toRemove = "add_post";
                        break;
                    case R.id.action_posts:
                        fragmentTransaction.add(R.id.frgmCont, nf);
//                        TextView txt = (TextView) findViewById(R.id.textView2);
//                        txt.setText("Апчхи");
                        toRemove = "news_feed";
                        break;
                    case R.id.action_1:
                        fragmentTransaction.add(R.id.frgmCont, liked);
                        toRemove = "liked";
                        break;
                }
                fragmentTransaction.commit();
                return true;
            }
        };
    }
}