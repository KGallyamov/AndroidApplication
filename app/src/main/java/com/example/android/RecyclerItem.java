package com.example.android;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;


public class RecyclerItem {

    private String title;
    private String description;

    private String image;
    private String heading;
    private ArrayList<String> tags;

    RecyclerItem(){
    }


    public RecyclerItem(String title, String description, String image, String heading, ArrayList<String> tags) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.heading = heading;
        this.tags = tags;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
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
