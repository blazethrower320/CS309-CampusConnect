package CampusConnect.Database.Models.Ratings;

import CampusConnect.Database.Models.Tutors.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RatingsRepository extends JpaRepository<Ratings, Long>
{
    @Transactional
    void deleteById(Long id);


    List<Ratings> getAllRatingsByTutor(Tutor tutor);
    List<Ratings> getAllRatingsByTutorId(Long tutorId);
    Ratings findById(long id);
}
