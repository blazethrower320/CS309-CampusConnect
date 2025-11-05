package CampusConnect.Database.Models.Images;
import jakarta.persistence.*;

import java.util.Date;

public class Images
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String filePath;

    public Images() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
