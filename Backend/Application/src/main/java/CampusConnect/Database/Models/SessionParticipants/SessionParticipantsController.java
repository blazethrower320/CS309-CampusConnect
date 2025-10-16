package CampusConnect.Database.Models.SessionParticipants;

import CampusConnect.Database.Models.Sessions.SessionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionParticipantsController
{
    @Autowired
    SessionParticipantsRepository sessionParticipantsRepositoryRepository;
}
