package com.example.android;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class PostPage extends AppCompatActivity {
    TextView title, description, middle;
    ImageView imageView;
    Button close, ok, refuse, heading, send;
    Context getActivity = this;
    String role = "", path;
    Context context = this;
    ListView comments_list;
    EditText leave_a_comment;
    ArrayList<String> tags;
    AutoLinkTextView autoLinkTextView;
    RatingBar ratingBar;
    float midValue = 0;
    int pos;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_page);
        title = (TextView) findViewById(R.id.txtTitle);
        description = (TextView) findViewById(R.id.txtDescription);
        imageView = (ImageView) findViewById(R.id.picture);
        close = (Button) findViewById(R.id.close);
        ok = (Button) findViewById(R.id.ok);
        send = (Button) findViewById(R.id.send_comment);
        leave_a_comment = (EditText) findViewById(R.id.leave_a_comment);
        comments_list = findViewById(R.id.comments);
        heading = (Button) findViewById(R.id.heading);
        refuse = (Button) findViewById(R.id.reject);
        ratingBar = (RatingBar) findViewById(R.id.rating);
        middle = (TextView) findViewById(R.id.middle_rating);
        autoLinkTextView = (AutoLinkTextView) findViewById(R.id.tags);

        Intent intent = getIntent();
        final String txt_title = intent.getStringExtra("title");
        final String txt_description = intent.getStringExtra("description");
        final String image_link = intent.getStringExtra("image link");
        final String txt_heading = intent.getStringExtra("heading");
        final HashMap<String, Float> rating = new HashMap<>();
        final HashMap<String, Comment> comment = new HashMap<>();
        final String where = intent.getStringExtra("Where");
        final float rate = intent.getFloatExtra("rating", 1);
        final String login = intent.getStringExtra("login");
        final DecimalFormat df = new DecimalFormat("#.##");
        pos = intent.getIntExtra("position", 0);
        tags = intent.getStringArrayListExtra("tags");
        role = intent.getStringExtra("role");
        path = intent.getStringExtra("post path");
        ratingBar.setRating(rate);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(where).child(path).child("rating");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    i++;
                    midValue += ds.getValue(Float.TYPE);
                }
                if(i==1){
                    middle.setText("0.0");
                }else{
                    Float f = midValue/(i-1);
                    middle.setText(df.format(f));
                }
                midValue = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        autoLinkTextView.addAutoLinkMode(AutoLinkMode.MODE_HASHTAG, AutoLinkMode.MODE_URL);
        autoLinkTextView.setHashtagModeColor(ContextCompat.getColor(this, R.color.colorAccent));
        autoLinkTextView.setUrlModeColor(ContextCompat.getColor(this, R.color.blue_800));
        autoLinkTextView.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
            @Override
            public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                String[] tag = matchedText.split(" ");
                String res = "";
                for(String i:tag){
                    res += i;
                }
                if(res.charAt(0) == '#') {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", res);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(context, "Tag copied", Toast.LENGTH_SHORT).show();
                }else{
                    Uri address = Uri.parse(res);
                    Intent openlink = new Intent(Intent.ACTION_VIEW, address);
                    startActivity(openlink);
                }


            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                databaseReference.child(where).child(path).child("rating").child(login).setValue(rating);
                Toast.makeText(context, Float.toString(rating), Toast.LENGTH_SHORT).show();
            }
        });
        String to_tag = "";
        for(String s:tags){
            to_tag += s + " ";
        }
        autoLinkTextView.setAutoLinkText(to_tag);

        //модерация
        if(!role.equals("user")){
            ok.setVisibility(View.VISIBLE);
            refuse.setVisibility(View.VISIBLE);
            comments_list.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            middle.setVisibility(View.GONE);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rating.put("zero", (float) 0);
                    comment.put("zero", new Comment("nothing", "interesting", "in here"));
                    RecyclerItem post = new RecyclerItem(txt_title, txt_description, image_link, txt_heading, tags, rating, comment);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Data").push().setValue(post, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Toast.makeText(context, "Post added", Toast.LENGTH_SHORT).show();
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                            db.child("Moderate").child(path).removeValue();
                        }
                    });
                }
            });
            refuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    db.child("Moderate").child(path).removeValue();
                    Toast.makeText(getActivity, "Success", Toast.LENGTH_SHORT).show();

                }
            });
        }

        //считывание комментариев с базы данных
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Data").child(path);
        databaseReference.child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Comment> opinion = new ArrayList<>();
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    if(!i.getKey().equals("zero")) {
                        opinion.add(i.getValue(Comment.class));
                    }
                }
                CommentAdapter adapter = new CommentAdapter(context, R.layout.comment_item, opinion.toArray(new Comment[0]));
                comments_list.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity, "Check your connection", Toast.LENGTH_SHORT).show();
            }
        });

        title.setText(txt_title);
        description.setText(txt_description);
        Glide.with(PostPage.this).load(image_link).into(imageView);
        heading.setText(txt_heading);

        //закроет текущее окно
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("position", MODE_PRIVATE);
                SharedPreferences.Editor ed = preferences.edit();
                ed.putInt("position", pos);
                ed.apply();
                finish();
            }
        });

        //откроет новое окно только с картинкой
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity, PhotoPage.class);
                intent.putExtra("link", image_link);
                startActivity(intent);
            }
        });

        // отправка комментария
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeSettings = android.provider.Settings.System.getString(
                        getContentResolver(),
                        android.provider.Settings.System.AUTO_TIME);
                if (timeSettings.contentEquals("0")) {
                    android.provider.Settings.System.putString(
                            getContentResolver(),
                            android.provider.Settings.System.AUTO_TIME, "1");
                }
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("dd MMMM yyyy/HH:mm:ss");
                String datetime = dateformat.format(c.getTime());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Data");
                reference.child(path).child("comments").push().setValue(new Comment(login,
                        leave_a_comment.getText().toString(), datetime));
                leave_a_comment.setText("");
            }
        });
    }



}
