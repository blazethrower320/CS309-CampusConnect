package CampusConnect.Database.Models.Sessions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface SessionsRepository extends JpaRepository<Sessions, Long>
{
    @Transactional
    void deleteById(Long id);

    Sessions findById(long id);
}
