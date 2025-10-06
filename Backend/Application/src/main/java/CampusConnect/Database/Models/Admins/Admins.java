package CampusConnect.Database.Models.Admins;
import jakarta.persistence.*;


@Entity
public class Admins {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long admin_id;
    private long userId;
    private String permissions;
    private Boolean isActive;

    public Admins(){}

    public Admins(long admin_id, long userId, String permissions) {
        this.admin_id = admin_id;
        this.userId = userId;
        this.permissions = permissions;
        this.isActive = true;
    }
    public long getAdmin_id() { return admin_id; }
    public long getUserId() { return userId; }
    public String getPermissions() { return permissions; }
}