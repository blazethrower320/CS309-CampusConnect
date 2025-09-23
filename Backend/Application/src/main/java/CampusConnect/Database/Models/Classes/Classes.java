package CampusConnect.Database.Models.Classes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;
    private String className;
    private String classCode;
    private int classCount;

    public Classes(){}

    public Classes(long user_id, int classCount, String className, String classCode) {
        this.userId = userId;
        this.className = className;
        this.classCode = classCode;
        this.classCount = classCount;
    }
    public long getUserId() { return userId; }
    public String getclassName() { return className; }
    public String getclassCode() { return classCode; }
    public int getclassCount() {return classCount; }
}
