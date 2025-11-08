package CampusConnect.WebSockets.Push;

import CampusConnect.Database.Models.Sessions.SessionsRepository;

import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Database.Models.Users.UserRepository;
import jakarta.websocket.*;
        import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


import java.io.IOException;
import java.util.*;

@Controller      // this is needed for this to be an endpoint to springboot
@ServerEndpoint(value = "/push/{tutorId}")  // this is Websocket url
public class PushSocket {

    // cannot autowire static directly (instead we do it by the below
    // method
    private static PushRepository msgRepo;
    private static UserRepository userRep;
    private static TutorRepository tutorRep;
    private static SessionsRepository sessionsRep;
    /*
     * Grabs the MessageRepository singleton from the Spring Application
     * Context.  This works because of the @Controller annotation on this
     * class and because the variable is declared as static.
     * There are other ways to set this. However, this approach is
     * easiest.
     */
    @Autowired
    public void setMessageRepository(PushRepository repo) {
        msgRepo = repo;
    }

    @Autowired
    public void setUserRepository(UserRepository repo) {userRep = repo; }

    @Autowired
    public void setTutorRepository(TutorRepository repo) {tutorRep = repo; }

    @Autowired
    public void setSessionsRepository(SessionsRepository repo) {sessionsRep = repo; }





    // Store all socket session and their corresponding username.
    private static Map<Long, Session> tutorIdSessionMap = new Hashtable<>();
    private static Map<Session, Long> sessionTutorIdMap =  new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(PushSocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("tutorId") long tutorId) throws IOException {

        logger.info("Tutor: " + tutorRep.getTutorByTutorId(tutorId));

        tutorIdSessionMap.put(tutorId, session);
        sessionTutorIdMap.put(session, tutorId);

        List<Push> unreadMessages = msgRepo.findAllByTutorIdAndReadFalseOrderByCreatedAtAsc(tutorId);
        for (Push msg : unreadMessages) {
            session.getBasicRemote().sendText(msg.getMessage());
            msg.setRead(true); // mark as read
            msgRepo.save(msg);
        }
    }

    @OnClose
    public void onClose(Session session) {
        Long tutorId = sessionTutorIdMap.get(session);
        tutorIdSessionMap.remove(tutorId);
        logger.info("Tutor disconnected");
    }


    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("Entered into Error");
        throwable.printStackTrace();
    }

    public static void sendNotificationToTutor(long tutorId, String message) {
        Push pushMessage = new Push(tutorId, message);
        msgRepo.save(pushMessage);

        Session session = tutorIdSessionMap.get(tutorId);

        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                pushMessage.setRead(true);
                msgRepo.save(pushMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
} // end of Class
