package CampusConnect.Database.Models.Messages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
public class PrivateMessagesController
{
    @Autowired
    PrivateMessagesRepository privateMessagesRepository;

    @GetMapping(path = "/privateMessages")
    public List<PrivateMessages> getAllMessages() {
        return privateMessagesRepository.findAll();
    }
}
