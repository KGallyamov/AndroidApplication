package com.example.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class splash extends AppCompatActivity {
    public SharedPreferences preferences;
    int notifyId = 100;
    String login, role, password;
    Context context = this;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void onStart() {
        super.onStart();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Intent intent = new Intent(context, MainActivity.class);
                    String[] data = new String[2];
                    int k = 0;
                    for(DataSnapshot i:dataSnapshot.getChildren()){
                        data[k] = i.getValue().toString();
                        k++;
                    }
                    intent.putExtra("role", data[1]);
                    intent.putExtra("Login", username);
                    intent.putExtra("password", data[0]);
                    Toast.makeText(context,"Signed In successfully",Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else {
            startActivity(new Intent(this, Authentication.class));

        }
    }
}
