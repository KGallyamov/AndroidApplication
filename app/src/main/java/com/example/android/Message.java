package com.example.android;

public class Message {
    private String text;
    private String author;
    private String time;
    private boolean read;
    private String image;
    Message(){}

    public Message(String text, String author, String time, boolean read, String image) {
        this.text = text;
        this.author = author;
        this.time = time;
        this.read = read;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
