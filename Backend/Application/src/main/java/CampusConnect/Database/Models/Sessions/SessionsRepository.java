package CampusConnect.Database.Models.Sessions;

import CampusConnect.Database.Models.Classes.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SessionsRepository extends JpaRepository<Sessions, Long>
{
    @Transactional
    void deleteById(Long id);

    Sessions findById(long id);
    Sessions findBySessionId(long id);
    Sessions findAllBySessionId(long sessionId);
    Sessions getSessionsBySessionId(long sessionId);
    List<Sessions> findAllByUsers_UserId(long userId);
    //List<Sessions> findAllByClassEntity_ClassId(long classId);
}
