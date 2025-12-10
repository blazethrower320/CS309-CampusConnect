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
public class SessionsSystemTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void sessionCreateTest() throws JSONException {
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
            assertEquals("Physics", obj.getString("className"));
            assertEquals("Phys123", obj.getString("classCode"));
            assertEquals("Carver", obj.getString("meetingLocation"));
            assertEquals("12/02/2025 12:55 PM", obj.getString("meetingTime"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void editSession() throws JSONException {
        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("""
                {
                    "tutorId": 2,
                    "className": "Development",
                    "classCode": "Coms309",
                    "meetingLocation": "SIC",
                    "meetingTime": "12/03/2025 02:55 PM",
                    "dateCreated": "2025-12-07T19:00:00"
                }
                """).
                when().
                put("/sessions/editSession/2");


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body for correct response
        JSONObject obj = new JSONObject(response.getBody().asString());
        try {
            assertEquals("Development", obj.getString("className"));
            assertEquals("Coms309", obj.getString("classCode"));
            assertEquals("SIC", obj.getString("meetingLocation"));
            assertEquals("12/03/2025 2:55 PM", obj.getString("meetingTime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void deleteSession() throws JSONException {
        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("""
                {
                    "tutorId": 2,
                    "className": "Development",
                    "classCode": "Coms309",
                    "meetingLocation": "SIC",
                    "meetingTime": "12/03/2025 02:55 PM",
                    "dateCreated": "2025-12-07T19:00:00"
                }
                """).
                when().
                put("/sessions/editSession/2");

        Response response2 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                post("/sessions/deleteSession/1");

        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);
    }

    @Test
    public void addUser() throws JSONException {
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
                    "meetingTime": "12/09/2025 12:55 PM",
                    "dateCreated": "2025-10-29T19:00:00"
                }
                """).
                when().
                post("/sessions/createSession");

        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        Response response2 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("""
                {
                    "tutorId": 1,
                    "className": "Math",
                    "classCode": "Math123",
                    "meetingLocation": "IE",
                    "meetingTime": "12/09/2025 12:55 PM",
                    "dateCreated": "2025-10-29T19:00:00"
                }
                """).
                when().
                post("/sessions/createSession");

        // Check status code
        int statusCode2 = response2.getStatusCode();
        assertEquals(200, statusCode2);

        Response response3 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("""
                {
                    "username": "Chet",
                    "password": "1234",
                    "isTutor": false,
                    "isAdmin": false
                }
                """).
                when().
                post("/users/createUser");
        
        // Check status code
        int statusCode3 = response3.getStatusCode();
        assertEquals(200, statusCode3);

        Response response4 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                post("/sessions/joinSession/Chet/2");

        // Check status code
        int statusCode4 = response4.getStatusCode();
        assertEquals(200, statusCode4);

        Response response15 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/sessions");

        // Check status code
        int statusCode15 = response15.getStatusCode();
        assertEquals(200, statusCode15);

        Response response14 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/sessions/getSessionTutor/2");

        // Check status code
        int statusCode14 = response14.getStatusCode();
        assertEquals(200, statusCode14);

        Response response13 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/sessions/users/2");

        // Check status code
        int statusCode13 = response13.getStatusCode();
        assertEquals(200, statusCode13);

        Response response16 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/sessions/user/499"); //UserID

        // Check status code
        int statusCode16 = response16.getStatusCode();
        assertEquals(200, statusCode16);

        Response response5 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/sessions/active");

        // Check status code
        int statusCode5 = response5.getStatusCode();
        assertEquals(200, statusCode5);


        String body = response5.getBody().asString();   // extract JSON string
        JSONArray sessionsArray = new JSONArray(body); // now parse JSON array
        for (int i = 0; i < sessionsArray.length(); i++) {
            JSONObject session = sessionsArray.getJSONObject(i);
            if(i==0) {
                try {
                    assertEquals("Physics", session.getString("className"));
                    assertEquals("Phys123", session.getString("classCode"));
                    assertEquals("Carver", session.getString("meetingLocation"));
                    assertEquals("12/09/2025 12:55 PM", session.getString("meetingTime"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(i==1){
                try {
                    assertEquals("Math", session.getString("className"));
                    assertEquals("Math123", session.getString("classCode"));
                    assertEquals("IE", session.getString("meetingLocation"));
                    assertEquals("12/09/2025 12:55 PM", session.getString("meetingTime"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Response response6 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/sessions/inactive");

        // Check status code
        int statusCode6 = response6.getStatusCode();
        assertEquals(200, statusCode6);

        Response response7 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                body("""
                        {
                         "tutorId": 2,
                         "className" : "NewComs",
                         "classCode" : "COMs309",
                         "meetingLocation": "Pearson",
                         "meetingTime": "12/03/2025 02:55 PM",
                         "dateCreated": "2025-10-29T19:00:00"
                        }
                        """).
                put("/sessions/editSession/2");

        int statusCode7 = response7.getStatusCode();
        assertEquals(200, statusCode7);




        Response response8 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                body("""
                        {
                        "username" : "Chet",
                        "password" : "1234"
                        }
                        """).
                post("/sessions/leaveSession/499/2");   //USERID

        int statusCode8 = response8.getStatusCode();
        assertEquals(200, statusCode8);

        Response response9 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                body("""
                        {
                        "username" : "Chet",
                        "password" : "1234"
                        }
                        """).
                post("/users/deleteUser");

        int statusCode9 = response9.getStatusCode();
        assertEquals(200, statusCode9);


        Response response11 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                post("sessions/deleteSession/297");

        int statusCode11 = response11.getStatusCode();
        assertEquals(200, statusCode11);


    }
}
