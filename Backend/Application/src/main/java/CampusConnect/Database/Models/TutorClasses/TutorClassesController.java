package CampusConnect.Database.Models.TutorClasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TutorClassesController
{
    @Autowired
    TutorClassesRepository TutorClassesRepository;

    @GetMapping(path = "/tutorclasses")
    public List<TutorClasses> getAllTutorClasses() {
        return TutorClassesRepository.findAll();
    }
}
