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
public class TutorSystemTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void createTutor() throws JSONException {
        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                post("/tutors/createTutor/jim");

        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Send request and receive response
        Response response2 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/tutors");

        // Check status code
        int statusCode2 = response2.getStatusCode();
        assertEquals(200, statusCode2);

        // Send request and receive response
        Response response3 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/tutors/info/2");

        // Check status code
        int statusCode3 = response3.getStatusCode();
        assertEquals(200, statusCode3);


        // Check response body for correct response
        JSONObject obj = new JSONObject(response3.getBody().asString());
        try {
            assertEquals("Will", obj.getString("username"));
            assertEquals(2.0, obj.getDouble("totalRating"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send request and receive response
        Response response4 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/tutors/getClasses/2");

        // Check status code
        int statusCode4 = response4.getStatusCode();
        assertEquals(200, statusCode4);

        // Send request and receive response
        Response response5 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/tutors/getTutorRating/2");

        // Check status code
        int statusCode5 = response5.getStatusCode();
        assertEquals(200, statusCode5);

        // Send request and receive response
        Response response7 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/tutors/getTutorFromUserId/4");

        // Check status code
        int statusCode7 = response7.getStatusCode();
        assertEquals(200, statusCode7);

        // Send request and receive response
        Response response6 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                post("/tutors/deleteTutor/jim");

        // Check status code
        int statusCode6 = response6.getStatusCode();
        assertEquals(200, statusCode6);

        // Send request and receive response
        Response response8 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                patch("/tutors/addClass/2/1");

        // Check status code
        int statusCode8 = response8.getStatusCode();
        assertEquals(200, statusCode8);

        // Send request and receive response
        Response response9 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                post("/tutors/removeClass/2/1");

        // Check status code
        int statusCode9 = response9.getStatusCode();
        assertEquals(200, statusCode9);


    }
}
