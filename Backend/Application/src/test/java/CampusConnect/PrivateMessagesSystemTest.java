package CampusConnect;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import CampusConnect.Database.Models.Messages.PrivateMessages;
import CampusConnect.Database.Models.Messages.PrivateMessagesRepository;
import CampusConnect.Database.Models.Ratings.RatingsRepository;
import CampusConnect.Database.Models.Tutors.TutorRepository;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class PrivateMessagesSystemTest {

    @LocalServerPort
    int port;
    @Autowired
    private PrivateMessagesRepository privateMessagesRepository;
    @Before
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        privateMessagesRepository.deleteAll();
    }
    @Test
    public void getAllMessagesTest() throws JSONException {
        privateMessagesRepository.deleteAll();

        privateMessagesRepository.save(new PrivateMessages(
                101, 102, "Check out the latest session notes!", "Alice", "Bob"
        ));

        privateMessagesRepository.save(new PrivateMessages(
                102, 101, "Somethign random", "Bob", "Alice"
        ));

        Response response = RestAssured.given()
                .contentType("application/json")
                .get("/privateMessages");

        assertEquals(200, response.statusCode());
        List<String> messages = response.jsonPath().getList("message");
        assertEquals(2, messages.size());
        assertTrue(messages.contains("Check out the latest session notes!"));
        assertTrue(messages.contains("Somethign random"));

        privateMessagesRepository.deleteAll();
    }

    @Test
    public void getMessagesForUserTest() throws JSONException {
        privateMessagesRepository.deleteAll();
        privateMessagesRepository.save(new PrivateMessages(
                201, 202, "hi 1", "TargetUser", "ConverseUser"
        ));

        privateMessagesRepository.save(new PrivateMessages(
                202, 201, "hi 2", "ConverseUser", "TargetUser"
        ));

        privateMessagesRepository.save(new PrivateMessages(
                203, 202, "hi 3.", "NoiseUser", "ConverseUser"
        ));

        Response response = RestAssured.given()
                .contentType("application/json")
                .pathParam("userId", 201)
                .get("/privateMessages/user/{userId}");

        assertEquals(200, response.statusCode());
        List<String> messages = response.jsonPath().getList("message");

        assertEquals(2, messages.size());

        assertTrue(messages.contains("hi 1"));
        assertTrue(messages.contains("hi 2"));

        privateMessagesRepository.deleteAll();
    }


}