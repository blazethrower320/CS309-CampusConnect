package CampusConnect.Database.Models.Classes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
    @Transactional
    void deleteById(Long id);
}