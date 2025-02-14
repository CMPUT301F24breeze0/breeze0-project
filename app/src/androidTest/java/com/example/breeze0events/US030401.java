package com.example.breeze0events;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.IdlingPolicies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;


/**
 * UI test for browsing an event from the admin interface.
 */
@RunWith(AndroidJUnit4.class)
public class US030401 {

    private OverallStorageController overallStorageController;
    private String mockEventId;
    private final String mockEventName = "Mock Event for Browse Test";

    /**
     * Custom matcher to find a list item with specific text.
     */
    public static Matcher<Object> withItemContent(final String text) {
        return new TypeSafeMatcher<Object>() {
            @Override
            public boolean matchesSafely(Object item) {
                return item.toString().contains(text);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("List item with text: " + text);
            }
        };
    }

    @Before
    public void setup() throws InterruptedException {
        IdlingPolicies.setMasterPolicyTimeout(5, TimeUnit.SECONDS);

        Intents.init();
        overallStorageController = new OverallStorageController();

        Event mockEvent = createMockEvent();
        overallStorageController.addEvent(mockEvent);
        mockEventId = mockEvent.getEventId();


        verifyEventInDatabase();
    }

    /**
     * Creates a mock event for testing.
     *
     * @return A fully initialized Event object.
     */
    private Event createMockEvent() {
        return new Event(
                "mock_browse_test_id",
                mockEventName,
                "QRCode123",
                "mockPosterPath",
                "Mock Facility",
                "2024-11-07",
                "2024-11-08",
                "10",
                "51.5074, -0.1278",
                new ArrayList<>(Arrays.asList("entrant1")),
                new ArrayList<>(Arrays.asList("organizer1"))
        );
    }

    /**
     * Verifies that the mock event exists in the database before proceeding with tests.
     */
    private void verifyEventInDatabase() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        overallStorageController.getEvent(mockEventId, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                if (event != null && event.getEventId().equals(mockEventId)) {
                    latch.countDown(); // Event confirmed
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // No action needed, will fail the assertion
            }
        });

        // Await the confirmation with a timeout
        assertTrue("Failed to confirm event was added to the database", latch.await(5, TimeUnit.SECONDS));
    }

    @After
    public void tearDown() {
        // Remove the mock event from the database and release intents
        overallStorageController.deleteEvent(mockEventId);
        Intents.release();
    }

    /**
     * Tests if the event list is displayed and event details can be accessed.
     */
    @Test
    public void testEventListDisplayedAndEventDetailsOpened() throws InterruptedException {
        try (ActivityScenario<AdminEventActivity> scenario = ActivityScenario.launch(AdminEventActivity.class)) {
            // Verify the event list is displayed
            Thread.sleep(4000);
            onView(withId(R.id.eventsList))
                    .check(ViewAssertions.matches(isDisplayed()));
            Thread.sleep(4000);
            // Verify the mock event is present in the list
            onData(withItemContent(mockEventName))
                    .inAdapterView(withId(R.id.eventsList))
                    .check(ViewAssertions.matches(isDisplayed()));
        }
    }
}
