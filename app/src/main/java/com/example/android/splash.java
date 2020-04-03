package com.example.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class splash extends AppCompatActivity {
    public SharedPreferences preferences;
    String login, role, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        preferences = getSharedPreferences("UserData", MODE_PRIVATE);

        login = preferences.getString("login", "");
        password = preferences.getString("password", "");
        role = preferences.getString("role", "");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(login.equals("")) {
            startActivity(new Intent(this, Authentication.class));
        }else{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("Login", login);
            intent.putExtra("role", role);
            intent.putExtra("password", password);
            startActivity(intent);
        }


    }
}
