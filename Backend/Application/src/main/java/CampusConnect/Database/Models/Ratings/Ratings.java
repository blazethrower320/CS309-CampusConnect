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
    private Integer rating = 0;
    private String comments = "";


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
    public Ratings(Tutor tutor, User user, int rating, String comments) {
        this.tutor = tutor;
        this.user = user;
        this.rating = rating;
        this.comments = comments;
    }

    public Ratings(){}

    public long getId() { return id; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    // Add these
    public Tutor getTutor() { return tutor; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
