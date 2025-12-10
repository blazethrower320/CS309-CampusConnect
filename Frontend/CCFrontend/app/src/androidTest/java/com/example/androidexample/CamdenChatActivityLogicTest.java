package com.example.androidexample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * End-to-end test for ChatActivity logic, running on a device/emulator.
 * This class verifies that the activity correctly processes real messages
 * from a live WebSocket server.
 */
@RunWith(AndroidJUnit4.class)
public class CamdenChatActivityLogicTest {

    private static final long NETWORK_WAIT_TIME = 2000; // 2 seconds

    @Before
    public void setUp() {
        // This user MUST exist in your backend database for the test to work.
        User user = User.getInstance();
        user.setUserId(101);
        user.setUsername("testUser");
    }

    /**
     * ✓ Tests onWebSocketMessage() for loading chat history from a live server.
     * Verifies that when the activity connects, the RecyclerView is populated with messages.
     *
     * PRE-REQUISITE: The chat session (ID 50) must exist on your server and have at least one message.
     */
    @Test
    public void testOnWebSocketMessage_LoadsHistoryFromRealServer() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ChatActivity.class)
                .putExtra("isGroupChat", true)
                .putExtra("sessionId", 50) // This session MUST have messages.
                .putExtra("chatName", "Test Group");

        ActivityScenario.launch(intent);

        // Wait for the WebSocket to connect and receive the chat history.
        try {
            Thread.sleep(NETWORK_WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the RecyclerView has more than 0 items. This confirms history was loaded.
        onView(withId(R.id.messages_recycler_view)).check(new RecyclerViewItemCountAssertion(true));
    }

    /**
     * ✓ Tests parsing and displaying an image URL message sent over the live WebSocket.
     *
     */
    @Test
    public void testSendImageUrl_DisplaysImageMessageInChat() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ChatActivity.class)
                .putExtra("isGroupChat", true)
                .putExtra("sessionId", 50)
                .putExtra("chatName", "Test Group");

        ActivityScenario<ChatActivity> scenario = ActivityScenario.launch(intent);

        // Wait for the initial WebSocket connection.
        try {
            Thread.sleep(NETWORK_WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // This is a stand-in for a URL that would be returned by your image upload server.
        String imageUrl = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png";

        // Directly call the method that sends the image URL through the WebSocket.
        // This bypasses the need to interact with the Android image picker UI.
        scenario.onActivity(activity -> {
            activity.sendImageUrlOverWebSocket(imageUrl);
        });

        // Wait for the message to be sent and rendered in the UI.
        try {
            Thread.sleep(NETWORK_WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify the RecyclerView now contains an item that holds an ImageView,
        // which confirms an image message was successfully parsed and displayed.
        //TODO
        onView(withId(R.id.messages_recycler_view))
                .check(matches(hasDescendant(ViewMatchers.withId(R.id.sent_image_view))));
    }
}

/**
 * Helper class to assert the number of items in a RecyclerView.
 */
class RecyclerViewItemCountAssertion implements ViewAssertion {
    private final boolean assertGreaterThanZero;

    public RecyclerViewItemCountAssertion(boolean assertGreaterThanZero) {
        this.assertGreaterThanZero = assertGreaterThanZero;
    }

    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
            throw noViewFoundException;
        }

        RecyclerView recyclerView = (RecyclerView) view;
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (assertGreaterThanZero)
        {
            assertThat("RecyclerView item count", adapter.getItemCount(), is(greaterThan(0)));
        }
    }
}
