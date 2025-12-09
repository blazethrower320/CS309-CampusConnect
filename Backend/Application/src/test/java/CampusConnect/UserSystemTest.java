package CampusConnect;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import CampusConnect.Database.Models.Ratings.Ratings;
import CampusConnect.Database.Models.Ratings.RatingsRepository;
import CampusConnect.Database.Models.Users.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
public class UserSystemTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    public void deleteTestUser() throws JSONException {
        JSONObject deleteBody = new JSONObject();
        deleteBody.put("username", "TestUser");
        RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");
    }

    public void createTestUser() throws JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("username", "TestUser");
        requestBody.put("password", "password");
        requestBody.put("isTutor", false);
        requestBody.put("firstName", "First");
        requestBody.put("lastName", "Test");
        requestBody.put("isAdmin", false);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(requestBody.toString())
                .post("/users/createUser");
    }

    @Test
    public void getUserByFirstName() throws JSONException {
        deleteUserTest();
        createTestUser();

        JSONObject login = new JSONObject();
        login.put("username", "TestUser");
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
            assertEquals("TestUser", finalResponse.jsonPath().getString("username"));
        }
    }





    @Test
    public void getUserByIdTest() throws JSONException {
        deleteUserTest();
        createTestUser();

        JSONObject login = new JSONObject();
        login.put("username", "TestUser");
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
        assertEquals("TestUser", finalResponse.jsonPath().getString("username"));
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

    @Test
    public void editUsernameTest() throws JSONException {
        String originalUsername = "UserToEdit";
        String newUsername = "EditedUser";
        String password = "testpassword";
        JSONObject deleteBody = new JSONObject();
        deleteBody.put("username", originalUsername);
        RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");
        deleteBody.put("username", newUsername);
        RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");
        JSONObject createBody = new JSONObject();
        createBody.put("username", originalUsername);
        createBody.put("password", password);
        createBody.put("isTutor", false);
        createBody.put("isAdmin", false);
        RestAssured.given()
                .contentType("application/json")
                .body(createBody.toString())
                .post("/users/createUser");
        JSONObject loginBody = new JSONObject();
        loginBody.put("username", originalUsername);
        loginBody.put("password", password);
        Response loginResponse = RestAssured.given()
                .contentType("application/json")
                .body(loginBody.toString())
                .post("/users/login");
        assertEquals(200, loginResponse.statusCode());
        long userId = loginResponse.jsonPath().getLong("userId");
        JSONObject editUserBody = new JSONObject();
        editUserBody.put("newUsername", newUsername);
        editUserBody.put("userID", userId);

        Response editResponse = RestAssured.given()
                .contentType("application/json")
                .body(editUserBody.toString())
                .patch("/users/editUsername");

        assertEquals(200, editResponse.statusCode());
        assertEquals("true", editResponse.getBody().asString());
        JSONObject newLoginBody = new JSONObject();
        newLoginBody.put("username", newUsername);
        newLoginBody.put("password", password);

        Response finalLoginResponse = RestAssured.given()
                .contentType("application/json")
                .body(newLoginBody.toString())
                .post("/users/login");

        assertEquals(200, finalLoginResponse.statusCode());
        assertEquals(newUsername, finalLoginResponse.jsonPath().getString("username"));

        JSONObject deleteBodyJson = new JSONObject();
        deleteBodyJson.put("username", newUsername);
        Response deleteResponse = RestAssured.given()
                .contentType("application/json")
                .body(deleteBodyJson.toString())
                .post("/users/deleteUser");

        assertEquals(200, deleteResponse.statusCode());
    }

    @Test
    public void editPasswordTest() throws JSONException {
        // Define original and new passwords
        String originalPassword = "oldPassword123";
        String newPassword = "newPassword!";
        String username = "testingPassword";
        JSONObject deleteBody = new JSONObject();
        deleteBody.put("username", username);
        RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");

        JSONObject createBody = new JSONObject();
        createBody.put("username", username);
        createBody.put("password", originalPassword);
        createBody.put("isTutor", false);
        createBody.put("isAdmin", false);
        RestAssured.given()
                .contentType("application/json")
                .body(createBody.toString())
                .post("/users/createUser");

        JSONObject editUserBody = new JSONObject();
        editUserBody.put("newPassword", newPassword);
        editUserBody.put("newUsername", username);
        Response editResponse = RestAssured.given()
                .contentType("application/json")
                .body(editUserBody.toString())
                .patch("/users/editPassword");

        assertEquals(200, editResponse.statusCode());
        assertEquals("true", editResponse.getBody().asString());

        JSONObject loginBody = new JSONObject();
        loginBody.put("username", username);
        loginBody.put("password", newPassword);

        Response finalLoginResponse = RestAssured.given()
                .contentType("application/json")
                .body(loginBody.toString())
                .post("/users/login");

        assertEquals(200, finalLoginResponse.statusCode());
        assertEquals(username, finalLoginResponse.jsonPath().getString("username"));

        JSONObject oldLoginBody = new JSONObject();
        oldLoginBody.put("username", username);
        oldLoginBody.put("password", originalPassword);

        Response oldLoginResponse = RestAssured.given()
                .contentType("application/json")
                .body(oldLoginBody.toString())
                .post("/users/login");

        assertEquals(403, oldLoginResponse.statusCode());
        JSONObject delteBodyJson = new JSONObject();
        delteBodyJson.put("username", username);
        Response deleteResponse = RestAssured.given()
                .contentType("application/json")
                .body(delteBodyJson.toString())
                .post("/users/deleteUser");

        assertEquals(200, deleteResponse.statusCode());
    }

    @Test
    public void getUserIdTestt() throws JSONException {
        String username = "testUser3";
        String password = "password123";

        JSONObject deleteBody = new JSONObject();
        deleteBody.put("username", username);
        RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");
        JSONObject createBody = new JSONObject();
        createBody.put("username", username);
        createBody.put("password", password);
        createBody.put("isTutor", false);
        createBody.put("isAdmin", false);
        Response createResponse = RestAssured.given()
                .contentType("application/json")
                .body(createBody.toString())
                .post("/users/createUser");
        assertEquals(200, createResponse.statusCode());

        JSONObject loginBody = new JSONObject();
        loginBody.put("username", username);
        loginBody.put("password", password);
        Response loginResponse = RestAssured.given()
                .contentType("application/json")
                .body(loginBody.toString())
                .post("/users/login");

        assertEquals(200, loginResponse.statusCode());
        long expectedUserId = loginResponse.jsonPath().getLong("userId");



        Response finalResponse = RestAssured.given()
                .contentType("application/json")
                .pathParam("username", username)
                .get("/users/getUserId/{username}");

        assertEquals(200, finalResponse.getStatusCode());
        long actualUserId = Long.parseLong(finalResponse.getBody().asString());
        assertEquals(expectedUserId, actualUserId);

        RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");
    }

    @Test
    public void findUsernameTest() throws JSONException {

        JSONObject deleteBody = new JSONObject();
        deleteBody.put("username", "Test4");
        RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");

        JSONObject createBody = new JSONObject();
        createBody.put("username", "Test4");
        createBody.put("password", "password");
        createBody.put("isTutor", false);
        createBody.put("isAdmin", false);
        RestAssured.given()
                .contentType("application/json")
                .body(createBody.toString())
                .post("/users/createUser");

        Response finalResponse = RestAssured.given()
                .contentType("application/json")
                .pathParam("username", "Test4")
                .get("/users/findUsername/{username}");



        assertEquals(200, finalResponse.getStatusCode());
        List<Map<String, String>> users = finalResponse.jsonPath().getList("$");
        assertEquals(1, users.size());

        Map<String, String> foundUser = users.get(0);
        assertEquals("Test4", foundUser.get("username"));

        RestAssured.given()
                .contentType("application/json")
                .body(deleteBody.toString())
                .post("/users/deleteUser");
    }



}

