package CampusConnect.Database.Models.Users;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";
    private String userNotFound = "User not found";
    private String wrongUsernamePassword = "Wrong username or password";
    private String userCreated = "New user created";
    private String userCreationFailed = "New user failed to create";

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
    public List<User> getAllUserByUsername(@PathVariable String username)
    {
        return userRepository.findAllByUsername(username);
    }
    @GetMapping("/users/findUser/{name}")
    public List<User> getUserByFirstName(@PathVariable String name)
    {
        return userRepository.findAllByFirstName(name);
    }

    @PostMapping("/users/login")
    public ResponseEntity<Object> loginUser(@RequestBody User userBody)
    {
        String username = userBody.getUsername();
        String password = userBody.getPassword();

        User user = userRepository.findByUsername(username);
        if(user == null)
        {
            return ResponseEntity.status(404).body(userNotFound);
        }

        if(!user.getPassword().equals(password))
        {
            // Dah wrong password
            return ResponseEntity.status(403).body(wrongUsernamePassword);
        }

        // Username and Password is correct
        return ResponseEntity.ok(user);
    }

    // Returns the list of usernames
    @GetMapping("/usernames")
    public List<String> getAllUsernames()
    {
        ArrayList<String> usernamesReturn = new ArrayList<String>();
        for(User user : getAllUsers())
        {
            usernamesReturn.add(user.getUsername());
        }
        return usernamesReturn;
    }

    @GetMapping("/users/password/{username}")
    public String getPasswordByUsername(@PathVariable String username){
        return userRepository.findByUsername(username).getPassword();
    }

    @PostMapping("/users/createUser")
    public String createUser(@RequestBody User userBody) {
        if (getAllUserByUsername(userBody.getUsername()) != null) {  //Assume no same usernames
            return userCreationFailed;
        } else {
            User newUser = new User(userBody.getFirstName(), userBody.getLastName(), userBody.getUsername(), userBody.getPassword());
            return userCreated;
        }
    }
}

