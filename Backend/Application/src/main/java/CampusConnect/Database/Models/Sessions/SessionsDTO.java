package CampusConnect.Database.Models.Sessions;

import java.time.LocalDateTime;

public class SessionsDTO {
    private long tutorId;
    private long classId;
    private String meetingLocation;
    private String meetingTime;
    private LocalDateTime dateCreated;

    // getters and setters
    public long getTutorId() { return tutorId; }
    public void setTutorId(long tutorId) { this.tutorId = tutorId; }

    public Long getClassId() { return classId; }
    public void setclassId(long classId) {this.classId = classId; }

    public String getMeetingLocation() { return meetingLocation; }
    public void setMeetingLocation(String meetingLocation) { this.meetingLocation = meetingLocation; }

    public String getMeetingTime() { return meetingTime; }
    public void setMeetingTime(String meetingTime) { this.meetingTime = meetingTime; }

    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }
}
