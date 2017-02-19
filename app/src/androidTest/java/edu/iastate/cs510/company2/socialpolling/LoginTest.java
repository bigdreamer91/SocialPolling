package edu.iastate.cs510.company2.socialpolling;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;

import edu.iastate.cs510.company2.activities.LoginActivity;
import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.infrastructure.ServiceRegistry;
import edu.iastate.cs510.company2.models.User;
import edu.iastate.cs510.company2.persistence.MemStoreGw;
import edu.iastate.cs510.company2.testsupport.MockData;
import edu.iastate.cs510.company2.testsupport.MockUser;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

/**
 * Created by David on 10/27/2016.
 */

@RunWith(AndroidJUnit4.class)
public class LoginTest {
    public static String server = "http://iastate.510.com/SocialPolling";
    public ServiceRegistry locator = ServiceRegistry.getInstance();
    Gson gson;

    @Before
    public void init(){


         gson = new Gson();
    }

    @Rule
    public ActivityTestRule<LoginActivity> lActivityRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void loginWithEmptyFields() throws Exception{

        onView(withId(R.id.btn_login))      // withId(R.id.my_view) is a ViewMatcher
                .perform(click())               // click() is a ViewAction
                .check(matches(isDisplayed())); // matches(isDisplayed()) is a ViewAssertion.
                                                // This verifies we didnt go to a new activity.
    }



    @Test
    public void loginUser() throws Exception{
        MemStoreGw testGw = new MemStoreGw();
        HashSet<User> users = new HashSet<>();
        MockUser user = MockData.getUser();
        users.add(user);
        testGw.getMemStore().create("account", "users", gson.toJson(users));
        locator.register(PsGateway.class, testGw);

        onView(withId(R.id.input_email))
                .perform(replaceText(user.getEmail()));
        onView(withId(R.id.input_password))
                .perform(replaceText(user.getClearPassword()), closeSoftKeyboard());
        onView(withId(R.id.btn_login))      // withId(R.id.my_view) is a ViewMatcher
                .perform(click());              // click() is a ViewAction

        //We made it to main activity if this passes
        onView(withText("Hello " + user.getUsername() + ", welcome to Social Polling!")).check(matches(isDisplayed()));
        Context appContext = getTargetContext();
        SharedPreferences mPrefs = appContext.getSharedPreferences("account", MODE_PRIVATE);
        String currentUsername = mPrefs.getString("user", "User not found.");
        assertEquals(currentUsername, user.getUsername());
    }

    @Test @Ignore
    //This isn't quite working yet but I believe it's very close
    public void mockitoTest(){
        //Save for later

        //INIT METHOD
        //MockitoAnnotations.initMocks(this);

        //Thought this would reset context so mocked service is registered
        //lActivityRule.launchActivity(new Intent());
        //Setup data/objects for testing
        //
//        ReadMsg readMessage = new ReadMsg("http://iastate.510.com/SocialPolling", "account", "users");
//        List<Record> userRecords = new ArrayList<>();
//        userRecords.add(new Record("acccount", "users", 1, payload));
//        PsGateway.Response userCbResponse = new BaseCbResponse(server, "account", userRecords, 1);


        // PsGateway gwMock = Mockito.mock(PsGateway.class);
        //  locator.register(PsGateway.class, gwMock); //I think we will need to call this before we program in each test..

        //Program mockito
        //doReturn(userCbResponse).when(gwMock).send(readMessage);
        //when(gwMock.send((ReadMsg) null)).thenReturn(userCbResponse);

        //install mock in facade retrieved from registry
        //might not need this if have rest of above code
        //PsGateway gw = locator.getService(PsGateway.class);
        //MockTestControl<PsGateway> tc = (MockTestControl) gw;
        //tc.setMockInstance(gwMock);
    }

}
