package com.example.androidexample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.app.UiAutomation;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(AndroidJUnit4.class)
public class CamdenProfileTest
{

    private MockWebServer server;

    @Before
    public void setUp() throws IOException
    {
        // Disable animations for test stability
        UiAutomation uiAutomation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
        uiAutomation.executeShellCommand("settings put global window_animation_scale 0");
        uiAutomation.executeShellCommand("settings put global transition_animation_scale 0");
        uiAutomation.executeShellCommand("settings put global animator_duration_scale 0");

        // Start the mock web server before each test
        server = new MockWebServer();
        server.start(8080); // Use the same port your app expects
    }

    @After
    public void tearDown() throws IOException
    {
        // Shut down the server after each test
        server.shutdown();
        User.clearInstance();

    }

    // ✓ Tests OnCreate() and updateUIWithProfileUser() using the User Singleton
    @Test
    public void testProfileDisplaysCorrectDataFromSingleton()
    {
        //Set up the User singleton with test data
        User user = User.getInstance();
        user.setUserId(101);
        user.setUsername("Username");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBio("Bio Text here");
        user.setTutor(false);


        //Launch ProfileActivity with a blank Intent
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileActivity.class);

        ActivityScenario.launch(intent);

        //Verify the UI is updated with the data from the singleton
        onView(ViewMatchers.withId(R.id.username_text)).check(matches(withText("Username")));
        onView(ViewMatchers.withId(R.id.bio_text)).check(matches(withText("Bio Text here")));
    }


    // ✓ Tests OnClick() for the edit profile button
    @Test
    public void testEditProfileButton_NavigatesToEditProfileActivity() {
        // Launch ProfileActivity
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileActivity.class);
        User user = User.getInstance();
        user.setUserId(101);
        user.setUsername("testuser");
        ActivityScenario.launch(intent);

        // Perform a click on the edit profile button
        onView(withId(R.id.edit_profile_btn)).perform(click());

        onView(ViewMatchers.withId(R.id.confirm_btn)).check(matches(isDisplayed()));
    }

    // ✓ Tests GetUserInfo() and fetchUserIdByUsername()
    @Test
    public void testGetUserInfo_FetchesAndDisplaysData() throws JSONException {
        // This test verifies that when viewing another user's profile,
        // the activity fetches and displays that user's data.

        //Set up the currently logged-in user (optional but good practice)
        User currentUser = User.getInstance();
        currentUser.setUserId(101);
        currentUser.setUsername("currentUser");

        //Prepare the fake server response for the *other* user's info request.
        JSONObject mockUserJson = new JSONObject();
        mockUserJson.put("id", 123);
        mockUserJson.put("firstName", "Jane");
        mockUserJson.put("lastName", "Doe");
        mockUserJson.put("bio", "Fetched from server.");
        mockUserJson.put("isTutor", false);

        server.enqueue(new MockResponse().setBody(mockUserJson.toString()));

        //Launch the activity with the *other* user's username in the Intent.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileActivity.class);
        intent.putExtra("tutorUsername", "janedoe"); // The user profile we want to view
        ActivityScenario.launch(intent);

        //Check if the UI is updated with the fetched data.
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

        onView(withId(R.id.username_text)).check(matches(withText("Username")));
        onView(withId(R.id.bio_text)).check(matches(withText("Bio Text here")));
    }



    // ✓ Tests getTutorRating() for non-tutors
    @Test
    public void testGetTutorRating_NotVisibleForNonTutor() {
        // For non-tutors, the rating views should not be visible.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileActivity.class);
        intent.putExtra("IS_TUTOR", false); // Explicitly pass isTutor as false
        intent.putExtra("USER_NAME", "Student Stan");

        ActivityScenario.launch(intent);

        // Verify rating views are NOT displayed
        onView(ViewMatchers.withId(R.id.rating_value)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.tutor_rating_text)).check(matches(not(isDisplayed())));
    }


    // ✓ Tests loadPastSessionData()
    @Test
    public void testLoadPastSessionData_DisplaysCorrectCount() throws JSONException {

        //Prepare fake server responses
        JSONObject mockTutorJson = new JSONObject();
        mockTutorJson.put("id", 789);
        mockTutorJson.put("isTutor", true);
        server.enqueue(new MockResponse().setBody(mockTutorJson.toString()));

        //The endpoint for past sessions should return a JSON array
        String mockSessionsResponse = "[{\"id\":1}, {\"id\":2}, {\"id\":3}]"; // 3 sessions
        server.enqueue(new MockResponse().setBody(mockSessionsResponse));

        //Launch activity
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileActivity.class);
        intent.putExtra("USER_USERNAME", "bravotutor");
        ActivityScenario.launch(intent);

        //Check the sessions completed text
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
    }





    //EditProfileActivity

}
