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
import org.springframework.boot.test.web.server.LocalServerPort;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class CarsonSystemTest {

    @LocalServerPort
    int port;
    @Before
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    public void createUserTest() throws JSONException {

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

}

