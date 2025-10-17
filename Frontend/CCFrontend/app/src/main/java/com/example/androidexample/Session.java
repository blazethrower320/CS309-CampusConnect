package com.example.androidexample;

public class Session {
    private int sessionId;
    private String className;
    private String classCode;
    private String meetingLocation;
    private String meetingTime;
    private String tutorUsername; // 👈 new field

    public Session(int sessionId, String className, String classCode, String meetingLocation, String meetingTime, String tutorUsername) {
        this.sessionId = sessionId;
        this.className = className;
        this.classCode = classCode;
        this.meetingLocation = meetingLocation;
        this.meetingTime = meetingTime;
        this.tutorUsername = tutorUsername;
    }

    public int getSessionId() { return sessionId; }
    public String getClassName() { return className; }
    public String getClassCode() { return classCode; }
    public String getMeetingLocation() { return meetingLocation; }
    public String getMeetingTime() { return meetingTime; }

    public String getTutorUsername() { return tutorUsername; }
    public void setTutorUsername(String tutorUsername) { this.tutorUsername = tutorUsername; }
}
