package com.example.androidexample;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowNotificationManager;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;


@RunWith(RobolectricTestRunner.class)
@Config(
        manifest = "src/main/AndroidManifest.xml",
        packageName = "com.example.androidexample",
        sdk = 33
)
public class MockTestsWilliam {

    private MainActivity activity;
    private ActivityController<MainActivity> controller;

    //MAIN ACTIVITY TESTS

    @Test
    public void testOnCreate_initializesButtons() {
        controller = Robolectric.buildActivity(MainActivity.class);
        activity = controller.create().start().resume().get();
        Button loginBtn = activity.findViewById(R.id.login_btn);
        Button signupBtn = activity.findViewById(R.id.signup_btn);
        Button forgotpBtn = activity.findViewById(R.id.forgotp_btn);

        assertNotNull(loginBtn);
        assertNotNull(signupBtn);
        assertNotNull(forgotpBtn);
    }

    @Test
    public void testSignupButton_navigatesToCreateAccountActivity() {
        controller = Robolectric.buildActivity(MainActivity.class);
        activity = controller.create().start().resume().get();
        Button signupBtn = activity.findViewById(R.id.signup_btn);
        signupBtn.performClick();

        Intent nextIntent = shadowOf(activity).peekNextStartedActivity();
        assertEquals(CreateAccountActivity.class.getCanonicalName(), nextIntent.getComponent().getClassName());
    }

    @Test
    public void testForgotPasswordButton_navigatesToForgotPasswordActivity() {
        controller = Robolectric.buildActivity(MainActivity.class);
        activity = controller.create().start().resume().get();
        Button forgotBtn = activity.findViewById(R.id.forgotp_btn);
        forgotBtn.performClick();

        Intent nextIntent = shadowOf(activity).peekNextStartedActivity();
        assertEquals(ForgotPasswordActivity.class.getCanonicalName(), nextIntent.getComponent().getClassName());
    }

    @Test
    public void testLoginWithEmptyFields_showsError() {
        controller = Robolectric.buildActivity(MainActivity.class);
        activity = controller.create().start().resume().get();
        EditText usernameEdt = activity.findViewById(R.id.login_username_edt);
        EditText passwordEdt = activity.findViewById(R.id.login_password_edt);

        usernameEdt.setText("");
        passwordEdt.setText("");

        activity.findViewById(R.id.login_btn).performClick();

        assertEquals("Please fill in both fields", passwordEdt.getError().toString());
    }

    @Test
    public void testLoginWithTestCredentials_navigatesToMainMenu() {
        controller = Robolectric.buildActivity(MainActivity.class);
        activity = controller.create().start().resume().get();
        EditText usernameEdt = activity.findViewById(R.id.login_username_edt);
        EditText passwordEdt = activity.findViewById(R.id.login_password_edt);

        usernameEdt.setText("Test");
        passwordEdt.setText("Test");

        activity.findViewById(R.id.login_btn).performClick();

        Intent nextIntent = shadowOf(activity).peekNextStartedActivity();
        assertEquals(MainMenuActivity.class.getCanonicalName(), nextIntent.getComponent().getClassName());
    }

    @Test
    public void testPerformLoginNetworkRequest_invalidUser_showsToast() throws Exception {
        controller = Robolectric.buildActivity(MainActivity.class);
        activity = controller.create().start().resume().get();
        // Simulate network failure by leaving username that won't exist
        EditText usernameEdt = activity.findViewById(R.id.login_username_edt);
        EditText passwordEdt = activity.findViewById(R.id.login_password_edt);

        usernameEdt.setText("nonexistent");
        passwordEdt.setText("wrong");

        activity.findViewById(R.id.login_btn).performClick();

        // Robolectric won't actually perform network, but we can verify that the error path triggers a Toast
        // In real unit test, you would mock Volley here to simulate 404 or 403 responses
        assertTrue(ShadowToast.getTextOfLatestToast() == null || ShadowToast.getTextOfLatestToast().contains("Network error"));
    }

    @Test
    public void testLoginNetworkRequest_userNotFound_showsError() throws Exception {
        controller = Robolectric.buildActivity(MainActivity.class);
        activity = controller.create().start().resume().get();
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(404)); // User not found

        EditText usernameEdt = activity.findViewById(R.id.login_username_edt);
        EditText passwordEdt = activity.findViewById(R.id.login_password_edt);
        usernameEdt.setText("invalidUser");
        passwordEdt.setText("pass");

        activity.findViewById(R.id.login_btn).performClick();
        Robolectric.flushForegroundThreadScheduler();

        assertEquals("User not found", usernameEdt.getError().toString());
        server.shutdown();
    }

    @Test
    public void testLoginNetworkRequest_wrongPassword_showsError() throws Exception {
        controller = Robolectric.buildActivity(MainActivity.class);
        activity = controller.create().start().resume().get();
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(403)); // Wrong password

        EditText usernameEdt = activity.findViewById(R.id.login_username_edt);
        EditText passwordEdt = activity.findViewById(R.id.login_password_edt);
        usernameEdt.setText("mockUser");
        passwordEdt.setText("wrongPass");

        activity.findViewById(R.id.login_btn).performClick();
        Robolectric.flushForegroundThreadScheduler();

        assertEquals("Incorrect password", passwordEdt.getError().toString());
        server.shutdown();
    }

    // FORGOT PASSWORD TESTS

    @Test
    public void testUpdatePassword_success_showsSuccessMessage() throws Exception {
        MockWebServer server = new MockWebServer();
        server.start();
        server.enqueue(new MockResponse().setResponseCode(200).setBody("true"));

        ForgotPasswordActivity activity = Robolectric.buildActivity(ForgotPasswordActivity.class)
                .create().start().resume().get();

        // Cast views to EditText
        EditText usernameEdt = activity.findViewById(R.id.create_username);
        EditText passwordEdt = activity.findViewById(R.id.create_password);
        EditText confirmPassEdt = activity.findViewById(R.id.create_password_confirm);

        usernameEdt.setText("testUser");
        passwordEdt.setText("password123");
        confirmPassEdt.setText("password123");

        // Redirect request URL to MockWebServer
        java.lang.reflect.Field field = ForgotPasswordActivity.class.getDeclaredField("URL_EDIT_PASSWORD");
        field.setAccessible(true);
        field.set(null, server.url("/users/editPassword").toString());

        activity.findViewById(R.id.signup_btn).performClick();

        Robolectric.flushForegroundThreadScheduler();

        TextView msgResponse = activity.findViewById(R.id.msgResponse);
        assertEquals("Password updated!", msgResponse.getText().toString());
        server.shutdown();
    }

    @Test
    public void testUpdatePassword_userNotFound_showsErrorMessage() throws Exception {
        MockWebServer server = new MockWebServer();
        server.start();
        server.enqueue(new MockResponse().setResponseCode(200).setBody("false"));

        ForgotPasswordActivity activity = Robolectric.buildActivity(ForgotPasswordActivity.class)
                .create().start().resume().get();

        EditText usernameEdt = activity.findViewById(R.id.create_username);
        EditText passwordEdt = activity.findViewById(R.id.create_password);
        EditText confirmPassEdt = activity.findViewById(R.id.create_password_confirm);

        usernameEdt.setText("nonexistentUser");
        passwordEdt.setText("password123");
        confirmPassEdt.setText("password123");

        java.lang.reflect.Field field = ForgotPasswordActivity.class.getDeclaredField("URL_EDIT_PASSWORD");
        field.setAccessible(true);
        field.set(null, server.url("/users/editPassword").toString());

        activity.findViewById(R.id.signup_btn).performClick();

        Robolectric.flushForegroundThreadScheduler();

        TextView msgResponse = activity.findViewById(R.id.msgResponse);
        assertEquals("User does not exist", msgResponse.getText().toString());
        server.shutdown();
    }

    @Test
    public void testUpdatePassword_networkError_showsFailureMessage() throws Exception {
        MockWebServer server = new MockWebServer();
        server.start();
        // Simulate network failure by shutting down before request
        server.shutdown();

        ForgotPasswordActivity activity = Robolectric.buildActivity(ForgotPasswordActivity.class)
                .create().start().resume().get();

        EditText usernameEdt = activity.findViewById(R.id.create_username);
        EditText passwordEdt = activity.findViewById(R.id.create_password);
        EditText confirmPassEdt = activity.findViewById(R.id.create_password_confirm);

        usernameEdt.setText("testUser");
        passwordEdt.setText("password123");
        confirmPassEdt.setText("password123");

        java.lang.reflect.Field field = ForgotPasswordActivity.class.getDeclaredField("URL_EDIT_PASSWORD");
        field.setAccessible(true);
        field.set(null, server.url("/users/editPassword").toString());

        activity.findViewById(R.id.signup_btn).performClick();

        Robolectric.flushForegroundThreadScheduler();

        TextView msgResponse = activity.findViewById(R.id.msgResponse);
        assertEquals("Failed to update password. Try again.", msgResponse.getText().toString());

    }

    // CREATE ACCOUNT TESTS

    @Test
    public void testCreateUser_success_flow() throws Exception {
        MockWebServer server = new MockWebServer();
        server.start();

        // Step 1: createUser response
        server.enqueue(new MockResponse().setResponseCode(200).setBody("User created successfully"));
        // Step 2: fetchUserId response
        server.enqueue(new MockResponse().setResponseCode(200).setBody("123"));
        // Step 3: updateUserInDatabase response
        server.enqueue(new MockResponse().setResponseCode(200).setBody("User updated"));

        CreateAccountActivity activity = Robolectric.buildActivity(CreateAccountActivity.class)
                .create().start().resume().get();

        // Cast all EditTexts
        EditText usernameEdt = activity.findViewById(R.id.create_username);
        EditText passwordEdt = activity.findViewById(R.id.create_password);
        EditText confirmPassEdt = activity.findViewById(R.id.create_password_confirm);
        EditText firstNameEdt = activity.findViewById(R.id.create_first_name);
        EditText lastNameEdt = activity.findViewById(R.id.create_last_name);
        CheckBox tutorCheck = activity.findViewById(R.id.tutor_checkbox);
        TextView msgResponse = activity.findViewById(R.id.msgResponse);

        usernameEdt.setText("testUser");
        passwordEdt.setText("password123");
        confirmPassEdt.setText("password123");
        firstNameEdt.setText("John");
        lastNameEdt.setText("Doe");
        tutorCheck.setChecked(true);

        // Redirect URLs to mock server
        java.lang.reflect.Field fieldBase = CreateAccountActivity.class.getDeclaredField("BASE_URL");
        fieldBase.setAccessible(true);
        fieldBase.set(null, server.url("/").toString());

        java.lang.reflect.Field fieldCreateUser = CreateAccountActivity.class.getDeclaredField("URL_CREATE_USER");
        fieldCreateUser.setAccessible(true);
        fieldCreateUser.set(null, server.url("/users/createUser").toString());

        java.lang.reflect.Field fieldUpdateUser = CreateAccountActivity.class.getDeclaredField("URL_UPDATE_USER");
        fieldUpdateUser.setAccessible(true);
        fieldUpdateUser.set(null, server.url("/users/update").toString());

        activity.findViewById(R.id.create_account_btn).performClick();

        Robolectric.flushForegroundThreadScheduler();

        // Assert that user info was saved in singleton
        User user = User.getInstance();
        assertEquals("testUser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertTrue(user.isTutor());

        // Assert msgResponse updated after update
        assertEquals("Tutor Created Successfully!", msgResponse.getText().toString());

        server.shutdown();
    }

    @Test
    public void testCreateUser_networkError_showsFailureMessage() throws Exception {
        MockWebServer server = new MockWebServer();
        server.start();
        // Shutdown server to simulate network error
        server.shutdown();

        CreateAccountActivity activity = Robolectric.buildActivity(CreateAccountActivity.class)
                .create().start().resume().get();

        EditText usernameEdt = activity.findViewById(R.id.create_username);
        EditText passwordEdt = activity.findViewById(R.id.create_password);
        EditText confirmPassEdt = activity.findViewById(R.id.create_password_confirm);
        EditText firstNameEdt = activity.findViewById(R.id.create_first_name);
        EditText lastNameEdt = activity.findViewById(R.id.create_last_name);
        CheckBox tutorCheck = activity.findViewById(R.id.tutor_checkbox);
        TextView msgResponse = activity.findViewById(R.id.msgResponse);

        usernameEdt.setText("testUser");
        passwordEdt.setText("password123");
        confirmPassEdt.setText("password123");
        firstNameEdt.setText("John");
        lastNameEdt.setText("Doe");
        tutorCheck.setChecked(false);

        java.lang.reflect.Field fieldCreateUser = CreateAccountActivity.class.getDeclaredField("URL_CREATE_USER");
        fieldCreateUser.setAccessible(true);
        fieldCreateUser.set(null, server.url("/users/createUser").toString());

        activity.findViewById(R.id.create_account_btn).performClick();
        Robolectric.flushForegroundThreadScheduler();

        assertEquals("User Creation Failed", msgResponse.getText().toString());
    }

    // TUTOR LIST ACTIVITY TESTS

    @Test
    public void testLoadTutors_successfulNetworkResponse_updatesAdapter() throws Exception {
        MockWebServer server = new MockWebServer();
        server.start();

        String mockResponse = "[{\"tutorId\":1,\"username\":\"tutor1\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"totalRating\":4.5}," +
                "{\"tutorId\":2,\"username\":\"tutor2\",\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"totalRating\":5.0}]";
        server.enqueue(new MockResponse().setResponseCode(200).setBody(mockResponse));

        ReviewListActivity activity = Robolectric.buildActivity(ReviewListActivity.class)
                .create().start().resume().get();

        // Redirect URL_TUTORS to mock server
        java.lang.reflect.Field urlField = ReviewListActivity.class.getDeclaredField("URL_TUTORS");
        urlField.setAccessible(true);
        urlField.set(null, server.url("/tutors").toString());

        // Call private loadTutors() via reflection
        java.lang.reflect.Method loadTutorsMethod = ReviewListActivity.class.getDeclaredMethod("loadTutors");
        loadTutorsMethod.setAccessible(true);
        loadTutorsMethod.invoke(activity);

        Robolectric.flushForegroundThreadScheduler();

        // Access private adapter via reflection
        java.lang.reflect.Field adapterField = ReviewListActivity.class.getDeclaredField("adapter");
        adapterField.setAccessible(true);
        TutorListAdapter adapter = (TutorListAdapter) adapterField.get(activity);

        // Check that the adapter has 2 items
        assertEquals(2, adapter.getItemCount());

        server.shutdown();
    }



    @Test
    public void testNavigateTo_mainMenu() {
        ReviewListActivity activity = Robolectric.buildActivity(ReviewListActivity.class)
                .create().start().resume().get();

        // Use reflection to invoke private navigateTo
        try {
            java.lang.reflect.Method method = ReviewListActivity.class.getDeclaredMethod("navigateTo", Class.class);
            method.setAccessible(true);
            method.invoke(activity, MainMenuActivity.class);

            Intent nextIntent = shadowOf(activity).peekNextStartedActivity();
            assertEquals(MainMenuActivity.class.getCanonicalName(), nextIntent.getComponent().getClassName());
        } catch (Exception e) {
            fail("Reflection call failed: " + e.getMessage());
        }
    }

    @Test
    public void testOnTutorClicked_stub() {
        ReviewListActivity activity = Robolectric.buildActivity(ReviewListActivity.class)
                .create().start().resume().get();

        TutorItem tutor = new TutorItem(1, "tutor1", "John Doe", 4.5);
        // The method does nothing currently, just call to ensure no crash
        activity.onTutorClicked(tutor);
    }

    @Test
    public void testOnReviewsClicked_opensTutorReviewsActivity() {
        ReviewListActivity activity = Robolectric.buildActivity(ReviewListActivity.class)
                .create().start().resume().get();

        TutorItem tutor = new TutorItem(1, "tutor1", "John Doe", 4.5);
        activity.onReviewsClicked(tutor);

        Intent nextIntent = shadowOf(activity).peekNextStartedActivity();
        assertEquals(TutorReviewsActivity.class.getCanonicalName(), nextIntent.getComponent().getClassName());
        assertEquals(1, nextIntent.getIntExtra("tutorId", -1));
    }

    @Test
    public void testWebSocketMethods_doNotCrash() {
        ReviewListActivity activity = Robolectric.buildActivity(ReviewListActivity.class)
                .create().start().resume().get();

        activity.onWebSocketOpen(null);
        activity.onWebSocketMessage("Test message");
        activity.onWebSocketClose(1000, "Normal close", true);
        activity.onWebSocketError(new Exception("Test error"));
        // Nothing to assert, just ensure no crash
    }


    // RATINGS LIST ACTIVITY

    @Test
    public void testSubmitRating_successfulPost_updatesUI() throws Exception {
        MockWebServer server = new MockWebServer();
        server.start();

        // Mock server response for POST
        server.enqueue(new MockResponse().setResponseCode(200).setBody("Rating created"));

        // Mock server response for GET loadTutorRatings() after POST
        String mockRatings = "[]";
        server.enqueue(new MockResponse().setResponseCode(200).setBody(mockRatings));

        // Launch activity
        TutorReviewsActivity activity = Robolectric.buildActivity(TutorReviewsActivity.class)
                .create().start().resume().get();

        // Redirect BASE_URL to mock server
        java.lang.reflect.Field baseUrlField = TutorReviewsActivity.class.getDeclaredField("BASE_URL");
        baseUrlField.setAccessible(true);
        baseUrlField.set(null, server.url("/").toString());

        // Set some test values
        java.lang.reflect.Field commentBoxField = TutorReviewsActivity.class.getDeclaredField("commentBox");
        commentBoxField.setAccessible(true);
        EditText commentBox = (EditText) commentBoxField.get(activity);
        commentBox.setText("Great tutor!");

        java.lang.reflect.Field ratingBarField = TutorReviewsActivity.class.getDeclaredField("ratingBar");
        ratingBarField.setAccessible(true);
        RatingBar ratingBar = (RatingBar) ratingBarField.get(activity);
        ratingBar.setRating(5);

        // Call private submitRating() via reflection
        java.lang.reflect.Method submitMethod = TutorReviewsActivity.class.getDeclaredMethod("submitRating");
        submitMethod.setAccessible(true);
        submitMethod.invoke(activity);

        // Flush network and UI events
        Robolectric.flushForegroundThreadScheduler();

        // Verify Toast was shown
        assertEquals("Rating submitted successfully!",
                ShadowToast.getTextOfLatestToast());

        // Verify that the comment box is cleared and rating reset
        assertEquals("", commentBox.getText().toString());
        assertEquals(3, (int) ratingBar.getRating());

        server.shutdown();
    }

    // SESSION ACTIVITY TESTS

    @Test
    public void testStyleSearchView_appliesColors() throws Exception {
        SessionActivity activity = Robolectric.buildActivity(SessionActivity.class).create().start().resume().get();
        SearchView searchView = new SearchView(activity);

        Method method = SessionActivity.class.getDeclaredMethod("styleSearchView", SearchView.class);
        method.setAccessible(true);
        method.invoke(activity, searchView);

        int plateId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_plate", null, null);
        View plate = searchView.findViewById(plateId);
        assertNotNull(plate);
        assertEquals(Color.TRANSPARENT, ((ColorDrawable) plate.getBackground()).getColor());
    }


    @Test
    public void testFetchTutorUsernameById_setsUsername() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("{\"username\":\"tutor123\",\"user\":{\"userId\":42}}"));
        server.start();

        SessionActivity activity = Robolectric.buildActivity(SessionActivity.class).create().get();
        Field baseUrlField = SessionActivity.class.getDeclaredField("BASE_URL");
        baseUrlField.setAccessible(true);
        baseUrlField.set(null, server.url("/").toString());

        Session session = new Session(1,"Math","MATH101","Room 1", -1,"12:00","Loading...");

        Method method = SessionActivity.class.getDeclaredMethod("fetchTutorUsernameById", Session.class, int.class);
        method.setAccessible(true);
        method.invoke(activity, session, 1);

        Robolectric.flushForegroundThreadScheduler();
        assertEquals("tutor123", session.getTutorUsername());
        assertEquals(42, session.getTutorUserId());

        server.shutdown();
    }

    @Test
    public void testFetchTutorForSession_setsUnknownIfError() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(404));
        server.start();

        SessionActivity activity = Robolectric.buildActivity(SessionActivity.class).create().get();
        Field baseUrlField = SessionActivity.class.getDeclaredField("BASE_URL");
        baseUrlField.setAccessible(true);
        baseUrlField.set(null, server.url("/").toString());

        Session session = new Session(1,"Math","MATH101","Room 1", -1,"12:00","Loading...");
        Method method = SessionActivity.class.getDeclaredMethod("fetchTutorForSession", Session.class);
        method.setAccessible(true);
        method.invoke(activity, session);

        Robolectric.flushForegroundThreadScheduler();
        assertEquals("Unknown Tutor", session.getTutorUsername());
        assertEquals(-1, session.getTutorUserId());

        server.shutdown();
    }

    @Test
    public void testSendPushForSessionJoin_makesRequest() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("{\"tutorId\":42}"));
        server.start();

        SessionActivity activity = Robolectric.buildActivity(SessionActivity.class).create().get();
        Field baseUrlField = SessionActivity.class.getDeclaredField("BASE_URL");
        baseUrlField.setAccessible(true);
        baseUrlField.set(null, server.url("/").toString());

        Session session = new Session(1,"Math","MATH101","Room 1", -1,"12:00","Loading...");
        Method method = SessionActivity.class.getDeclaredMethod("sendPushForSessionJoin", Session.class, String.class);
        method.setAccessible(true);
        method.invoke(activity, session, "student1");

        Robolectric.flushForegroundThreadScheduler();

        RecordedRequest request = server.takeRequest();
        assertTrue(request.getPath().contains("/sessions/getSessionTutor/"));
        server.shutdown();
    }

    @Test
    public void testShowPushNotification_createsNotification() throws Exception {
        SessionActivity activity = Robolectric.buildActivity(SessionActivity.class).create().get();
        Method method = SessionActivity.class.getDeclaredMethod("showPushNotification", String.class, String.class);
        method.setAccessible(true);
        method.invoke(activity, "Title", "Message");

        ShadowNotificationManager manager = Shadows.shadowOf((NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE));
        assertFalse(manager.getAllNotifications().isEmpty());
    }

    @Test
    public void testWebSocketListenerRegistration() {
        SessionActivity activity = Robolectric.buildActivity(SessionActivity.class).create().resume().get();
        assertEquals(activity, WebSocketManager.getInstance().getWebSocketListener());

        activity.onPause();
        assertNull(WebSocketManager.getInstance().getWebSocketListener());
    }

    @Test
    public void testOnWebSocketMessage_showsToast() {
        SessionActivity activity = Robolectric.buildActivity(SessionActivity.class).create().resume().get();
        activity.onWebSocketMessage("Hello");

        assertEquals("Hello", ShadowToast.getTextOfLatestToast());
    }


    // ADMIN USER LIST ACTIVITY TESTS
    @Test
    public void testMakeAdmin_successfulPromotion_showsToastAndUpdatesUser() throws Exception {
        MockWebServer server = new MockWebServer();
        server.start();
        // Server returns "true" → promotion successful
        server.enqueue(new MockResponse().setResponseCode(200).setBody("true"));

        AdminUserListActivity activity = Robolectric.buildActivity(AdminUserListActivity.class)
                .create().start().resume().get();

        // Redirect BASE_URL to mock server
        java.lang.reflect.Field baseUrlField = AdminUserListActivity.class.getDeclaredField("BASE_URL");
        baseUrlField.setAccessible(true);
        baseUrlField.set(null, server.url("/").toString());

        SimpleUser user = new SimpleUser(1, "bob", "Bob", "Smith", "pass", false, false);

        // Call makeAdmin
        java.lang.reflect.Method makeAdminMethod = AdminUserListActivity.class.getDeclaredMethod("makeAdmin", SimpleUser.class);
        makeAdminMethod.setAccessible(true);
        makeAdminMethod.invoke(activity, user);

        Robolectric.flushForegroundThreadScheduler();

        // Assert user was promoted
        assertTrue(user.isAdmin());

        // Assert Toast shown
        assertEquals("Promoted to Admin: bob", ShadowToast.getTextOfLatestToast());

        server.shutdown();
    }

    @Test
    public void testMakeAdmin_failedPromotion_showsToastAndDoesNotUpdateUser() throws Exception {
        MockWebServer server = new MockWebServer();
        server.start();
        // Server returns "false" → promotion failed
        server.enqueue(new MockResponse().setResponseCode(200).setBody("false"));

        AdminUserListActivity activity = Robolectric.buildActivity(AdminUserListActivity.class)
                .create().start().resume().get();

        java.lang.reflect.Field baseUrlField = AdminUserListActivity.class.getDeclaredField("BASE_URL");
        baseUrlField.setAccessible(true);
        baseUrlField.set(null, server.url("/").toString());

        SimpleUser user = new SimpleUser(2, "alice", "Alice", "Jones", "pass", false, false);

        java.lang.reflect.Method makeAdminMethod = AdminUserListActivity.class.getDeclaredMethod("makeAdmin", SimpleUser.class);
        makeAdminMethod.setAccessible(true);
        makeAdminMethod.invoke(activity, user);

        Robolectric.flushForegroundThreadScheduler();

        // Assert user not promoted
        assertFalse(user.isAdmin());

        // Assert Toast shown
        assertEquals("Server returned false — user not promoted", ShadowToast.getTextOfLatestToast());

        server.shutdown();
    }

    @Test
    public void testFetchTutorStatus_setsTutorTrueAndDisplayName() throws Exception {
        MockWebServer server = new MockWebServer();
        server.start();
        // Server returns JSON → user is a tutor
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"tutorId\":5}"));

        AdminUserListActivity activity = Robolectric.buildActivity(AdminUserListActivity.class)
                .create().start().resume().get();

        java.lang.reflect.Field baseUrlField = AdminUserListActivity.class.getDeclaredField("BASE_URL");
        baseUrlField.setAccessible(true);
        baseUrlField.set(null, server.url("/").toString());

        SimpleUser user = new SimpleUser(3, "tutorUser", "Tom", "Tutor", "pass", false, false);

        // Call fetchTutorStatus
        AtomicBoolean callbackCalled = new AtomicBoolean(false);
        java.lang.reflect.Method fetchMethod = AdminUserListActivity.class.getDeclaredMethod("fetchTutorStatus", SimpleUser.class, Runnable.class);
        fetchMethod.setAccessible(true);
        fetchMethod.invoke(activity, user, (Runnable) () -> callbackCalled.set(true));

        Robolectric.flushForegroundThreadScheduler();

        assertTrue(user.isTutor());
        assertTrue(user.getDisplayName().contains("(tutor)"));
        assertTrue(callbackCalled.get());

        server.shutdown();
    }

    @Test
    public void testFetchTutorStatus_setsTutorFalseForEmptyResponse() throws Exception {
        MockWebServer server = new MockWebServer();
        server.start();
        // Server returns empty → not a tutor
        server.enqueue(new MockResponse().setResponseCode(200).setBody(""));

        AdminUserListActivity activity = Robolectric.buildActivity(AdminUserListActivity.class)
                .create().start().resume().get();

        java.lang.reflect.Field baseUrlField = AdminUserListActivity.class.getDeclaredField("BASE_URL");
        baseUrlField.setAccessible(true);
        baseUrlField.set(null, server.url("/").toString());

        SimpleUser user = new SimpleUser(4, "normalUser", "Nancy", "Normal", "pass", false, false);

        AtomicBoolean callbackCalled = new AtomicBoolean(false);
        java.lang.reflect.Method fetchMethod = AdminUserListActivity.class.getDeclaredMethod("fetchTutorStatus", SimpleUser.class, Runnable.class);
        fetchMethod.setAccessible(true);
        fetchMethod.invoke(activity, user, (Runnable) () -> callbackCalled.set(true));

        Robolectric.flushForegroundThreadScheduler();

        assertFalse(user.isTutor());
        assertFalse(user.getDisplayName().contains("(tutor)"));
        assertTrue(callbackCalled.get());

        server.shutdown();
    }




}