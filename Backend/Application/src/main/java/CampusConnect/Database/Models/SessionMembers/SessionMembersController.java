package CampusConnect.Database.Models.SessionMembers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionMembersController
{
    @Autowired
    SessionMembersRepository sessionParticipantsRepositoryRepository;
}
