package com.example.androidexample;

public class MessageGroup {
    private int sessionId;
    private String groupName;
    private String messageTime;

    public MessageGroup(int sessionId, String groupName, String messageTime) {
        this.sessionId = sessionId;
        this.groupName = groupName;
        this.messageTime = messageTime;
    }

    // --- Getters ---
    public int getSessionId() {
        return sessionId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getMessageTime() {
        return messageTime;
    }
}
