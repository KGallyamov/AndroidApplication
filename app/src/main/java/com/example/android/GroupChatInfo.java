package com.example.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupChatInfo extends AppCompatActivity {
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_info);
        path = getIntent().getStringExtra("path");
        final EditText new_member_name = (EditText) findViewById(R.id.member_name);
        final ListView members = (ListView) findViewById(R.id.members);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("GroupChats").child(path).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> list_members = new ArrayList<>();
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    list_members.add(i.getValue().toString());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(GroupChatInfo.this, android.R.layout.simple_list_item_1, list_members.toArray(new String[0]));
                members.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ((TextView) findViewById(R.id.exit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((Button) findViewById(R.id.add_member)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new_member_name.getText().toString().equals("")){
                    Toast.makeText(GroupChatInfo.this, "Enter the name", Toast.LENGTH_SHORT).show();
                }else{
                    String name = new_member_name.getText().toString();
                    DatabaseReference add_member = FirebaseDatabase.getInstance().getReference();
                    add_member.child("GroupChats").child(path).child("members").child(name).setValue(name);
                    DatabaseReference add_chat = FirebaseDatabase.getInstance().getReference();
                    add_chat.child("Users").child(name).child("chats").push().setValue(path).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(GroupChatInfo.this, "User added", Toast.LENGTH_SHORT).show();
                            new_member_name.setText("");
                        }
                    });

                }
            }
        });
    }
}
