package CampusConnect.Database.Models.Tutors;

import CampusConnect.Database.Models.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
    @Transactional
    void deleteById(Long id);

    Tutor getTutorByTutorId(Long tutorId);
    Boolean existsByTutorId(Long tutorId);
    Boolean existsByUsername(String username);
    void deleteByUsername(String username);
    Tutor findByUsername(String username);
    Tutor getTutorByUserUserId(long userId);
    Tutor getTutorByUsername(String username);
    boolean existsByUser(User user);
    Tutor findByUser(User user);
    ArrayList<Tutor> findAllByClasses_ClassId(long classId);
    Tutor findByUser_UserId(long userId);

}