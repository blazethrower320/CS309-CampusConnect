package CampusConnect.Database.Models.Tutors;

import jakarta.persistence.*;

@Entity
public class Tutor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;
    private int totalClasses;
    private double rating;

    public void Tutor(long user_id, int totalClasses, double rating) {
        this.userId = userId;
        this.totalClasses = totalClasses;
        this.rating = rating;
    }
    public long getUserId() { return userId; }
    public int gettotalClasses() { return totalClasses; }
    public double getRating() { return rating; }
}
