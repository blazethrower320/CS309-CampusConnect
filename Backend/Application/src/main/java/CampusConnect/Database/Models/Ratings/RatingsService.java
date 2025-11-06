package CampusConnect.Database.Models.Ratings;

import CampusConnect.Database.Models.Sessions.Sessions;
import CampusConnect.Database.Models.Sessions.SessionsDTO;
import CampusConnect.Database.Models.Tutors.Tutor;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RatingsService {

    @Autowired
    RatingsRepository RatingsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private RatingsRepository ratingsRepository;

    @Transactional
    public Ratings createRating(RatingsDTO ratingsDTO) {
        Tutor tutor = tutorRepository.getTutorByTutorId(ratingsDTO.getTutorId());
        User user = userRepository.getUserByUserId(ratingsDTO.getUserId());

        if (tutor == null || user == null) {
            throw new IllegalArgumentException("Tutor or User not found");
        }
        Ratings rating = new Ratings(
                tutor,
                user,
                ratingsDTO.getRating(),
                ratingsDTO.getComments()
        );

        ratingsRepository.save(rating);

        updateTutorRating(tutor);
        return rating;
    }

    public void updateTutorRating(Tutor tutor)
    {
        double rating = tutor.getTotalRating();
        List<Ratings> tutorRatings = ratingsRepository.getAllRatingsByTutor(tutor);

        int totalStars = 0;
        for(Ratings ratings : tutorRatings)
            totalStars += ratings.getRating();

        tutor.setTotalRating(totalStars / tutorRatings.size());
        tutorRepository.save(tutor);
    }


}
