package CampusConnect.Database.Models.Users;

import jakarta.persistence.*;

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




    public User(String firstName, String lastName, String username, String password) {
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

}
