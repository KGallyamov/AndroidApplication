package com.example.android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PostPage extends AppCompatActivity {
    TextView title, description;
    ImageView imageView;
    Button close, ok, refuse, heading;
    Context getActivity = this;
    String role = "", path;
    Context context = this;
    ArrayList<String> tags;
    AutoLinkTextView autoLinkTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_page);
        title = (TextView) findViewById(R.id.txtTitle);
        description = (TextView) findViewById(R.id.txtDescription);
        imageView = (ImageView) findViewById(R.id.picture);
        close = (Button) findViewById(R.id.close);
        ok = (Button) findViewById(R.id.ok);
        heading = (Button) findViewById(R.id.heading);
        refuse = (Button) findViewById(R.id.reject);
        autoLinkTextView = (AutoLinkTextView) findViewById(R.id.tags);
        autoLinkTextView.addAutoLinkMode(AutoLinkMode.MODE_HASHTAG);
        autoLinkTextView.setHashtagModeColor(ContextCompat.getColor(this, R.color.blue_800));

        Intent intent = getIntent();
        final String txt_title = intent.getStringExtra("title");
        final String txt_description = intent.getStringExtra("description");
        final String image_link = intent.getStringExtra("image link");
        final String txt_heading = intent.getStringExtra("heading");
        tags = intent.getStringArrayListExtra("tags");
        String to_tag = "";
        for(String s:tags){
            to_tag += s + " ";
        }
        autoLinkTextView.setAutoLinkText(to_tag);
        role = intent.getStringExtra("role");
        path = intent.getStringExtra("post path");
        if(!role.equals("user")){
            ok.setVisibility(View.VISIBLE);
            refuse.setVisibility(View.VISIBLE);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecyclerItem post = new RecyclerItem(txt_title, txt_description, image_link, txt_heading, tags);
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

        title.setText(txt_title);
        description.setText(txt_description);
        Glide.with(PostPage.this).load(image_link).into(imageView);
        heading.setText(txt_heading);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity, PhotoPage.class);
                intent.putExtra("link", image_link);
                startActivity(intent);
            }
        });

    }
}
