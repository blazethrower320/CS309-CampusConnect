package CampusConnect.Database.Models.Classes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long classId;
    private String className;
    private String classCode;

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
