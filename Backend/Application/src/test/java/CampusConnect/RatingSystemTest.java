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
public class RatingSystemTest {

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
                    //assertEquals("3", rating.getString("rating"));
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
    @Test
    public void createRatingMissingTutorTest() throws JSONException {
        JSONObject createBody = new JSONObject();
        createBody.put("rating", 4);
        createBody.put("comments", "Tutor DNE test");
        createBody.put("userId", 2);
        createBody.put("tutorId", 999);
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(createBody.toString())
                .post("/ratings/createRating");

        assertEquals(false, response.as(Boolean.class));
    }

}

