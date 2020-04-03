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
import android.widget.TextView;
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

public class NewAccount extends AppCompatActivity {
    DatabaseReference databaseReference, check;
    EditText login, password;
    Button confirm;
    TextView cancel;
    Context getContext = this;
    String text_password, text_login;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_create_an_account);

        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        confirm = (Button) findViewById(R.id.confirm);
        cancel = (TextView) findViewById(R.id.cancel);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        check = FirebaseDatabase.getInstance().getReference().child("Users");


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                check.addValueEventListener(new ValueEventListener() {
                    boolean already_exists = false;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            if(ds.getKey().equals(text_login)){
                                already_exists = true;
                            }
                        }
                        if(!already_exists & !(text_login.length() == 0)){
                            already_exists = true;
                            databaseReference.child(text_login).setValue(new User(text_password, "user"), new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    Toast.makeText(getContext, "Success", Toast.LENGTH_SHORT).show();
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                    ArrayList<String> t = new ArrayList<>();
                                    t.add("system");
                                    t.add("greetings");
                                    t.add("admin");
                                    HashMap<String, Float> rating = new HashMap<>();
                                    rating.put("zero", (float) 0);
                                    ref.child("Favorite" + text_login).child("0").setValue(new RecyclerItem("Добро пожаловать!",
                                            "Это лента избранных постов",
                                            "https://firebasestorage.googleapis.com/v0/b/android-824bc.appspot.com/o/sigma.jpg?alt=media&token=328187a2-65a5-4623-885f-1d19c12d72d2",
                                            "System message",
                                            t,
                                            rating));

                                    finish();
                                }
                            });
                        }else if(text_login.length() == 0){
                            Toast.makeText(getContext, "Enter the login", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getContext, "Login is already used", Toast.LENGTH_LONG).show();
                            login.setText("");
                            text_login = "";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

    }

}
