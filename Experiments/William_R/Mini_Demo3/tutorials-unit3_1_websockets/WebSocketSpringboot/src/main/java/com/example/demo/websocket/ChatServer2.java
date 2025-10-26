package com.example.demo.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.Map;

@ServerEndpoint("/chat/2/{username}")
@Component
public class ChatServer2 {

    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();
    private final Logger logger = LoggerFactory.getLogger(ChatServer2.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        logger.info("[onOpen] " + username);

        if (usernameSessionMap.containsKey(username)) {
            session.getBasicRemote().sendText("⚠️ Username already taken. Please try again.");
            session.close();
        } else {
            sessionUsernameMap.put(session, username);
            usernameSessionMap.put(username, session);

            sendMessageToPArticularUser(username, "🟢 Connected successfully as " + username + "!");
            broadcast("📢 System: " + username + " joined the chat at " + LocalDateTime.now().withNano(0));
            sendMessageToPArticularUser(username, "💡 Tip: If connection drops, just refresh to reconnect!");
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        String username = sessionUsernameMap.get(session);
        logger.info("[onMessage] " + username + ": " + message);

        if (message.equalsIgnoreCase("/help")) {
            sendMessageToPArticularUser(username, "🧠 Commands:\n- @username <msg>: send private message\n- /time: show server time\n- /help: show this list");
        } else if (message.equalsIgnoreCase("/time")) {
            sendMessageToPArticularUser(username, "⏰ Server time: " + LocalDateTime.now().withNano(0));
        } else if (message.startsWith("@")) {
            String[] split_msg = message.split("\\s+");
            String destUserName = split_msg[0].substring(1);
            String actualMessage = message.replaceFirst("@\\w+\\s+", "");
            sendMessageToPArticularUser(destUserName, "[Private from " + username + "]: " + actualMessage);
            sendMessageToPArticularUser(username, "[Sent to " + destUserName + "]: " + actualMessage);
        } else {
            broadcast(username + ": " + message);
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String username = sessionUsernameMap.get(session);
        logger.info("[onClose] " + username);
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);
        broadcast("🔴 System: " + username + " has disconnected.");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        String username = sessionUsernameMap.get(session);
        logger.info("[onError] " + username + ": " + throwable.getMessage());
    }

    private void sendMessageToPArticularUser(String username, String message) {
        try {
            usernameSessionMap.get(username).getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.info("[DM Exception] " + e.getMessage());
        }
    }

    private void broadcast(String message) {
        sessionUsernameMap.forEach((session, username) -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                logger.info("[Broadcast Exception] " + e.getMessage());
            }
        });
    }
}
