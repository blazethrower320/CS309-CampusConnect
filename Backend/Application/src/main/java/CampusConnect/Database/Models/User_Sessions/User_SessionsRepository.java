package CampusConnect.Database.Models.User_Sessions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface User_SessionsRepository extends JpaRepository<User_Sessions, Long> {
    @Transactional
    void deleteById(Long id);

    User_Sessions findByclassName(String name);
}