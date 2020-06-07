package com.example.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class splash extends AppCompatActivity {
    String login;
    Context context = this;
    String time_for_database;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void onStart() {
        super.onStart();

        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            // аутентификация
            updateUI(currentUser);
        }catch (NullPointerException e){
            startActivity(new Intent(this, Authentication.class));
        }
    }


    public void  updateUI(FirebaseUser account){
        if(account != null){
            final String username = account.getEmail().split("@")[0];
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("Login", username);
            startActivity(intent);
        }else {
            startActivity(new Intent(this, Authentication.class));
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        new AsyncRequest().execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences preferences = getSharedPreferences("Main", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("first");
        editor.apply();
        new AsyncRequest().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!(mAuth.getCurrentUser() == null)) {
            String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("Users").child(login).child("lastSeen").setValue(time_for_database);
        }
    }

    class AsyncRequest extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg) {
            String time = "failed";
            try {
                TimeTCPClient client = new TimeTCPClient();
                try {
                    client.setDefaultTimeout(30000);
                    client.connect("time-a-b.nist.gov");
                    time =  client.getDate().toString();
                } finally {
                    client.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return time;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            HashMap<String, String> months = new HashMap<>();
            months.put("May", "мая");
            months.put("Jun", "июня");
            months.put("Jul", "июля");
            months.put("Aug", "августа");

            months.put("Sep", "сентября");
            months.put("Oct", "октября");
            months.put("Nov", "ноября");
            months.put("Dec", "декабря");

            months.put("Jan", "января");
            months.put("Feb", "февраля");
            months.put("Mar", "марта");
            months.put("Apr", "апреля");
            // успешное соединение с сервером
            if(!s.equals("failed")) {
                String[] time_data = s.split(" ");
                for (String i : time_data) {
                    Log.d("Look", i);
                }

                time_for_database = time_data[3] + " " + time_data[2] +
                        "." + months.get(time_data[1]) + "." + time_data[5];
                Log.d("Splash", time_data[1]);

            }
            // если не получилось взять время с сервера
            else{
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss dd.MMMM.yyyy");
                time_for_database = dateformat.format(c.getTime());
            }


        }
    }


}
