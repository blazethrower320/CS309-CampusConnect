package CampusConnect.Database.Models.Users;

import CampusConnect.Database.Models.Sessions.Sessions;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username", unique=true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "is_tutor", nullable = false)
    private boolean isTutor = false;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin = false;

    private String major;
    private String year;




    @ManyToMany
    @JoinTable(
            name = "User_Sessions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "session_id")
    )
    private Set<Sessions> userSessions;

    public User(String username, String password, boolean tutor, boolean admin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.isTutor = tutor;
        this.isAdmin = admin;
        this.major = "";
        this.year = "";
    }



    public User(){

    }
    public boolean isAdmin(){ return isAdmin;}
    public Long getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isTutor() { return isTutor; }
    public String getMajor() { return major; }
    public String getYear() { return year; }
    public User setUsername(String username) { this.username = username; return this; }
    public User setPassword(String password) { this.password = password; return this; }

}
