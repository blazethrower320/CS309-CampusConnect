package com.example.androidexample;

//Class for ChatMessage Objects(individual message bubbles)
public class ChatMessage {
    private String content;
    private boolean isSentByUser;
    private int messageType; // 0 for "TEXT", 1 for "IMAGE

    public ChatMessage(String content, boolean isSentByUser, int messageType)
    {
        this.content = content;
        this.isSentByUser = isSentByUser;
        this.messageType = messageType;
    }

    // Add getters for all three fields
    public String getContent() { return content; }
    public boolean isSentByUser() { return isSentByUser; }
    public int getMessageType() { return messageType; }
}

    