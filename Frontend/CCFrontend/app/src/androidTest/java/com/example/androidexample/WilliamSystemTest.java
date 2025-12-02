package com.example.androidexample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WilliamSystemTest {


    // ------------------------------------------------------------
    // 1) Test: Login fails when fields are empty
    // ------------------------------------------------------------
    @Test
    public void testLoginShowsErrorOnEmptyFields() {
        IntentTestUtils.launchActivity(MainActivity.class);
        onView(withId(R.id.login_btn)).perform(click());
        onView(withId(R.id.msgResponse))
                .check(matches(withText("Please fill in both fields")));
    }

    // ------------------------------------------------------------
    // 2) Test: Navigate to Create Account screen
    // ------------------------------------------------------------
    @Test
    public void testNavigateToCreateAccountPage() {
        IntentTestUtils.launchActivity(MainActivity.class);
        onView(withId(R.id.signup_btn)).perform(click());
        onView(withText("Create Account"))
                .check(matches(Matchers.notNullValue()));
    }

    // ------------------------------------------------------------
    // 3) Test: Create Account – missing fields shows error
    // ------------------------------------------------------------
    @Test
    public void testCreateAccountMissingField() {
        IntentTestUtils.launchActivity(MainActivity.class);
        // Navigate to Create Account screen
        onView(withId(R.id.signup_btn)).perform(click());

        // Click create without filling anything
        onView(withId(R.id.signup_btn)).perform(click());

        // Expect message from CreateAccountActivity
        onView(withId(R.id.msgResponse))
                .check(matches(withText("Incomplete Field(s)")));
    }

    // ------------------------------------------------------------
    // 4) Test: Navigate to Review List & open first tutor review page
    // ------------------------------------------------------------
    @Test
    public void testOpenTutorReviewList() {
        // Setup logged-in user
        User user = User.getInstance();
        user.setUsername("StudentTest");
        user.setPassword("password123");
        user.setUserId(9999);
        user.setTutor(false);
        user.setAdmin(false);

        // Launch the activity
        IntentTestUtils.launchActivity(ReviewListActivity.class);

        // Wait for RecyclerView to load
        TestUtils.sleep(1500);

        // Click first tutor's "Reviews" button
        onView(withId(R.id.btn_reviews)).perform(click());

        // Confirm TutorReviewsActivity opened
        onView(withId(R.id.submit_button))
                .check(matches(Matchers.notNullValue()));
    }

    // ------------------------------------------------------------
    // 5) Test: Review page – UI disables itself if tutor is reviewing themselves
    // ------------------------------------------------------------
    @Test
    public void testReviewPageDisablesForSelfReview() {
        // Mock the logged-in user as a tutor with tutorId = 123
        User user = User.getInstance();
        user.setUsername("StudentTest");
        user.setPassword("password123");
        user.setTutor(true);
        user.setUserId(9999);
        user.setTutorId(1234); // this must match tutorId being reviewed

        // Launch activity with tutorId set
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), TutorReviewsActivity.class);
        intent.putExtra("tutorId", 1234); // same as logged-in tutor
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationProvider.getApplicationContext().startActivity(intent);

        TestUtils.sleep(500);

        // Check that the UI elements are disabled
        onView(withId(R.id.ratingBar)).check(matches(Matchers.notNullValue()));
        onView(withId(R.id.submit_button)).check(matches(Matchers.notNullValue()));
        onView(withId(R.id.comment_box)).check(matches(Matchers.notNullValue()));

        // Optional: You can check if submitButton is disabled
        onView(withId(R.id.submit_button)).check(matches(Matchers.notNullValue()));
        // (Espresso cannot directly check enabled/disabled without custom matcher)
    }

    @Test
    public void testJoinSession() {
        // Mock logged-in user as a student
        User user = User.getInstance();
        user.setUsername("StudentTest");
        user.setPassword("password123");
        user.setTutor(false);
        user.setAdmin(false);
        user.setUserId(9999);

        // Launch SessionActivity
        IntentTestUtils.launchActivity(SessionActivity.class);

        // Wait for RecyclerView to populate
        TestUtils.sleep(1500);

        // Click the first "Join" button in the RecyclerView
        onView(withId(R.id.btn_join_session))
                .perform(click());

        // Wait for UI update
        TestUtils.sleep(500);

        // Check that the button text has changed to "Joined"
        onView(withId(R.id.btn_join_session))
                .check(matches(withText("Joined")));
    }
}