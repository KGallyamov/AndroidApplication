package com.example.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

public class AnotherUserPage extends AppCompatActivity {
    TextView login, posts, role, exit;
    ImageView avatar;
    Button write_message;
    RecyclerView user_posts;
    String author_login;
    Context context = this;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.another_user_page);
        avatar = (ImageView) findViewById(R.id.avatar);
        login = (TextView) findViewById(R.id.login);
        role = (TextView) findViewById(R.id.role);
        posts = (TextView) findViewById(R.id.posts);
        write_message = (Button) findViewById(R.id.start_converstaion);
        exit = (TextView) findViewById(R.id.exit);
        user_posts = (RecyclerView) findViewById(R.id.users_posts);
        Intent intent = getIntent();

        author_login = intent.getStringExtra("author");
        login.setText(author_login);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(author_login).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int k = 0;
                ArrayList<String> user_links = new ArrayList<>();
                for(DataSnapshot i: dataSnapshot.getChildren()) {
                    if (i.getKey().equals("role")) {
                        role.setText(i.getValue().toString());
                    } else if (i.getKey().equals("avatar")) {
                        Glide.with(context).load(i.getValue().toString()).into(avatar);
                    } else if (i.getKey().equals("posts")) {
                        for (DataSnapshot j : i.getChildren()) {
                            if (!j.getKey().equals("zero")) {
                                k++;
                                user_links.add(j.getValue().toString());
                            }
                        }
                    } else if(i.getKey().equals("lastSeen")){
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss dd.MMMM.yyyy");
                        String[] now = dateformat.format(c.getTime()).split(" ");
                        String time = i.getValue().toString();
                        TextView lastSeen = findViewById(R.id.lastSeen);
                        if(time.equals("online")){
                            lastSeen.setTextColor(getResources().getColor(R.color.active_blue));
                            lastSeen.setText(i.getValue().toString());
                        }else {
                            if (now[1].equals(i.getValue().toString().split(" ")[1])) {
                                String[] refactor = time.split(" ")[0].split(":");
                                lastSeen.setText(refactor[0] + ":" + refactor[1]);
                            } else {
                                String[] refactor = time.split(" ");
                                String day_month = refactor[1].substring(0, refactor[1].length() - 5);
                                String hour_minute = refactor[0].substring(0, refactor[0].length() - 3);
                                lastSeen.setText("Was online at " + hour_minute + " " + day_month);
                            }
                        }
                    } else if(i.getKey().equals("rating")){
                        float rating = i.getValue(Float.TYPE);
                        TextView tv_rating = (TextView) findViewById(R.id.rating);
                        tv_rating.setText(i.getValue().toString());
                        if(rating > 0){
                            tv_rating.setTextColor(getResources().getColor(R.color.rating_green));
                        }else if(rating < 0){
                            tv_rating.setTextColor(getResources().getColor(R.color.colorAccent));
                        }
                    }
                }
                fill(user_links);
                posts.setText(Integer.toString(k));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        write_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialogBuilder = new AlertDialog.Builder(AnotherUserPage.this).create();
                View dialogView = AnotherUserPage.this.getLayoutInflater().inflate(R.layout.start_dialog, null);

                final EditText message = (EditText) dialogView.findViewById(R.id.edt_comment);
                final EditText ed_another_user = (EditText) dialogView.findViewById(R.id.address);
                TextView send = (TextView) dialogView.findViewById(R.id.Submit);
                TextView cancel = (TextView) dialogView.findViewById(R.id.Cancel);
                ed_another_user.setText(author_login);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                });
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!message.getText().toString().equals("") && !ed_another_user.getText().toString().equals("")){
                            final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
                            final String recevier = ed_another_user.getText().toString();
                            DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("Users");
                            users.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    boolean user_exists = false;
                                    for(DataSnapshot i:dataSnapshot.getChildren()){
                                        if(i.getKey().equals(recevier)){
                                            user_exists = true;
                                        }
                                    }
                                    if(!user_exists){
                                        Toast.makeText(AnotherUserPage.this, "Specified user doesn't exist", Toast.LENGTH_SHORT).show();
                                    }else{
                                        String[] arr = new String[]{login, recevier};
                                        Arrays.sort(arr);
                                        final String name = arr[0] + "_" + arr[1];
                                        DatabaseReference chat = FirebaseDatabase.getInstance().getReference().child("Messages");
                                        Calendar c = Calendar.getInstance();
                                        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss dd.MMMM.yyyy");
                                        String now = dateformat.format(c.getTime());
                                        Message mes = new Message(message.getText().toString(), login, now, false);
                                        chat.child(name).push().setValue(mes);
                                        dialogBuilder.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(AnotherUserPage.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else{
                            Toast.makeText(AnotherUserPage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void fill(final ArrayList<String> links){
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Data");
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<RecyclerItem> listItems = new ArrayList<>();
                final ArrayList<String> pt = new ArrayList<>();
                final LinearLayoutManager manager = new LinearLayoutManager(context);
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    if(links.contains(dataSnapshot1.getKey())) {
                        RecyclerItem p = dataSnapshot1.getValue(RecyclerItem.class);
                        listItems.add(p);
                        pt.add(dataSnapshot1.getKey());
                    }
                }
                Collections.reverse(listItems);
                Collections.reverse(pt);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0]).
                        child("rating").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        float rating = dataSnapshot.getValue(Float.TYPE);
                        MyAdapter adapter = new MyAdapter(listItems, context, "Data", "user", pt, login.getText().toString(), rating);
                        user_posts.setAdapter(adapter);

                        user_posts.setLayoutManager(manager);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
