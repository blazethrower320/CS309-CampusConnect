package CampusConnect.Database.Models.Messages;

import CampusConnect.Database.Models.Ratings.Ratings;
import CampusConnect.Database.Models.Ratings.RatingsRepository;
import CampusConnect.Database.Models.Ratings.RatingsService;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Database.Models.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessagesController
{
    @Autowired
    MessagesRepository messagesRepository;

    @GetMapping(path = "/messages")
    public List<Messages> getAllMessages() {
        return messagesRepository.findAll();
    }
}
