package CampusConnect.Database.Models.SessionMembers;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SessionMembers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long sessionId;
    private long userId;
    private boolean isTutor;

    public SessionMembers(){}

    public SessionMembers(long userId, long sessionId, boolean isTutor) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.isTutor = isTutor;
    }
    public long getUserId(){return userId;}
    public boolean isTutor(){return isTutor;}
    public long getSessionId() {return sessionId;}
}
