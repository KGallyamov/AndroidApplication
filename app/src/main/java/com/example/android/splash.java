package com.example.android;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss dd.MMMM.yyyy");
        String now = dateformat.format(c.getTime());
        if(mAuth.getCurrentUser() != null) {
            login = mAuth.getCurrentUser().getEmail().split("@")[0];
            reference.child("Users").child(login).child("lastSeen").setValue(now).addOnSuccessListener(splash.this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }
    }
}
