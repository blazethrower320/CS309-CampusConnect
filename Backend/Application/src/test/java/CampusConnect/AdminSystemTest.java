package CampusConnect;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
public class AdminSystemTest {

    @LocalServerPort
    int port;
    @Autowired
    TutorRepository tutorRepository;
    @Before
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }
    public void createTestUserAsTutor(String username) throws JSONException {
        JSONObject deleteBody = new JSONObject();
        deleteBody.put("username", username);
        RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("password", "tutorpass");
        requestBody.put("isTutor", true); // CRITICAL
        requestBody.put("firstName", "AdminTest");
        requestBody.put("lastName", "Tutor");
        requestBody.put("isAdmin", false);

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody.toString())
                .post("/users/createUser");
    }
    public long createTestUser(String username) throws JSONException {
        JSONObject deleteBody = new JSONObject();
        deleteBody.put("username", username);
        RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");
        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("password", "adminpass");
        requestBody.put("isTutor", false);
        requestBody.put("isAdmin", false);
        requestBody.put("firstName", "AdminCreate");
        requestBody.put("lastName", "Test");

        Response createResponse = RestAssured.given()
                .contentType("application/json")
                .body(requestBody.toString())
                .post("/users/createUser");

        Response loginResponse = RestAssured.given()
                .contentType("application/json")
                .body(requestBody.toString())
                .post("/users/login");

        return loginResponse.jsonPath().getLong("userId");
    }

    @Test
    public void incrementAndGetNukedUsersTest() throws JSONException {

        long userId = createTestUser("adminUser");
        JSONObject userBodyJson = new JSONObject();
        userBodyJson.put("userId", userId);
        String userBody = userBodyJson.toString();

        Response createAdminResponse = RestAssured.given()
                .contentType("application/json")
                .pathParam("userId", userId)
                .post("/admin/createAdmin/{userId}");
        assertTrue(createAdminResponse.as(Boolean.class));

        Response initialGetResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(userBody)
                .get("/admin/getAdminNukedUsers");

        assertEquals(200, initialGetResponse.statusCode());
        assertEquals(0, initialGetResponse.as(Integer.class));

        Response incrementResponse1 = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(userBody)
                .get("/admin/incrementNukedUsers");

        assertEquals(200, incrementResponse1.statusCode());
        assertTrue(incrementResponse1.as(Boolean.class));

        Response getResponse1 = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(userBody)
                .get("/admin/getAdminNukedUsers");



        assertEquals(1, getResponse1.as(Integer.class));
        Response incrementResponse2 = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(userBody)
                .get("/admin/incrementNukedUsers");

        assertTrue(incrementResponse2.as(Boolean.class));

        Response getResponse2 = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(userBody)
                .get("/admin/getAdminNukedUsers");

        assertEquals(2, getResponse2.as(Integer.class));
        deleteTestUser("adminUser");
    }
    @Test
    public void getAdminNukedTest() throws JSONException {

        long adminUserId = createTestUser("admintest");
        Response createAdminResponse = RestAssured.given()
                .contentType("application/json")
                .pathParam("userId", adminUserId)
                .post("/admin/createAdmin/{userId}");

        JSONObject adminBodyJson = new JSONObject();
        adminBodyJson.put("userId", adminUserId);
        String adminBody = adminBodyJson.toString();
        long nonAdminUserId = createTestUser("userTestAdmin");

        JSONObject nonAdminBodyJson = new JSONObject();
        nonAdminBodyJson.put("userId", nonAdminUserId);
        String nonAdminBody = nonAdminBodyJson.toString();




        Response successGetResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(adminBody)
                .get("/admin/getAdminNukedUsers");

        assertEquals(200, successGetResponse.statusCode());
        assertEquals(0, successGetResponse.as(Integer.class));


        Response failureGetResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(nonAdminBody)
                .get("/admin/getAdminNukedUsers");

        assertEquals(200, failureGetResponse.statusCode());
        assertEquals(0, failureGetResponse.as(Integer.class));


        deleteTestUser("admintest");
        deleteTestUser("userTestAdmin");
    }



    @Test
    public void updateRatingsTutorTest() throws JSONException {
        double ratingValue = 4.5;

        JSONObject deleteBody = new JSONObject();
        deleteBody.put("username", "ratingTutor");
        RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", "ratingTutor");
        requestBody.put("password", "tutorpass");
        requestBody.put("isTutor", true); // CRITICAL
        requestBody.put("firstName", "AdminTest");
        requestBody.put("lastName", "Tutor");
        requestBody.put("isAdmin", false);

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody.toString())
                .post("/users/createUser");
        Response updateResponse = RestAssured.given()
                .contentType("application/json")
                .pathParam("username", "ratingTutor")
                .pathParam("rating", ratingValue)
                .post("/admin/updateRatingsTutor/{username}/{rating}");

        assertEquals(200, updateResponse.statusCode());

        double actualRating = tutorRepository.findByUsername("ratingTutor").getTotalRating();
        assertEquals(ratingValue, actualRating);
        deleteTestUser("ratingTutor");
    }

    public void deleteAdminTest() throws JSONException {

        long userId = createTestUser("Test6");
        RestAssured.given()
                .contentType("application/json")
                .pathParam("userId", userId)
                .post("/admin/createAdmin/{userId}");

        List<Long> adminIdsBefore = RestAssured.given().get("/admins").jsonPath().getList("user.userId", Long.class);
        assertTrue(adminIdsBefore.contains(userId));
        Response deleteResponse = RestAssured.given()
                .contentType("application/json")
                .pathParam("userId", userId)
                .post("/admin/deleteAdmin/{userId}");

        assertEquals(200, deleteResponse.statusCode());

        Response getAdminsResponse = RestAssured.given().get("/admins");

        List<Long> adminIdsAfter = getAdminsResponse.jsonPath().getList("user.userId", Long.class);
        assertFalse(adminIdsAfter.contains(userId));
        deleteTestUser("Test6");
    }


    public void deleteTestUser(String username) throws JSONException {
        JSONObject deleteBody = new JSONObject();
        deleteBody.put("username", username);
        RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");
    }


    @Test
    public void createAdminTest() throws JSONException {
        long userId = createTestUser("newadmin");

        Response createAdminResponse = RestAssured.given()
                .contentType("application/json")
                .pathParam("userId", userId)
                .post("/admin/createAdmin/{userId}");

        assertEquals(200, createAdminResponse.statusCode());

        Response getAdminsResponse = RestAssured.given()
                .get("/admins");

        assertEquals(200, getAdminsResponse.statusCode());

        List<Map<String, Object>> admins = getAdminsResponse.jsonPath().getList("$");

        boolean adminFound = false;
        for (Map<String, Object> admin : admins)
        {
            Map<String, Object> user = (Map<String, Object>) admin.get("user");
            if (user != null && user.get("userId") instanceof Number && ((Number) user.get("userId")).longValue() == userId)
            {
                adminFound = true;
                break;
            }
        }

        assertTrue(adminFound);

        deleteTestUser("newadmin");
    }

    @Test
    public void userNotFoundAdminTest() throws JSONException {

        Response createAdminResponse = RestAssured.given()
                .contentType("application/json")
                .pathParam("userId", 9999999)
                .post("/admin/createAdmin/{userId}");

        assertEquals(500, createAdminResponse.statusCode());
    }
}