package CampusConnect.Database.Models.Sessions;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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



    public Sessions(){}

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
