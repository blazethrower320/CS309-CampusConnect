package CampusConnect.Database.Models.Tutors;
import CampusConnect.Database.Models.Admins.Admins;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Users.UserRepository;
import java.util.List;

@RestController
public class TutorController
{
    @Autowired
    TutorRepository tutorRepository;
    @Autowired
    UserRepository userRepository;

    private String userNotFound = "User not found";
    private String tutorNotFound = "Tutor not found";
    private String tutorAlreadyExists = "Tutor already exists";
    private String tutorRemoved = "Tutor removed";
    private String tutorUpdated = "Tutor updated";


    @GetMapping(path = "/tutors")
    public List<Tutor> getAllTutors()
    {
        return tutorRepository.findAll();
    }

    @GetMapping("/tutors/info/{tutorID}")
    public Tutor getTutor(@PathVariable long tutorID)
    {
        return tutorRepository.getTutorByTutorId(tutorID);
    }

    @PostMapping("/tutors/createTutor/{username}")
    public ResponseEntity<Object> createTutor(@PathVariable String username)
    {
        User givenUser = userRepository.findByUsername(username);
        if(givenUser == null){
            return ResponseEntity.status(404).body(userNotFound);
        }
        boolean exists = tutorRepository.existsByUsername(username);
        if (exists){
            return ResponseEntity.status(403).body(tutorAlreadyExists);
        }
        Tutor tutor =  new Tutor(givenUser);
        tutorRepository.save(tutor);
        return ResponseEntity.ok(tutor);
    }

    @PostMapping("/tutors/deleteTutor/{username}")
    public ResponseEntity<Object> deleteTutor(@PathVariable String username)
    {
        Tutor tutor = tutorRepository.findByUsername(username);
        if(tutor != null){
            tutorRepository.delete(tutor);
            return ResponseEntity.ok(tutorRemoved);
        }

        return ResponseEntity.status(403).body(tutorNotFound);
    }

    @PutMapping("tutors/editTotalClasses")
    public ResponseEntity<Object> editTotalClasses(@RequestBody Tutor newTutor)
    {
        Tutor tutor = tutorRepository.findByUsername(newTutor.getUsername());
        if (tutor != null) {
            tutor.setTotalClasses(newTutor.gettotalClasses());
            tutorRepository.save(tutor);
            return ResponseEntity.ok(tutorUpdated);
        }
        return ResponseEntity.status(403).body(tutorNotFound);
    }


    //@GetMapping("tutors/getTutorFromUserId/{userId}")
    //public Tutor getTutorFromUserId(@PathVariable long userId)
   // {
   //     return tutorRepository.getTutorByUserId(userId);
    //}
}
