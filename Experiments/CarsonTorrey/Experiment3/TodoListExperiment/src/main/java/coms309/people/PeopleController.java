package coms309.people;

import coms309.Todo.Todo;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Vivek Bengre
 */

@RestController
public class PeopleController {

    // Note that there is only ONE instance of PeopleController in 
    // Springboot system.
    HashMap<String, Person> peopleList = new  HashMap<>();

    //CRUDL (create/read/update/delete/list)
    // use POST, GET, PUT, DELETE, GET methods for CRUDL

    // THIS IS THE LIST OPERATION
    // gets all the people in the list and returns it in JSON format
    // This controller takes no input. 
    // Springboot automatically converts the list to JSON format 
    // in this case because of @ResponseBody
    // Note: To LIST, we use the GET method
    @GetMapping("/people")
    public  HashMap<String,Person> getAllPersons() {
        return peopleList;
    }

    // THIS IS THE CREATE OPERATION
    // springboot automatically converts JSON input into a person object and 
    // the method below enters it into the list.
    // It returns a string message in THIS example.
    // Note: To CREATE we use POST method
    @PostMapping("/people")
    public  String createPerson(@RequestBody Person person) {
        System.out.println(person);
        peopleList.put(person.getFirstName(), person);
        String s = "New person "+ person.getFirstName() + " Saved";
        return s;
        //public  ResponseEntity<Map<String, String>>  //unused
        // createPerson(@RequestBody Person person) { // unused
        //Map <String, String> body = new HashMap<>();// unused
        //body.put("message", s); // unused
        //ResponseEntity<>(body, HttpStatus.OK); // unused
    }

    // THIS IS THE READ OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We extract the person from the HashMap.
    // springboot automatically converts Person to JSON format when we return it
    // Note: To READ we use GET method
    @GetMapping("/people/{firstName}")
    public Person getPerson(@PathVariable String firstName) {
        Person p = peopleList.get(firstName);
        return p;
    }

    // THIS IS A GET METHOD
    // RequestParam is expected from the request under the key "name"
    // returns all names that contains value passed to the key "name"
    @GetMapping("/people/contains")
    public List<Person> getPersonByParam(@RequestParam("name") String name) {
        List<Person> res = new ArrayList<>(); 
        for (Person p : peopleList.values()) {
            if (p.getFirstName().contains(name) || p.getLastName().contains(name))
                res.add(p);
        }
        return res;
    }

    // THIS IS THE UPDATE OPERATION
    // We extract the person from the HashMap and modify it.
    // Springboot automatically converts the Person to JSON format
    // Springboot gets the PATHVARIABLE from the URL
    // Here we are returning what we sent to the method
    // Note: To UPDATE we use PUT method
    @PutMapping("/people/{firstName}")
    public Person updatePerson(@PathVariable String firstName, @RequestBody Person p) {
        peopleList.replace(firstName, p);
        return peopleList.get(firstName);
    }


    // THIS IS THE DELETE OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We return the entire list -- converted to JSON
    // Note: To DELETE we use delete method
    
    @DeleteMapping("/people/{firstName}")
    public HashMap<String, Person> deletePerson(@PathVariable String firstName) {
        peopleList.remove(firstName);
        return peopleList;
    }



    @PostMapping("/people/clone/{firstName}/{newFirstName}")
    public HashMap<String, Person> clone(@PathVariable String firstName, @PathVariable String newFirstName) {
        Person clonePerson = new Person();
        for(Person p : peopleList.values())
        {
            if(p.getFirstName().equals(firstName))
            {
                clonePerson = p;
                break;
            }
        }
        var newPerson = new Person();
        newPerson.setFirstName(newFirstName);
        newPerson.setLastName(clonePerson.getLastName());
        newPerson.setTelephone(clonePerson.getTelephone());
        newPerson.setAddress((clonePerson.getAddress()));

        peopleList.put(newPerson.getFirstName(), newPerson);
        return peopleList;
    }

    @DeleteMapping("/people/deleteall")
    public HashMap<String, Person> deleteEveryone()
    {
        peopleList.clear();
        return peopleList;
    }

    @GetMapping("/people/findnumber/{number}")
    public Person getPersonByNumber(@PathVariable String number)
    {
        Person foundPerson = new Person();
        for(Person p : peopleList.values())
        {
            if(p.getTelephone().equals((number)))
            {
                foundPerson = p;
                break;
            }
        }
        return foundPerson;
    }
    @GetMapping("/people/lastName/{lastName}")
    public Person getPersonByLastName(@PathVariable String lastName)
    {
        Person foundPerson = new Person();
        for(Person p : peopleList.values())
        {
            if(p.getLastName().equals((lastName)))
            {
                foundPerson = p;
                break;
            }
        }
        return foundPerson;
    }
} // end of people controller

