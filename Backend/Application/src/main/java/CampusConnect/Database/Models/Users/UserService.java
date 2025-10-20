package CampusConnect.Database.Models.Users;

import CampusConnect.Database.Models.Users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;



    public void updateMajor(String username, String major){
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found.");
        }
        user.setMajor(major);
    }

    public void updateBio(String username, String bio){
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new RuntimeException("User not found");
        }
        user.setBio(bio);
    }

    public void updateClassification(String username, String classification){
        User user = userRepository.findByUsername(username);
        if(user == null){
            throw new RuntimeException("User not found");
        }
        user.setClassification(classification);
    }

}
