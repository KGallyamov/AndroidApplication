package com.example.android;

import java.util.HashMap;

public class GroupChat {
    private String chat_avatar;
    private HashMap<String, String> members;
    private HashMap<String, Message> messages;
    private String title;
    private String creator;
    private String pinned_message;
    GroupChat(){}

    GroupChat(String chat_avatar, HashMap<String, String> members, HashMap<String, Message> messages,
              String title, String creator, String pinned_message){
        this.chat_avatar = chat_avatar;
        this.members = members;
        this.messages = messages;
        this.title = title;
        this.creator = creator;
        this.pinned_message = pinned_message;
    }

    public String getPinned_message() {
        return pinned_message;
    }

    public void setPinned_message(String pinned_message) {
        this.pinned_message = pinned_message;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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
