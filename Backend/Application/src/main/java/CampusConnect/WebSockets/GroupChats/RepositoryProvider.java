package CampusConnect.WebSockets.GroupChats;

import CampusConnect.Database.Models.Users.UserRepository;
import CampusConnect.WebSockets.GroupChats.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepositoryProvider {

    private static UserRepository userRepository;
    private static MessageRepository messageRepository;

    @Autowired
    public void setUserRepository(UserRepository repo) { RepositoryProvider.userRepository = repo;}

    @Autowired
    public void setMessageRepository(MessageRepository repo) {RepositoryProvider.messageRepository = repo;}

    public static UserRepository getUserRepository() {return userRepository;}

    public static MessageRepository getMessageRepository() {return messageRepository;}
}