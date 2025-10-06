package CampusConnect.Database.Models.Tutors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TutorController
{
    @Autowired
    TutorRepository tutorRepository;

    @GetMapping(path = "/tutors")
    public List<Tutor> getAllTutors()
    {
        return tutorRepository.findAll();
    }

    @GetMapping("tutors/info/{tutorID}")
    public Tutor getTutor(@PathVariable long tutorID)
    {
        return tutorRepository.getTutorByTutorId(tutorID);
    }



}
