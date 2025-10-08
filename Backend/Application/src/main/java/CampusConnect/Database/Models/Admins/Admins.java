package CampusConnect.Database.Models.Admins;
import jakarta.persistence.*;


@Entity
public class Admins {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long admin_id;
    private long userId;
    private String username;
    private String permissions;
    private boolean isActive;

    public Admins(){}

    public Admins(long userId, String username, String permissions) {
        this.userId = userId;
        this.username = username;
        this.permissions = permissions;
        this.isActive = true;
    }
    public String getUsername() { return username; }
    public long getAdmin_id() { return admin_id; }
    public long getUserId() { return userId; }
    public String getPermissions() { return permissions; }
    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
}