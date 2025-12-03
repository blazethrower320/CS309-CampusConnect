package CampusConnect.Database.Models.Messages;

import CampusConnect.Database.Models.Ratings.Ratings;
import CampusConnect.Database.Models.Ratings.RatingsRepository;
import CampusConnect.Database.Models.Ratings.RatingsService;
import CampusConnect.Database.Models.Sessions.Sessions;
import CampusConnect.Database.Models.Sessions.SessionsRepository;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

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
        Map<Long, String> combinedMessagesUsers = new HashMap<>();
        for (PrivateMessages msg : privateMessages)
        {
            long otherUserID;
            String otherName;
            if (msg.getUserId() == userId)
            {
                otherUserID = msg.getReceiverUserId();
                otherName = msg.getReceiverUsername();
            }
            else
            {
                otherUserID = msg.getUserId();
                otherName = msg.getUserUsername();
            }

            if (!combinedMessagesUsers.containsKey(otherUserID))
            {
                if (otherName == null || otherName.isEmpty())
                {
                    User user = userRepository.findById(otherUserID).orElse(null);
                    if (user != null) otherName = user.getUsername();
                }

                if (otherName != null)
                {
                    combinedMessagesUsers.put(otherUserID, otherName);
                }
            }
        }

        // Putting it into the Display Chat for Cam
        for (Map.Entry<Long, String> entry : combinedMessagesUsers.entrySet())
        {
            DisplayChat chat = new DisplayChat();
            chat.Id = entry.getKey();
            chat.Name = entry.getValue();
            chat.isGroupChat = false;
            combinedChats.add(chat);
        }
        List<Messages> userGroupMessages = messagesRepository.findByUserId(userId);
        Set<Long> sessionIdsList = new HashSet<>();
        for (Messages msg : userGroupMessages)
        {
            sessionIdsList.add(msg.getSessionId());
        }

        for (Long sessionId : sessionIdsList)
        {
            Sessions session = sessionsRepository.findById(sessionId).orElse(null);
            if (session != null) {
                DisplayChat chat = new DisplayChat();
                chat.Id =session.getSessionId();
                chat.Name = session.getClassName();
                chat.isGroupChat = true;
                combinedChats.add(chat);
            }
        }

        return combinedChats;
    }
}
