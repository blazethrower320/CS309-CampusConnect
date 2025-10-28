package CampusConnect.Database.Models.Ratings;

import CampusConnect.Database.Models.Classes.Classes;
import CampusConnect.Database.Models.Classes.ClassesRepository;
import CampusConnect.Database.Models.Tutors.Tutor;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Database.Models.Tutors.TutorRequest;
import CampusConnect.Database.Models.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping(path = "/ratings")
    public List<Ratings> getAllRatings() {
        return RatingsRepository.findAll();
    }

    @PostMapping("/ratings/createRating")
    public ResponseEntity<Object> createRating(@RequestBody Ratings Ratings)
    {
        if(!userRepository.existsById(Ratings.getId()) || !tutorRepository.existsByTutorId(Ratings.getTutor().getTutorID()))
            return ResponseEntity.status(404).body("Invalid User / Tutor");
        Ratings rating = new Ratings(Ratings.getTutor(), Ratings.getUser(), Ratings.getRating(), Ratings.getComments());
        RatingsRepository.save(rating);

        updateTutorRating(Ratings.getTutor());

        return ResponseEntity.status(200).body(rating);
    }

    public void updateTutorRating(Tutor tutor)
    {
        double rating = tutor.getTotalRating();
        List<Ratings> tutorRatings = ratingsRepository.getAllRatingsByTutor(tutor);
        int totalStars = 0;
        for(Ratings ratings : tutorRatings)
            totalStars += ratings.getRating();

        tutor.setTotalRating(totalStars / tutorRatings.size()-1);
        tutorRepository.save(tutor);
    }


}
