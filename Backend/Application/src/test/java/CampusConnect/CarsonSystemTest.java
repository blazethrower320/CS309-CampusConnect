package CampusConnect;
import static org.junit.jupiter.api.Assertions.assertEquals;

import CampusConnect.Database.Models.Ratings.Ratings;
import CampusConnect.Database.Models.Ratings.RatingsRepository;
import CampusConnect.Database.Models.Users.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class CarsonSystemTest {

    @Autowired
    RatingsRepository ratingRepository;

    @LocalServerPort
    int port;
    @Autowired
    private RatingsRepository ratingsRepository;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        ratingsRepository.deleteAll();
    }



    @Test
    public void getUserByFirstName() throws JSONException {

        JSONObject deleteBody = new JSONObject();
        deleteBody.put("username", "Test1");
        RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", "Test1");
        requestBody.put("password", "password");
        requestBody.put("isTutor", false);
        requestBody.put("firstName", "First");
        requestBody.put("lastName", "Test");
        requestBody.put("isAdmin", false);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(requestBody.toString())
                .post("/users/createUser");

        assertEquals(200, response.getStatusCode());
        assertEquals("User created successfully", response.asString());

        JSONObject login = new JSONObject();
        login.put("username", "Test1");
        login.put("password", "password");
        Response loginResponse = RestAssured.given()
                .contentType("application/json")
                .body(login.toString())
                .post("/users/login");

        Response finalResponse = RestAssured.given()
                .contentType("application/json")
                .pathParam("name", "First")
                .get("/users/findUser/{name}");

        if(finalResponse.asString() != null && !finalResponse.asString().isEmpty() && !finalResponse.asString().equals("null"))
        {
            assertEquals(200, finalResponse.getStatusCode());
            assertEquals("Test1", finalResponse.jsonPath().getString("username"));
        }
    }





    @Test
    public void getUserByIdTest() throws JSONException {

        JSONObject deleteBody = new JSONObject();
        deleteBody.put("username", "Test1");
        RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", "Test1");
        requestBody.put("password", "password");
        requestBody.put("isTutor", false);
        requestBody.put("isAdmin", false);
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(requestBody.toString())
                .post("/users/createUser");

        assertEquals(200, response.getStatusCode());
        assertEquals("User created successfully", response.asString());
        JSONObject login = new JSONObject();
        login.put("username", "Test1");
        login.put("password", "password");
        Response loginResponse = RestAssured.given()
                .contentType("application/json")
                .body(login.toString())
                .post("/users/login");

        int userId = loginResponse.jsonPath().getInt("userId");
        Response finalResponse = RestAssured.given()
                .contentType("application/json")
                .pathParam("id", userId)
                .get("/users/{id}");
        assertEquals(200, finalResponse.getStatusCode());
        assertEquals("Test1", finalResponse.jsonPath().getString("username"));
        assertEquals("password", finalResponse.jsonPath().getString("password"));
    }


    @Test
    public void createUserTest() throws JSONException {

        JSONObject deleteBody = new JSONObject();

        deleteBody.put("username", "Joe");
        RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", "Joe");
        requestBody.put("password", "password");
        requestBody.put("isTutor", false);
        requestBody.put("isAdmin", false);
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(requestBody.toString())
                .post("/users/createUser");
        assertEquals(200, response.statusCode());
        assertEquals("User created successfully", response.getBody().asString());
    }


    @Test
    public void deleteUserTest() throws JSONException {

        JSONObject createBody = new JSONObject();
        createBody.put("username", "Joe2");
        createBody.put("password", "password");
        createBody.put("isTutor", false);
        createBody.put("isAdmin", false);

        RestAssured.given()
                .contentType("application/json")
                .body(createBody.toString())
                .post("/users/createUser");

        JSONObject deleteBody = new JSONObject();
        deleteBody.put("username", "Joe2");

        Response deleteResponse = RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");

        assertEquals(200, deleteResponse.statusCode());
        assertEquals("Deleted User", deleteResponse.getBody().asString());
    }

    @Test
    public void duplicateUserTestt() throws JSONException {
        JSONObject createBody = new JSONObject();
        createBody.put("username", "joe4");
        createBody.put("password", "password");
        createBody.put("isTutor", false);
        createBody.put("isAdmin", false);

        RestAssured.given()
                .contentType("application/json")
                .body(createBody.toString())
                .post("/users/createUser");
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(createBody.toString())
                .post("/users/createUser");
        assertEquals(400, response.statusCode());
        assertEquals("Username already exists", response.getBody().asString());
    }

    @Test
    public void loginTest() throws JSONException {
        JSONObject createBody = new JSONObject();
        createBody.put("username", "joe3");
        createBody.put("password", "password");
        createBody.put("isTutor", false);
        createBody.put("isAdmin", false);
        RestAssured.given()
                .contentType("application/json")
                .body(createBody.toString())
                .post("/users/createUser");
        JSONObject loginBody = new JSONObject();
        loginBody.put("username", "joe3");
        loginBody.put("password", "password");

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(loginBody.toString())
                .post("/users/login");

        assertEquals(200, response.statusCode());
        assertEquals("joe3", response.jsonPath().getString("username"));
    }




    // Rating Test

    @Test
    public void createRatingTest() throws JSONException {

        JSONObject createBody = new JSONObject();
        createBody.put("rating", 3);
        createBody.put("comments", "Very Good");
        createBody.put("userId", 2);
        createBody.put("tutorId", 1);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(createBody.toString())
                .post("/ratings/createRating");
        assertEquals(true, response.as(Boolean.class));

        ratingRepository.deleteAll();
    }

    @Test
    public void deleteRatingTest() throws JSONException {
        JSONObject createBody = new JSONObject();
        createBody.put("rating", 3);
        createBody.put("comments", "Very Good");
        createBody.put("userId", 2);
        createBody.put("tutorId", 1);

        Response ratingResponse = RestAssured.given()
                .contentType("application/json")
                .body(createBody.toString())
                .post("/ratings/createRating");

        Response getRatingsResponse = RestAssured.given()
                .get("/ratings");
        List<Map<String, Object>> ratings = getRatingsResponse.jsonPath().getList("$");

        int createdRatingId = ((Number) ratings.get(ratings.size() - 1).get("id")).intValue();
        Response deleteResponse = RestAssured.given()
                .contentType("application/json")
                .pathParam("ratingId", createdRatingId)
                .post("/ratings/deleteRating/{ratingId}");
        assertEquals(true, deleteResponse.as(Boolean.class));
    }

    @Test
    public void wrongUserTest() throws JSONException {
        JSONObject createBody = new JSONObject();
        createBody.put("rating", 3);
        createBody.put("comments", "Good");
        createBody.put("userId", 999);
        createBody.put("tutorId", 999);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(createBody.toString())
                .post("/ratings/createRating");

        assertEquals(false, response.as(Boolean.class));
    }


    @Test
    public void getTutorRatingsTest() throws JSONException
    {
        ratingRepository.deleteAll();
        JSONObject createBody = new JSONObject();
        createBody.put("rating", 3);
        createBody.put("comments", "10/10");
        createBody.put("userId", 3);
        createBody.put("tutorId", 1);

        Response response1 = RestAssured.given()
                .contentType("application/json")
                .body(createBody.toString())
                .post("/ratings/createRating");

        JSONObject createBody2 = new JSONObject();
        createBody2.put("rating", 5);
        createBody2.put("comments", "Hi");
        createBody2.put("userId", 2);
        createBody2.put("tutorId", 1);

        Response response2 = RestAssured.given()
                .contentType("application/json")
                .body(createBody2.toString())
                .post("/ratings/createRating");


        Response response = RestAssured.given()
                .contentType("application/json")
                .get("/ratings/getTutorRatings/1");

        assertEquals(200, response.statusCode());
    }


    @Test
    public void getAllTutorRatings() throws JSONException
    {
        ratingRepository.deleteAll();
        JSONObject createBody = new JSONObject();
        createBody.put("rating", 3);
        createBody.put("comments", "10/10");
        createBody.put("userId", 3);
        createBody.put("tutorId", 1);

        Response response1 = RestAssured.given()
                .contentType("application/json")
                .body(createBody.toString())
                .post("/ratings/createRating");

        JSONObject createBody2 = new JSONObject();
        createBody2.put("rating", 5);
        createBody2.put("comments", "Hi");
        createBody2.put("userId", 2);
        createBody2.put("tutorId", 1);

        Response response2 = RestAssured.given()
                .contentType("application/json")
                .body(createBody2.toString())
                .post("/ratings/createRating");


        Response response = RestAssured.given()
                .contentType("application/json")
                .get("/ratings");

        assertEquals(200, response.statusCode());

        String body = response.getBody().asString();
        JSONArray jsonArray = new JSONArray(body);
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject rating = jsonArray.getJSONObject(i);
            JSONObject userObject = rating.getJSONObject("user");
            JSONObject tutorObject = rating.getJSONObject("tutor");
            try
            {
                if(i == 0)
                {
                    assertEquals("3", rating.getString("rating"));
                    assertEquals("10/10", rating.getString("comments"));
                    //assertEquals(3, userObject.getString("id"));
                    //assertEquals(1, tutorObject.getString("id"));
                }
                else if(i == 1)
                {
                    assertEquals("5", rating.getString("rating"));
                    assertEquals("Hi", rating.getString("comments"));
                    //assertEquals(2, rating.getString("user_id"));
                    //assertEquals(1, rating.getString("tutor_id"));
                }
            }
            catch(Exception e)
            {
                throw new JSONException(e.getMessage());
            }
        }
    }


    // Private Messages Test

}

