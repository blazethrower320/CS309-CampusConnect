package CampusConnect.Database.Models.Admins;

import CampusConnect.Database.Models.Tutors.Tutor;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminsController {
    private String userNotFound = "User not found";
    private String adminNotFound = "Admin not found";
    private String adminAlreadyExists = "Admin already exists";

    @Autowired
    AdminsRepository adminsRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TutorRepository tutorRepository;

    //Returns all classes
    @GetMapping(path = "/admins")
    public List<Admins> getAllAdmins() {
        return adminsRepository.findAll();
    }

    public ResponseEntity<Object> CreateAdmin(User user)
    {
        if(user == null)
            return ResponseEntity.status(404).body(userNotFound);

        Admins admin = new Admins(user);

        adminsRepository.save(admin);
        return ResponseEntity.ok(admin);
    }


    @PostMapping("/admin/updateRatingsTutor/{username}/{rating}")
    public boolean UpdateRatings(@PathVariable String username, @PathVariable double rating)
    {
        Tutor tutor = tutorRepository.findByUsername(username);
        if(tutor == null)
        {
            return false;
        }

        tutor.setTotalRating(rating);
        tutorRepository.save(tutor);

        return true;
    }

    @PostMapping("/admin/deleteAdmin/{userId}")
    public boolean DeleteAdmin(@PathVariable long userId)
    {
        User user = userRepository.getUserByUserId(userId);
        Admins admin = adminsRepository.findByUser(user);
        if(admin == null)
        {
            return false;
        }
        // SOME CRAZY UPDATE HERE!!!
        adminsRepository.delete(admin);
        return true;
    }

    @GetMapping("/admin/incrementNukedUsers")
    public boolean incrementNukedUsers(@RequestBody User user)
    {
        Admins admin = adminsRepository.findByUser(user);
        if(admin == null)
            return false;

        admin.incrementNukedUsersCount();
        return true;
    }

    @GetMapping("/admin/getAdminNukedUsers")
    public int getAdminNukedUsers(@RequestBody User user)
    {
        Admins admin = adminsRepository.findByUser(user);
        if(admin == null)
            return 0;

        return admin.getNukedUsersCount();
    }

}
