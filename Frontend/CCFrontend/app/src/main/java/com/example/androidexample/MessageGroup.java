package com.example.androidexample;

public class MessageGroup {
    private int sessionId;
    private String groupName;
    private String lastMessage;
    private String messageTime;

    public MessageGroup(int sessionId, String groupName, String lastMessage, String messageTime) {
        this.sessionId = sessionId;
        this.groupName = groupName;
        this.lastMessage = lastMessage;
        this.messageTime = messageTime;
    }

    // --- Getters ---
    public int getSessionId() {
        return sessionId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getMessageTime() {
        return messageTime;
    }
}
