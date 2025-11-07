package CampusConnect.WebSockets.GroupChats;

import java.io.IOException;
import java.util.*;

import CampusConnect.Database.Models.Images.Images;
import CampusConnect.Database.Models.Messages.Messages;
import CampusConnect.Database.Models.Messages.PrivateMessages;
import CampusConnect.Database.Models.Sessions.Sessions;
import CampusConnect.Database.Models.Users.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class GroupChatSocket {

    // Store all socket session and their corresponding username.
    private static Map<Long, Set<SocketDTO>> tutorSessionMaps = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(GroupChatSocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") long userId, @PathParam("sessionId") long sessionId)
            throws IOException {

        logger.info("Entered into Open");

        User user = RepositoryProvider.getUserRepository().getUserByUserId(userId);

        if (user == null) {
            logger.error("User not found: " + user.getUsername());
            session.close();
            return;
        }


        Sessions tutorSession = RepositoryProvider.getSessionsRepository().getSessionsBySessionId(sessionId);
        if(tutorSession == null)
        {
            logger.error("Session was not found");
            session.close();
            return;
        }

        SocketDTO tutorSessionDTO = new SocketDTO(user, tutorSession, session);

        Set<SocketDTO> foundSession = tutorSessionMaps.get(sessionId);
        if(foundSession == null)
        {
            foundSession = new HashSet<>();
            tutorSessionMaps.put(sessionId, foundSession);
        }

        foundSession.add(tutorSessionDTO);

        sendMessageToPArticularUser(user.getUserId(), sessionId, getChatHistory(sessionId));
        String message = "User:" + user.getUsername() + " has Joined the Chat";
    }


    @OnMessage
    public void onMessage(Session session, @PathParam("sessionId") long sessionId,
                          @PathParam("userId") long userId, String message) throws IOException {

        Set<SocketDTO> allConnectedUsers = tutorSessionMaps.get(sessionId);
        SocketDTO userInfo = allConnectedUsers.stream().filter(c -> c.getUser().getUserId() == userId).findFirst().orElse(null);
        Sessions tutorSession = RepositoryProvider.getSessionsRepository().getSessionsBySessionId(userInfo.getSessions().getSessionId());



        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(message);

        int type = json.get("type").asInt();  // 0=text, 1=image



        Messages savedMessage = new Messages(userId, sessionId, message, userInfo.getUser().getUsername());
        if (type == 1) {
            int imageId = json.get("message").asInt();
            Images img = RepositoryProvider.getImagesRepository().findById(imageId);

            savedMessage.setMessage(null);
            if (img != null) {
                savedMessage.setImageUrl("/images/" + img.getId());
            }
        }
        else
        {
            String messageText = json.get("message").asText();
            savedMessage.setMessage(messageText);
        }


        RepositoryProvider.getMessageRepository().save(savedMessage);

        String seenMessage;
        if (savedMessage.getImageUrl() != null) {
            seenMessage = userInfo.getUser().getUsername() + " sent an image: " + savedMessage.getImageUrl();
        } else {
            seenMessage = userInfo.getUser().getUsername() + ": " + savedMessage.getMessage();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("messageId", savedMessage.getId());
        response.put("type", type);
        response.put("sender", userInfo.getUser().getUsername());
        response.put("sessionId", sessionId);
        response.put("message", savedMessage.getMessage());
        response.put("imageUrl", savedMessage.getImageUrl() != null
                ? "http://coms-3090-037.class.las.iastate.edu:8080" + savedMessage.getImageUrl()
                : null);
        response.put("timestamp", savedMessage.getMessageSent().toString());

        String jsonResponse = mapper.writeValueAsString(response);

        // Send JSON to all users
        sendMessageToGroupChat(allConnectedUsers, jsonResponse);
    }


    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Entered into Close");

        Long sessionId = null;
        SocketDTO user = null;
        for (Map.Entry<Long, Set<SocketDTO>> entry : tutorSessionMaps.entrySet())
        {
            for (SocketDTO dto : entry.getValue())
            {
                if (dto.getSocketSession().equals(session))
                {
                    sessionId = entry.getKey();
                    user = dto;
                    break;
                }
            }
            if (sessionId != null) break;
        }

        if(user != null)
        {
            Set<SocketDTO> userSet = tutorSessionMaps.get(sessionId);
            logger.info("User: " + user.getUser().getUsername() + " left.");
            userSet.remove(user);
        }
    }


    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.info("Entered into Error");
        throwable.printStackTrace();
    }


    private void sendMessageToPArticularUser(long userId, long sessionId, String message) {
        try {
            Set<SocketDTO> users = tutorSessionMaps.get(sessionId);

            SocketDTO user = users.stream().filter(c -> c.getUser().getUserId() == userId).findFirst().orElse(null);
            if(user == null)
                return;


            Session userSocketSession = user.getSocketSession();
            if(userSocketSession != null && userSocketSession.isOpen())
            {
                userSocketSession.getBasicRemote().sendText(message);
            }
        }
        catch (IOException e) {
            logger.info("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendMessageToGroupChat(Set<SocketDTO> users, String message)
    {
        try
        {
            for(SocketDTO user : users)
            {
                Session socketSession = user.getSocketSession();
                if(socketSession != null && socketSession.isOpen())
                {
                    socketSession.getBasicRemote().sendText(message);
                }
            }
        }
        catch (IOException e)
        {
            logger.info("Exception: " + e.getMessage());
            e.printStackTrace();
        }

    }


    private String getChatHistory(long sessionId) {
        List<Messages> messages = RepositoryProvider.getMessageRepository().findAll();

        StringBuilder sb = new StringBuilder();
        if(messages != null && messages.size() != 0) {
            for (Messages message : messages) {
                if(message.getSessionId() == sessionId)
                {
                    String seenMessage;
                    if (message.getImageUrl() != null) {
                        seenMessage = "http://coms-3090-037.class.las.iastate.edu:8080" + message.getImageUrl();
                    } else {
                        seenMessage = message.getMessage();
                    }

                    sb.append(message.getUsername()).append(": ").append(seenMessage).append("\n");
                }
            }
        }
        return sb.toString();
    }

}