package CampusConnect.Database.Models.Sessions;

import CampusConnect.Database.Models.Ratings.Ratings;
import CampusConnect.Database.Models.Ratings.RatingsRepository;
import CampusConnect.Database.Models.SessionParticipants.SessionParticipants;
import CampusConnect.Database.Models.SessionParticipants.SessionParticipantsRepository;
import CampusConnect.Database.Models.Tutors.Tutor;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Database.Models.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SessionsController
{
    @Autowired
    SessionsRepository sessionsRepository;
    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private SessionParticipantsRepository participantsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionParticipantsRepository sessionParticipantsRepository;

    @GetMapping(path = "/sessions")
    public List<Sessions> getAllSessions() {
        return sessionsRepository.findAll();
    }

    /*
    @PostMapping("/sessions/joinSession")
    public ResponseEntity<Object> joinSession(@RequestBody Sessions session)
    {
        Sessions sessionExists = sessionsRepository.findAllBySessionId(session.getId());
        if(sessionExists == null)
        {
            return ResponseEntity.status(404).body("Session Not found");
            // something, not found
        }

        // A group session
        Sessions newSession = new Sessions
                (
                        session.getUserId(),
                        sessionExists.getClassName(),
                        sessionExists.getClassCode(),
                        sessionExists.getMeetingLocation(),
                        sessionExists.getMeetingTime(),
                        sessionExists.getTutorId(),
                        false
                );
        nextSessionId++;
        sessionsRepository.save(newSession);
        return ResponseEntity.ok(newSession);
    }
    */
    @PostMapping("/sessions/createSession")
    public ResponseEntity<Object> createSession(@RequestBody Sessions session)
    {
        // A group session
        Tutor tutor = tutorRepository.getTutorByTutorId(session.getTutorId());
        Sessions newSession = new Sessions
                (
                        session.getUserId(),
                        session.getClassName(),
                        session.getClassCode(),
                        session.getMeetingLocation(),
                        session.getMeetingTime(),
                        tutor.getTutorID()
                );
        sessionsRepository.save(newSession);

        System.out.println(newSession.getId());

        SessionParticipants newMember = new SessionParticipants
                (
                        newSession.getUserId(),
                        newSession.getId(),
                        true
                );
        sessionParticipantsRepository.save(newMember);

        return ResponseEntity.ok(newSession);
    }
}
