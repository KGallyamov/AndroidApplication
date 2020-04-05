package com.example.android;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class PostPage extends AppCompatActivity {
    TextView title, description, middle;
    ImageView imageView;
    Button close, ok, refuse, heading;
    Context getActivity = this;
    String role = "", path;
    Context context = this;
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
        final String where = intent.getStringExtra("Where");
        final float rate = intent.getFloatExtra("rating", 1);
        final String login = intent.getStringExtra("login");
        pos = intent.getIntExtra("position", 0);
        tags = intent.getStringArrayListExtra("tags");
        role = intent.getStringExtra("role");
        path = intent.getStringExtra("post path");
        ratingBar.setRating(rate);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(where).child(path).child("rating");
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
                    middle.setText(Float.toString(midValue/(i-1)));
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
        rating.put("zero", (float) 0);

        autoLinkTextView.setAutoLinkText(to_tag);
        if(!role.equals("user")){
            ok.setVisibility(View.VISIBLE);
            refuse.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.GONE);
            middle.setVisibility(View.GONE);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecyclerItem post = new RecyclerItem(txt_title, txt_description, image_link, txt_heading, tags, rating);
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
                SharedPreferences preferences = getSharedPreferences("position", MODE_PRIVATE);
                SharedPreferences.Editor ed = preferences.edit();
                ed.putInt("position", pos);
                ed.apply();
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
