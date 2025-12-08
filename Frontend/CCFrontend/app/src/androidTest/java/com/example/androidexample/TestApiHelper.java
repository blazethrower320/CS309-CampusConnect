package com.example.androidexample;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestApiHelper {

    private static final String BASE_URL = "http://coms-3090-037.class.las.iastate.edu:8080";
    private static final String URL_CREATE_USER = BASE_URL + "/users/createUser";
    private static final String URL_DELETE_USER = BASE_URL + "/users/deleteUser";
    private static final String URL_UPDATE_USER = BASE_URL + "/users/update";
    private static final String URL_GET_USER_ID = BASE_URL + "/users/getUserId/";

    private final Context context;
    private final RequestQueue queue;

    public TestApiHelper(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    // ---------------------------------------------------------
    // STEP 1: POST /users/createUser
    // ---------------------------------------------------------
    private boolean stepCreateUser(String username, String password)
            throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = {false};

        try {
            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("password", password);

            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.POST,
                    URL_CREATE_USER,
                    body,
                    response -> {
                        success[0] = response.toString().contains("success");
                        latch.countDown();
                    },
                    error -> {
                        error.printStackTrace();
                        latch.countDown();
                    }
            );

            queue.add(req);

            latch.await(4, TimeUnit.SECONDS);
            return success[0];

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------------------------------------------------
    // STEP 2: GET /users/getUserId/{username}
    // ---------------------------------------------------------
    private Integer stepGetUserId(String username)
            throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        final Integer[] result = {null};

        StringRequest req = new StringRequest(
                Request.Method.GET,
                URL_GET_USER_ID + username,
                response -> {
                    try {
                        result[0] = Integer.parseInt(response.trim());
                    } catch (Exception ignore) {}
                    latch.countDown();
                },
                error -> {
                    error.printStackTrace();
                    latch.countDown();
                }
        );

        queue.add(req);
        latch.await(4, TimeUnit.SECONDS);

        return result[0];
    }

    // ---------------------------------------------------------
    // STEP 3: PUT /users/update
    // ---------------------------------------------------------
    private boolean stepFullUpdate(String username,
                                   String password,
                                   String firstName,
                                   String lastName)
            throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = {false};

        try {
            JSONObject body = new JSONObject();
            body.put("firstName", firstName);
            body.put("lastName", lastName);
            body.put("username", username);
            body.put("password", password);

            // default fields required by backend
            body.put("isTutor", false);
            body.put("isAdmin", false);
            body.put("major", "");
            body.put("classification", "");
            body.put("bio", "");

            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.PUT,
                    URL_UPDATE_USER,
                    body,
                    response -> {
                        success[0] = response.toString().contains("updated");
                        latch.countDown();
                    },
                    error -> {
                        error.printStackTrace();
                        latch.countDown();
                    }
            );

            queue.add(req);

            latch.await(4, TimeUnit.SECONDS);
            return success[0];

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createCompleteUser(String username,
                                      String password,
                                      String firstName,
                                      String lastName) {
        try {
            // Step 1: Minimal create
            if (!stepCreateUser(username, password)) return false;

            // Step 2: Must wait for GET to succeed
            Integer id = stepGetUserId(username);
            if (id == null) return false;

            // Step 3: Update required fields
            return stepFullUpdate(username, password, firstName, lastName);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // ---------------------------------------------------------
    // Delete user
    // ---------------------------------------------------------
    public boolean deleteUser(String username, String password) {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] result = {false};

        try {
            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("password", password);

            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    URL_DELETE_USER,
                    response -> {
                        Log.d("TestDeleteUser", "Response: " + response);
                        result[0] = response.equalsIgnoreCase("userDeleted");
                        latch.countDown();
                    },
                    error -> {
                        Log.e("TestDeleteUser", "Error: " + error.toString());
                        latch.countDown();
                    }
            ) {
                @Override
                public byte[] getBody() {
                    return body.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            queue.add(request);

            latch.await(5, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result[0];
    }
}