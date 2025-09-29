package CampusConnect.Database.Models.TutorAvailability;

import CampusConnect.Database.Models.Sessions.Sessions;
import CampusConnect.Database.Models.Sessions.SessionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TutorAvailabilityController
{
    @Autowired
    TutorAvailabilityRepository TutorAvailabilityRepository;

    @GetMapping(path = "/tutoravailability")
    public List<TutorAvailability> getAllAvailability() {
        return TutorAvailabilityRepository.findAll();
    }
}
