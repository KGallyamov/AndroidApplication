package com.example.android;

import java.util.HashMap;

public class User {
    private String password;
    private String role;
    private String avatar;
    private HashMap<String, String> posts;
    private String lastSeen;
    User(){
    }

    User(String password, String role, String avatar, HashMap<String, String> posts, String lastSeen){
        this.password = password;
        this.role = role;
        this.avatar = avatar;
        this.posts = posts;
        this.lastSeen = lastSeen;
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
