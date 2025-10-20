package CampusConnect.Database.Models.SessionMembers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SessionMembersRepository extends JpaRepository<SessionMembers, Long> {
    @Transactional
    void deleteById(Long id);

    SessionMembers findById(long id);
    List<SessionMembers> findAllBySessionId(long sessionId);
}
