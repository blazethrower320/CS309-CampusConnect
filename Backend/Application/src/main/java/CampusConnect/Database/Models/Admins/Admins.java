package CampusConnect.Database.Models.Admins;
import CampusConnect.Database.Models.Users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


@Entity
@Table(name = "admins")
public class Admins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;
    private int nukedUsersCount = 0;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public Admins() {}

    public Admins(User user) {
        this.user = user;
    }

    public int getNukedUsersCount() { return nukedUsersCount; }
    public void setNukedUsersCount(int nukedUsersCount)
    {
       this.nukedUsersCount = nukedUsersCount;
    }
    public int incrementNukedUsersCount() {
        return ++this.nukedUsersCount;
    }
    public long getAdminId() { return adminId; }
    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
        if(user != null && user.getAdmin() != this){
            user.setAdmin(this);
        }
    }
}