package com.example.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProfileSettings extends AppCompatActivity {
    Button log_out;
    TextView back;
    RadioGroup send_messages, see_posts, add_to_chats;
    final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        log_out = (Button) findViewById(R.id.exit123456);
        back = (TextView) findViewById(R.id.back);

        send_messages = (RadioGroup) findViewById(R.id.send_messages);
        see_posts = (RadioGroup) findViewById(R.id.see_posts);
        add_to_chats = (RadioGroup) findViewById(R.id.add_to_chats);
        DatabaseReference settings = FirebaseDatabase.getInstance().getReference();
        settings.child("Users").child(login).child("privacy_settings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    if (i.getKey().equals("add_to_group_chats")) {
                        if (i.getValue().toString().equals("friends")) {
                            add_to_chats.check(R.id.just_friends_can_add);
                        } else {
                            add_to_chats.check(R.id.all_users_can_add);
                        }
                    } else if (i.getKey().equals("see_my_posts")) {
                        if (i.getValue().toString().equals("friends")) {
                            see_posts.check(R.id.just_friends_see_posts);
                        } else if (i.getValue().toString().equals("everyone")) {
                            see_posts.check(R.id.all_users_see_posts);
                        } else {
                            see_posts.check(R.id.no_one_see_posts);
                        }
                    } else if (i.getKey().equals("send_messages")) {
                        if (i.getValue().toString().equals("friends")) {
                            send_messages.check(R.id.just_friends);
                        } else {
                            send_messages.check(R.id.all_users);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder ask = new AlertDialog.Builder(ProfileSettings.this, R.style.MyAlertDialogStyle);
                ask.setMessage("Are you sure you want to log out?").setCancelable(false)
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                auth.signOut();
                                new AsyncRequest().execute();

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = ask.create();
                alertDialog.setTitle("Log out");
                alertDialog.show();
            }
        });

    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.all_users_see_posts:
                send_setting("see_my_posts", "everyone");
                break;
            case R.id.just_friends_see_posts:
                send_setting("see_my_posts", "friends");
                break;
            case R.id.no_one_see_posts:
                send_setting("see_my_posts", "me");
                break;
            case R.id.all_users_can_add:
                send_setting("add_to_group_chats", "everyone");
                break;
            case R.id.just_friends_can_add:
                send_setting("add_to_group_chats", "friends");
                break;
            case R.id.all_users:
                send_setting("send_messages", "everyone");
                break;
            case R.id.just_friends:
                send_setting("send_messages", "friends");
                break;
        }
    }

    private void send_setting(String where, String data) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(login).child("privacy_settings").child(where).setValue(data);
    }

    class AsyncRequest extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg) {
            String time = "failed";
            try {
                TimeTCPClient client = new TimeTCPClient();
                try {
                    client.setDefaultTimeout(30000);
                    client.connect("time.nist.gov");
                    time = client.getDate().toString();
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
            String time_for_database;
            if (!s.equals("failed")) {
                String[] time_data = s.split(" ");
                for (String i : time_data) {
                    Log.d("Look", i);
                }

                time_for_database = time_data[3] + " " + time_data[2] +
                        "." + months.get(time_data[1]) + "." + time_data[5];

            }
            // если не получилось взять время с сервера
            else {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss dd.MMMM.yyyy");
                time_for_database = dateformat.format(c.getTime());
            }
            Log.d("Look", time_for_database);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("Users").child(login).child("lastSeen").setValue(time_for_database);
            Intent mStartActivity = new Intent(ProfileSettings.this, Authentication.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(ProfileSettings.this, mPendingIntentId, mStartActivity,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) ProfileSettings.this.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
            System.exit(0);

        }
    }
}
