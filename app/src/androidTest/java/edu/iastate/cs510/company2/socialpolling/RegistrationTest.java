package edu.iastate.cs510.company2.socialpolling;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.iastate.cs510.company2.activities.RegisterActivity;
import edu.iastate.cs510.company2.testsupport.MockData;
import edu.iastate.cs510.company2.testsupport.MockUser;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;

;

/**
 * Created by David on 10/27/2016.
 */

@RunWith(AndroidJUnit4.class)
public class RegistrationTest {

    //By using ActivityTestRule, the testing framework launches the activity under test before each test method annotated with @Test
    // and before any method annotated with @Before. The framework handles shutting down the activity after the test finishes
    // and all methods annotated with @After are run.
    @Rule
    public ActivityTestRule<RegisterActivity> rActivityRule = new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = getTargetContext();
        assertEquals("edu.iastate.cs510.company2.socialpolling", appContext.getPackageName());
    }

    @Test
    public void registerWithEmptyFields() throws Exception{
//        ActivityMonitor monitor = mInstrumentation.addMonitor(LoginActivity.class.getName(), null, false);
//        Activity nextActivity = mInstrumentation.waitForMonitorWithTimeout(monitor, TIME_OUT);
        onView(withId(R.id.btnCreateUser))      // withId(R.id.my_view) is a ViewMatcher
                .perform(click())               // click() is a ViewAction
                .check(matches(isDisplayed())); // matches(isDisplayed()) is a ViewAssertion. This verifies we didnt go to a new activity.
    }

    @Test
    public void registerNewUser(){
        MockUser user = MockData.getUser();
        populateRegistrationForm(user);
        onView(withId(R.id.btnCreateUser))      // withId(R.id.my_view) is a ViewMatcher
                .perform(click());          // click() is a ViewAction);

        //If this shows up then we made it to the MainActivity.
        onView(withText("Hello " + user.getUsername() + ", welcome to Social Polling!")).check(matches(isDisplayed()));
        Context appContext = getTargetContext();
        SharedPreferences mPrefs = appContext.getSharedPreferences("account", MODE_PRIVATE);
        String currentUsername = mPrefs.getString("user", "User not found.");
        Assert.assertEquals(currentUsername, user.getUsername());
    }

    private void populateRegistrationForm(MockUser user){
        onView(withId(R.id.username))
                .perform(typeText(user.getUsername()));
        onView(withId(R.id.email))
                .perform(typeText(user.getEmail()));
        onView(withId(R.id.password))
                .perform(typeText(user.getClearPassword()));
        onView(withId(R.id.name))
                .perform(typeText(user.getName()));
        onView(withId(R.id.birthday))
                .perform(typeText(user.getBirthdate()));
        //SPIN?
//        onView(withId(R.id.genderSpinner))
//                .perform((user.getGender()));

        onView(withId(R.id.city))
                .perform(typeText(user.getCity()));
        onView(withId(R.id.country))
                .perform(typeText(user.getCountry()), closeSoftKeyboard());
    }
}
