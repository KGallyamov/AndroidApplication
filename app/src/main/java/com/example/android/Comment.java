package com.example.android;

public class Comment {
    String author, text;

    Comment(){}

    Comment(String author, String text){
        this.author = author;
        this.text = text;
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
