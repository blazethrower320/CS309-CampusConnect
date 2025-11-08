package CampusConnect.Database.Models.Sessions;


import CampusConnect.Database.Models.Tutors.Tutor;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Users.UserRepository;
import CampusConnect.WebSockets.Push.PushSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

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
    public List<Sessions> getSessions(){
        return sessionsRepository.findAll();
    }

    @GetMapping(path = "/sessions/inactive")
    public List<Sessions> getPreviousSessions() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        return sessionsRepository.findAll().stream()
                .filter(s -> {
                    try {
                        if (s.getMeetingTime() == null) return false;
                        return s.getMeetingTime().isBefore(currentTime) || s.getMeetingTime().isEqual(currentTime);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();
    }

    @GetMapping(path = "/sessions/active")
    public List<Sessions> getCurrentSessions() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        return sessionsRepository.findAll().stream()
                .filter(s -> {
                    try {
                        if (s.getMeetingTime() == null) return false;
                        return s.getMeetingTime().isAfter(currentTime);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();
    }

    @GetMapping("/sessions/users/{sessionId}")
    public Set<User> getAllUsers(@PathVariable long sessionId){
        Set<User> users = sessionsRepository.findById(sessionId).getUsers();
        if (users != null){
            return users;
        }
        else throw new RuntimeException("No users found");
    }

    @GetMapping("/sessions/user/{userId}")
    public List<Sessions> getSessionsForUser(@PathVariable long userId){
        List<Sessions> allSessions = sessionsRepository.findAllByUsers_UserId(userId);
        return allSessions;
    }

    @GetMapping("/sessions/getSessionTutor/{sessionId}")
    public Tutor getSessionTutor(@PathVariable long sessionId){
        Tutor tutor = sessionsRepository.findById(sessionId).getTutor();
        return tutor;
    }

    @PostMapping("/sessions/joinSession/{username}/{sessionId}")
    public Sessions joinSession(@PathVariable String username, @PathVariable long sessionId) {

        Sessions session = sessionsRepository.findBySessionId(sessionId);
        if (session == null) {
            throw new RuntimeException("Session Not Found");
        }


        User user = userRepository.findByUsername(username);    
        if (user == null) {
            throw new RuntimeException("User Not Found");
        }

        sessionsService.addUser(username, sessionId);
        sessionsRepository.save(session);

        Long tutorId = sessionsRepository.getSessionsBySessionId(sessionId).getTutor().getTutorId();
        String message =  user.getUsername() + " joined your study session: " + session.getClassName();
        PushSocket.sendNotificationToTutor(tutorId, message);

        return session;
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

    @PatchMapping("/sessions/setMeetingTime/{time}/{sessionId}")
    public Sessions setTime(@PathVariable String time, @PathVariable long sessionId)
    {
        Sessions session = sessionsRepository.getSessionsBySessionId(sessionId);
        if(session == null){
            throw new RuntimeException("Session not found");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        try {
            LocalDateTime meetingTime = LocalDateTime.parse(time, formatter);
            session.setMeetingTime(meetingTime);
            return sessionsRepository.save(session);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid date/time format. Use MM/dd/yyyy hh:mm a");
        }
    }

    @PatchMapping("/sessions/setMeetingLocation/{location}/{sessionId}")
    public Sessions setMeetingLocation(@PathVariable String location, @PathVariable long sessionId){
        Sessions session = sessionsRepository.getSessionsBySessionId(sessionId);
        if(session == null){
            throw new RuntimeException("Session not found");
        }
        session.setMeetingLocation(location);
        return sessionsRepository.save(session);
    }

    @GetMapping("/sessions/getMeetingDate/{sessionId}")
    public String getMeetingTime(@PathVariable long sessionId)
    {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

        Sessions session = sessionsRepository.getSessionsBySessionId(sessionId);
        if(session == null || session.getMeetingTime() == null) {
            throw new RuntimeException("Meeting time does not exist");
        }
        return session.getMeetingTime().format(format);
    }
}
