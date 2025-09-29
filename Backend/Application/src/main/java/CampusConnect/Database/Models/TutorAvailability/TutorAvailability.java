package CampusConnect.Database.Models.TutorAvailability;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.sql.Time;
import java.util.Date;

@Entity
public class TutorAvailability
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long tutor_id;
    private Time startTime;
    private Time endTime;

    public TutorAvailability(){}

    public TutorAvailability( long id, long tutor_id, Time startTime, Time endTime) {
        this.tutor_id = tutor_id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.id = id;
    }

    public long getId() { return id; }
    public long getTutorId() { return tutor_id; }
    public Time getStartTime() { return startTime; }
    public Time getEndTime() { return endTime; }
}
