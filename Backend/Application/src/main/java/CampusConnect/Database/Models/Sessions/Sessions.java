package CampusConnect.Database.Models.Sessions;

import CampusConnect.Database.Models.Classes.Classes;
import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Tutors.Tutor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Sessions
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long sessionId;
    private String className;
    private String classCode;
    private String meetingLocation;
    private String meetingTime;
    private LocalDateTime dateCreated;
    private boolean pastSession;


    @ManyToMany
    @JoinTable(
            name = "StudySessions",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private  Set<User> users = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;


    public Sessions(){}

    public Sessions(Tutor tutor, String className, String classCode, String meetingLocation, String meetingTime, LocalDateTime date) {
        this.tutor = tutor;
        this.className = className;
        this.classCode = classCode;
        this.meetingLocation = meetingLocation;
        this.meetingTime = meetingTime;
        this.dateCreated = date;
    }
    public void setMeetingTime(String meetingTime)
    {
        this.meetingTime = meetingTime;
    }
    public String getMeetingTime(){ return meetingTime; }

    public void setMeetingLocation(String meetingLocation) { this.meetingLocation = meetingLocation; }
    public String getMeetingLocation(){ return meetingLocation; }

    public long getSessionId() { return sessionId; }

    public String getClassName() { return className; }

    public String getClassCode() { return classCode; }

    public LocalDateTime getDateCreated() { return dateCreated; }

    public Tutor getTutor() { return tutor; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }

    public Set<User> getUsers(){
        return users;
    }

    public void addUser(User user){
        if(!users.add(user)){
            throw new RuntimeException("User couldn't be added");
        }
    }

}
