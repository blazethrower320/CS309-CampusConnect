package CampusConnect.WebSockets.GroupChats;

import CampusConnect.Database.Models.Sessions.Sessions;
import CampusConnect.Database.Models.Users.User;
import jakarta.websocket.Session;

public class SocketDTO
{
    public User user;
    public Sessions sessions;
    private Session socketSession;

    public SocketDTO(User user, Sessions sessions, Session socketSession)
    {
        this.user = user;
        this.sessions = sessions;
        this.socketSession = socketSession;
    }

    public User getUser(){return user;}
    public Sessions getSessions(){return sessions;}
    public Session getSocketSession(){return socketSession;}
}
