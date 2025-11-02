package CampusConnect.Database.Models.Ratings;

import CampusConnect.Database.Models.Classes.Classes;
import CampusConnect.Database.Models.Classes.ClassesRepository;
import CampusConnect.Database.Models.Sessions.SessionsService;
import CampusConnect.Database.Models.Tutors.Tutor;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Database.Models.Tutors.TutorRequest;
import CampusConnect.Database.Models.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RatingsController
{
    @Autowired
    RatingsRepository RatingsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private RatingsRepository ratingsRepository;
    @Autowired
    private RatingsService ratingsService;

    @GetMapping(path = "/ratings")
    public List<Ratings> getAllRatings() {
        return RatingsRepository.findAll();
    }

    @GetMapping("/ratings/getTutorRatings/{tutorId}")
    public List<Ratings> getTutorRatings(@PathVariable long tutorId)
    {
        return ratingsRepository.getAllRatingsByTutorTutorId(tutorId);
    }

    @PostMapping("/ratings/createRating")
    public void createRating(@RequestBody RatingsDTO ratingDTO)
    {
        ratingsService.createRating(ratingDTO);
    }

}
