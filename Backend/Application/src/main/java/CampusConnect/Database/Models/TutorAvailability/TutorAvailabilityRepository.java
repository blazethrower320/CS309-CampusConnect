package CampusConnect.Database.Models.TutorAvailability;

import CampusConnect.Database.Models.Sessions.Sessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface TutorAvailabilityRepository extends JpaRepository<TutorAvailability, Long>
{
    @Transactional
    void deleteById(Long id);

    TutorAvailability findById(long id);
}