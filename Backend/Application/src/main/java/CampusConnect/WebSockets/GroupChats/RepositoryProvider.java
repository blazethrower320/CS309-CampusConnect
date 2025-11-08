package CampusConnect.WebSockets.GroupChats;

import CampusConnect.Database.Models.Images.ImagesRepository;
import CampusConnect.Database.Models.Messages.MessagesRepository;
import CampusConnect.Database.Models.Messages.PrivateMessagesRepository;
import CampusConnect.Database.Models.Sessions.SessionsRepository;
import CampusConnect.Database.Models.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepositoryProvider {

    private static UserRepository userRepository;
    private static MessagesRepository messageRepository;
    private static SessionsRepository sessionsRepository;
    private static PrivateMessagesRepository privateMessagesRepository;
    private static ImagesRepository imagesRepository;

    @Autowired
    public void setUserRepository(UserRepository repo) { RepositoryProvider.userRepository = repo;}

    @Autowired
    public void setMessageRepository(MessagesRepository repo) {RepositoryProvider.messageRepository = repo;}
    @Autowired
    public void setSessionsRepository(SessionsRepository repo) {RepositoryProvider.sessionsRepository = repo;}

    @Autowired
    public void setPrivateMessagesRepository(PrivateMessagesRepository repo) {RepositoryProvider.privateMessagesRepository = repo;}
    @Autowired
    public void setImagesRepository(ImagesRepository repo) {RepositoryProvider.imagesRepository = repo;}

    public static SessionsRepository getSessionsRepository() {return RepositoryProvider.sessionsRepository;}
    public static UserRepository getUserRepository() {return userRepository;}

    public static ImagesRepository getImagesRepository() {return RepositoryProvider.imagesRepository;}
    public static MessagesRepository getMessageRepository() {return messageRepository;}
    public static PrivateMessagesRepository getPrivateMessagesRepository() {return RepositoryProvider.privateMessagesRepository;}
}
