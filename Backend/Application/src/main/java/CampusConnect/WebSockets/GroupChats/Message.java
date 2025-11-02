package CampusConnect.WebSockets.GroupChats;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;


@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private long userId;
    private String username;
    private long sessionId;
    @Lob
    private String message;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "messageSent")
    private Date messageSent = new Date();


    public Message() {};

    public Message(long userId, long sessionId, String message, String username) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.message = message;
        this.username = username;
    }

    public String getUsername() { return username;}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public long getUserId() { return userId; } public void setUserId(long userId) { this.userId = userId; }
    public long getSessionId() { return sessionId; } public void setSessionId(long sessionId) { this.sessionId = sessionId; }
    public String getMessage() { return message; } public void setMessage(String message) { this.message = message; }
    public Date getMessageSent() { return messageSent; } public void setMessageSent(Date messageSent) { this.messageSent = messageSent; }


}


