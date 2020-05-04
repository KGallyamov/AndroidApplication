package com.example.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

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
                    String role="", password="", avatar="";
                    ArrayList<String> map = new ArrayList<>();
                    for(DataSnapshot i:dataSnapshot.getChildren()){
                        if(i.getKey().equals("password")){
                            password = i.getValue().toString();
                        }else if(i.getKey().equals("avatar")){
                            avatar = i.getValue().toString();
                        }else if(i.getKey().equals("role")){
                            role = i.getValue().toString();
                        }else{
                            for(DataSnapshot j:i.getChildren()){
                                if(!j.getKey().equals("zero")) {
                                    map.add(j.getValue().toString());
                                }
                            }
                        }
                    }
                    intent.putExtra("role", role);
                    intent.putExtra("Login", username);
                    intent.putExtra("password", password);
                    intent.putExtra("posts", map);
                    intent.putExtra("avatar", avatar);
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
