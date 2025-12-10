package com.example.androidexample;

public class ChatMessage {
    private String content;
    private final boolean isSentByUser;
    private final int messageType; // 0 for text, 1 for image
    private String senderName; // Added to store the name of the sender

    /**
     * Constructor for messages sent by the current user (local echo).
     */
    public ChatMessage(String content, boolean isSentByUser, int messageType) {
        this.content = content;
        this.isSentByUser = isSentByUser;
        this.messageType = messageType;
        this.senderName = null; // Not needed for sent messages
    }

    public void appendContent(String textToAppend) {
        // The 'this.content' refers to the 'content' field of the ChatMessage object
        this.content += textToAppend;
    }

    /**
     * Constructor for messages received from others, including chat history.
     */
    public ChatMessage(String content, boolean isSentByUser, int messageType, String senderName) {
        this.content = content;
        this.isSentByUser = isSentByUser;
        this.messageType = messageType;
        this.senderName = senderName;
    }

    // --- Getters ---
    public String getContent() {
        return content;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    public int getMessageType() {
        return messageType;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setContent(String s)
    {
        this.content = s;
    }
}
