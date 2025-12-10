package CampusConnect.Database.Models.Messages;

import CampusConnect.Database.Models.Sessions.Sessions;
import CampusConnect.Database.Models.Sessions.SessionsRepository;
import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class MessagesController
{
    @Autowired
    MessagesRepository messagesRepository;
    @Autowired
    private PrivateMessagesRepository privateMessagesRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionsRepository sessionsRepository;

    @GetMapping(path = "/messages")
    public List<Messages> getAllMessages() {
        return messagesRepository.findAll();
    }

    @GetMapping("/messages/getAllChats/{userId}")
    public List<DisplayChat> getAllChatsForUser(@PathVariable long userId) {
        List<DisplayChat> combinedChats = new ArrayList<>();
        List<PrivateMessages> privateMessages = privateMessagesRepository.findByUserIdOrReceiverUserId(userId, userId);
        Set<Long> privateChatOtherIds = new HashSet<>();

        for (PrivateMessages msg : privateMessages) {
            long otherUserId;
            String otherUsername;

            if (msg.getUserId() == userId)
            {
                otherUserId = msg.getReceiverUserId();
                otherUsername = msg.getReceiverUsername();
            }
            else
            {
                otherUserId = msg.getUserId();
                otherUsername = msg.getUserUsername();
            }

            if (!privateChatOtherIds.contains(otherUserId))
            {

                if (otherUsername == null || otherUsername.isEmpty())
                {
                    User user = userRepository.findById(otherUserId).orElse(null);
                    if (user != null)
                    {
                        otherUsername = user.getUsername();
                    }
                    else
                    {
                        otherUsername = "user was not found";
                    }
                }

                DisplayChat chat = new DisplayChat();
                chat.Id = otherUserId;
                chat.Name = otherUsername;
                chat.isGroupChat = false;
                combinedChats.add(chat);

                privateChatOtherIds.add(otherUserId);
            }
        }

        List<Sessions> userSessions = sessionsRepository.findAllByUsers_UserId(userId);
        for (Sessions session : userSessions)
        {
            DisplayChat chat = new DisplayChat();
            chat.Id = session.getSessionId();
            chat.Name = session.getClassName();
            chat.isGroupChat = true;
            combinedChats.add(chat);
        }

        return combinedChats;
    }
}
