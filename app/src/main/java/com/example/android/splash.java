package com.example.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ConditionVariable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class splash extends AppCompatActivity {
    SignInUp sign;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sign = new SignInUp(this);
        //TODO: проверить в первый ли раз зашел

        startActivity(new Intent(this, Authentication.class));
    }

    public class SignInUp{
        Context cont;
        SharedPreferences pref;
        SharedPreferences.Editor editor;
        SignInUp(Context context){
            this.cont = context;
            pref = cont.getSharedPreferences("PREF", 0);
            editor = pref.edit();
        }

        public void setFirst(boolean isFirst){
            editor.putBoolean("Is First", isFirst);
            editor.commit();
        }

        public boolean isFirst(){
            return pref.getBoolean("Is First", true);
        }
    }
}
