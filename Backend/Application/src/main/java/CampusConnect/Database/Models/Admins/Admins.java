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

    private String permissions;

    private boolean isActive = true;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    //@JsonIgnore
    private User user;

    private String username;

    public Admins() {}

    public Admins(User user, String permissions) {
        this.user = user;
        this.username = user.getUsername();
        this.permissions = permissions;
        this.isActive = true;
    }

    public String getUsername() { return username; }
    public long getAdminId() { return adminId; }
    //public long getUserId() { return userId; }
    public String getPermissions() { return permissions; }
    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
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