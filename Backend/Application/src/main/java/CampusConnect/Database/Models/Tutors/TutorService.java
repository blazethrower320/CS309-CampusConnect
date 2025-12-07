package CampusConnect.Database.Models.Tutors;

import CampusConnect.Database.Models.Users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TutorService {

    @Autowired
    private TutorRepository tutorRepository;

    public Tutor createTutor(User user) {
        // Check if the admin already exists
        if (tutorRepository.existsByUser(user)) {
            throw new RuntimeException("Tutor already exists");
        }
        Tutor tutor = new Tutor(user);
        return tutorRepository.save(tutor);
    }
}
