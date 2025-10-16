package CampusConnect.Database.Models.SessionParticipants;

import CampusConnect.Database.Models.Sessions.Sessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface SessionParticipantsRepository extends JpaRepository<SessionParticipants, Long> {
    @Transactional
    void deleteById(Long id);

    SessionParticipants findById(long id);
}
