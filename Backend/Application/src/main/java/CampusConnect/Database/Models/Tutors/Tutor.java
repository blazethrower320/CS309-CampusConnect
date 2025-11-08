package CampusConnect.Database.Models.Tutors;


import CampusConnect.Database.Models.Ratings.Ratings;
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
    @Column(name = "rating")
    private double totalRating = 5.0;
    private double hourlyRate = 0;
    private String username;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Ratings> ratings = new HashSet<>();


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

    public long getTutorId() { return tutorId; }
    public int gettotalClasses() { return totalClasses; }
    public double getTotalRating() { return totalRating; }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }
    public void setTotalRating(double rating) {
        this.totalRating = rating;
    }

    public User getUser(){
        return user;
    }
    public double getHourlyRate(){
        return hourlyRate;
    }
    public void setHourlyRate(Double rate){
        this.hourlyRate = rate;
    }


    public void setUser(User user){
        this.user = user;
        if(user != null && user.getTutor() != this){
            user.setTutor(this);
        }
    }
    public Set<Ratings> getRatings() {
        return ratings;
    }
    public void setRatings(Set<Ratings> ratings) {
        this.ratings = ratings;
    }
}
