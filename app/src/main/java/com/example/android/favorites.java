package com.example.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class favorites extends AppCompatActivity {
    BottomNavigationView bnv;
    ListView list;
    TextView textView;
    int[] liked = {11, 15, 16, 20, 45};
    Activity getActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView) findViewById(R.id.favorites_list);
        textView = (TextView) findViewById(R.id.textView2);
        bnv = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        String[] val = new String[50];

        for(int i=0;i<val.length;++i){
            val[i] = "post " + liked[i];
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, val);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 11:
                        finish();
                        //startActivity(new Intent(getActivity, postActivity.class));
                        //putExtra("post_number", 11)
                        break;
                    default:
                        break;
                }
            }
        });
        bnv.setOnNavigationItemSelectedListener(getBottomNavigationListener());
    }
    @NonNull
    private BottomNavigationView.OnNavigationItemSelectedListener getBottomNavigationListener(){
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.action_profile:
                        Intent intent1 = new Intent(getActivity, ActivityTwo.class);
                        startActivity(intent1);
                        break;
                    case R.id.action_posts:
                        Intent intent2 = new Intent(getActivity, MainActivity.class);
                        startActivity(intent2);
                        break;
                }
                return true;
            }
        };
    }
}
