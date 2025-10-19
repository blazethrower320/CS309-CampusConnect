package CampusConnect;

import CampusConnect.Database.Models.Admins.Admins;
import CampusConnect.Database.Models.Admins.AdminsRepository;
import CampusConnect.Database.Models.Classes.Classes;
import CampusConnect.Database.Models.Classes.ClassesRepository;
import CampusConnect.Database.Models.SessionMembers.SessionMembers;
import CampusConnect.Database.Models.SessionMembers.SessionMembersRepository;
import CampusConnect.Database.Models.Sessions.Sessions;
import CampusConnect.Database.Models.Sessions.SessionsRepository;
import CampusConnect.Database.Models.Tutors.Tutor;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Users.UserRepository;
import com.mysql.cj.Session;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // Create 3 users with their machines
    /**
     * 
     * @param userRepository repository for the User entity
     * Creates a commandLine runner to enter dummy data into the database
     * As mentioned in User.java just associating the Laptop object with the User will save it into the database because of the CascadeType
     */
    @Bean
    CommandLineRunner initData(UserRepository userRepository, TutorRepository tutorRepository, ClassesRepository classesRepository, AdminsRepository adminsRepository, SessionMembersRepository sessionMembersRepository, SessionsRepository sessionsRepository) {
        return args -> {
            // Clear tables first
            tutorRepository.deleteAll();
            classesRepository.deleteAll();
            userRepository.deleteAll();
            adminsRepository.deleteAll();
            sessionsRepository.deleteAll();
            sessionMembersRepository.deleteAll();

            User user1 = new User( "JohnZeet", "password", false, false);
            User user2 = new User( "Zach", "password", false, false);
            User user3 = new User( "Chase", "password", true, false);


            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);

            Admins admin1 = new Admins(user1, "All");
            Admins admin2 = new Admins(user2, "All");

            adminsRepository.save(admin1);
            adminsRepository.save(admin2);

            Tutor tutor1 = new Tutor(user1.getUserId(), user1.getUsername(), 5, 3.2);

            tutorRepository.save(tutor1);

            Classes class1 = new Classes(1, 23, "Linear Algebra", "MATH207");
            Classes class2 = new Classes(2, 0, "Differential Equations", "MATH267");
            Classes class3 = new Classes(3, 17, "Calc 2", "MATH166");

            classesRepository.save(class1);
            classesRepository.save(class2);
            classesRepository.save(class3);


            Sessions session1 = new Sessions(
                    user1.getUserId(),
                    "Computer Science 309",
                    "COMS309",
                    "Pearson",
                    "3PM @ Friday",
                    tutor1.getTutorID()
            );

            sessionsRepository.save(session1);

            SessionMembers sessionMembers1 = new SessionMembers(user1.getUserId(), session1.getSessionId(), true);
            sessionMembersRepository.save(sessionMembers1);


        };
    }

    @Bean
    CommandLineRunner initClasses(ClassesRepository classesRepository) {
        return args -> {
            Classes class1 = new Classes(1, 23, "Linear Algebra", "MATH207");
            Classes class2 = new Classes(2, 0, "Differential Equations", "MATH267");
            Classes class3 = new Classes(3, 17, "Calc 2", "MATH166");

            classesRepository.deleteAll();
            classesRepository.save(class1);
            classesRepository.save(class2);
            classesRepository.save(class3);
        };
    }
}
