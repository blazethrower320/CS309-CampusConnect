package CampusConnect.WebSockets.GroupChats;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import CampusConnect.Database.Models.Users.User;
import CampusConnect.WebSockets.GroupChats.RepositoryProvider;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;


@Controller
@ServerEndpoint(value = "/groupChat/{sessionId}/{userId}")
public class ChatSocket {

    // Store all socket session and their corresponding username.
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") long userId)
            throws IOException {

        logger.info("Entered into Open");

        User user = RepositoryProvider.getUserRepository().getUserByUserId(userId);

        if (user == null) {
            logger.error("User not found: " + user.getUsername());
            session.close();
            return;
        }

        sessionUsernameMap.put(session, user.getUsername());
        usernameSessionMap.put(user.getUsername(), session);

        sendMessageToPArticularUser(user.getUsername(), getChatHistory());

        String message = "User:" + user.getUsername() + " has Joined the Chat";
        broadcast(message);
    }


    @OnMessage
    public void onMessage(Session session, @PathParam("sessionId") long chatSessionId,
                          @PathParam("userId") long userId, String message) throws IOException {

        logger.info("Entered into Message: Got Message:" + message);
        String username = sessionUsernameMap.get(session);

        if (username == null) {
            logger.error("Username not found in session map");
            return;
        }

        if (message.startsWith("@")) {
            String destUsername = message.split(" ")[0].substring(1);

            sendMessageToPArticularUser(destUsername, "[DM] " + username + ": " + message);
            sendMessageToPArticularUser(username, "[DM] " + username + ": " + message);

        }
        else {

            broadcast(username + ": " + message);
        }

        RepositoryProvider.getMessageRepository().save(new Message(userId, chatSessionId, message, username));
    }


    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Entered into Close");

        String username = sessionUsernameMap.get(session);

        if (username != null) {
            sessionUsernameMap.remove(session);
            usernameSessionMap.remove(username);

            String message = username + " disconnected";
            broadcast(message);
        } else {
            logger.warn("Session closed but username was null");
        }
    }


    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.info("Entered into Error");
        throwable.printStackTrace();
    }


    private void sendMessageToPArticularUser(String username, String message) {
        try {
            Session userSession = usernameSessionMap.get(username);
            if (userSession != null && userSession.isOpen()) {
                userSession.getBasicRemote().sendText(message);
            } else {
                logger.warn("Cannot send message to " + username + " - session not found or closed");
            }
        }
        catch (IOException e) {
            logger.info("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void broadcast(String message) {
        sessionUsernameMap.forEach((session, username) -> {
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                }
            }
            catch (IOException e) {
                logger.info("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }


    private String getChatHistory() {
        List<Message> messages = RepositoryProvider.getMessageRepository().findAll();

        StringBuilder sb = new StringBuilder();
        if(messages != null && messages.size() != 0) {
            for (Message message : messages) {
                sb.append(message.getUsername() + ": " + message.getMessage() + "\n");
            }
        }
        return sb.toString();
    }

}