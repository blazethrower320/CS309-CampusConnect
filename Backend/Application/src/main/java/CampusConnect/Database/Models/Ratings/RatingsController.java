package CampusConnect.Database.Models.Ratings;

import CampusConnect.Database.Models.Classes.Classes;
import CampusConnect.Database.Models.Classes.ClassesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RatingsController
{
    @Autowired
    RatingsRepository RatingsRepository;

    @GetMapping(path = "/ratings")
    public List<Ratings> getAllRatings() {
        return RatingsRepository.findAll();
    }

}
