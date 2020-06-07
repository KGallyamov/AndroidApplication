package com.example.android;

import java.util.HashMap;

public class Comment {
    // класс комментария к посту
    private String author, text, time;
    private String reply;
    private HashMap<String, String> likes;

    Comment() {
    }

    Comment(String author, String text, String time, HashMap<String, String> likes) {
        this.author = author;
        this.text = text;
        this.time = time;
        this.likes = likes;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public HashMap<String, String> getLikes() {
        return likes;
    }

    public void setLikes(HashMap<String, String> likes) {
        this.likes = likes;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
