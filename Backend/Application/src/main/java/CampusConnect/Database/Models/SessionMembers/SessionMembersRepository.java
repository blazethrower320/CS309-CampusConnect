package CampusConnect.Database.Models.SessionMembers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface SessionMembersRepository extends JpaRepository<SessionMembers, Long> {
    @Transactional
    void deleteById(Long id);

    SessionMembers findById(long id);
}
