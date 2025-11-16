package CampusConnect.Database.Models.Classes;

import CampusConnect.Database.Models.Sessions.Sessions;
import CampusConnect.Database.Models.Tutors.Tutor;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classId")
    private long classId;
    private String className;
    private String classCode;


    @OneToMany(mappedBy = "classEntity")
    private Set<Sessions> sessions;

    @ManyToMany(mappedBy = "classes")
    private Set<Tutor> tutors = new HashSet<>();

    public Classes(){}

    public Classes(String classCode, String className) {
        this.className = className;
        this.classCode = classCode;
    }

    public String getclassName() { return className; }

    public void setClassName(String className){
        this.className = className;
    }

    public String getclassCode() { return classCode; }

    public void setClassCode(String classCode){
        this.classCode = classCode;
    }


}
