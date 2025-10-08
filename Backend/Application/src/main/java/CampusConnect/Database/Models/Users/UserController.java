package CampusConnect.Database.Models.Users;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Models.editUser;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TutorRepository tutorRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";
    private String userNotFound = "User not found";
    private String wrongUsernamePassword = "Wrong username or password";
    private String userCreated = "New user created";
    private String userCreationFailed = "New user failed to create";
    private String userDNE = "User does not exist";
    private String userDeleted = "User deleted";

    public String getUserNotFound() {return userNotFound;}
    public String getUserCreated() {return userCreated;}
    public String getUserCreationFailed() {return userCreationFailed;}
    public String getUserDNE() {return userDNE;}
    public String getUserDeleted() {return userDeleted;}

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

    @PatchMapping("/users/editUsername")
    public boolean editUsername(@RequestBody editUser userBody)
    {
        String newUsername = userBody.getNewUsername();
        long userID = userBody.getUserID();

        User user = userRepository.findById(userID).orElse(null);

        if(user == null)
            return false;

        user.setUsername(newUsername);
        userRepository.save(user);
        return true;
    }

    @PatchMapping("/users/editPassword")
    public boolean editPassword(@RequestBody editUser userBody)
    {
        String newPassword = userBody.getNewPassword();
        User user = userRepository.findByUsername(userBody.getNewUsername());

        if(user == null)
            return false;

        user.setPassword(newPassword);
        userRepository.save(user);
        return true;
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
        if (getAllUserByUsername(userBody.getUsername()).isEmpty()) {  //Assume no same usernames
            User newUser = new User(userBody.getUsername(), userBody.getPassword(), false, false);
            userRepository.save(newUser);
            return userCreated;
        } else {
            return userCreationFailed;
        }
    }

    //Only requires username and password currently.
    // Need to switch this to post
    @PostMapping("/users/deleteUser")
    public String deleteUser(@RequestBody User userBody) {
        ArrayList<User> usernames = new ArrayList<>(getAllUserByUsername(userBody.getUsername()));
        if (usernames.isEmpty()) {    //Username does not match an existing one.
            return userDNE;
        }
        for (User username : usernames) {
            if (username.getPassword().equals(userBody.getPassword())) {
                userRepository.deleteById(username.getUserId());
                return userDeleted;
            }
        }
        return wrongUsernamePassword;
    }

    @GetMapping("/users/IsTutor/{userID}")
    public boolean isUserTutor(@PathVariable long userID)
    {
        return tutorRepository.findById(userID).isPresent();
    }


}

