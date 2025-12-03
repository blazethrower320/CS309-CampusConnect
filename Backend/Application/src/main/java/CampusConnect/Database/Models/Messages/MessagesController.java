package CampusConnect.Database.Models.Messages;

import CampusConnect.Database.Models.Ratings.Ratings;
import CampusConnect.Database.Models.Ratings.RatingsRepository;
import CampusConnect.Database.Models.Ratings.RatingsService;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class MessagesController
{
    @Autowired
    MessagesRepository messagesRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(path = "/messages")
    public List<Messages> getAllMessages() {
        return messagesRepository.findAll();
    }



}
