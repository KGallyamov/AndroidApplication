package com.example.android;

import java.util.HashMap;

public class User {
    private String password;
    private String role;
    private String avatar;
    private HashMap<String, String> posts;
    private HashMap<String, String> chats;
    private String lastSeen;
    private float rating;
    User(){
    }

    User(String password, String role, String avatar, HashMap<String, String> posts, String lastSeen, float rating, HashMap<String, String> chats){
        this.password = password;
        this.role = role;
        this.avatar = avatar;
        this.posts = posts;
        this.lastSeen = lastSeen;
        this.rating = rating;
        this.chats = chats;
    }

    public HashMap<String, String> getChats() {
        return chats;
    }

    public void setChats(HashMap<String, String> chats) {
        this.chats = chats;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public HashMap<String, String> getPosts() {
        return posts;
    }

    public void setPosts(HashMap<String, String> posts) {
        this.posts = posts;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
