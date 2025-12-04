package CampusConnect;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.server.LocalServerPort;	// SBv3

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class PreetSystemTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }
/*
    @Test
    public void reverseTest() {
        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                body("hello").
                when().
                post("/reverse");


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body for correct response
        String returnString = response.getBody().asString();
        try {
            JSONArray returnArr = new JSONArray(returnString);
            JSONObject returnObj = returnArr.getJSONObject(returnArr.length()-1);
            assertEquals("olleh", returnObj.get("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
*/
    @Test
    public void sessionTutorTest() throws JSONException {
        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("""
                {
                    "tutorId": 2,
                    "className": "Physics",
                    "classCode": "Phys123",
                    "meetingLocation": "Carver",
                    "meetingTime": "12/02/2025 12:55 PM",
                    "dateCreated": "2025-10-29T19:00:00"
                }
                """).
                when().
                post("/sessions/createSession");


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body for correct response
        JSONObject obj = new JSONObject(response.getBody().asString());
        try {
            assertEquals(2, obj.getLong("tutorId"));
            assertEquals("Physics", obj.getString("className"));
            assertEquals("Phys123", obj.getString("classCode"));
            assertEquals("Carver", obj.getString("meetingLocation"));
            assertEquals("2025-10-29T19:00:00", obj.getString("meetingTime"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
