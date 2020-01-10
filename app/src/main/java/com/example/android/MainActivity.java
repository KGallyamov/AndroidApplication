package com.example.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bnv;
    ListView list, liked;
    TextView textView;
    int[] like = {11, 15, 16, 20, 45};//ArrayList
    Activity getActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView) findViewById(R.id.listView);
        liked = (ListView) findViewById(R.id.liked);
        textView = (TextView) findViewById(R.id.textView2);
        bnv = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        String[] val = new String[50];
        String[] l = new String[like.length];
        for(int i=0;i<val.length;++i){
            val[i] = "post " + (i+1);
        }
        for(int i=0;i<l.length;i++){
            l[i] = "post " + like[i];
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, val);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, l);
        list.setAdapter(adapter);
        liked.setAdapter(adapter1);
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
                        textView.setVisibility(View.VISIBLE);
                        list.setVisibility(View.INVISIBLE);
                        liked.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.action_posts:
                        textView.setVisibility(View.INVISIBLE);
                        list.setVisibility(View.VISIBLE);
                        liked.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.action_1:
                        textView.setVisibility(View.INVISIBLE);
                        list.setVisibility(View.INVISIBLE);
                        liked.setVisibility(View.VISIBLE);
                }
                return true;
            }
        };
    }
}
