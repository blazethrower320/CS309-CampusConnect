package CampusConnect.Database.Models.Tutors;

import CampusConnect.Database.Models.Ratings.Ratings;
import CampusConnect.Database.Models.Users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Tutor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tutorId;
    //private long userId;
    private int totalClasses;
    private double totalRating;
    private String username;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToOne(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Ratings ratings;


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
    public long getTutorID() { return tutorId; }
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

    public void setUser(User user){
        this.user = user;
        if(user != null && user.getTutor() != this){
            user.setTutor(this);
        }
    }
    public Ratings getRatings() { return ratings; }
    public void setRatings(Ratings ratings){
        this.ratings = ratings;
        if(ratings != null && ratings.getTutor() != this){
            ratings.setTutor(this);
        }
    }
}
