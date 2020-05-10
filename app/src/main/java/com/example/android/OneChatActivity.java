package com.example.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class OneChatActivity extends AppCompatActivity {
    ListView messages;
    TextView another_user;
    TextView exit;
    EditText write_message;
    Button send;
    Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messages = (ListView) findViewById(R.id.messages_list);
        another_user = (TextView) findViewById(R.id.title);
        exit = (TextView) findViewById(R.id.exit);
        send = (Button) findViewById(R.id.send_comment);
        write_message = findViewById(R.id.write_message);
        Intent intent = getIntent();
        final String another_user_name = intent.getStringExtra("Another_person");
        final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        another_user.setText(another_user_name);
        String[] arr = new String[]{login, another_user_name};
        Arrays.sort(arr);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages");
        reference.child(arr[0] + "_" + arr[1]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Message> list = new ArrayList<>();
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    list.add(i.getValue(Message.class));
                }
                OneChatAdapter adapter = new OneChatAdapter(context, R.layout.message_out_item, list.toArray(new Message[0]));
                messages.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!write_message.getText().toString().equals("")){
                    String[] arr = new String[]{login, another_user_name};
                    Arrays.sort(arr);
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss dd.MMMM.yyyy");
                    String now = dateformat.format(c.getTime());
                    Message message = new Message(write_message.getText().toString(),
                            login,
                            now);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.child("Messages").child(arr[0] + "_" + arr[1]).push().setValue(message);
                    write_message.setText("");
                }
            }
        });
        another_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user_page = new Intent(OneChatActivity.this, AnotherUserPage.class);
                user_page.putExtra("author", another_user_name);
                startActivity(user_page);
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
