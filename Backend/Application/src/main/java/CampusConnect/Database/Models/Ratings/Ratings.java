package CampusConnect.Database.Models.Ratings;

import CampusConnect.Database.Models.Tutors.Tutor;
import CampusConnect.Database.Models.Users.User;
import jakarta.persistence.*;

@Entity
public class Ratings
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private double rating;
    private String className;


    @OneToOne
    @JoinColumn(name = "tutor_id", nullable = false, unique = true)
    private Tutor tutor;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public Ratings(Tutor tutor, User user) {
        this.tutor = tutor;
        this.user = user;
    }

    public Ratings(){}

    public long getId() { return id; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    // Add these
    public Tutor getTutor() { return tutor; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
