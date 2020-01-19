package com.example.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bnv;
    SharedPreferences sPref;
    TextView textView;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<RecyclerItem> listItems;
    final String SAVED = "favorite_posts";
    Activity getActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        textView = (TextView) findViewById(R.id.textView2);
        bnv = (BottomNavigationView) findViewById(R.id.bottom_navigation);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();

        for(int i = 0; i < 10; i++){


            Bitmap bitmap = Bitmap.createBitmap(100, 100,
                    Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.BLUE);
            RecyclerItem item = new RecyclerItem("Item " + (i + 1), "Description of item " + (i + 1), bitmap);


            listItems.add(item);
        }


        //Adapter
        adapter = new MyAdapter(listItems, this);
        recyclerView.setAdapter(adapter);



        saveFavorite();
        bnv.setOnNavigationItemSelectedListener(getBottomNavigationListener());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveFavorite();
    }
    void saveFavorite() {
        sPref = getPreferences(MODE_PRIVATE);
        Editor ed = sPref.edit();
        String s = "";

        ed.putString(SAVED, s);
        ed.apply();
    }
    @NonNull
    private BottomNavigationView.OnNavigationItemSelectedListener getBottomNavigationListener(){
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.action_profile:
                        textView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.action_posts:
                        textView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        break;
                    case R.id.action_1:
                        textView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        };
    }
}
