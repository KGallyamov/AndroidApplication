package com.example.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Authentication extends AppCompatActivity {
    EditText login, password;
    Button confirm;
    TextView create;

    String text_login="", text_password="";
    Context getContext = this;
    DatabaseReference databaseReference;
    ArrayList<User> users;
    User user;

    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        auth = FirebaseAuth.getInstance();
        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        confirm = (Button) findViewById(R.id.confirm);
        create = (TextView) findViewById(R.id.create);
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
                auth.signInWithEmailAndPassword(text_login, text_password).addOnCompleteListener((Activity) getContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final String username = text_login.split("@")[0];
                            password.setText("");
                            login.setText("");
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Intent intent = new Intent(getContext, MainActivity.class);
                                    String[] data = new String[2];
                                    int k = 0;
                                    for(DataSnapshot i:dataSnapshot.getChildren()){
                                        data[k] = i.getValue().toString();
                                        k++;
                                    }
                                    intent.putExtra("role", data[1]);
                                    intent.putExtra("Login", username);
                                    intent.putExtra("password", data[0]);
                                    Toast.makeText(getContext,"Signed In successfully",Toast.LENGTH_LONG).show();
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getContext, "Check connection", Toast.LENGTH_LONG).show();
                                }
                            });
                        }else{
                            Toast.makeText(getContext, "Wrong login/password", Toast.LENGTH_LONG).show();
                        }
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
