package CampusConnect.Database.Models.TutorClasses;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.sql.Time;

@Entity
public class TutorClasses
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long tutor_id;
    private String classCode;

    public TutorClasses(){}

    public TutorClasses(long tutor_id, String classCode) {
        this.tutor_id = tutor_id;
        this.classCode = classCode;
    }

    public long getId() { return id; }
    public long getTutorId() { return tutor_id; }
    public String getClassCode() { return classCode; }
}
