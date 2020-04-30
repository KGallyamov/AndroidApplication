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


    private HashMap<String, Float> rating;
    private HashMap<String, Comment> comments;



    RecyclerItem(){
    }


    public RecyclerItem(String title, String description, String image, String heading, ArrayList<String> tags, HashMap<String, Float> rating, HashMap<String, Comment> comments) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.heading = heading;
        this.tags = tags;
        this.rating = rating;
        this.comments = comments;
    }

    public HashMap<String, Comment> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, Comment> comments) {
        this.comments = comments;
    }

    public HashMap<String, Float> getRating() {
        return rating;
    }

    public void setRating(HashMap<String, Float> rating) {
        this.rating = rating;
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
