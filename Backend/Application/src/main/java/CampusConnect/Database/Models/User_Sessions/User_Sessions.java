package CampusConnect.Database.Models.User_Sessions;

import CampusConnect.Database.Models.Sessions.Sessions;
import CampusConnect.Database.Models.Users.User;
import jakarta.persistence.*;

@Entity
public class User_Sessions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")

    @ManyToMany
    @JoinColumn(name = "session_id")
    Sessions session;
    User user;
    public User_Sessions(){}

}
