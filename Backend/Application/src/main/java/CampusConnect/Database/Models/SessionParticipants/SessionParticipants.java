package CampusConnect.Database.Models.SessionParticipants;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SessionParticipants {

    @Id
    private long id;
    private long sessionId;
    private long userId;
    private boolean isTutor;

    public SessionParticipants(){}

    public SessionParticipants(long userId, long sessionId, boolean isTutor) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.isTutor = isTutor;
    }
    public long getUserId(){return userId;}
}
