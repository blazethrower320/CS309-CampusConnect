package CampusConnect.Database.Models.Sessions;

import CampusConnect.Database.Models.Classes.Classes;
import CampusConnect.Database.Models.Users.User;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Sessions
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String className;
    private String classCode;


    @ManyToMany(mappedBy = "userSessions")
    private Set<User> users;

    @ManyToOne  //Do we want many sessions for a class or just one? Currently more than 1 sessions per class possible.
    @JoinColumn(name = "class_id")
    Classes sessionClass;

    public Sessions(){}

    public Sessions(long user_id, long tutor_id, long session_id, double rating, String className, String classCode) {
        //this.user_id = user_id;
        //this.tutor_id = tutor_id;
        this.className = className;
        this.classCode = classCode;
    }

    public long getId() { return id; }
    //public long getUserId() { return user_id; }
    //public long getTutorId() { return tutor_id; }
    public String getClassName() { return className; }
    public String getClassCode() { return classCode; }
}
