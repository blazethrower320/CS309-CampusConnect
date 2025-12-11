package CampusConnect.Database.Models.Ratings;

import CampusConnect.Database.Models.Classes.Classes;
import CampusConnect.Database.Models.Classes.ClassesRepository;
import CampusConnect.Database.Models.Sessions.SessionsService;
import CampusConnect.Database.Models.Tutors.Tutor;
import CampusConnect.Database.Models.Tutors.TutorRepository;
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
    @GetMapping("/ratings/getTutorRatingsUserId/{userId}")
    public List<Ratings> getTutorRatingsUserID(@PathVariable long userId)
    {
        return ratingsRepository.getAllRatingsByUser_UserId(userId);
    }

    @GetMapping("/ratings/getTutorAverageUsrId/{userId}")
    public double getTutorAverageRating(@PathVariable long userId)
    {
        Tutor tutor = tutorRepository.findByUser_UserId(userId);
        List<Ratings> tutorRatings = ratingsRepository.getAllRatingsByTutor(tutor);

        int totalStars = 0;
        for(Ratings ratings : tutorRatings)
            totalStars += ratings.getRating();
        return totalStars;
    }

    @PostMapping("/ratings/createRating")
    public boolean createRating(@RequestBody RatingsDTO ratingDTO)
    {
        try
        {
            ratingsService.createRating(ratingDTO);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    @PostMapping("/ratings/deleteRating/{ratingId}")
    public boolean deleteRating(@PathVariable long ratingId)
    {
        try
        {
            ratingsRepository.deleteById(ratingId);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
