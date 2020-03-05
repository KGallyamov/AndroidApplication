package com.example.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

public class PostPage extends AppCompatActivity {
    TextView title, description;
    ImageView imageView;
    Button close;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_page);
        title = (TextView) findViewById(R.id.txtTitle);
        description = (TextView) findViewById(R.id.txtDescription);
        imageView = (ImageView) findViewById(R.id.picture);
        close = (Button) findViewById(R.id.close);

        Intent intent = getIntent();
        String txt_title = intent.getStringExtra("title");
        String txt_description = intent.getStringExtra("description");
        String image_link = intent.getStringExtra("image link");

        title.setText(txt_title);
        description.setText(txt_description);
        Glide.with(PostPage.this).load(image_link).into(imageView);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
