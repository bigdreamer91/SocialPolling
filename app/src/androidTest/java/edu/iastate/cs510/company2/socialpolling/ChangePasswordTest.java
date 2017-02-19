package edu.iastate.cs510.company2.socialpolling;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;

import edu.iastate.cs510.company2.activities.ChangePasswordActivity;
import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.infrastructure.ServiceRegistry;
import edu.iastate.cs510.company2.models.User;
import edu.iastate.cs510.company2.persistence.MemStoreGw;
import edu.iastate.cs510.company2.testsupport.MockData;
import edu.iastate.cs510.company2.testsupport.MockUser;

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

/**
 * Created by Dhaval on 11/23/2016.
 */

@RunWith(AndroidJUnit4.class)
public class ChangePasswordTest {

    public ServiceRegistry locator;
    public Gson gson;
    private MemStoreGw testGw;

    @Before
    public void init(){
        locator = ServiceRegistry.getInstance();
        gson = new Gson();
        testGw = new MemStoreGw();
    }

    @Rule
    public ActivityTestRule<ChangePasswordActivity> cpActivityRule = new ActivityTestRule<>(ChangePasswordActivity.class);

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = getTargetContext();
        assertEquals("edu.iastate.cs510.company2.socialpolling", appContext.getPackageName());
    }

    @Test
    public void changePasswordWithEmptyFields() throws Exception{
        onView(withId(R.id.btn_changePwd))      // withId(R.id.my_view) is a ViewMatcher
                .perform(click())     // click() is a ViewAction
                .check(matches(isDisplayed())); // Ensures that activity hasn't changed

    }

    @Test
    public void changePassword() throws Exception{

        MockUser user = MockData.getUser();
        HashSet<User> users = new HashSet<>();
        users.add(user);

        //Put our MockUser into the memstore and register to locator
        testGw.getMemStore().create("account", "users", gson.toJson(users));
        testGw.getMemStore().create("users", user.getUsername(), gson.toJson(user));
        locator.register(PsGateway.class, testGw);

        //Store currently logged in user in our context to simulate login
        Context appContext = getTargetContext();
        SharedPreferences mPrefs=appContext.getSharedPreferences("account",Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditors=mPrefs.edit();
        prefEditors.putString("user",user.getUsername());
        prefEditors.commit();

        enterChangePasswordData(user);
        onView(withId(R.id.btn_changePwd))      // withId(R.id.my_view) is a ViewMatcher
                .perform(click());

        // We made it to the main activity
        onView(withText("Hello " + user.getUsername() + ", welcome to Social Polling!")).check(matches(isDisplayed()));

    }

    private void enterChangePasswordData(MockUser user){
        onView(withId(R.id.input_email_id))
                .perform(typeText(user.getEmail()),closeSoftKeyboard());
        onView(withId(R.id.input_old_passwd))
                .perform(typeText(user.getClearPassword()),closeSoftKeyboard());
        onView(withId(R.id.input_new_passwd))
                .perform(typeText("newPass"),closeSoftKeyboard());
        onView(withId(R.id.input_confirm_passwd))
                .perform(typeText("newPass"),closeSoftKeyboard());

    }
}
