package CampusConnect.Database.Models.Sessions;


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
    private SessionsService sessionsService;
    @Autowired
    private SessionsRepository sessionsRepository;
    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private UserRepository userRepository;


    @GetMapping(path = "/sessions")
    public List<Sessions> getAllSessions() {
        return sessionsRepository.findAll();
    }


    @PostMapping("/sessions/joinSession/{username}/{sessionId}")
    public void joinSession(@PathVariable String username, @PathVariable long sessionId)
    {
        Sessions sessionExists = sessionsRepository.getSessionsBySessionId(sessionId);
        if(sessionExists == null)
        {
            throw new RuntimeException("Session Not Found");
        }
        if(!userRepository.existsByUsername(username)){
            throw new RuntimeException("User not found");
        }
        sessionsService.addUser(username, sessionId);


    }

    @PostMapping("/sessions/createSession")
    public Sessions createSession(@RequestBody SessionsDTO sessionsDTO)
    {
        return sessionsService.createSession(sessionsDTO);
    }

    @GetMapping("/sessions/getSession/{sessionId}")
    public ResponseEntity<Object> getSession(@PathVariable long sessionId)
    {
        Sessions session = sessionsRepository.findAllBySessionId(sessionId);
        if(session == null)
            return ResponseEntity.status(404).body("Session Not Found");
        return ResponseEntity.ok(session);
    }

    @GetMapping("/sessions/setMeetingTime")
    public boolean setSessionDate(@RequestBody SessionEditRequest edit)
    {
        Sessions session = sessionsRepository.getSessionsBySessionId(edit.getSessionId());
        if(session == null)
            return false;
        session.setMeetingTime(edit.getMeetingTime());
        sessionsRepository.save(session);
        return true;
    }
    @GetMapping("/sessions/getMeetingDate/{sessionId}")
    public String getMeetingTime(@PathVariable long sessionId)
    {
        return sessionsRepository.getSessionsBySessionId(sessionId).getMeetingTime();
    }
}
