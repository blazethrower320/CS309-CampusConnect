package com.example.androidexample;

import org.json.JSONObject;

public class User {

    private static User instance;

    private int userId = -1;
    private int tutorId = -1;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String bio;
    private String major;
    private String classification;
    private boolean isTutor;
    private boolean isAdmin;

    private User() {}

    public static synchronized User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public static void clearInstance() {
        instance = null;
    }

    public static User fromJson(JSONObject obj) {
        User u = new User();
        u.setUserId(obj.optInt("userId", -1));
        u.setUsername(obj.optString("username", ""));
        u.setFirstName(obj.optString("firstName", ""));
        u.setLastName(obj.optString("lastName", ""));
        u.setBio(obj.optString("bio", ""));
        u.setMajor(obj.optString("major", ""));
        u.setClassification(obj.optString("classification", ""));
        u.setTutor(obj.optBoolean("isTutor", false));
        u.setAdmin(obj.optBoolean("isAdmin", false));
        return u;
    }


    // --- Getters and setters ---
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTutorId() { return tutorId; }
    public void setTutorId(int tutorId) { this.tutorId = tutorId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }

    public boolean isTutor() { return isTutor; }
    public void setTutor(boolean tutor) { isTutor = tutor; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }
}
