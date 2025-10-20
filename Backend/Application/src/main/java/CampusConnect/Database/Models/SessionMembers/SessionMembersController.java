package CampusConnect.Database.Models.SessionMembers;

import CampusConnect.Database.Models.Sessions.Sessions;
import CampusConnect.Database.Models.Sessions.SessionsRepository;
import CampusConnect.Database.Models.Tutors.Tutor;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SessionMembersController
{
    @Autowired
    SessionMembersRepository sessionMemberRepository;
    @Autowired
    SessionsRepository sessionsRepository;
    @Autowired
    TutorRepository tutorRepository;


    @GetMapping("/sessions/getAllSessionMembers/{sessionId}")
    public List<SessionMembers> GetAllSessionMembers(@PathVariable long sessionId)
    {
        List<SessionMembers> members = sessionMemberRepository.findAllBySessionId(sessionId);
        return members;
    }

    @GetMapping("/sessions/getSessionTutor/{sessionId}")
    public ResponseEntity<Object> getSessionTutor(@PathVariable long sessionId)
    {
        Sessions session = sessionsRepository.getSessionsBySessionId(sessionId);
        if(session == null)
        {
            return ResponseEntity.status(400).body("Session not found");
        }


        Tutor tutor = tutorRepository.getTutorByTutorId(session.getTutorId());
        String username = tutor.getUser().getUsername();

        if(tutor == null)
        {
            return ResponseEntity.status(401).body("Tutor not found");
        }


        return ResponseEntity.ok(tutor);
    }
}
