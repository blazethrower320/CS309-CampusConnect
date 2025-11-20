package CampusConnect.Database.Models.Sessions;

import CampusConnect.Database.Models.Classes.Classes;
import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Tutors.Tutor;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
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
    private String meetingLocation;

    @JsonFormat(pattern = "M/dd/yyyy h:mm a")
    private LocalDateTime meetingTime; // 10/03/2025 04:00 PM

    private LocalDateTime dateCreated;

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

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Classes classEntity;

    public Sessions(){}

    public Sessions(Tutor tutor, String meetingLocation, LocalDateTime meetingTime, LocalDateTime date) {
        this.tutor = tutor;
        this.meetingLocation = meetingLocation;
        this.meetingTime = meetingTime;
        this.dateCreated = date;
    }
    public void setMeetingTime(LocalDateTime meetingTime)
    {
        this.meetingTime = meetingTime;
    }
    public LocalDateTime getMeetingTime(){ return meetingTime; }

    public void setMeetingLocation(String meetingLocation) { this.meetingLocation = meetingLocation; }
    public String getMeetingLocation(){ return meetingLocation; }

    public long getSessionId() { return sessionId; }


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

    public void removeUser(User user){
        if(!users.remove(user)){
            throw new RuntimeException("User couldn't be added");
        }
    }

    public Classes getClassEntity(){
        return classEntity;
    }


    public void setClassEntity(Classes classes){
        this.classEntity = classes;
    }

    public void removeClass(Classes classes){
        if(classes.equals(classEntity)){
            this.classEntity = null;
        }
        else throw new RuntimeException("Class does not match");
    }
}
