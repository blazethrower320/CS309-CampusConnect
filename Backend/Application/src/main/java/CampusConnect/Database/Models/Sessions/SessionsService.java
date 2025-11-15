package CampusConnect.Database.Models.Sessions;

import CampusConnect.Database.Models.Tutors.Tutor;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SessionsService {

    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionsRepository sessionsRepository;

    public Sessions createSession(SessionsDTO sessionDTO) {
        Tutor tutor = tutorRepository.findById(sessionDTO.getTutorId())
                .orElseThrow(() -> new RuntimeException("Tutor not found"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy h:mm a");

        Sessions session = new Sessions(
                tutor,
                sessionDTO.getClassName(),
                sessionDTO.getClassCode(),
                sessionDTO.getMeetingLocation(),
                LocalDateTime.parse(sessionDTO.getMeetingTime(), formatter),
                sessionDTO.getDateCreated()
        );

        return sessionsRepository.save(session);
    }

    public void addUser(String username, long sessionId){
        Sessions newSession = sessionsRepository.getSessionsBySessionId(sessionId);
        if (newSession == null){
            throw new RuntimeException("Session not found");
        }
        User newUser = userRepository.findByUsername(username);
        if(!userRepository.existsByUsername(username)){
            throw new RuntimeException("User not found");
        }
        newSession.addUser(newUser);
        sessionsRepository.save(newSession);
    }

    public void removeUser(long userId, long sessionId){
        Sessions session = sessionsRepository.findBySessionId(sessionId);
        if(session == null) {
            throw new RuntimeException("Session Not Found");
        }

        User user = userRepository.getUserByUserId(userId);
        if(user == null){
            throw new RuntimeException("User Not Found");
        }

        session.removeUser(user);
        sessionsRepository.save(session);
    }


}
