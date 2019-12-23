package com.example.android;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bnv;
    ListView list;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView) findViewById(R.id.listView);
        textView = (TextView) findViewById(R.id.textView2);
        bnv = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        String[] val = new String[50];

        for(int i=0;i<val.length;++i){
            val[i] = "post " + (i+1);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, val);
        list.setAdapter(adapter);
        bnv.setOnNavigationItemSelectedListener(getBottomNavigationListener());
    }
    @NonNull
    private BottomNavigationView.OnNavigationItemSelectedListener getBottomNavigationListener(){
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.action_profile:
                        list.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                        break;
                    case R.id.action_posts:
                        list.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.GONE);
                        break;
                }
                return true;

            }
        };
    }

//    public void change(){
//        Intent intent = new Intent(this, ActivityTwo.class);
//        startActivity(intent);
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
