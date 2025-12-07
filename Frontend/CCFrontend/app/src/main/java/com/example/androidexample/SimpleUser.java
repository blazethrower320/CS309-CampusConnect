package com.example.androidexample;

public class SimpleUser {
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private boolean isAdmin;
    private boolean isTutor;

    private String displayName;

    public SimpleUser(String username, String firstName, String lastName, String password, boolean isAdmin, boolean isTutor) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isTutor = isTutor;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }

    // getters/setters
    public String getUsername() {
        return username;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getPassword() {
        return password;
    }
    public boolean isAdmin() {
        return isAdmin;
    }
    public boolean isTutor() {
        return isTutor;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
    public void setTutor(boolean tutor) {
        isTutor = tutor;
    }
}
