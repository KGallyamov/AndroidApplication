package com.example.android;

import android.graphics.Bitmap;


public class RecyclerItem {

    private String title;
    private String description;

    private Bitmap image;


    //TODO: Image

    public RecyclerItem(String title, String description, Bitmap image) {
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
