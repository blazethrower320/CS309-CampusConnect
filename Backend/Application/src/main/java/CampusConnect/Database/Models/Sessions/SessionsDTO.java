package CampusConnect.Database.Models.Sessions;

import java.time.LocalDateTime;

public class SessionsDTO {
    private long tutorId;
    private String className;
    private String classCode;
    private String meetingLocation;
    private String meetingTime;
    private LocalDateTime dateCreated;

    // getters and setters
    public long getTutorId() { return tutorId; }
    public void setTutorId(long tutorId) { this.tutorId = tutorId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }

    public String getMeetingLocation() { return meetingLocation; }
    public void setMeetingLocation(String meetingLocation) { this.meetingLocation = meetingLocation; }

    public String getMeetingTime() { return meetingTime; }
    public void setMeetingTime(String meetingTime) { this.meetingTime = meetingTime; }

    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }
}
