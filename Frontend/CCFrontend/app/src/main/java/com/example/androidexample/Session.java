package com.example.androidexample;

public class Session {
    private int sessionId;
    private String className;
    private String classCode;
    private int tutorUserId;
    private String meetingLocation;
    private String meetingTime;

    private int tutorId;
    private String tutorUsername;
    private boolean joined;

    public Session(int sessionId, String className, String classCode, String meetingLocation, int tutorUserId, String meetingTime, String tutorUsername) {
        this.sessionId = sessionId;
        this.tutorUserId = tutorUserId;
        this.className = className;
        this.classCode = classCode;
        this.meetingLocation = meetingLocation;
        this.meetingTime = meetingTime;
        this.tutorUsername = tutorUsername;
        this.joined = false;
    }

    public int getSessionId() { return sessionId; }
    public String getClassName() { return className; }
    public String getClassCode() { return classCode; }
    public String getMeetingLocation() { return meetingLocation; }
    public String getMeetingTime() { return meetingTime; }

    public int getTutorId() { return tutorId; }

    public void setTutorId(int tutorId) { this.tutorId = tutorId; }

    public int getTutorUserId() { return tutorUserId; }

    public void setTutorUserId(int tutorUserId) { this.tutorUserId = tutorUserId; }

    public String getTutorUsername() { return tutorUsername; }
    public void setTutorUsername(String tutorUsername) { this.tutorUsername = tutorUsername; }

    public boolean isJoined() { return joined; }

    public void setJoined(boolean joined) { this.joined = joined; }
}
