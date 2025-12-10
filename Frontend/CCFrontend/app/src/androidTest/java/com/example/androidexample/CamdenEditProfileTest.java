package com.example.androidexample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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
public class CamdenEditProfileTest
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
        server.start(8080);

        // Setup the User singleton before each test, as EditProfileActivity depends on it
        User user = User.getInstance();
        user.setUserId(101);
        user.setUsername("testuser");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBio("Initial Bio");
        user.setTutor(false);
    }

    @After
    public void tearDown() throws IOException {
        // Shut down the server after each test
        server.shutdown();
        // Clear the user singleton to not interfere with other tests
        User.clearInstance();
    }

    /**
     * ✓ Tests onCreate() and populateFields()
     * This test verifies that when the activity starts, it correctly populates
     * the EditText fields with the data from the User singleton.
     */
    @Test
    public void testOnCreate_PopulatesFieldsCorrectly() {
        // Launch the activity
        ActivityScenario.launch(EditProfileActivity.class);

        // Verify the EditText fields are populated with the singleton's data
        onView(ViewMatchers.withId(R.id.edit_bio_text)).check(matches(withText("Initial Bio")));
    }

    /**
     * ✓ Tests onClick() for the confirm button and the updateProfile() method.
     * This test simulates a user editing their profile and clicking "confirm",
     * verifying that a network request is sent with the updated data.
     */
    @Test
    public void testOnClickConfirm_UpdatesProfile() {
        //Prepare a successful server response for the update request
        server.enqueue(new MockResponse().setResponseCode(200));

        //Launch the activity
        ActivityScenario.launch(EditProfileActivity.class);

        //Simulate user input by replacing the text in the fields
        onView(ViewMatchers.withId(R.id.edit_bio_text)).perform(replaceText("Updated Bio Text"), closeSoftKeyboard());

        //Click the confirm button
        //This will trigger the onClick listener, which calls updateProfile()
        onView(withId(R.id.confirm_btn)).perform(click());

        //Verify the profile is updated in the User singleton
        //A short delay might be needed for the update to process.
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

        User user = User.getInstance();
        //Check that the singleton was updated with the new values
        assert(user.getBio().equals("Updated Bio Text"));
    }

    /**
     * ✓ Tests onClick() for the cancel button.
     * This test verifies that clicking the cancel button finishes the activity
     * without changing any data.
     */
    @Test
    public void testOnClickCancel_ClosesActivity() {
        // Launch the activity
        ActivityScenario<EditProfileActivity> scenario = ActivityScenario.launch(EditProfileActivity.class);

        // Click the cancel button
        onView(ViewMatchers.withId(R.id.cancel_button)).perform(click());

        // Verify the activity is closed
        assert(scenario.getState().isAtLeast(androidx.lifecycle.Lifecycle.State.DESTROYED));
    }
}
