package com.example.android;

public class User {
    private String password;
    private String role;
    private String avatar;
    User(){
    }

    User(String password, String role, String avatar){
        this.password = password;
        this.role = role;
        this.avatar = avatar;
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
