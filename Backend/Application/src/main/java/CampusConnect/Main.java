package CampusConnect;

import CampusConnect.Database.Models.Classes.Classes;
import CampusConnect.Database.Models.Classes.ClassesRepository;
import CampusConnect.Database.Models.Users.User;
import CampusConnect.Database.Models.Users.UserRepository;
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
    CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {
            userRepository.deleteAll();
            User user1 = new User("John", "zeet", "JohnZeet", "password");
            User user2 = new User("Zach", "wehet", "Zach", "password");
            User user3 = new User("Chase", "woodle", "Chase", "password");

            // This is like INSERT

            Classes class1 = new Classes(1, 23, "Linear Algebra", "MATH207");
            Classes class2 = new Classes(2, 0, "Differential Equations", "MATH267");
            Classes class3 = new Classes(3, 17, "Calc 2", "MATH166");


            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);


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
