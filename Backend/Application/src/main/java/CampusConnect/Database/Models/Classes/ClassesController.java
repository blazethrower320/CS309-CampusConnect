package CampusConnect.Database.Models.Classes;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClassesController {
    @Autowired
    ClassesRepository classesRepository;

    @Autowired
    ClassesService classesService;

    //Returns all classes
    @GetMapping(path = "/classes")
    public List<Classes> getAllClasses() {
        return classesRepository.findAll();
    }

    //Returns the class given from the class Id.
    @GetMapping(path = "/classes/{classId}")
    public Classes getClassById(@PathVariable Long Id) {
        return classesRepository.findById(Id).orElse(null);
    }


    //Returns the class code given from the class name.
    @GetMapping(path = "/classes/code/{className}")
    public String getClassCode(@PathVariable String className){
        String currentClass = classesRepository.findByclassName(className).getclassCode();
        return currentClass;
    }

    @PostMapping(path = "/classes/delete/{classId}")
    public void deleteClass(@PathVariable long classId){
        classesRepository.findById(classId);
        classesService.deleteClass(classId);
    }

    @PostMapping(path = "/classes/create")
    public void createClass(@RequestBody Classes classEntity){
        if(!classesRepository.existsByclassName(classEntity.getclassName()) &&  !classesRepository.existsByclassCode(classEntity.getclassCode())){
            classesRepository.save(classEntity);
        }
    }


}
