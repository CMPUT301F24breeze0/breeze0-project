package com.example.breeze0events;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.*;


import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class US010102 {

    @Rule
    public ActivityTestRule<EntrantPreLoginActivity> activityRule =
            new ActivityTestRule<>(EntrantPreLoginActivity.class, true, false);

    private String deviceId;
    private OverallStorageController storageController;

    @Before
    public void setUp() {
        init();

        Context context = ApplicationProvider.getApplicationContext();
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        storageController = new OverallStorageController();

        activityRule.launchActivity(null);
    }

    @After
    public void tearDown() {
        release();
        storageController.deleteEntrant(deviceId);
    }

    @Test
    public void testNewUserSignupJoinAndUnjoinEvent() throws InterruptedException {
        // Step 1: New User Signup
        onView(withId(R.id.buttonFirstTimeUse)).perform(click());

        // Verify EntrantLoginActivity is launched
        Thread.sleep(2000);
        intended(hasComponent(EntrantLoginActivity.class.getName()));

        // Fill in personal information
        onView(withId(R.id.editTextName)).perform(replaceText("John Doe"));
        onView(withId(R.id.editTextEmail)).perform(replaceText("johndoe@example.com"));
        onView(withId(R.id.editTextPhone)).perform(replaceText("1234567890"));
        onView(withId(R.id.buttonSignUp)).perform(click());

        // Verify EntrantMylistActivity is launched after signup
        Thread.sleep(1000);
        intended(hasComponent(EntrantMylistActivity.class.getName()));

        // Step 2: Search for an Event
        onView(withId(R.id.buttonEventSearch)).perform(click());

        // Verify EntrantSearchingActivity is launched
        Thread.sleep(1000);
        intended(hasComponent(EntrantSearchingActivity.class.getName()));

        // Click on the first event in the event_search_view ListView
        onData(anything()).inAdapterView(withId(R.id.event_search_view)).atPosition(0).perform(click());

        // Verify EntrantEventDetail is launched
        Thread.sleep(1000);
        intended(hasComponent(EntrantEventDetail.class.getName()));

        // Step 3: Join the Event
        onView(withId(R.id.entrant_event_join)).perform(click());

        final String eventId = "1"; // Replace with actual event ID for testing

        // Wait for data to load
        Thread.sleep(2000);

        // Step 5: Click on the Unjoin button in the ListView
        onData(withEventId(eventId))
                .inAdapterView(withId(R.id.entrant_mylist))
                .onChildView(withId(R.id.buttonEventStatus))
                .perform(click());

        // Confirm "Yes" on Unjoin Alert
        onView(withText("Yes")).perform(click());

        // Step 6: Verify Entrant is removed from Event Entrants List
        Thread.sleep(2000); // Wait for unjoin action to complete

        storageController.getEvent(eventId, new EventCallback() {
            @Override
            public void onSuccess(Event updatedEvent) {
                List<String> updatedEntrants = updatedEvent.getEntrants();
                if (!updatedEntrants.contains(deviceId)) {
                    Log.d("Test", "Entrant successfully unjoined the event");
                } else {
                    throw new AssertionError("Entrant was not removed from event entrants list");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                throw new AssertionError("Failed to retrieve updated event: " + errorMessage);
            }
        });
    }

    // Custom matcher to match the data item with the given eventId
    public static Matcher<Object> withEventId(final String eventId) {
        return new BoundedMatcher<Object, NewPair<String, String>>(castClass(NewPair.class)) {
            @Override
            protected boolean matchesSafely(NewPair<String, String> item) {
                return item.getLeft().equals(eventId);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with event id: " + eventId);
            }
        };
    }

    // Helper method to perform the cast
    @SuppressWarnings("unchecked")
    private static <T> Class<T> castClass(Class<?> clazz) {
        return (Class<T>) clazz;
    }


}
