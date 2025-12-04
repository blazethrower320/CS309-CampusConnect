package CampusConnect.Database.Models.Classes;

import CampusConnect.Database.Models.Sessions.Sessions;
import CampusConnect.Database.Models.Sessions.SessionsDTO;
import CampusConnect.Database.Models.Sessions.SessionsRepository;
import CampusConnect.Database.Models.Tutors.Tutor;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClassesService {

    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private SessionsRepository sessionsRepository;
    @Autowired
    private ClassesRepository classesRepository;

    public void deleteClass(long classId) {
        ArrayList<Tutor> tutors = tutorRepository.findAllByClasses_ClassId(classId);
        Classes givenClass = classesRepository.findById(classId).orElseThrow(() -> new RuntimeException("Class not found"));
        for (Tutor tutor : tutors){
            tutor.removeClass(givenClass);
        }

       /* List<Sessions> sessions = sessionsRepository.findAllByClassEntity_ClassId(classId);
        for(Sessions session: sessions){
            session.removeClass(givenClass);
        }
*/
        classesRepository.deleteById(classId);
    }



}
