package com.example.android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
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
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void onStart() {
        super.onStart();

        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
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
    protected void onDestroy() {
        super.onDestroy();
        new AsyncRequest().execute();
    }
    class AsyncRequest extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg) {
            String time = "failed";
            try {
                TimeTCPClient client = new TimeTCPClient();
                try {
                    client.setDefaultTimeout(10000);
                    client.connect("time.nist.gov");
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
            if(mAuth.getCurrentUser() != null) {
                login = mAuth.getCurrentUser().getEmail().split("@")[0];
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                HashMap<String, String> months = new HashMap<>();
                months.put("May", "мая");
                months.put("June", "июня");
                months.put("July", "июля");
                months.put("August", "августа");

                months.put("September", "сентября");
                months.put("October", "октября");
                months.put("November", "ноября");
                months.put("December", "декабря");

                months.put("January", "января");
                months.put("February", "февраля");
                months.put("March", "марта");
                months.put("April", "апреля");

                String[] time_data = s.split(" ");
                String time_for_database = time_data[3] + " " + time_data[2] +
                        "." + months.get(time_data[1]) + "." + time_data[5];


                reference.child("Users").child(login).child("lastSeen").setValue(time_for_database);
            }

        }
    }
}
