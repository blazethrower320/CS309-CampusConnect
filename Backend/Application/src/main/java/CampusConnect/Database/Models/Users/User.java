package CampusConnect.Database.Models.Users;

import CampusConnect.Database.Models.Admins.Admins;
import CampusConnect.Database.Models.Ratings.Ratings;
import CampusConnect.Database.Models.Sessions.Sessions;
import CampusConnect.Database.Models.Tutors.Tutor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username", unique=true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "major")
    private String major;

    @Column(name = "classification")
    private String classification;

    @Column(name = "contact_info")
    private String contractInfo;

    @Column(name = "bio")
    private String bio;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Admins admin;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Tutor tutor;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Ratings ratings;

    /*
    @ManyToMany
    @JoinTable(
            name = "User_Sessions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "session_id")
    )
    private Set<Sessions> userSessions;     */
    @ManyToMany(mappedBy = "users")
    @JsonIgnore
    private Set<Sessions> Sessions = new HashSet<>();


    public User(String username, String password) {
        this.username = username;
        this.password = password;
        major = null;
        classification = null;
        bio = null;
    }



    public User(){

    }

    public long getUserId() { return userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName){ this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public void setMajor(String major){this.major = major; }
    public String getMajor() { return major; }

    public void setClassification(String classification){ this.classification = classification; }
    public String getClassification() { return classification; }

    public void setBio(String bio){ this.bio = bio; }
    public String getBio() { return bio; }

    public void setContractInfo(String contactInfo){ this.contractInfo =contactInfo; }
    public String getContractInfo(){ return contractInfo; }



    public Admins getAdmin(){
        return admin;
    }

    public void setAdmin(Admins admin) {
        this.admin = admin;
        if (admin != null && admin.getUser() != this){
            admin.setUser(this);
        }
    }

    public Tutor getTutor(){
        return tutor;
    }

    public void setTutor(Tutor tutor){
        this.tutor = tutor;
        if(tutor != null && tutor.getUser() != this){
            tutor.setUser(this);
        }
    }
    public Ratings getRatings(){ return ratings; }
    public void setRatings(Ratings ratings){
        this.ratings = ratings;
        if(ratings != null && ratings.getUser() != this){
            ratings.setUser(this);
        }
    }

}
