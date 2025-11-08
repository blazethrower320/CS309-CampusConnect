package CampusConnect.Database.Models.Messages;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class PrivateMessages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private long userId;
    private long receiverUserId;
    private String userUsername;
    private String receiverUsername;
    @Lob
    private String message;
    private String imageUrl;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "messageSent")
    private Date messageSent = new Date();


    public PrivateMessages() {};

    public PrivateMessages(long userId, long receiverUserId, String message, String userUsername, String receiverUsername) {
        this.userId = userId;
        this.message = message;
        this.userUsername = userUsername;
        this.receiverUserId = receiverUserId;
        this.receiverUsername = receiverUsername;
    }
    public PrivateMessages(long userId, long receiverUserId, String message, String userUsername, String receiverUsername, String imageUrl) {
        this.userId = userId;
        this.message = message;
        this.userUsername = userUsername;
        this.receiverUserId = receiverUserId;
        this.receiverUsername = receiverUsername;
        this.imageUrl = imageUrl;
    }
    public String getUserUsername() { return userUsername;}
    public String getReceiverUsername() { return receiverUsername;}
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public long getUserId() { return userId; } public void setUserId(long userId) { this.userId = userId; }
    public String getMessage() { return message; } public void setMessage(String message) { this.message = message; }
    public Date getMessageSent() { return messageSent; } public void setMessageSent(Date messageSent) { this.messageSent = messageSent; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

}