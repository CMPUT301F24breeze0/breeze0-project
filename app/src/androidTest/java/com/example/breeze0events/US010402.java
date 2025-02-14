package com.example.breeze0events;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

import android.content.Context;
import android.provider.Settings;
import android.service.autofill.FieldClassification;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class US010402 {
    @Rule
    public ActivityTestRule<OverallLoginPage> activityRule =
            new ActivityTestRule<>(OverallLoginPage.class, true, false);

    private String deviceId;
    private OverallStorageController storageController;
    private Context context;

    @Before
    public void setUp() {
        init();
        context = ApplicationProvider.getApplicationContext();
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        storageController = new OverallStorageController();

        activityRule.launchActivity(null);
    }

    @After
    public void tearDown() {
        release();
        storageController.deleteEntrant(deviceId);
        storageController.deleteEvent("1");
    }
    @Test
    public void testReceiveLoseNotification() throws InterruptedException{
        // initialize the event to send win notification
        // delete Entrant if Entrant exist
        storageController.deleteEvent("1");
        String QRHash = QRHashGenerator.generateHash("1");
        Event event = new Event("1", "Test",QRHash, null,"1","2024-10-05", "2024-12-12","1", "false",null, null);
        storageController.addEvent(event);

        Thread.sleep(1000);
        onView(withId(R.id.entrant_button)).perform(click());

        onView(withId(R.id.buttonFirstTimeUse)).perform(click());

        // Verify EntrantLoginActivity is launched
        Thread.sleep(3000);
        onView(withId(R.id.editTextName)).perform(replaceText("Jane Doe"));
        onView(withId(R.id.editTextEmail)).perform(replaceText("janedoe@example.com"));
        onView(withId(R.id.editTextPhone)).perform(replaceText("0987654321"));
        onView(withId(R.id.buttonSignUp)).perform(click());
        // Verify EntrantMylistActivity is launched after signup
        Thread.sleep(1000);
        onView(withId(R.id.buttonEventSearch)).perform(click());

        Thread.sleep(5000);

        // Click on the first event in the event_search_view ListView
        onData(anything()).inAdapterView(withId(R.id.event_search_view)).atPosition(0).perform(click());

        Thread.sleep(5000);

        onView(withId(R.id.entrant_event_join)).perform(click());

        Thread.sleep(5000);
        storageController.getEntrant(deviceId, new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                entrant.set_add_Event("1", "Test","Rejected");
                List<NewPair<String, String>> notification = new ArrayList<>();
                entrant.addNotification("Test", "Sorry your application was rejected. But don't worry you still have chance by re-join");
                storageController.updateEntrant(entrant);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });

        Thread.sleep(5000);

        onView(withId(R.id.buttonNotification)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.notification_message)).perform(click());


        Thread.sleep(3000);
        onView(withText("View MyList")).perform(click());

        Thread.sleep(2000);
        onView(withId(R.id.buttonEventStatus)).perform(click());

    }
//    public static Matcher<View> isDialogTitle(String expectedTitle) {
//        return new BoundedMatcher<View, TextView>(TextView.class) {
//            @Override
//            protected boolean matchesSafely(TextView textView) {
//                return textView.getText().toString().equals(expectedTitle);
//            }
//
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("with dialog title: " + expectedTitle);
//            }
//        };
//    }
}
