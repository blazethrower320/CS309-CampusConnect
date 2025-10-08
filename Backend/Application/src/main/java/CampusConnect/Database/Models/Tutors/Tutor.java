package CampusConnect.Database.Models.Tutors;

import jakarta.persistence.*;

@Entity
public class Tutor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tutorId;
    private long userId;
    private int totalClasses;
    private double rating;
    private String username;

    public Tutor() {}

    public Tutor(long user_id, String username, int totalClasses, double rating) {
        this.userId = userId;
        this.totalClasses = totalClasses;
        this.rating = rating;
        this.username = username;
    }
    public String getUsername(){ return username; }
    public long getUserId() { return userId; }
    public long getTutorID() { return tutorId; }
    public int gettotalClasses() { return totalClasses; }
    public double getRating() { return rating; }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }
}
