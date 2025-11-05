package CampusConnect.WebSockets.GroupChats;
import java.io.IOException;
import java.util.*;

import CampusConnect.Database.Models.Messages.Messages;
import CampusConnect.Database.Models.Messages.PrivateMessages;
import CampusConnect.Database.Models.Sessions.Sessions;
import CampusConnect.Database.Models.Users.User;
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
@ServerEndpoint(value = "/DM/{userId1}/{userId2}")
public class DirectMessageChatSocket
{
    // Store all socket session and their corresponding username.
    private static Map<Long, SocketDTO> activeSessions = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(GroupChatSocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("userId1") long userId1, @PathParam("userId2") long userId2)
            throws IOException {

        logger.info("Entered into Open");

        User user = RepositoryProvider.getUserRepository().getUserByUserId(userId1);

        if (user == null) {
            logger.error("User not found: " + user.getUsername());
            session.close();
            return;
        }
        activeSessions.put(userId1, new SocketDTO(user, null, session));

        sendMessageToPArticularUser(userId1, getChatHistory(userId1, userId2));
    }
    @OnMessage
    public void onMessage(Session session, @PathParam("userId1") long userId1,
                          @PathParam("userId2") long userId2, String message) throws IOException {

        User user1 = RepositoryProvider.getUserRepository().getUserByUserId(userId1);
        if(user1 == null)
        {
            return;
        }
        User receiver = RepositoryProvider.getUserRepository().getUserByUserId(userId2);
        if(receiver == null)
        {
            return;
        }


        PrivateMessages newMessage = new PrivateMessages(userId1, userId2, message, user1.getUsername(), receiver.getUsername());
        RepositoryProvider.getPrivateMessagesRepository().save(newMessage);

        sendMessageToPArticularUser(userId1, user1.getUsername() + ": " + message);
        sendMessageToPArticularUser(userId2, user1.getUsername() + ": " + message);
    }
    @OnClose
    public void onClose(Session session) throws IOException
    {
        logger.info("Entered into Close");

        Long userId = null;
        for (Map.Entry<Long, SocketDTO> entry : activeSessions.entrySet())
        {
            if (entry.getValue().getSocketSession().equals(session))
            {
                userId = entry.getKey();
                break;
            }
        }

        if (userId != null)
        {
            activeSessions.remove(userId);
        }

    }
    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.info("Entered into Error");
        throwable.printStackTrace();
    }



    private void sendMessageToPArticularUser(long userId, String message) {
        SocketDTO socketDTO = activeSessions.get(userId);
        if (socketDTO != null && socketDTO.getSocketSession().isOpen()) {
            try
            {
                socketDTO.getSocketSession().getBasicRemote().sendText(message);
            } catch (IOException e)
            {
                logger.error("Failed to send message to user {}", userId, e);
            }
        }
    }



    private String getChatHistory(long userId1, long userId2) {
        List<PrivateMessages> sent = RepositoryProvider.getPrivateMessagesRepository().findAllByUserIdAndReceiverUserId(userId1, userId2);
        List<PrivateMessages> received = RepositoryProvider.getPrivateMessagesRepository().findAllByUserIdAndReceiverUserId(userId2, userId1);

        List<PrivateMessages> directMessages = new ArrayList<>();
        directMessages.addAll(sent);
        directMessages.addAll(received);
        directMessages.sort(Comparator.comparing(PrivateMessages::getMessageSent));

        StringBuilder sb = new StringBuilder();
        for (PrivateMessages msg : directMessages) {
            sb.append(msg.getUserUsername()).append(": ").append(msg.getMessage()).append("\n");
        }
        return sb.toString();
    }
}
