package CampusConnect.Database.Models.Users;

import CampusConnect.Database.Models.Sessions.Sessions;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String firstName;
    private String lastName;
    @Column(unique=true)
    private String username;
    private String password;
    private boolean tutor;
    private String major;
    private String year;



    @ManyToMany
    @JoinTable(
            name = "User_Sessions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "session_id")
    )
    private Set<Sessions> userSessions;

    public User(String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.tutor = false;
        this.major = "";
        this.year = "";
    }



    public User(){

    }
    public Long getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isTutor() { return tutor; }
    public String getMajor() { return major; }
    public String getYear() { return year; }
    public User setUsername(String username) { this.username = username; return this; }
    public User setPassword(String password) { this.password = password; return this; }

}
