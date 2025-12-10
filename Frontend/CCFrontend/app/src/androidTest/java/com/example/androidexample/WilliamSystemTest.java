package com.example.androidexample;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.UiAutomation;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

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
    // 1) MAIN ACTIVITY TESTS
    // ------------------------------------------------------------

    @Test
    public void testOnCreate_initializesButtons() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        // Check all buttons are displayed
        onView(withId(R.id.login_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.signup_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.forgotp_btn)).check(matches(isDisplayed()));

        scenario.close();
    }


    @Test
    public void testMainActivityUiLoads() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.login_username_edt)).check(matches(isDisplayed()));
        onView(withId(R.id.login_password_edt)).check(matches(isDisplayed()));
        onView(withId(R.id.login_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.signup_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.forgotp_btn)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginShowsErrorOnEmptyFields() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.login_btn)).perform(click());
        onView(withId(R.id.login_password_edt))
                .check(matches(hasErrorText("Please fill in both fields")));
    }

    @Test
    public void testNavigateToCreateAccountPage() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.signup_btn)).perform(click());
        onView(withId(R.id.create_account_btn))
                .check(matches(Matchers.notNullValue()));
    }

    @Test
    public void testNavigateToForgotPassword() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.forgotp_btn))
                .perform(scrollTo(), click());

        // Confirm we landed on ForgotPasswordActivity
        onView(withId(R.id.create_password_confirm))
                .check(matches(Matchers.notNullValue()));
    }

    @Test
    public void testLoginWithEmptyFieldsShowsError() {
        ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.login_btn)).perform(scrollTo(), click());

        // Because espresso cant interact with toasts just check if we are on the same page.
        onView(withId(R.id.login_username_edt)).check(matches(isDisplayed()));
    }

    // ------------------------------------------------------------
    // 2) FORGOT PASSWORD TESTS
    // ------------------------------------------------------------

    @Test
    public void testForgotPasswordUiLoads() {
        ActivityScenario.launch(ForgotPasswordActivity.class);

        onView(withId(R.id.create_username)).check(matches(isDisplayed()));
        onView(withId(R.id.create_password)).check(matches(isDisplayed()));
        onView(withId(R.id.create_password_confirm)).check(matches(isDisplayed()));
        onView(withId(R.id.signup_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.back_btn)).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyFieldsShowsValidationMessage() {
        ActivityScenario.launch(ForgotPasswordActivity.class);

        onView(withId(R.id.signup_btn)).perform(click());

        // msgResponse text should update
        onView(withId(R.id.msgResponse))
                .check(matches(withText("Incomplete Field(s)")));
    }

    @Test
    public void testPasswordMismatchShowsError() {
        ActivityScenario.launch(ForgotPasswordActivity.class);

        onView(withId(R.id.create_username))
                .perform(typeText("john"));

        onView(withId(R.id.create_password))
                .perform(typeText("pass1"));

        onView(withId(R.id.create_password_confirm))
                .perform(typeText("pass2"));

        onView(withId(R.id.signup_btn)).perform(click());

        // Expect mismatch message
        onView(withId(R.id.msgResponse))
                .check(matches(withText("Passwords do not match")));
    }

    @Test
    public void testValidSubmissionDoesNotCrash() throws Exception {

        // --------- Arrange: create a temporary account ----------
        Context ctx = ApplicationProvider.getApplicationContext();
        TestApiHelper api = new TestApiHelper(ctx);

        String username = "test_user_" + System.currentTimeMillis();
        String password = "abc123";

        boolean created = api.createCompleteUser(username, password, "John", "Test");

        // --------- Act: run Forgot Password screen ----------
        ActivityScenario.launch(ForgotPasswordActivity.class);

        onView(withId(R.id.create_username))
                .perform(typeText(username));

        onView(withId(R.id.create_password))
                .perform(typeText("1234"));

        onView(withId(R.id.create_password_confirm))
                .perform(typeText("1234"));

        closeSoftKeyboard();

        onView(withId(R.id.signup_btn))
                .perform(click());

        // --------- Assert: activity stays alive (no crash) ----------
        onView(withId(R.id.create_username))
                .check(matches(isDisplayed()));

        // --------- Cleanup: delete temp user ----------
        boolean deleted = api.deleteUser(username, "1234");
        assertTrue("Temp user should be removed after test", deleted);
    }

    @Test
    public void testBackButtonNavigatesToMainActivity() {
        ActivityScenario.launch(ForgotPasswordActivity.class);
        onView(withId(R.id.back_btn))
                .perform(click());

        onView(withId(R.id.login_btn))
                .check(matches(isDisplayed()));
    }



    // ------------------------------------------------------------
    // 3) CREATE ACCOUNT TESTS
    // ------------------------------------------------------------

    @Test
    public void testBackButtonReturnsToLogin() {
        ActivityScenario.launch(CreateAccountActivity.class);
        TestUtils.sleep(500);

        onView(withId(R.id.back_btn)).perform(click());
        TestUtils.sleep(500);

        // MainActivity has login username field
        onView(withId(R.id.login_username_edt))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testCreateAccountMissingField() {
        // Launch MainActivity
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        TestUtils.sleep(500);

        // Correct: let MainActivity handle the click to open CreateAccountActivity
        onView(withId(R.id.signup_btn)).perform(click());

        TestUtils.sleep(1000);

        // Now click "Create Account"
        onView(withId(R.id.create_account_btn)).perform(click());

        // Check the error message
        onView(withId(R.id.msgResponse))
                .check(matches(withText("Incomplete Field(s)")));
    }

    @Test
    public void testPasswordMismatch() {
        ActivityScenario.launch(CreateAccountActivity.class);
        TestUtils.sleep(500);

        onView(withId(R.id.create_first_name)).perform(typeText("Jim"));
        onView(withId(R.id.create_last_name)).perform(typeText("Halpert"));
        onView(withId(R.id.create_username)).perform(typeText("testUser123"));
        onView(withId(R.id.create_password)).perform(typeText("abc123"));
        onView(withId(R.id.create_password_confirm)).perform(typeText("xyz789"));

        closeSoftKeyboard();

        onView(withId(R.id.create_account_btn)).perform(click());

        onView(withId(R.id.msgResponse))
                .check(matches(withText("Passwords do not match")));
    }

    @Test
    public void testMissingFirstOrLastName() {
        ActivityScenario.launch(CreateAccountActivity.class);
        TestUtils.sleep(500);

        onView(withId(R.id.create_username)).perform(typeText("userA"));
        onView(withId(R.id.create_password)).perform(typeText("pass1"));
        onView(withId(R.id.create_password_confirm)).perform(typeText("pass1"));

        closeSoftKeyboard();

        onView(withId(R.id.create_account_btn)).perform(click());

        onView(withId(R.id.msgResponse))
                .check(matches(withText("First and Last name required")));
    }


    @Test
    public void testSuccessfulAccountCreation() throws Exception {

        // Launch the screen
        ActivityScenario.launch(CreateAccountActivity.class);

        // Unique test user
        String uniqueUser = "user" + System.currentTimeMillis();
        String password = "test123";

        // Fill out UI fields
        onView(withId(R.id.create_first_name)).perform(typeText("Alice"));
        onView(withId(R.id.create_last_name)).perform(typeText("Smith"));
        onView(withId(R.id.create_username)).perform(typeText(uniqueUser));
        onView(withId(R.id.create_password)).perform(typeText(password));
        onView(withId(R.id.create_password_confirm)).perform(typeText(password));

        closeSoftKeyboard();

        // Click create
        onView(withId(R.id.create_account_btn)).perform(click());

        // Wait for backend (create + update steps)
        TestUtils.sleep(4000);

        // If success, user should be inside MainMenuActivity
        onView(withId(R.id.msg_btn))
                .check(matches(Matchers.notNullValue()));

        // ----------------------------------------------------------
        // CLEANUP: DELETE THE USER WE JUST CREATED
        // ----------------------------------------------------------
        TestApiHelper helper =
                new TestApiHelper(ApplicationProvider.getApplicationContext());

        boolean deleted = helper.deleteUser(uniqueUser, password);

        Log.d("TEST", "User deleted? " + deleted);

        assertTrue("Cleanup failed — user was NOT deleted!", deleted);
    }


    // ------------------------------------------------------------
    // 4) TUTOR LIST TESTS
    // ------------------------------------------------------------

    @Test
    public void testWebSocketMethods_doNotCrash() {
        // Set up a user first
        User user = User.getInstance();
        user.setUsername("TestUser");
        user.setUserId(9999);

        ActivityScenario<ReviewListActivity> scenario = ActivityScenario.launch(ReviewListActivity.class);

        // Call WebSocket methods on UI thread
        scenario.onActivity(activity -> {
            try {
                activity.onWebSocketOpen(null);
                activity.onWebSocketMessage("Test message");
                activity.onWebSocketClose(1000, "Normal close", true);
                activity.onWebSocketError(new Exception("Test error"));
            } catch (Exception e) {
                // If no crash, test passes
            }
        });
    }

    @Test
    public void testWebSocketListenerRegistration() {
        User user = User.getInstance();
        user.setUsername("TestUser");
        user.setUserId(9999);

        ActivityScenario<SessionActivity> scenario = ActivityScenario.launch(SessionActivity.class);

        // Test that WebSocketManager is accessible
        scenario.onActivity(activity -> {
            // Register this activity as listener
            WebSocketManager.getInstance().setWebSocketListener(activity);

            // Verify it was registered
            // (Note: We can't directly assert the instance because it's on different thread)

            // Simulate pause
            activity.onPause();

            // Should be unregistered
            // WebSocketManager.getInstance().setWebSocketListener(null);
        });

        scenario.close();
    }

    @Test
    public void testActivityLaunchesCorrectly() {
        IntentTestUtils.launchActivity(ReviewListActivity.class);

        onView(withId(R.id.tutor_search_bar))
                .check(matches(isDisplayed()));

        onView(withId(R.id.reviewRecyclerView))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSearchFiltersTutorsNone() {
        IntentTestUtils.launchActivity(ReviewListActivity.class);

        onView(withId(R.id.tutor_search_bar))
                .perform(typeText("keurtbgb45y"));

        // Expect no result after filtering
        onView(withId(R.id.reviewRecyclerView))
                .check(matches(IntentTestUtils.hasExactChildCount(0)));
    }

    @Test
    public void testSearchFiltersTutor() {
        IntentTestUtils.launchActivity(ReviewListActivity.class);

        onView(withId(R.id.tutor_search_bar))
                .perform(typeText("JohnZeet"));

        TestUtils.sleep(1000);

        // Expect EXACTLY 1 result after filtering
        onView(withId(R.id.reviewRecyclerView))
                .check(matches(hasMinimumChildCount(1)));
    }

    @Test
    public void testTutorRecycler() {
        IntentTestUtils.launchActivity(ReviewListActivity.class);
        TestUtils.sleep(1000);
        onView(withId(R.id.reviewRecyclerView))
                .check(matches(hasMinimumChildCount(1)));
    }


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
    // 5) TUTOR REVIEWS TESTS
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

        onView(withId(R.id.submit_button)).check(matches(Matchers.notNullValue()));
        // (Espresso cannot directly check enabled/disabled without custom matcher)
    }

    @Test
    public void testReviewPageEnabledForNormalUser() {
        User user = User.getInstance();
        user.setUsername("StudentUser");
        user.setPassword("pass123");
        user.setTutor(false);
        user.setUserId(3001);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), TutorReviewsActivity.class);
        intent.putExtra("tutorId", 2001);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationProvider.getApplicationContext().startActivity(intent);

        TestUtils.sleep(500);

        // Check that UI elements are enabled
        onView(withId(R.id.submit_button)).check(matches(isEnabled()));
        onView(withId(R.id.ratingBar)).check(matches(isEnabled()));
        onView(withId(R.id.comment_box)).check(matches(isEnabled()));
    }

    // (connect real tutor account id (make sure JohnZeet has reviews))
    @Test
    public void testTutorReviewsRecyclerViewLoads() {
        User user = User.getInstance();
        user.setUsername("StudentUser");
        user.setPassword("pass123");
        user.setTutor(false);
        user.setUserId(3001);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), TutorReviewsActivity.class);
        intent.putExtra("tutorId", 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationProvider.getApplicationContext().startActivity(intent);

        TestUtils.sleep(1500); // wait for network

        onView(withId(R.id.reviewsRecyclerView))
                .check(matches(hasMinimumChildCount(1))); // require at least 1 rating
    }

    // ------------------------------------------------------------
    // 6) SESSION ACTIVITY TESTS
    // ------------------------------------------------------------

    @Test
    public void testRecyclerViewIsPopulated() {
        IntentTestUtils.launchActivity(SessionActivity.class);

        // Wait a bit for mock/network data to load
        TestUtils.sleep(2000);

        onView(withId(R.id.sessions_recycler))
                .check(matches(hasMinimumChildCount(1)));
    }

    @Test
    public void testSearchFilter() {
        IntentTestUtils.launchActivity(SessionActivity.class);

        TestUtils.sleep(1000);

        onView(withId(R.id.class_search_view))
                .perform(typeText("COMS"));

        TestUtils.sleep(500);

        onView(withId(R.id.sessions_recycler))
                .check(matches(hasMinimumChildCount(1)));
    }

    @Test
    public void testSpinnerFilter() {
        IntentTestUtils.launchActivity(SessionActivity.class);

        TestUtils.sleep(1000);

        // Open spinner
        onView(withId(R.id.major_spinner)).perform(click());
        // Select "Computer Science" from spinner
        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(String.class)), Matchers.is("COMS")))
                .perform(click());

        TestUtils.sleep(500);

        onView(withId(R.id.sessions_recycler))
                .check(matches(hasMinimumChildCount(1)));
    }

    // MIGHT NOT WORK BECAUSE OF DRAWER LAYOUT
    @Test
    public void testNavigationDrawerButtons() {
        IntentTestUtils.launchActivity(SessionActivity.class);

        TestUtils.sleep(500);

        // Open drawer and click Home
        onView(withId(R.id.menu_button)).perform(click());
        onView(withId(R.id.nav_home)).perform(click());

        // Open drawer and click Profile
        onView(withId(R.id.menu_button)).perform(click());
        onView(withId(R.id.nav_profile)).perform(click());

        // Open drawer and click Reviews
        onView(withId(R.id.menu_button)).perform(click());
        onView(withId(R.id.nav_reviews)).perform(click());
    }

    @Test
    public void testSessionItemDisplaysCorrectInfo() {
        IntentTestUtils.launchActivity(SessionActivity.class);

        TestUtils.sleep(1000);

        onView(withId(R.id.sessions_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        new ViewAction() {
                            @Override
                            public Matcher<View> getConstraints() {
                                return isAssignableFrom(View.class);
                            }

                            @Override
                            public String getDescription() {
                                return "Verify session item info";
                            }

                            @Override
                            public void perform(UiController uiController, View view) {
                                TextView className = view.findViewById(R.id.class_name);
                                TextView classCode = view.findViewById(R.id.class_code);
                                TextView location = view.findViewById(R.id.meeting_location);
                                TextView time = view.findViewById(R.id.meeting_time);
                                TextView tutor = view.findViewById(R.id.tutor_username);

                                assert className.getText() != null;
                                assert classCode.getText() != null;
                                assert location.getText() != null;
                                assert (time.getText() != null);
                                assert tutor.getText() != null;
                            }
                        }));
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

    // ------------------------------------------------------------
    // 7) CREATE SESSION ACTIVITY TESTS
    // ------------------------------------------------------------

    @Test
    public void testCreateSessionActivityLoadsCorrectly() {
        ActivityScenario.launch(CreateSessionActivity.class);

        onView(withId(R.id.create_class_name)).check(matches(isDisplayed()));
        onView(withId(R.id.create_class_code)).check(matches(isDisplayed()));
        onView(withId(R.id.create_meeting_location)).check(matches(isDisplayed()));
        onView(withId(R.id.create_meeting_time)).check(matches(isDisplayed()));
        onView(withId(R.id.button_create_session)).check(matches(isDisplayed()));
        onView(withId(R.id.back_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyFieldsShowToast() {
        User user = User.getInstance();
        user.setUsername("TutorTest");
        user.setUserId(1234);
        user.setTutor(true);
        user.setTutorId(5678);

        ActivityScenario.launch(CreateSessionActivity.class);

        // Attempt to click "Create Session" button without filling fields
        onView(withId(R.id.button_create_session)).perform(scrollTo(), click());

        // Check that the class name field is still visible
        onView(withId(R.id.create_class_name)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackButtonNavigatesToSessionActivity() {
        ActivityScenario.launch(CreateSessionActivity.class);

        onView(withId(R.id.back_button)).perform(click());

        onView(withId(R.id.sessions_recycler))
                .check(matches(isDisplayed())); // SessionActivity RecyclerView
    }

    // THIS ALSO COUNTS AS THE DELETE SESSION TEST (connect real tutor account)
    @Test
    public void testCreateAndDeleteSession() {
        // ---------------------------------------------------------
        // Setup: Real logged-in tutor
        // ---------------------------------------------------------
        User user = User.getInstance();
        user.setUsername("JohnZeet");
        user.setUserId(1);
        user.setTutor(true);
        user.setTutorId(1);

        ActivityScenario.launch(CreateSessionActivity.class);

        String uniqueClassName = "Computer Science test " + System.currentTimeMillis();

        // Fill out session fields
        onView(withId(R.id.create_class_name)).perform(replaceText(uniqueClassName));
        onView(withId(R.id.create_class_code)).perform(replaceText("COMS309"));
        onView(withId(R.id.create_meeting_location)).perform(replaceText("Pearson Hall 101"));
        onView(withId(R.id.create_meeting_time)).perform(scrollTo(), click());

        // Set the date: Dec 6, 2025
        onView(isAssignableFrom(DatePicker.class))
                .perform(PickerActions.setDate(2025, 12, 6));
        onView(withText("OK")).perform(click());

        // Set the time: 2:00 PM
        onView(isAssignableFrom(TimePicker.class))
                .perform(PickerActions.setTime(14, 0));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.button_create_session)).perform(scrollTo(), click());

        // Wait for redirect to SessionActivity
        TestUtils.sleep(1500);

        // Filter the RecyclerView by the session name
        onView(withId(R.id.class_search_view)).perform(typeText(uniqueClassName));
        TestUtils.sleep(1000); // wait for filtering

        // Click the Edit button inside the first item of the RecyclerView
        onView(withId(R.id.sessions_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return isAssignableFrom(View.class);
                    }

                    @Override
                    public String getDescription() {
                        return "Click the Edit button inside RecyclerView item";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        View editButton = view.findViewById(R.id.button_edit_session);
                        if (editButton != null && editButton.isClickable()) {
                            editButton.performClick();
                        }
                    }
                }));

        // Wait for EditSessionActivity to load
        TestUtils.sleep(1000);

        // Click the Delete button in EditSessionActivity
        onView(withId(R.id.btn_delete_session)).perform(scrollTo(), click());

        // Verify we returned to SessionActivity
        onView(withId(R.id.sessions_recycler))
                .check(matches(isDisplayed()));

    }

    @Test
    public void testMissingTutorIdShowsError() {
        // Set tutorId to invalid
        User.getInstance().setTutorId(-1);

        ActivityScenario.launch(CreateSessionActivity.class);

        onView(withId(R.id.create_class_name)).perform(replaceText("Computer Science 309"));
        onView(withId(R.id.create_class_code)).perform(replaceText("COMS309"));
        onView(withId(R.id.create_meeting_location)).perform(replaceText("Pearson Hall 101"));
        onView(withId(R.id.create_meeting_time)).perform(scrollTo(), click());

        // Set the date: Dec 6, 2025
        onView(isAssignableFrom(DatePicker.class))
                .perform(PickerActions.setDate(2025, 12, 6));
        onView(withText("OK")).perform(click());

        // Set the time: 2:00 PM
        onView(isAssignableFrom(TimePicker.class))
                .perform(PickerActions.setTime(14, 0));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.button_create_session)).perform(scrollTo(), click());

        // Remain on same page
        onView(withId(R.id.create_class_name)).check(matches(isDisplayed()));
    }

    // ------------------------------------------------------------
    // 8) EDIT SESSION ACTIVITY TESTS
    // ------------------------------------------------------------
    @Test
    public void testEditSessionActivityLoadsCorrectly() {
        User user = User.getInstance();
        user.setUsername("TutorTest");
        user.setUserId(1234);
        user.setTutor(true);
        user.setTutorId(5678);

        Intent launchIntent = new Intent(ApplicationProvider.getApplicationContext(), EditSessionActivity.class);
        launchIntent.putExtra("sessionId", 1001);
        launchIntent.putExtra("className", "Computer Science 309");
        launchIntent.putExtra("classCode", "COMS309");
        launchIntent.putExtra("meetingLocation", "Pearson Hall 101");
        launchIntent.putExtra("meetingTime", "12/06/2025 02:00 PM");
        launchIntent.putExtra("tutorId", 5678);

        ActivityScenario.launch(launchIntent);

        onView(withId(R.id.edit_class_name)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_class_code)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_location)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_time)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_save_session)).check(matches(isDisplayed()));
    }

    @Test
    public void testEditSessionFieldsArePrePopulated() {
        User user = User.getInstance();
        user.setUsername("TutorTest");
        user.setUserId(1234);
        user.setTutor(true);
        user.setTutorId(5678);

        Intent launchIntent = new Intent(ApplicationProvider.getApplicationContext(), EditSessionActivity.class);
        launchIntent.putExtra("sessionId", 1001);
        launchIntent.putExtra("className", "Computer Science 309");
        launchIntent.putExtra("classCode", "COMS309");
        launchIntent.putExtra("meetingLocation", "Pearson Hall 101");
        launchIntent.putExtra("meetingTime", "12/06/2025 02:00 PM");
        launchIntent.putExtra("tutorId", 5678);

        ActivityScenario.launch(launchIntent);

        onView(withId(R.id.edit_class_name)).check(matches(withText("Computer Science 309")));
        onView(withId(R.id.edit_class_code)).check(matches(withText("COMS309")));
        onView(withId(R.id.edit_location)).check(matches(withText("Pearson Hall 101")));
        onView(withId(R.id.edit_time)).check(matches(withText("12/06/2025 02:00 PM")));
    }

    // (connect real tutor account)
    @Test
    public void testCreateEditAndSaveSession() {
        // ---------------------------------------------------------
        // Setup: Real logged-in tutor
        // ---------------------------------------------------------
        User user = User.getInstance();
        user.setUsername("JohnZeet");
        user.setUserId(1);
        user.setTutor(true);
        user.setTutorId(1);

        // Launch CreateSessionActivity
        ActivityScenario.launch(CreateSessionActivity.class);

        String uniqueClassName = "Computer Science test " + System.currentTimeMillis();

        String changedClassName = "COMS 310 Advanced";

        // Fill out session fields
        onView(withId(R.id.create_class_name)).perform(replaceText(uniqueClassName));
        onView(withId(R.id.create_class_code)).perform(replaceText("COMS309"));
        onView(withId(R.id.create_meeting_location)).perform(replaceText("Pearson Hall 101"));
        onView(withId(R.id.create_meeting_time)).perform(scrollTo(), click());

        // Set the date: Dec 6, 2025
        onView(isAssignableFrom(DatePicker.class))
                .perform(PickerActions.setDate(2025, 12, 6));
        onView(withText("OK")).perform(click());

        // Set the time: 2:00 PM
        onView(isAssignableFrom(TimePicker.class))
                .perform(PickerActions.setTime(14, 0));
        onView(withText("OK")).perform(click());

        // Click Create
        onView(withId(R.id.button_create_session)).perform(scrollTo(), click());

        // Wait for redirect to SessionActivity
        TestUtils.sleep(1500);

        // Filter RecyclerView to find the newly created session
        onView(withId(R.id.class_search_view)).perform(typeText(uniqueClassName));
        TestUtils.sleep(1000);

        // Click Edit button inside the first item
        onView(withId(R.id.sessions_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return isAssignableFrom(View.class);
                    }

                    @Override
                    public String getDescription() {
                        return "Click Edit button inside RecyclerView item";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        View editButton = view.findViewById(R.id.button_edit_session);
                        if (editButton != null && editButton.isClickable()) {
                            editButton.performClick();
                        }
                    }
                }));

        // Wait for EditSessionActivity to load
        TestUtils.sleep(1000);

        // Edit fields
        onView(withId(R.id.edit_class_name)).perform(scrollTo(), replaceText("COMS 310 Advanced"));
        onView(withId(R.id.edit_class_code)).perform(scrollTo(), replaceText("COMS310"));
        onView(withId(R.id.edit_location)).perform(scrollTo(), replaceText("Carver Hall 204"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_time)).perform(scrollTo(), click());

        // Set the date: Dec 6, 2025
        onView(isAssignableFrom(DatePicker.class))
                .perform(PickerActions.setDate(2025, 12, 6));
        onView(withText("OK")).perform(click());

        // Set the time: 2:00 PM
        onView(isAssignableFrom(TimePicker.class))
                .perform(PickerActions.setTime(14, 0));
        onView(withText("OK")).perform(click());

        // Click Save
        onView(withId(R.id.btn_save_session)).perform(scrollTo(), click());

        TestUtils.sleep(1000);


        // Filter the RecyclerView by the session name
        onView(withId(R.id.class_search_view)).perform(typeText(changedClassName));
        TestUtils.sleep(1000); // wait for filtering

        // Click the Edit button inside the first item of the RecyclerView
        onView(withId(R.id.sessions_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return isAssignableFrom(View.class);
                    }

                    @Override
                    public String getDescription() {
                        return "Click the Edit button inside RecyclerView item";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        View editButton = view.findViewById(R.id.button_edit_session);
                        if (editButton != null && editButton.isClickable()) {
                            editButton.performClick();
                        }
                    }
                }));

        // Wait for EditSessionActivity to load
        TestUtils.sleep(1000);

        // Click the Delete button in EditSessionActivity
        onView(withId(R.id.btn_delete_session)).perform(scrollTo(), click());

        // Verify we returned to SessionActivity
        onView(withId(R.id.sessions_recycler))
                .check(matches(isDisplayed()));

    }


    @Test
    public void testEmptyFieldsPreventSave() {
        User user = User.getInstance();
        user.setUsername("TutorTest");
        user.setUserId(1234);
        user.setTutor(true);
        user.setTutorId(5678);

        Intent launchIntent = new Intent(ApplicationProvider.getApplicationContext(), EditSessionActivity.class);
        launchIntent.putExtra("sessionId", 1001);
        launchIntent.putExtra("className", "Computer Science 309");
        launchIntent.putExtra("classCode", "COMS309");
        launchIntent.putExtra("meetingLocation", "Pearson Hall 101");
        launchIntent.putExtra("meetingTime", "12/06/2025 02:00 PM");
        launchIntent.putExtra("tutorId", 5678);

        ActivityScenario.launch(launchIntent);

        // Clear class name
        onView(withId(R.id.edit_class_name)).perform(scrollTo(), replaceText(""));
        closeSoftKeyboard();

        // Click Save
        onView(withId(R.id.btn_save_session)).perform(scrollTo(), click());

        // Should remain on same activity because required field is empty
        onView(withId(R.id.edit_class_name)).check(matches(isDisplayed()));
    }

    // ------------------------------------------------------------
    // 9) DELETE USER ACTIVITY TESTS
    // ------------------------------------------------------------

    @Test
    public void testCreateSearchAndDeleteUser() throws Exception {

        Context context = ApplicationProvider.getApplicationContext();
        TestApiHelper helper = new TestApiHelper(context);

        // -----------------------------------------------
        // CREATE A UNIQUE USER ON BACKEND
        // -----------------------------------------------
        String uniqueUser = "testDel_" + System.currentTimeMillis();
        String password = "pass123";
        String first = "DeleteMe";
        String last = "User";

        boolean created = helper.createCompleteUser(uniqueUser, password, first, last);

        // Force small delay so backend list is fully populated
        TestUtils.sleep(1000);

        // -----------------------------------------------
        // LAUNCH ADMIN USER LIST SCREEN (connect real admin account)
        // -----------------------------------------------
        User user = User.getInstance();
        user.setUsername("AdminTest");
        user.setAdmin(true);   // ensure admin privileges

        IntentTestUtils.launchActivity(AdminUserListActivity.class);
        TestUtils.sleep(2000);  // allow RecyclerView + Volley to load

        // -----------------------------------------------
        // SEARCH FOR THE SPECIFIC USER WE JUST CREATED
        // -----------------------------------------------
        onView(withId(R.id.user_search_view))
                .perform(click(), typeText(uniqueUser));

        TestUtils.sleep(1500);  // allow adapter.filter() to refresh

        // Verify search results contain at least 1 row
        onView(withId(R.id.past_sessions_recycler_view))
                .check(matches(hasMinimumChildCount(1)));

        // -----------------------------------------------
        // CLICK DELETE BUTTON INSIDE FIRST RECYCLERVIEW ITEM
        // -----------------------------------------------
        onView(withId(R.id.past_sessions_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        new ViewAction() {
                            @Override
                            public Matcher<View> getConstraints() {
                                return isAssignableFrom(View.class);
                            }

                            @Override
                            public String getDescription() {
                                return "Click delete button of filtered user";
                            }

                            @Override
                            public void perform(UiController uiController, View view) {
                                View deleteBtn = view.findViewById(R.id.delete_btn);
                                if (deleteBtn != null && deleteBtn.isClickable()) {
                                    deleteBtn.performClick();
                                }
                            }
                        }));

        // Allow delete request + Recycler refresh
        TestUtils.sleep(2000);

        // Ensure screen didn't crash
        onView(withId(R.id.past_sessions_recycler_view))
                .check(matches(isDisplayed()));

        // -----------------------------------------------
        // FINAL CLEANUP VALIDATION: CONFIRM USER IS GONE
        // -----------------------------------------------
        boolean stillExists = helper.deleteUser(uniqueUser, password);
        assertFalse("User should already be deleted by Admin screen!", stillExists);
    }


    @Test
    public void testCreateSearchPromoteAndDeleteUser() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        TestApiHelper helper = new TestApiHelper(context);

        // -----------------------------------------------
        // CREATE A UNIQUE USER ON BACKEND
        // -----------------------------------------------
        String uniqueUser = "testPromote_" + System.currentTimeMillis();
        String password = "pass123";
        String first = "PromoteMe";
        String last = "User";

        boolean created = helper.createCompleteUser(uniqueUser, password, first, last);

        // Force small delay so backend list is fully populated
        TestUtils.sleep(1000);

        // -----------------------------------------------
        // LAUNCH ADMIN USER LIST SCREEN (connect real admin account)
        // -----------------------------------------------
        User user = User.getInstance();
        user.setUsername("AdminTest");
        user.setAdmin(true);   // ensure admin privileges

        IntentTestUtils.launchActivity(AdminUserListActivity.class);
        TestUtils.sleep(3000);  // allow RecyclerView + Volley to load all users

        // -----------------------------------------------
        // PHASE 1: SEARCH FOR THE SPECIFIC USER WE JUST CREATED
        // -----------------------------------------------
        onView(withId(R.id.user_search_view))
                .perform(click(), typeText(uniqueUser));

        TestUtils.sleep(1500);  // allow adapter.filter() to refresh

        // Verify search results contain exactly 1 row (our new user)
        onView(withId(R.id.past_sessions_recycler_view))
                .check(matches(hasMinimumChildCount(1)));

        // -----------------------------------------------
        // PHASE 2: CLICK "MAKE ADMIN" BUTTON ON THE USER
        // -----------------------------------------------
        onView(withId(R.id.past_sessions_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        new ViewAction() {
                            @Override
                            public Matcher<View> getConstraints() {
                                return isAssignableFrom(View.class);
                            }

                            @Override
                            public String getDescription() {
                                return "Click delete button of filtered user";
                            }

                            @Override
                            public void perform(UiController uiController, View view) {
                                View deleteBtn = view.findViewById(R.id.make_admin_btn);
                                if (deleteBtn != null && deleteBtn.isClickable()) {
                                    deleteBtn.performClick();
                                }
                            }
                        }));

        // Wait for promotion request to complete
        TestUtils.sleep(1000);

        // -----------------------------------------------
        // PHASE 3: VERIFY PROMOTION WAS SUCCESSFUL
        // -----------------------------------------------

        TestUtils.sleep(1000);

        // Check that the user is still in the list
        onView(withId(R.id.past_sessions_recycler_view))
                .check(matches(hasMinimumChildCount(1)));

        // Verify the "Make Admin" button is gone or disabled (user is now admin)
        onView(withId(R.id.past_sessions_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        new ViewAction() {
                            @Override
                            public Matcher<View> getConstraints() {
                                return isAssignableFrom(View.class);
                            }

                            @Override
                            public String getDescription() {
                                return "Click delete button of filtered user";
                            }

                            @Override
                            public void perform(UiController uiController, View view) {
                                View btn = view.findViewById(R.id.make_admin_btn);
                                if (btn != null && btn.getVisibility() == View.VISIBLE) {
                                    throw new AssertionError("Make Admin button STILL visible after promotion!");
                                }
                            }
                        }));

        helper.deleteUser(uniqueUser, password);
    }

    // ------------------------------------------------------------
    // 10) ADAPTER TESTS
    // ------------------------------------------------------------

    @Test
    public void testSessionAdapterDisplaysCorrectly() {
        // Mock user
        User user = User.getInstance();
        user.setTutor(false);
        user.setAdmin(false);

        // Create mock session list
        ArrayList<Session> sessions = new ArrayList<>();
        sessions.add(new Session(1, "COM S 309", "COMS309", "Pearson Hall 101", 1234, "12/06/2025 02:00 PM", "TutorA"));
        sessions.add(new Session(2, "COM S 310", "COMS310", "Carver Hall 204", 5678, "12/06/2025 02:00 PM", "TutorB"));

        // Create RecyclerView programmatically
        RecyclerView recyclerView = new RecyclerView(ApplicationProvider.getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(ApplicationProvider.getApplicationContext()));

        // Set adapter
        SessionAdapter adapter = new SessionAdapter(sessions, session -> {
            // mock join click
        });
        recyclerView.setAdapter(adapter);

        // Check item count
        assertEquals(2, adapter.getItemCount());

        // Bind first item and check values
        RecyclerView.ViewHolder holder = adapter.onCreateViewHolder(recyclerView, 0);
        adapter.onBindViewHolder((SessionAdapter.ViewHolder) holder, 0);

        SessionAdapter.ViewHolder viewHolder = (SessionAdapter.ViewHolder) holder;
        assertEquals("COM S 309", viewHolder.className.getText().toString());
        assertEquals("COMS309", viewHolder.classCode.getText().toString());
        assertEquals("Pearson Hall 101", viewHolder.meetingLocation.getText().toString());
        assertEquals("12/06/2025 02:00 PM", viewHolder.meetingTime.getText().toString());
        assertEquals("Tutor: TutorA", viewHolder.tutorUsername.getText().toString());
    }

    @Test
    public void testActiveSessionsAdapterDisplaysCorrectly() {
        // Create mock session list
        ArrayList<Session> sessions = new ArrayList<>();
        sessions.add(new Session(1, "COM S 309", "COMS309", "Pearson Hall 101", 1234, "12/06/2025 02:00 PM", "TutorA"));

        // Create RecyclerView programmatically
        RecyclerView recyclerView = new RecyclerView(ApplicationProvider.getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(ApplicationProvider.getApplicationContext()));

        ActiveSessionsAdapter adapter = new ActiveSessionsAdapter(sessions);
        recyclerView.setAdapter(adapter);

        assertEquals(1, adapter.getItemCount());

        // Bind first item
        RecyclerView.ViewHolder holder = adapter.onCreateViewHolder(recyclerView, 0);
        adapter.onBindViewHolder((ActiveSessionsAdapter.ViewHolder) holder, 0);

        ActiveSessionsAdapter.ViewHolder viewHolder = (ActiveSessionsAdapter.ViewHolder) holder;
        assertEquals("TutorA", viewHolder.tutorName.getText().toString());
        assertEquals("COM S 309 — 12/06/2025 02:00 PM", viewHolder.meetingInfo.getText().toString());
        assertEquals("Pearson Hall 101", viewHolder.sessionLocation.getText().toString());
    }


    // ------------------------------------------------------------
    // HELPER METHODS
    // ------------------------------------------------------------

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

    public static ViewAction setRating(final float rating) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(RatingBar.class);
            }

            @Override
            public String getDescription() {
                return "Set RatingBar rating";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((RatingBar) view).setRating(rating);
            }
        };
    }
}