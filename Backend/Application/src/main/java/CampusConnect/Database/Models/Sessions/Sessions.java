package CampusConnect.Database.Models.Sessions;

import CampusConnect.Database.Models.User_Sessions.User_Sessions;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Sessions
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long user_id;
    private long tutor_id;
    private String className;
    private String classCode;

    private String name;




    public Sessions(){}
    @OneToMany(mappedBy = "sessions")
    Set<User_Sessions> user_sessions;
    public Sessions(long user_id, long tutor_id, long session_id, double rating, String className, String classCode) {
        this.user_id = user_id;
        this.tutor_id = tutor_id;
        this.className = className;
        this.classCode = classCode;
    }

    public long getId() { return id; }
    public long getUserId() { return user_id; }
    public long getTutorId() { return tutor_id; }
    public String getClassName() { return className; }
    public String getClassCode() { return classCode; }
}
