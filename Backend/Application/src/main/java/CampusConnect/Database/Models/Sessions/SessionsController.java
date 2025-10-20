package CampusConnect.Database.Models.Sessions;

import CampusConnect.Database.Models.SessionMembers.SessionMembers;
import CampusConnect.Database.Models.SessionMembers.SessionMembersRepository;
import CampusConnect.Database.Models.Tutors.Tutor;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Database.Models.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class SessionsController
{
    @Autowired
    SessionsRepository sessionsRepository;
    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private SessionMembersRepository participantsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionMembersRepository sessionMembersRepository;

    @GetMapping(path = "/sessions")
    public List<Sessions> getAllSessions() {
        return sessionsRepository.findAll();
    }


    @PostMapping("/sessions/joinSession")
    public ResponseEntity<Object> joinSession(@RequestBody Sessions session)
    {
        Sessions sessionExists = sessionsRepository.getSessionsBySessionId(session.getSessionId());
        if(sessionExists == null)
        {
            return ResponseEntity.status(404).body("Session Not found");
        }

        SessionMembers newMember = new SessionMembers
                (
                        session.getUserId(),
                        sessionExists.getSessionId(),
                        false
                );

        sessionMembersRepository.save(newMember);
        return ResponseEntity.ok(newMember);
    }

    @PostMapping("/sessions/createSession")
    public ResponseEntity<Object> createSession(@RequestBody Sessions session)
    {
        // A group session
        Tutor tutor = tutorRepository.getTutorByTutorId(session.getTutorId());
        if(tutor == null)
            return ResponseEntity.status(400).body("Tutor Not found");



        Sessions newSession = new Sessions
                (
                        session.getUserId(),
                        session.getClassName(),
                        session.getClassCode(),
                        session.getMeetingLocation(),
                        session.getMeetingTime(),
                        tutor.getTutorID(),
                        LocalDateTime.now()
                );
        sessionsRepository.save(newSession);

        SessionMembers newMember = new SessionMembers
                (
                        newSession.getUserId(),
                        newSession.getSessionId(),
                        true
                );
        sessionMembersRepository.save(newMember);

        return ResponseEntity.ok(newSession);
    }

    @GetMapping("/sessions/getSession/{sessionId}")
    public ResponseEntity<Object> getSession(@PathVariable long sessionId)
    {
        Sessions session = sessionsRepository.findAllBySessionId(sessionId);
        if(session == null)
            return ResponseEntity.status(404).body("Session Not Found");
        return ResponseEntity.ok(session);
    }
}
