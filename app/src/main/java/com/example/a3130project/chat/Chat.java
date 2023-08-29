package com.example.a3130project.chat;

public class Chat {
    //    keys
    private String username;
    private String chatMessage;
    //required empty constructor for firebase
    public Chat() {

    }
    //initialising constructor
    public Chat(String username, String chatMessage) {
        this.username = username;
        this.chatMessage = chatMessage;
    }
    //getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }
}
