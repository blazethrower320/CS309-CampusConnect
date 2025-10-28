package com.cs309.websocket3.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller      // this is needed for this to be an endpoint to springboot
@ServerEndpoint(value = "/chat/{username}/{role}/{tutor}")  // this is Websocket url
public class ChatSocket {

  // cannot autowire static directly (instead we do it by the below
  // method
	private static MessageRepository msgRepo; 

	/*
   * Grabs the MessageRepository singleton from the Spring Application
   * Context.  This works because of the @Controller annotation on this
   * class and because the variable is declared as static.
   * There are other ways to set this. However, this approach is
   * easiest.
	 */
	@Autowired
	public void setMessageRepository(MessageRepository repo) {
		msgRepo = repo;  // we are setting the static variable
	}

	// Store all socket session and their corresponding username.
	private static Map<Session, String> sessionUsernameMap= new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();
    private static Map<String, Session> tutorSessionMap = new Hashtable<>();
	private static Map<String, String> userTutorMap = new Hashtable<>();
    private static Map<String, List<String>> pendingNotifications = new Hashtable<>();


    private final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username, @PathParam("role") String role, @PathParam("tutor") String tutorUsername)
      throws IOException {

		logger.info("User connected: " + username + "as: " + role);

        //Map user to session and vice versa
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);

        // store connecting user information
		if(role.equalsIgnoreCase("tutor")){
            tutorSessionMap.put(username, session);
            List<String> pending = pendingNotifications.get(username);
            if (pending != null) {
                for (String msg : pending) {
                    sendMessageToPArticularUser(username, msg);
                }
                pendingNotifications.remove(username);
            }
        }
        else {
            userTutorMap.put(username, tutorUsername);
        }
		//Send chat history to the newly connected user
		sendMessageToPArticularUser(username, getChatHistory());

        //Notify tutor when a student joins their session
        if(role.equalsIgnoreCase("user")){
            notifyTutor(tutorUsername, username + " has joined your session");
        }


        // broadcast that new user joined
		//String message = "User:" + username + " has Joined the Chat";
		//broadcast(message);
	}


	@OnMessage
	public void onMessage(Session session, String message) throws IOException {

		// Handle new messages
		logger.info("Entered into Message: Got Message:" + message);
		String username = sessionUsernameMap.get(session);

    // Direct message to a user using the format "@username <message>"
		if (message.startsWith("@")) {
			String destUsername = message.split(" ")[0].substring(1); 

      // send the message to the sender and receiver
			sendMessageToPArticularUser(destUsername, "[DM] " + username + ": " + message);
			sendMessageToPArticularUser(username, "[DM] " + username + ": " + message);

		} 
    else { // broadcas
			broadcast(username + ": " + message);
		}

		// Saving chat history to repository
		msgRepo.save(new Message(username, message));
	}


	@OnClose
	public void onClose(Session session) throws IOException {
		logger.info("Entered into Close");

    // remove the user connection information
		String username = sessionUsernameMap.get(session);
		sessionUsernameMap.remove(session);
		usernameSessionMap.remove(username);
        tutorSessionMap.remove(username);
        userTutorMap.remove(username);

    // broadcase that the user disconnected
		String message = username + " disconnected";
		broadcast(message);
	}


	@OnError
	public void onError(Session session, Throwable throwable) {
		// Do error handling here
		logger.info("Entered into Error");
		throwable.printStackTrace();
	}


	private void sendMessageToPArticularUser(String username, String message) {
		try {
			usernameSessionMap.get(username).getBasicRemote().sendText(message);
		} 
    catch (IOException e) {
			logger.info("Exception: " + e.getMessage().toString());
			e.printStackTrace();
		}
	}


	private void broadcast(String message) {
		sessionUsernameMap.forEach((session, username) -> {
			try {
				session.getBasicRemote().sendText(message);
			} 
      catch (IOException e) {
				logger.info("Exception: " + e.getMessage().toString());
				e.printStackTrace();
			}

		});

	}
	

  // Gets the Chat history from the repository
	private String getChatHistory() {
		List<Message> messages = msgRepo.findAll();
    
    // convert the list to a string
		StringBuilder sb = new StringBuilder();
		if(messages != null && messages.size() != 0) {
			for (Message message : messages) {
				sb.append(message.getUserName() + ": " + message.getContent() + "\n");
			}
		}
		return sb.toString();
	}

    private void notifyTutor(String tutor, String message) {
        try {
            Session tutorSession = tutorSessionMap.get(tutor);
            if (tutorSession != null && tutorSession.isOpen()) {
                tutorSession.getBasicRemote().sendText(message);
            } else {
               pendingNotifications.computeIfAbsent(tutor, k -> new ArrayList<>()).add(message);
            }
        } catch (IOException e) {
            logger.error("Error sending tutor notification", e);
        }
        //}
    }
} // end of Class
