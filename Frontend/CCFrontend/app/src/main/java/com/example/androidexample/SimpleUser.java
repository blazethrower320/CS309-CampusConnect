package com.example.androidexample;

public class SimpleUser {

    private int userId;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private boolean isAdmin;
    private boolean isTutor;

    private String displayName;

    public SimpleUser(int userId, String username, String firstName, String lastName,
                      String password, boolean isAdmin, boolean isTutor) {

        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isTutor = isTutor;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPassword() { return password; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    public boolean isTutor() { return isTutor; }
    public void setTutor(boolean tutor) { isTutor = tutor; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}
