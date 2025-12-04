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
}
