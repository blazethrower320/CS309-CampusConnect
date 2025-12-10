package com.example.androidexample;

public class RatingItem {
    public String username;
    public String comment;
    public double rating;
    public boolean isAdmin;

    public RatingItem(String username, String comment, double rating, boolean isAdmin) {
        this.username = username;
        this.comment = comment;
        this.rating = rating;
        this.isAdmin = isAdmin;
    }
}
