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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.vision.text.Line;
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
    boolean pinned_exists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        path = getIntent().getStringExtra("path");
        final String pinned_message_link = getIntent().getStringExtra("pinned");
        if(pinned_message_link.equals("no_message")){
            setContentView(R.layout.activity_group_chat);
        }else{
            setContentView(R.layout.activity_group_chat_pinned_message);
            pinned_exists = true;
            final TextView pinned_text = (TextView) findViewById(R.id.text);
            final TextView pinned_author = (TextView) findViewById(R.id.author_login);
            final TextView pinned_time = (TextView)  findViewById(R.id.message_time);
            DatabaseReference pinned_data = FirebaseDatabase.getInstance().getReference();
            pinned_data.child("GroupChats").child(path).child("messages").child(pinned_message_link).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Message message = dataSnapshot.getValue(Message.class);
                    try{
                        pinned_text.setText(message.getText().substring(0, 20));
                    }catch (Exception e){
                        pinned_text.setText(message.getText());
                    }
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat dateformat = new SimpleDateFormat("dd.MMMM.yyyy");
                    SimpleDateFormat year = new SimpleDateFormat("yyyy");
                    String now = dateformat.format(c.getTime());
                    final String message_time = message.getTime();
                    String[] h = message_time.split(" ")[0].split(":");
                    // отправили не сегодня
                    if(!now.equals(message_time.split(" ")[1])){
                        String dm = message_time.split(" ")[1];
                        if(year.format(c.getTime()).equals(dm.substring(dm.length() - 4))) {
                            dm = dm.substring(0, dm.length() - 5);
                        }
                        pinned_time.setText(h[0] + ":" + h[1] + " " + dm);
                    }else{
                        pinned_time.setText(h[0] + ":" + h[1]);
                    }
                    pinned_author.setText(message.getAuthor());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        messages = (ListView) findViewById(R.id.messages_list);
        chat_title = (TextView) findViewById(R.id.title);
        exit = (TextView) findViewById(R.id.exit);
        attach_image = (Button) findViewById(R.id.attach_image);
        send = (Button) findViewById(R.id.send_comment);
        write_message = (EditText) findViewById(R.id.write_message);

        DatabaseReference db_chat_creator = FirebaseDatabase.getInstance().getReference();
        db_chat_creator.child("GroupChats").child(path).child("creator").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                (findViewById(R.id.chat_info)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GroupChatActivity.this, GroupChatInfo.class);
                        intent.putExtra("path", path);
                        intent.putExtra("creator", dataSnapshot.getValue().toString());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                final ArrayList<String> paths = new ArrayList<>();
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    list.add(i.getValue(Message.class));
                    paths.add(i.getKey());
                }
                GroupChatAdapter adapter = new GroupChatAdapter(GroupChatActivity.this,
                        R.layout.message_out_item,list.toArray(new Message[0]), path, paths, getLayoutInflater(), pinned_message_link);
                if(pinned_exists){
                    LinearLayout pinned = (LinearLayout) findViewById(R.id.pinned_message);
                    pinned.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            messages.smoothScrollToPosition(paths.indexOf(getIntent().getStringExtra("pinned")));
                        }
                    });
                }
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
            months.put("Jun", "июня");
            months.put("Jul", "июля");
            months.put("Aug", "августа");

            months.put("Sep", "сентября");
            months.put("Oct", "октября");
            months.put("Nov", "ноября");
            months.put("Dec", "декабря");

            months.put("Jan", "января");
            months.put("Feb", "февраля");
            months.put("Mar", "марта");
            months.put("Apr", "апреля");

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
