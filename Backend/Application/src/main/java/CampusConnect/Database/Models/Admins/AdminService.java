package CampusConnect.Database.Models.Admins;

import CampusConnect.Database.Models.Users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminsRepository adminsRepository;

    public Admins createAdmin(User user) {
        // Check if the admin already exists
        if (adminsRepository.existsByUser(user)) {
            throw new RuntimeException("Admin already exists");
        }
        Admins admin = new Admins(user, "All");
        admin.setUser(user);
        user.setisAdmin(true);
        return adminsRepository.save(admin);
    }

    public String deleteAdmin(User user){
        Admins admin = adminsRepository.findByUser(user);
        if(adminsRepository.existsByUsername(admin.getUsername())){
            adminsRepository.delete(admin);
            return "Admin Deleted";
        }
        else {
            return "Admin Does Not Exist";
        }
    }
}
