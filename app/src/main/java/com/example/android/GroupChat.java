package com.example.android;

import java.util.HashMap;

public class GroupChat {
    private String chat_avatar;
    private HashMap<String, String> members;
    private HashMap<String, Message> messages;
    private String title;
    GroupChat(){}

    GroupChat(String chat_avatar, HashMap<String, String> members, HashMap<String, Message> messages, String title){
        this.chat_avatar = chat_avatar;
        this.members = members;
        this.messages = messages;
        this.title = title;
    }

    public String getChat_avatar() {
        return chat_avatar;
    }

    public void setChat_avatar(String chat_avatar) {
        this.chat_avatar = chat_avatar;
    }

    public HashMap<String, String> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, String> members) {
        this.members = members;
    }

    public HashMap<String, Message> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<String, Message> messages) {
        this.messages = messages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
