package CampusConnect.Database.Models.Messages;

import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class PrivateMessagesController {
    @Autowired
    PrivateMessagesRepository privateMessagesRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping(path = "/privateMessages")
    public List<PrivateMessages> getAllMessages() {
        return privateMessagesRepository.findAll();
    }

    @GetMapping("/privateMessages/user/{userId}")
    public List<PrivateMessages> getMessagesForUser(@PathVariable long userId) {
        return privateMessagesRepository.findByUserIdOrReceiverUserId(userId, userId);
    }

    @GetMapping("/messages/getDMs/{userId}")
    public List<DisplayChat> getDMChatsForUser(@PathVariable long userId) {
        List<PrivateMessages> messages = privateMessagesRepository.findByUserIdOrReceiverUserId(userId, userId);
        Map<Long, String> otherUsers = new HashMap<>();
        for (PrivateMessages msg : messages)
        {
            if (msg.getUserId() != userId)
            {
                otherUsers.put(msg.getUserId(), msg.getUserUsername());
            } else if (msg.getReceiverUserId() != userId)
            {
                User receiver = userRepository.findByUsername(msg.getReceiverUsername());
                if (receiver != null)
                {
                    otherUsers.put(receiver.getUserId(), receiver.getUsername());
                }
            }
        }

        List<DisplayChat> chatList = new ArrayList<>();
        for (Map.Entry<Long, String> entry : otherUsers.entrySet())
        {
            DisplayChat chat = new DisplayChat();
            chat.Id = entry.getKey().intValue();
            chat.Name = entry.getValue();
            chat.isGroupChat = false;

            chatList.add(chat);
        }

        return chatList;
    }
}
