package com.example.android;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {
    String path;
    ListView messages;
    TextView chat_title;
    TextView exit;
    Button send, attach_image;
    EditText write_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        path = getIntent().getStringExtra("path");
        messages = (ListView) findViewById(R.id.messages_list);
        chat_title = (TextView) findViewById(R.id.title);
        exit = (TextView) findViewById(R.id.exit);
        attach_image = (Button) findViewById(R.id.attach_image);
        send = (Button) findViewById(R.id.send_comment);
        write_message = (EditText) findViewById(R.id.write_message);

        (findViewById(R.id.chat_info)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatActivity.this, GroupChatInfo.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        DatabaseReference db_chat_title = FirebaseDatabase.getInstance().getReference();
        db_chat_title.child("GroupChats").child(path).child("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     chat_title.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference chat_avatar = FirebaseDatabase.getInstance().getReference();
        chat_avatar.child("GroupChats").child(path).child("chat_avatar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(GroupChatActivity.this).load(dataSnapshot.getValue().toString()).into((ImageView) findViewById(R.id.chat_avatar));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("GroupChats");
        reference.child(path).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Message> list = new ArrayList<>();
                ArrayList<String> paths = new ArrayList<>();
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    list.add(i.getValue(Message.class));
                    paths.add(i.getKey());
                }
                GroupChatAdapter adapter = new GroupChatAdapter(GroupChatActivity.this,
                        R.layout.message_out_item,list.toArray(new Message[0]), path, paths, getLayoutInflater());
                messages.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        attach_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!write_message.getText().toString().equals("")){
                    new AsyncRequest().execute();
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 71);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 71 && resultCode == -1
                && data != null && data.getData() != null )
        {
            Uri filePath = data.getData();
            Intent intent = new Intent(GroupChatActivity.this, SendMessageWithImage.class);
            intent.putExtra("path", filePath.toString());
            intent.putExtra("where", "GroupChats");
            intent.putExtra("db_path", path);
            intent.putExtra("message", write_message.getText().toString());
            startActivity(intent);

        }
    }
    class AsyncRequest extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg) {
            String time = "failed";
            try {
                TimeTCPClient client = new TimeTCPClient();
                try {
                    client.setDefaultTimeout(10000);
                    client.connect("time.nist.gov");
                    time =  client.getDate().toString();
                } finally {
                    client.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return time;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Data");
            HashMap<String, String> months = new HashMap<>();
            months.put("May", "мая");
            months.put("June", "июня");
            months.put("July", "июля");
            months.put("August", "августа");

            months.put("September", "сентября");
            months.put("October", "октября");
            months.put("November", "ноября");
            months.put("December", "декабря");

            months.put("January", "января");
            months.put("February", "февраля");
            months.put("March", "марта");
            months.put("April", "апреля");

            String time_for_database;
            if(!s.equals("failed")) {
                String[] time_data = s.split(" ");
                for (String i : time_data) {
                    Log.d("Look", i);
                }

                time_for_database = time_data[3] + " " + time_data[2] +
                        "." + months.get(time_data[1]) + "." + time_data[5];

            }
            // если не получилось взять время с сервера
            else{
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss dd.MMMM.yyyy");
                time_for_database = dateformat.format(c.getTime());
            }

            Message message = new Message(write_message.getText().toString(),
                    login,
                    time_for_database, false, "no_image");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("GroupChats").child(path).child("messages").push().setValue(message);
            write_message.setText("");

        }
    }
}
