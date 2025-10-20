package CampusConnect.Database.Models.Users;

import CampusConnect.Database.Models.Admins.Admins;
import CampusConnect.Database.Models.Sessions.Sessions;
import CampusConnect.Database.Models.Tutors.Tutor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

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


    //@Column(name = "is_admin", nullable = false)
    //private boolean isAdmin = false;

    private String major;
    private String year;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Admins admin;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Tutor tutor;

    /*
    @ManyToMany
    @JoinTable(
            name = "User_Sessions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "session_id")
    )
    private Set<Sessions> userSessions;     */


    public User(String username, String password) {
        this.username = username;
        this.password = password;
        //this.isTutor = tutor;
        //this.isAdmin = admin;
        this.major = "";
        this.year = "";
    }



    public User(){

    }
    //public boolean isAdmin(){ return isAdmin;}
    public long getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getMajor() { return major; }
    public String getYear() { return year; }
    public User setUsername(String username) { this.username = username; return this; }
    public User setPassword(String password) { this.password = password; return this; }





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

}
