package CampusConnect.Database.Models.Classes;

import CampusConnect.Database.Models.Sessions.Sessions;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classId")
    private long classId;
    private String className;
    private String classCode;

    /*
    @OneToMany(mappedBy = "sessionClass", cascade = CascadeType.ALL)
    private Set<Sessions> sessions;
    */

    public Classes(){}

    public Classes(long user_id, int classCount, String className, String classCode) {
        this.classId = classId;
        this.className = className;
        this.classCode = classCode;
    }
    public long getUserId() { return classId; }
    public String getclassName() { return className; }
    public String getclassCode() { return classCode; }
}
