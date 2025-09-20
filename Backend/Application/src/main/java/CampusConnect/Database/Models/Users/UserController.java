package CampusConnect.Database.Models.Users;

import java.util.List;

import CampusConnect.Database.Models.Tutors.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TutorRepository tutorRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @GetMapping(path = "/users")
    public List<User> getAllUsers()
    {
        return userRepository.findAll();
    }

    @GetMapping(path = "/users/{id}")
    public User getUserById(@PathVariable Long id)
    {
        return userRepository.findById(id).orElse(null);
    }

    @GetMapping("/users/findUsername/{username}")
    public List<User> getUserByUsername(@PathVariable String username)
    {
        return userRepository.findByUsername(username);
    }
    @GetMapping("/users/findUser/{name}")
    public List<User> getUserByFirstName(@PathVariable String name)
    {
        return userRepository.findAllByFirstName(name);
    }
}

