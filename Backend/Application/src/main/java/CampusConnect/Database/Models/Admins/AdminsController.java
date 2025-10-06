package CampusConnect.Database.Models.Admins;

import CampusConnect.Database.Models.Admins.Admins;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminsController {
    @Autowired
    AdminsRepository adminsRepository;

    //Returns all classes
    @GetMapping(path = "/admins")
    public List<Admins> getAllAdmins() {
        return adminsRepository.findAll();
    }
}
