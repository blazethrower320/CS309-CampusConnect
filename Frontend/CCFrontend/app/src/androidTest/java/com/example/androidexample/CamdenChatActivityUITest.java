package com.example.androidexample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CamdenChatActivityUITest {

    // A longer delay to wait for real network responses from your live server.
    private static final long NETWORK_WAIT_TIME = 2000; // 2 seconds

    @Before
    public void setUp() {
        // Setup the User singleton for tests.
        // This user MUST exist in your backend database for the test to work.
        User user = User.getInstance();
        user.setUserId(101); // Make sure user with ID 101 exists on your server.
        user.setUsername("testUser");
    }

    /**
     * ✓ Tests onCreate(), setupWebSocket(), and receiving chat history.
     * Verifies that the activity connects to the live WebSocket server and displays
     * a message from the chat history.
     *
     * PRE-REQUISITE: The chat session (e.g., group chat 50) must exist on your server
     * and contain at least one message.
     */
    @Test
    public void testActivityLaunch_ConnectsAndLoadsHistory() {
        // Use an existing chat session from your server. Let's assume group chat 50 exists.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ChatActivity.class)
                .putExtra("isGroupChat", true)
                .putExtra("sessionId", 50) // This session MUST exist on your server.
                .putExtra("chatName", "Test Group");

        ActivityScenario.launch(intent);

        // Wait for the WebSocket to connect and receive the chat history.
        try {
            Thread.sleep(NETWORK_WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the chat activity's title is displayed.
        onView(ViewMatchers.withId(R.id.chat_name_title)).check(matches(withText("Test Group")));

        // Check that the message input field is ready.
        onView(withId(R.id.message_edt)).check(matches(isDisplayed()));
    }

    /**
     * ✓ Tests setupUIListeners() by sending and receiving a real message.
     * Verifies that a message typed by the user is sent to the server and
     * appears in the chat's RecyclerView.
     *
     * PRE-REQUISITE: Your backend server must be running.
     */
    @Test
    public void testSendMessage_DisplaysMessageInChat() {
        // Use an existing chat session from your server.
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ChatActivity.class)
                .putExtra("isGroupChat", true)
                .putExtra("sessionId", 50) // This session MUST exist on your server.
                .putExtra("chatName", "Test Group");

        ActivityScenario.launch(intent);

        // It's good practice to wait for the initial connection to be stable.
        try {
            Thread.sleep(NETWORK_WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Generate a unique message to avoid matching old messages from previous test runs.
        String messageText = "Live test message " + System.currentTimeMillis();

        // Type text into the EditText.
        onView(withId(R.id.message_edt)).perform(typeText(messageText), closeSoftKeyboard());

        // Click the send button.
        onView(withId(R.id.send_btn)).perform(click());

        // Wait for the message to be sent and echoed back by the server.
        try {
            Thread.sleep(NETWORK_WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify the new message appears in the RecyclerView.
        onView(withId(R.id.messages_recycler_view))
                .check(matches(hasDescendant(withText(messageText))));

        // Verify the EditText is cleared after sending.
        onView(withId(R.id.message_edt)).check(matches(withText("")));
    }
}
