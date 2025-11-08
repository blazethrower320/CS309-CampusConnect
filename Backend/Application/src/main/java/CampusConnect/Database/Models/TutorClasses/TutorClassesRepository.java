package CampusConnect.Database.Models.TutorClasses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface TutorClassesRepository extends JpaRepository<TutorClasses, Long>
{
    @Transactional
    void deleteById(Long id);

    TutorClasses findById(long id);
}
