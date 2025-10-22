package CampusConnect.Database.Models.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional
    void deleteById(Long id);

    User findByUserId(Long userId);
    List<User> findAllByUsername(String username);
    List<User> findAllByFirstName(String firstName);
    //User findByUsernameAndPassword(String username, String password);
    User findByUsername(String username);
    User getUserByUsername(String username);
    //void updateByUserId(String username, long userId);
    Boolean existsByUsername(String username);
}

