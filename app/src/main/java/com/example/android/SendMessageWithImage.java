package com.example.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class SendMessageWithImage extends AppCompatActivity {
    public EditText message;
    public ImageView image;
    public Button send;
    public String where;
    public String image_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_with_image);
        message = (EditText) findViewById(R.id.write_message);
        send = (Button) findViewById(R.id.submit);
        image = (ImageView) findViewById(R.id.image);
        Intent intent = getIntent();
        image_path  = intent.getStringExtra("path");
        where = intent.getStringExtra("where");
        String txt_message = intent.getStringExtra("message");
        message.setText(txt_message);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(image_path));
            image.setImageBitmap(bitmap);
        } catch (IOException e){
            e.printStackTrace();
        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }
    private void uploadImage() {

        if(image_path != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(SendMessageWithImage.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference storage = FirebaseStorage.getInstance().getReference();
            final StorageReference ref = storage.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(Uri.parse(image_path)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String image_txt = uri.toString();
                            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                            SharedPreferences.Editor ed = preferences.edit();
                            ed.putString("image", image_txt);
                            ed.apply();
                            new AsyncRequest().execute();
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SendMessageWithImage.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
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
                time_for_database = time_data[3] + " " + time_data[2] +
                        "." + months.get(time_data[1]) + "." + time_data[5];

            }
            // если не получилось взять время с сервера
            else{
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss dd.MMMM.yyyy");
                time_for_database = dateformat.format(c.getTime());
            }
            String image_txt = getPreferences(MODE_PRIVATE).getString("image", "");
            String message_text = message.getText().toString();
            if(message_text.equals("")) message_text = "Фотография";
            Message message_db = new Message(message_text,
                    login,
                    time_for_database, false, image_txt);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            if(where.equals("Messages")) {
                ref.child("Messages").child(getIntent().getStringExtra("db_path")).push().setValue(message_db);
            } else{
                ref.child("GroupChats").child(getIntent().getStringExtra("db_path")).child("messages").push().setValue(message_db);
            }
            finish();

        }
    }
}
