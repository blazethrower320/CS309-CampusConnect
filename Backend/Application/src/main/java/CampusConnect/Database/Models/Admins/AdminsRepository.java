package CampusConnect.Database.Models.Admins;

import CampusConnect.Database.Models.Admins.Admins;
import CampusConnect.Database.Models.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface AdminsRepository extends JpaRepository<Admins, Long> {
    @Transactional
    void deleteById(Long id);
    Admins getAdminByAdminId(Long id);
    Admins findByUser(User user);
    boolean existsByUser(User user);

}
