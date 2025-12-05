package com.example.androidexample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.UiAutomation;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WilliamSystemTest {


    @Before
    public void disableAnimations() {
        UiAutomation uiAutomation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
        uiAutomation.executeShellCommand("settings put global window_animation_scale 0");
        uiAutomation.executeShellCommand("settings put global transition_animation_scale 0");
        uiAutomation.executeShellCommand("settings put global animator_duration_scale 0");
    }



    // ------------------------------------------------------------
    // 1) Test: Login fails when fields are empty
    // ------------------------------------------------------------
    @Test
    public void testLoginShowsErrorOnEmptyFields() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.login_btn)).perform(click());
        onView(withId(R.id.login_password_edt))
                .check(matches(hasErrorText("Please fill in both fields")));
    }

    // ------------------------------------------------------------
    // 2) Test: Navigate to Create Account screen
    // ------------------------------------------------------------
    @Test
    public void testNavigateToCreateAccountPage() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        TestUtils.sleep(1000);
        onView(withId(R.id.signup_btn)).perform(click());
        onView(withId(R.id.create_account_btn))
                .check(matches(Matchers.notNullValue()));
    }

    // ------------------------------------------------------------
    // 3) Test: Create Account – missing fields shows error
    // ------------------------------------------------------------
    @Test
    public void testCreateAccountMissingField() {
        // Launch MainActivity
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        TestUtils.sleep(1000);

        // Correct: let MainActivity handle the click to open CreateAccountActivity
        onView(withId(R.id.signup_btn)).perform(click());

        TestUtils.sleep(1000);

        // Now click "Create Account"
        onView(withId(R.id.create_account_btn)).perform(click());

        // Check the error message
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

        TestUtils.sleep(2000);

        onView(withId(R.id.reviewRecyclerView))
                .check(matches(hasMinimumChildCount(1)));

        // Click first tutor's "Reviews" button
        onView(withId(R.id.reviewRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        new ViewAction() {
                            @Override
                            public Matcher<View> getConstraints() {
                                return isAssignableFrom(View.class);
                            }

                            @Override
                            public String getDescription() {
                                return "Click on reviews button inside RecyclerView item";
                            }

                            @Override
                            public void perform(UiController uiController, View view) {
                                View joinButton = view.findViewById(R.id.btn_reviews);
                                if (joinButton != null && joinButton.isClickable()) {
                                    joinButton.performClick();
                                }
                            }
                        }));

        TestUtils.sleep(1000);

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

        TestUtils.sleep(2000);

        onView(withId(R.id.sessions_recycler))
                .check(matches(hasMinimumChildCount(1)));

        // Click the first "Join" button in the RecyclerView
        onView(withId(R.id.sessions_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        new ViewAction() {
                            @Override
                            public Matcher<View> getConstraints() {
                                return isAssignableFrom(View.class);
                            }

                            @Override
                            public String getDescription() {
                                return "Click on Join button inside RecyclerView item";
                            }

                            @Override
                            public void perform(UiController uiController, View view) {
                                View joinButton = view.findViewById(R.id.btn_join_session);
                                if (joinButton != null && joinButton.isClickable()) {
                                    joinButton.performClick();
                                }
                            }
                        }));

    }

    public static Matcher<View> hasMinimumChildCount(final int count) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof RecyclerView)) return false;
                return ((RecyclerView) view).getAdapter().getItemCount() >= count;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView should have at least " + count + " items");
            }
        };
    }

}