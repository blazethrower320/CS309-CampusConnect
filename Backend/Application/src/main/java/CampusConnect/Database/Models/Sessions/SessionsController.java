package CampusConnect.Database.Models.Sessions;

import CampusConnect.Database.Models.Ratings.Ratings;
import CampusConnect.Database.Models.Ratings.RatingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SessionsController
{
    @Autowired
    SessionsRepository SessionsRepository;

    @GetMapping(path = "/sessions")
    public List<Sessions> getAllSessions() {
        return SessionsRepository.findAll();
    }
}
