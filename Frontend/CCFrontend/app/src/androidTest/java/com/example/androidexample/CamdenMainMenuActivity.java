package com.example.androidexample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.UiAutomation;
import android.content.Context;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(AndroidJUnit4.class)
public class CamdenMainMenuActivity {

    private MockWebServer server;

    // This is a custom Volley HurlStack that redirects all traffic to our MockWebServer.
    private static class MockServerHurlStack extends HurlStack {
        private final String mockUrl;

        public MockServerHurlStack(String mockUrl) {
            // Ensure the mock URL ends with a slash
            this.mockUrl = mockUrl.endsWith("/") ? mockUrl : mockUrl + "/";
        }

        @Override
        protected HttpURLConnection createConnection(URL url) throws IOException {
            // Get the original path and query from the app's request
            String path = url.getPath();
            String query = url.getQuery();

            // Ignore the original host and build a new URL pointing to our mock server
            String redirectedUrl = mockUrl + (path.startsWith("/") ? path.substring(1) : path);
            if (query != null) {
                redirectedUrl += "?" + query;
            }

            // Create a connection to the new, redirected URL
            URL newUrl = new URL(redirectedUrl);
            return super.createConnection(newUrl);
        }
    }

    /**
     * Uses reflection to replace the app's singleton Volley RequestQueue
     * with one that redirects all traffic to the MockWebServer.
     */
    private void hijackVolley(String mockServerUrl) {
        try {
            // Step 1: Create a custom HurlStack that redirects all requests
            MockServerHurlStack customStack = new MockServerHurlStack(mockServerUrl);

            // Step 2: Create a new Volley RequestQueue using our custom stack
            Context context = ApplicationProvider.getApplicationContext();
            RequestQueue testQueue = Volley.newRequestQueue(context, customStack);

            // Step 3: Use reflection to find the Volley's private singleton field
            Field queueField = Volley.class.getDeclaredField("sRequestQueue");
            queueField.setAccessible(true);

            // Step 4: Forcefully replace the app's real queue with our test queue
            queueField.set(null, testQueue);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to find 'sRequestQueue' in Volley. Volley library might have changed.", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to replace Volley's RequestQueue due to security restrictions.", e);
        }
    }

    @Before
    public void setUp() throws IOException {
        UiAutomation uiAutomation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
        uiAutomation.executeShellCommand("settings put global window_animation_scale 0");
        uiAutomation.executeShellCommand("settings put global transition_animation_scale 0");
        uiAutomation.executeShellCommand("settings put global animator_duration_scale 0");

        // Start the mock web server
        server = new MockWebServer();
        server.start(8080);

        // Hijack Volley to redirect its requests to our mock server
        hijackVolley(server.url("/").toString());

        Intents.init();

        // Setup user data needed for the test
        User user = User.getInstance();
        user.setUserId(101);
        user.setUsername("testuser");
        user.setFirstName("John");
        user.setLastName("Doe");
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
        User.clearInstance();
        Intents.release();
    }

    /**
     * ✓ Tests onCreate() and populateFields()
     * This test verifies that the activity starts, displays its core buttons,
     * and populates the welcome message with data from the User singleton.
     */
    @Test
    public void testOnCreate_PopulatesWelcomeMessage() {
        // Prepare the mock server to respond to any network call
        server.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));

        // Launch the activity. Its Volley requests will now be hijacked.
        ActivityScenario.launch(MainMenuActivity.class);

        // Perform UI checks as before
        onView(withId(R.id.msg_btn)).check(matches(isDisplayed()));
        String expectedWelcome = "Welcome, testuser!";
        onView(withId(R.id.welcome_text)).check(matches(withText(expectedWelcome)));
    }
}
