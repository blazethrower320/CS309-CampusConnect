package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Hello and welcome to COMS 309 \n Options:\n - Name/Section\n - Name\n -About\n - Add/Num1/Num2";
    }
    
    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello and welcome to COMS 309: " + name;
    }

    @GetMapping("/{name}/{section}")
    public String welcome(@PathVariable String name, @PathVariable String section) {
        return "Hello and welcome to COMS 309: " + name + " | Section: " + section; }

    @GetMapping("/about")
    public String about()
    {
        return "There is so much to learn about Springboot......";
    }

    @GetMapping("/add/{num1}/{num2}")
    public String add(@PathVariable String num1, @PathVariable String num2) {
        return num1 + " + " + num2 + " = " + Integer.toString (Integer.parseInt(num1) + Integer.parseInt(num2));
    }
    @GetMapping("/time")
    public String time(){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm");
        return "Local Time: " + LocalDateTime.now().format(format);
    }
}
