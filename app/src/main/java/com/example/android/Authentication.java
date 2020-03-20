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
    Button confirm, create;

    String text_login="", text_password="";
    Context getContext = this;
    DatabaseReference databaseReference;
    ArrayList<User> users;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        confirm = (Button) findViewById(R.id.confirm);
        create = (Button) findViewById(R.id.create);
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
                    databaseReference.child(text_login).addValueEventListener(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                user = dataSnapshot.getValue(User.class);
                                if (user.getPassword().equals(text_password)) {
                                    Intent intent = new Intent(getContext, MainActivity.class);
                                    intent.putExtra("Login", text_login);
                                    intent.putExtra("role", user.getRole());
                                    password.setText("");
                                    login.setText("");
                                    startActivity(intent);

                                } else {
                                    password.setText("");
                                    Toast.makeText(getContext, "Перепроверьте данные", Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e){
                                Toast.makeText(getContext, "Перепроверьте данные", Toast.LENGTH_SHORT).show();
                                password.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext, "Проверьте соединение", Toast.LENGTH_SHORT).show();
                        }
                    });

            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext, NewAccount.class));
            }
        });


    }
}
