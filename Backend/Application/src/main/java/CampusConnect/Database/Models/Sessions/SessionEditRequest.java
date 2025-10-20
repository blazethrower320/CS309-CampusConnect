package CampusConnect.Database.Models.Sessions;

public class SessionEditRequest
{
    private long sessionId;
    private String assignedDate;

    public SessionEditRequest(long id, String assignedDate)
    {
        this.sessionId = id;
        this.assignedDate = assignedDate;
    }

    public String getAssignedDate() { return assignedDate; }
    public long getSessionId() { return sessionId; }
}
