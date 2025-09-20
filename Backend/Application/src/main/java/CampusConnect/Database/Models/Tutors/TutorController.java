package CampusConnect.Database.Models.Tutors;

import CampusConnect.Database.Models.Users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TutorController
{
    @Autowired
    TutorRepository tutorRepository;

    @GetMapping(path = "/tutors")
    public List<User> getAllTutors()
    {
        return tutorRepository.findAll();
    }
}
