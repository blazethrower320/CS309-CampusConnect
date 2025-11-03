package CampusConnect.Database.Models.Tutors;

import CampusConnect.Database.Models.Sessions.Sessions;
import CampusConnect.Database.Models.Users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Tutor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tutorId;
    private int totalClasses;
    private double rating = 0;
    private String username;
    private double hourlyRate;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "tutor")
    @JsonIgnore
    private Set<Sessions> tutorSessions = new HashSet<>();

    public Tutor() {}

    public Tutor(User user) {
        this.user = user;
        this.username = user.getUsername();
        //this.userId = userId;
        //this.totalClasses = totalClasses;
        //this.rating = rating;
        //this.username = username;
    }
    public String getUsername(){ return username; }
    //public long getUserId() { return userId; }
    public long getTutorId() { return tutorId; }
    public int gettotalClasses() { return totalClasses; }
    public double getRating() { return rating; }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
        if(user != null && user.getTutor() != this){
            user.setTutor(this);
        }
    }

    public double getHourlyRate(){
        return hourlyRate;
    }
    public void setHourlyRate(Double rate){
        this.hourlyRate = rate;
    }
}
