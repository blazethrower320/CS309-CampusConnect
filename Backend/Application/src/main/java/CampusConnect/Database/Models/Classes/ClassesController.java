package CampusConnect.Database.Models.Classes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClassesController {
    @Autowired
    ClassesRepository ClassesRepository;

    //Returns all classes
    @GetMapping(path = "/classes")
    public List<Classes> getAllClasses() {
        return ClassesRepository.findAll();
    }

    //Returns the class given from the class id.
    @GetMapping(path = "/classes/{id}")
    public Classes getClassById(@PathVariable Long id) {
        return ClassesRepository.findById(id).orElse(null);
    }

    //Returns the class count of the requested class.
    @GetMapping(path = "/classes/size/{className}")
    public int getClassCount(@PathVariable String className) {
        Classes currentClass = ClassesRepository.findByclassName(className);
        return currentClass.getclassCount();
    }

    //Returns the class code given from the class name.
    @GetMapping(path = "/classes/code/{className}")
    public String getClassCode(@PathVariable String className){
        String currentClass = ClassesRepository.findByclassName(className).getclassCode();
        return currentClass;
    }




}
