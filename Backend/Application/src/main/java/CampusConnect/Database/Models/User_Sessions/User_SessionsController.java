package CampusConnect.Database.Models.User_Sessions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class User_SessionsController {
    @Autowired
    User_SessionsRepository User_SessionsRepository;

    //Returns all classes
    @GetMapping(path = "/classes")
    public List<User_Sessions> getAllClasses() {
        return User_SessionsRepository.findAll();
    }

    //Returns the class given from the class id.
    @GetMapping(path = "/classes/{id}")
    public User_Sessions getClassById(@PathVariable Long id) {
        return User_SessionsRepository.findById(id).orElse(null);
    }


    //Returns the class code given from the class name.
    @GetMapping(path = "/classes/code/{className}")
    public String getClassCode(@PathVariable String className){
        String currentClass = User_SessionsRepository.findByclassName(className).getclassCode();
        return currentClass;
    }




}
