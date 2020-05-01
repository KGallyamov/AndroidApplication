package com.example.android;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

public class PhotoPage extends AppCompatActivity {
    PhotoViewAttacher attacher;
    @Override
    protected void onStart() {
        super.onStart();
        boolean isLarge =  (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        if(isLarge){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.photo_view);

        Intent intent = getIntent();
        String image_link = intent.getStringExtra("link");
        ImageView imageView = (ImageView) findViewById(R.id.photo);
        Button close = (Button) findViewById(R.id.close);
        attacher = new PhotoViewAttacher(imageView);
        Glide.with(PhotoPage.this).load(image_link).into(imageView);
        attacher.update();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
