package CampusConnect.Database.Models.Ratings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RatingsRepository extends JpaRepository<Ratings, Long>
{
    @Transactional
    void deleteById(Long id);

    Ratings findById(long id);
}
