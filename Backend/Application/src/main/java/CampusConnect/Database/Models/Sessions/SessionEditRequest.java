package CampusConnect.Database.Models.Sessions;

public class SessionEditRequest
{
    private long sessionId;
    private String meetingTime;

    public SessionEditRequest(long id, String meetingTime)
    {
        this.sessionId = id;
        this.meetingTime = meetingTime;
    }

    public String getMeetingTime() { return meetingTime; }
    public long getSessionId() { return sessionId; }
}
