package CampusConnect.Database.Models.Tutors;
import CampusConnect.Database.Models.Admins.Admins;
import CampusConnect.Database.Models.Classes.Classes;
import CampusConnect.Database.Models.Classes.ClassesRepository;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Users.UserRepository;
import java.util.List;

@RestController
public class TutorController {
    @Autowired
    TutorService tutorService;
    @Autowired
    TutorRepository tutorRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    ClassesRepository classesRepository;

    private String userNotFound = "User not found";
    private String tutorNotFound = "Tutor not found";
    private String tutorAlreadyExists = "Tutor already exists";
    private String tutorRemoved = "Tutor removed";
    private String tutorUpdated = "Tutor updated";


    @GetMapping(path = "/tutors")
    public List<Tutor> getAllTutors() {
        return tutorRepository.findAll();
    }

    @GetMapping("/tutors/info/{tutorId}")
    public Tutor getTutor(@PathVariable long tutorId) {
        return tutorRepository.getTutorByTutorId(tutorId);
    }

    @GetMapping("/tutors/getClasses/{tutorId}")
    public List<Classes> getTutorClasses(@PathVariable long tutorId) {
        return classesRepository.findAll();
    }

    @PostMapping("/tutors/createTutor/{username}")
    public Tutor createTutor(@PathVariable String username) {

        User givenUser = userRepository.findByUsername(username);

        /*if(givenUser == null){
            return ResponseEntity.status(404).body(userNotFound);
        }
        boolean exists = tutorRepository.existsByUsername(username);
        if (exists){
            return ResponseEntity.status(403).body(tutorAlreadyExists);
        }
        Tutor tutor =  new Tutor(givenUser);
        tutorRepository.save(tutor);
        return ResponseEntity.ok(tutor);
         */
        return tutorService.createTutor(givenUser);
    }

    @GetMapping("/tutors/getTutorRating/{tutorId}")
    public double getTutorRating(@PathVariable long tutorId) {
        Tutor tutor = tutorRepository.getTutorByTutorId(tutorId);
        return tutor.getTotalRating();
    }

    @PostMapping("/tutors/deleteTutor/{username}")
    public ResponseEntity<Object> deleteTutor(@PathVariable String username) {
        Tutor tutor = tutorRepository.findByUsername(username);
        if (tutor != null) {
            User user = tutor.getUser();
            userRepository.save(user);
            tutorRepository.delete(tutor);
            return ResponseEntity.ok(tutorRemoved);
        }

        return ResponseEntity.status(403).body(tutorNotFound);
    }

    @GetMapping("/tutors/getTutorFromUserId/{userId}")
    public Tutor getTutorFromUserId(@PathVariable long userId) {
        return tutorRepository.getTutorByUserUserId(userId);
    }

    @PatchMapping("tutors/addClass/{tutorId}/{classId}")
    public Tutor addClassToTutor(@PathVariable long tutorId, @PathVariable long classId) {
        Tutor tutor = tutorRepository.findById(tutorId).orElseThrow(() -> new RuntimeException("Tutor not found"));
        Classes setClass = classesRepository.findById(classId).orElseThrow(() -> new RuntimeException("Class not found"));
        tutor.addClasses(setClass);
        return tutorRepository.save(tutor);

    }

    @PostMapping("tutors/removeClass/{tutorId}/{classId}")
    public Tutor removeClassToTutor(@PathVariable long tutorId, @PathVariable long classId) {
        Tutor tutor = tutorRepository.findById(tutorId).orElseThrow(() -> new RuntimeException("Tutor not found"));
        Classes setClass = classesRepository.findById(classId).orElseThrow(() -> new RuntimeException("Class not found"));
        tutor.removeClass(setClass);
        return tutorRepository.save(tutor);
    }
}