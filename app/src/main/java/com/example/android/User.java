package com.example.android;

import java.util.HashMap;

public class User {
    // класс пользователя
    private String password;
    private String role;
    private String avatar;
    private HashMap<String, String> posts;
    private HashMap<String, String> chats;
    private String lastSeen;
    private float rating;
    private HashMap<String, String> friends;
    private HashMap<String, String> privacy_settings;
    User(){
    }

    User(String password, String role, String avatar, HashMap<String, String> posts, String lastSeen,
         float rating, HashMap<String, String> chats, HashMap<String, String> friends,
         HashMap<String, String> privacy_settings){
        this.password = password;
        this.role = role;
        this.avatar = avatar;
        this.posts = posts;
        this.lastSeen = lastSeen;
        this.rating = rating;
        this.chats = chats;
        this.friends = friends;
        this.privacy_settings = privacy_settings;
    }

    public HashMap<String, String> getPrivacy_settings() {
        return privacy_settings;
    }

    public void setPrivacy_settings(HashMap<String, String> privacy_settings) {
        this.privacy_settings = privacy_settings;
    }

    public HashMap<String, String> getFriends() {
        return friends;
    }

    public void setFriends(HashMap<String, String> friends) {
        this.friends = friends;
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
