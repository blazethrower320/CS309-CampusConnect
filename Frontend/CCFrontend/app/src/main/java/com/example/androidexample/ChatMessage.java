package com.example.androidexample;

//Class for ChatMessage Objects(individual message bubbles)
public class ChatMessage {
    private String content;
    private boolean isSentByUser;
    private int messageType; // 0 for "TEXT", 1 for "IMAGE
    private String senderName;

    public ChatMessage(String content, boolean isSentByUser, int messageType)
    {
        this.content = content;
        this.isSentByUser = isSentByUser;
        this.messageType = messageType;
        this.senderName = null;
    }
    //Constructor for messages recieved from others.
    public ChatMessage(String content, boolean isSentByUser, int messageType, String senderName) {
        this.content = content;
        this.isSentByUser = isSentByUser;
        this.messageType = messageType;
        this.senderName = senderName; // Set the sender's name
    }

    // Add getters for all four fields
    public String getContent()
    {
        return content;
    }
    public boolean isSentByUser()
    {
        return isSentByUser;
    }
    public int getMessageType()
    {
        return messageType;
    }
    public String getSenderName()
    {
        return senderName;
    }
}

    