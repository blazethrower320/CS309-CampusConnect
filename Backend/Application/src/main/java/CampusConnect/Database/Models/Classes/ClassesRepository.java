package CampusConnect.Database.Models.Classes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ClassesRepository extends JpaRepository<Classes, Long> {
    @Transactional
    void deleteById(Long id);

    Classes findByclassName(String name);
}