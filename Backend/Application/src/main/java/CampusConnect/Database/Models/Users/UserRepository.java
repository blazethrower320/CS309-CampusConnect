<<<<<<< HEAD:Backend/Application/src/main/java/CampusConnect/Database/Models/Users/UserRepository.java
package CampusConnect.Database.Models.Users;
=======
package CampusConnect.Users;
>>>>>>> main:Backend/Application/src/main/java/CampusConnect/Users/UserRepository.java

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional
    void deleteById(Long id);

    List<User> findByUsername(String username);
    List<User> findAllByFirstName(String firstName);
}

