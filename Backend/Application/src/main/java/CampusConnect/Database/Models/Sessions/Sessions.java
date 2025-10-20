package CampusConnect.Database.Models.Sessions;

import CampusConnect.Database.Models.Classes.Classes;
import CampusConnect.Database.Models.Users.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
public class Sessions
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long sessionId;
    private long userId;
    private long tutorId;
    private String className;
    private String classCode;
    private String meetingLocation;
    private String meetingTime;
    private LocalDateTime date;

    /*
    @ManyToMany(mappedBy = "userSessions")
    private Set<User> users;

    @ManyToOne
    @JoinColumn(name = "classId")
    private Classes sessionClass;
     */

    public Sessions(){}

    public Sessions(long userId, String className, String classCode, String meetingLocation, String meetingTime, long tutorId, LocalDateTime date) {
        this.userId = userId;
        this.tutorId = tutorId;
        this.className = className;
        this.classCode = classCode;
        this.meetingLocation = meetingLocation;
        this.meetingTime = meetingTime;
        this.date = date;
    }
    public long getUserId(){return userId;}
    public long getTutorId(){ return tutorId; }
    public String getMeetingLocation(){ return meetingLocation; }
    public String getMeetingTime(){ return meetingTime; }
    public long getSessionId() { return sessionId; }
    public String getClassName() { return className; }
    public String getClassCode() { return classCode; }
    public LocalDateTime getDate() { return date; }
}
