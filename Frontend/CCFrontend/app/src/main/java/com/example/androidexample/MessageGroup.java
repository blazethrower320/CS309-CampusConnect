package com.example.androidexample;

public class MessageGroup {
    private final int id; // The chat ID (from backend)
    private final String name; // The chat name (from backend)
    private final boolean isGroupChat; // The type of chat (from backend)
    private final String extraText; // For the "Enter Chat" text

    public MessageGroup(int id, String name, boolean isGroupChat, String extraText) {
        this.id = id;
        this.name = name;
        this.isGroupChat = isGroupChat;
        this.extraText = extraText;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public String getExtraText() {
        return extraText;
    }
}
