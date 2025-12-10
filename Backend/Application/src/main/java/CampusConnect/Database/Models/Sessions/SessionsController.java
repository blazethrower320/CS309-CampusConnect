package CampusConnect.Database.Models.Sessions;


import CampusConnect.Database.Models.Classes.Classes;
import CampusConnect.Database.Models.Classes.ClassesRepository;
import CampusConnect.Database.Models.Tutors.Tutor;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Users.UserRepository;
import CampusConnect.WebSockets.Push.PushSocket;
import com.mysql.cj.Session;
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
    @Autowired
    private ClassesRepository classesRepository;

    @GetMapping(path = "/sessions")
    public List<Sessions> getSessions(){
        return sessionsRepository.findAll();
    }

    @GetMapping(path = "/sessions/inactive/{userId}")
    public List<Sessions> getPreviousSessions(@PathVariable long userId) {
        LocalDateTime currentTime = LocalDateTime.now();
        return sessionsRepository.findAll().stream()
                .filter(s -> {
                    try {
                        if (s.getMeetingTime() == null) return false;
                        boolean isPastOrNow = s.getMeetingTime().isBefore(currentTime) || s.getMeetingTime().isEqual(currentTime);
                        boolean containsUser = s.getUsers().stream().anyMatch(u -> u.getUserId() == userId);
                        return isPastOrNow && containsUser;
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

    @PostMapping("/sessions/leaveSession/{userId}/{sessionId}")
    public void leaveSession(@PathVariable long userId, @PathVariable long sessionId){
        sessionsService.removeUser(userId, sessionId);
    }

    @PostMapping("/sessions/createSession")
    public Sessions createSession(@RequestBody SessionsDTO sessionsDTO)
    {
        return sessionsService.createSession(sessionsDTO);
    }

    @PostMapping("/sessions/deleteSession/{sessionId}")
    public void deleteSession(@PathVariable long sessionId){
        Sessions session = sessionsRepository.getSessionsBySessionId(sessionId);
        if(session == null){
            throw new RuntimeException("No session found");
        }
        sessionsService.deleteSession(sessionId);
    }

    @PutMapping("/sessions/editSession/{sessionId}")
    public Sessions editSession(@RequestBody SessionsDTO sessionDTO, @PathVariable long sessionId){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        LocalDateTime meetingTime = LocalDateTime.parse(sessionDTO.getMeetingTime(), format);

        Sessions session = sessionsRepository.findById(sessionId);
        Tutor setTutor = tutorRepository.findById(sessionDTO.getTutorId()).orElseThrow(()-> new RuntimeException("Tutor not found"));

        return sessionsService.editSession(sessionDTO,sessionId);
    }
}
