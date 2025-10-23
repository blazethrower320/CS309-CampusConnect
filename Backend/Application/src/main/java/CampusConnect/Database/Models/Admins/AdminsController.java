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

    /*
    @PostMapping("/admin/createAdmin/{username}")
     /*
    public ResponseEntity<Object> CreateAdmin(@PathVariable String username)
    {
        User user = userRepository.findByUsername(username);
        if(user == null)
            return ResponseEntity.status(404).body(userNotFound);

        boolean exists = adminsRepository.existsByUsername(username);
        if(exists)
        {
            return ResponseEntity.status(403).body(adminAlreadyExists);
        }

        Admins admin = new Admins(user, "All");

        adminsRepository.save(admin);

        return ResponseEntity.ok(admin);
    }
    */



    public ResponseEntity<Object> CreateAdmin(User user)
    {
        if(user == null)
            return ResponseEntity.status(404).body(userNotFound);

        //boolean exists = adminsRepository.existsByUsername(username);
        //if(exists)
        //{
        //    return ResponseEntity.status(403).body(adminAlreadyExists);
        //}

        Admins admin = new Admins(user, "All");

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

    @PostMapping("/admin/deleteAdmin/{username}")
    public boolean DeleteAdmin(@PathVariable String username)
    {
        boolean exists = adminsRepository.existsByUsername(username);
        if(!exists)
        {
            return false;
        }
        Admins admin = adminsRepository.findByUsername(username);
        adminsRepository.delete(admin);
        return true;
    }

    @GetMapping("/admin/getPermissions/{username}")
    public String getAdminPermissions(@PathVariable String username)
    {
        boolean exists = adminsRepository.existsByUsername(username);
        if(!exists)
        {
            return null;
        }
        Admins admin = adminsRepository.findByUsername(username);
        return admin.getPermissions();
    }
    @PatchMapping("/admin/updateStatus/{username}")
    public ResponseEntity<Object> updateAdminStatus(@PathVariable String username)
    {
        User user = userRepository.findByUsername(username);
        if(user == null)
            return ResponseEntity.status(404).body(userNotFound);
        boolean exists = adminsRepository.existsByUsername(username);
        if(!exists)
        {
            return ResponseEntity.status(403).body(adminNotFound);
        }
        Admins admin = adminsRepository.findByUsername(username);
        admin.setIsActive(!admin.getIsActive());

        adminsRepository.save(admin);
        return ResponseEntity.ok(admin.getIsActive());
    }
}
