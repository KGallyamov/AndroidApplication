package com.example.android;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class NewAccount extends AppCompatActivity {
    DatabaseReference databaseReference, check;
    EditText login, password;
    Button confirm;
    TextView cancel;
    Context getContext = this;
    String text_password, text_login;
    FirebaseAuth auth;



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
                        String mail = text_login.split("@")[1];
                        if(!already_exists && !(text_login.length() == 0) && !(text_password.length() < 8) && mail.equals("gmail.com")){
                            already_exists = true;
                            auth = FirebaseAuth.getInstance();
                            auth.createUserWithEmailAndPassword(text_login, text_password).addOnCompleteListener((Activity) getContext, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Toast.makeText(getContext, String.valueOf(task.isSuccessful()), Toast.LENGTH_LONG).show();
                                }
                            });
                            final String db_login = text_login.split("@")[0];
                            HashMap<String, String> posts = new HashMap<>();
                            posts.put("zero", "nothing");
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat dateformat = new SimpleDateFormat("dd MMMM yyyy");
                            String now = dateformat.format(c.getTime());
                            HashMap<String, String> chats = new HashMap<>();
                            chats.put("zero", "nothing");
                            HashMap<String, String> friends = new HashMap<>();
                            friends.put("zero", "nothing");
                            HashMap<String, String> privacy_settings = new HashMap();
                            privacy_settings.put("send_messages", "everyone");
                            privacy_settings.put("see_my_posts", "everyone");
                            privacy_settings.put("add_to_group_chats", "friends");
                            databaseReference.child(db_login).setValue(new User(text_password,
                                                                                "user",
                                    "https://firebasestorage.googleapis.com/v0/b/android-824bc.appspot.com/o/images%2Fdefault_avatar.png?alt=media&token=7807ba53-1240-41d1-8f63-70f2e3e38cec",
                                            posts, now, 0, chats, friends, privacy_settings),
                                    new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    Toast.makeText(getContext, "Success", Toast.LENGTH_SHORT).show();
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                    ArrayList<String> t = new ArrayList<>();
                                    t.add("system");
                                    t.add("greetings");
                                    t.add("admin");
                                    HashMap<String, String> rating = new HashMap<>();
                                    rating.put("zero", "nothing");
                                    HashMap<String, Comment> comments = new HashMap<>();
                                    HashMap<String, String> likes = new HashMap<>();
                                    likes.put("zero", "nothing");
                                    comments.put("zero", new Comment("nothing", "interesting", "in here", likes));
                                    ref.child("Favorite" + db_login).child("0").setValue(new RecyclerItem("Добро пожаловать!",
                                            "Это лента избранных постов",
                                            "https://firebasestorage.googleapis.com/v0/b/android-824bc.appspot.com/o/sigma.jpg?alt=media&token=328187a2-65a5-4623-885f-1d19c12d72d2",
                                            "System message",
                                            t,
                                            rating, comments, "kgalliamov", "14:52:02 14.мая.2020"));
                                    finish();
                                }
                            });
                        }else if(text_password.length() < 8){
                            Toast.makeText(getContext, "Create stronger password", Toast.LENGTH_LONG).show();
                        }else if(!mail.equals("gmail.com")){
                            Toast.makeText(getContext, "Enter gmail email", Toast.LENGTH_LONG).show();
                        }
                        else if(text_login.length() == 0){
                            Toast.makeText(getContext, "Enter the login", Toast.LENGTH_LONG).show();
                        }else if(already_exists){
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
