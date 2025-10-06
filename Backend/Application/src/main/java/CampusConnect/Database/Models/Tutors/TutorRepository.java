package CampusConnect.Database.Models.Tutors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
    @Transactional
    void deleteById(Long id);

    Tutor getTutorByTutorId(Long tutorId);
}