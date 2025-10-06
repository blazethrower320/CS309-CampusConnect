package CampusConnect.Database.Models.Admins;

import CampusConnect.Database.Models.Sessions.Sessions;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Admins {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long classId;
    private String className;
    private String classCode;

    @OneToMany(mappedBy = "sessionClass")
    Set<Sessions> sessions;

    public Admins(){}

    public Admins(long user_id, int classCount, String className, String classCode) {
        this.classId = classId;
        this.className = className;
        this.classCode = classCode;
    }
    public long getUserId() { return classId; }
    public String getclassName() { return className; }
    public String getclassCode() { return classCode; }
}
