package com.example.androidexample;

public class TutorItem {
    public int tutorId;
    public String username;
    public String displayName;
    public double rating;

    public TutorItem(int tutorId, String username, String displayName, double rating) {
        this.tutorId = tutorId;
        this.username = username;
        this.displayName = displayName;
        this.rating = rating;
    }
}