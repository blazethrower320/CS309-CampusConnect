package CampusConnect.Database.Models.Ratings;

public class RatingsDTO
{
    private int rating = 0;
    private String comments = "";
    private long userId;
    private long tutorId;


    public RatingsDTO(int rating, String comments, long userId, long tutorId)
    {
        this.rating = rating;
        this.comments = comments;
        this.userId = userId;
        this.tutorId = tutorId;
    }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getTutorId() { return tutorId; }
    public void setTutorId(long tutorId) { this.tutorId = tutorId; }
}
