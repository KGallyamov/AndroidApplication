package com.example.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseSmartReply;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestion;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestionResult;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class OneChatActivity extends AppCompatActivity {
    ListView messages;
    TextView another_user;
    TextView exit;
    TextView lastSeen;
    String[] arr;
    int click = 0;
    boolean not_supported = false;
    ArrayList<String> reply_list;
    EditText write_message;
    Button send, quick_reply, attach_image;
    Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messages = (ListView) findViewById(R.id.messages_list);
        another_user = (TextView) findViewById(R.id.title);
        lastSeen = (TextView) findViewById(R.id.lastSeen);
        exit = (TextView) findViewById(R.id.exit);
        attach_image = (Button) findViewById(R.id.attach_image);
        quick_reply = (Button) findViewById(R.id.quick_reply);
        send = (Button) findViewById(R.id.send_comment);
        write_message = findViewById(R.id.write_message);
        Intent intent = getIntent();
        final String another_user_name = intent.getStringExtra("Another_person");
        final String login = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        another_user.setText(another_user_name);
        arr = new String[]{login, another_user_name};
        Arrays.sort(arr);
        final DatabaseReference update_read = FirebaseDatabase.getInstance().getReference().child("Messages");
        update_read.child(arr[0] + "_" + arr[1]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Message message = ds.getValue(Message.class);
                    if(!message.isRead() && !login.equals(message.getAuthor())){
                        DatabaseReference update_read_2 = FirebaseDatabase.getInstance().getReference().child("Messages");
                        update_read_2.child(arr[0] + "_" + arr[1]).child(ds.getKey()).child("read").setValue(true);
                    }
                }
                update_read.removeEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages");
        reference.child(arr[0] + "_" + arr[1]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Message> list = new ArrayList<>();
                ArrayList<String> paths = new ArrayList<>();
                List<FirebaseTextMessage> conversation = new ArrayList<>();
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    list.add(i.getValue(Message.class));
                    paths.add(i.getKey());
                    if(login.equals(i.getValue(Message.class).getAuthor())) {
                        conversation.add(FirebaseTextMessage.createForLocalUser(
                                i.getValue(Message.class).getText(), System.currentTimeMillis()));
                    } else{
                        conversation.add(FirebaseTextMessage.createForRemoteUser(
                                i.getValue(Message.class).getText(), System.currentTimeMillis(), i.getValue(Message.class).getAuthor()));
                    }
                }
                OneChatAdapter adapter = new OneChatAdapter(context, R.layout.message_out_item, list.toArray(new Message[0]),
                        arr[0] + "_" + arr[1], paths);
                messages.setAdapter(adapter);
                FirebaseSmartReply smartReply = FirebaseNaturalLanguage.getInstance().getSmartReply();
                smartReply.suggestReplies(conversation)
                        .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                            @Override
                            public void onSuccess(SmartReplySuggestionResult result) {
                                reply_list = new ArrayList<>();
                                not_supported = false;
                                if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                                    not_supported = true;
                                } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                                    for (SmartReplySuggestion suggestion : result.getSuggestions()) {
                                        reply_list.add(suggestion.getText());
                                    }
                                }

                                quick_reply.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(not_supported) {
                                            Toast.makeText(OneChatActivity.this, "Not supported language. Sorry!", Toast.LENGTH_SHORT).show();
                                        }else{
                                            write_message.setText(reply_list.get(click % reply_list.size()));
                                            ++click;
                                        }
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference userTime = FirebaseDatabase.getInstance().getReference();
        userTime.child("Users").child(another_user_name).child("lastSeen").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String time = dataSnapshot.getValue().toString();
                if(time.equals("online")){
                    lastSeen.setTextColor(getResources().getColor(R.color.active_blue));
                }
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("dd.MMMM.yyyy");
                String now = dateformat.format(c.getTime());
                if(now.equals(time.split(" ")[1])){
                    lastSeen.setText(time.split(" ")[0]);
                }else{
                    lastSeen.setText(time);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            String[] arr = new String[]{login,
                    another_user.getText().toString()};
            Arrays.sort(arr);
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
                    time_for_database, false, "");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("Messages").child(arr[0] + "_" + arr[1]).push().setValue(message);
            write_message.setText("");

        }
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
            Intent intent = new Intent(OneChatActivity.this, SendMessageWithImage.class);
            intent.putExtra("path", filePath.toString());
            intent.putExtra("where", "Messages");
            intent.putExtra("db_path", arr[0] + "_" + arr[1]);
            intent.putExtra("message", write_message.getText().toString());
            startActivity(intent);

        }
    }
}