package com.example.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Authentication extends AppCompatActivity {
    EditText login, password;
    Button confirm;

    String text_login, text_password;
    Context getContext = this;
    DatabaseReference databaseReference;
    ArrayList<User> users;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        confirm = (Button) findViewById(R.id.confirm);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");


        login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text_login = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text_password = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_login.equals("") || text_password.equals("")){
                    Toast.makeText(getContext, "Заполните все поля", Toast.LENGTH_SHORT).show();
                }else{
                    User user = new User(text_password, "user");
                    users = new ArrayList<>();


                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            users = new ArrayList<>();
                            for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                            {
                                User u = dataSnapshot1.getValue(User.class);
                                users.add(u);
                                Log.d("MyLogs", dataSnapshot1.getKey());

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext, "Проверьте соединение", Toast.LENGTH_LONG).show();
                        }
                    });




                    databaseReference.child(text_login).setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(getContext, "Success", Toast.LENGTH_SHORT).show();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            ref.child("Favorite" + text_login).setValue(new User("a", "b"));

                            //TODO:только класс не юзера а RecyclerItem(какой-нибудь приветственный пост),
                            //TODO:но только если такой пользователь появился впервые

                            startActivity(new Intent(getContext, MainActivity.class));
                        }
                    });

                }
            }
        });

    }
}
