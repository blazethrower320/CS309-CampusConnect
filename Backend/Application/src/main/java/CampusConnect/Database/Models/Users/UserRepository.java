package CampusConnect.Database.Models.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional
    void deleteById(Long id);

    List<User> findByUsername(String username);
    List<User> findAllByFirstName(String firstName);
}

