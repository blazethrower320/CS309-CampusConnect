package com.example.demo.websocket;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Hashtable;
import java.util.Map;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ServerEndpoint("/chat/1/{username}")
@Component
public class ChatServer1 {

    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();
    private final Logger logger = LoggerFactory.getLogger(ChatServer1.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        logger.info("[onOpen] " + username);

        if (usernameSessionMap.containsKey(username)) {
            session.getBasicRemote().sendText("⚠️ Username already exists! Please reconnect with a different name.");
            session.close();
        } else {
            sessionUsernameMap.put(session, username);
            usernameSessionMap.put(username, session);

            sendMessageToPArticularUser(username, "🎉 Welcome " + username + "! Enjoy chatting here.");
            broadcast("💬 " + username + " joined the chat!");
            broadcastUserCount();
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        String username = sessionUsernameMap.get(session);
        String time = LocalTime.now().withNano(0).toString();

        logger.info("[onMessage] " + username + ": " + message);

        if (message.startsWith("@")) {
            String[] split_msg = message.split("\\s+");
            StringBuilder actualMessageBuilder = new StringBuilder();
            for (int i = 1; i < split_msg.length; i++) {
                actualMessageBuilder.append(split_msg[i]).append(" ");
            }
            String destUserName = split_msg[0].substring(1);
            String actualMessage = actualMessageBuilder.toString();
            sendMessageToPArticularUser(destUserName, "📩 [DM from " + username + " at " + time + "]: " + actualMessage);
            sendMessageToPArticularUser(username, "📤 [DM to " + destUserName + " at " + time + "]: " + actualMessage);
        } else {
            broadcast("[" + time + "] " + username + ": " + message);
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String username = sessionUsernameMap.get(session);
        logger.info("[onClose] " + username);
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);
        broadcast("❌ " + username + " disconnected.");
        broadcastUserCount();
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

    private void broadcastUserCount() {
        broadcast("👥 Current users online: " + sessionUsernameMap.size());
    }
}
