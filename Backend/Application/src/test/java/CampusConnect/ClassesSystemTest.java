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
public class ClassesSystemTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void ClassesTest() throws JSONException {
        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/classes");

        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Send request and receive response
        Response response2 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/classes/1");

        // Check status code
        int statusCode2 = response2.getStatusCode();
        assertEquals(200, statusCode2);

        // Send request and receive response
        Response response3 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                body("""
                        {
                        "className" : "Hardware",
                        "classCode" : "CPRE488"
                        }
                        """
                ).
                post("/classes/create");

        // Check status code
        int statusCode3 = response3.getStatusCode();
        assertEquals(200, statusCode3);

        // Send request and receive response
        Response response4 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                post("/classes/delete/10");

    }
}
