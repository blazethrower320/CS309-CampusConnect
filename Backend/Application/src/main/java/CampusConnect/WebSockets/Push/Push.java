package CampusConnect.WebSockets.Push;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "push")
@Data
public class Push {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Setter
    @Getter
    private long tutorId;

    @Column
    @Setter
    @Getter
    private String message;

    @Column(name = "is_read")
    @Setter
    @Getter
    private boolean read = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();


    //private LocalDateTime createdAt;
    public Push(long tutorId, String message){
        this.tutorId = tutorId;
        this.message = message;
    }

}
