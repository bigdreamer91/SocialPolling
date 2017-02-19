package edu.iastate.cs510.company2.socialpolling;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;

import edu.iastate.cs510.company2.activities.MainActivity;
import edu.iastate.cs510.company2.activities.ProfileDetailActivity;
import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.infrastructure.ServiceRegistry;
import edu.iastate.cs510.company2.models.User;
import edu.iastate.cs510.company2.persistence.MemStoreGw;
import edu.iastate.cs510.company2.testsupport.MockData;
import edu.iastate.cs510.company2.testsupport.MockUser;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;

;
/**
 * Created by hosseini on 10/30/16.
 */
@RunWith(AndroidJUnit4.class)
public class ProfileDetailTest {
    public ServiceRegistry locator = ServiceRegistry.getInstance();
    private MockUser user;
    private MockUser prevuser;
    private MemStoreGw testGw;
    Gson gson;
    @Rule
    public ActivityTestRule<MainActivity> dActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init(){
        gson = new Gson();
         testGw = new MemStoreGw();

    }



    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = getTargetContext();
        user = MockData.getUser();
        SharedPreferences mPrefs=appContext.getSharedPreferences("account",Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditors=mPrefs.edit();
        prefEditors.putString("user",user.getUsername());
        prefEditors.commit();
        assertEquals("edu.iastate.cs510.company2.socialpolling", appContext.getPackageName());
    }



    @Test
    public void editProfile(){

        user = MockData.getUser();
        HashSet<User> users = new HashSet<>();
        users.add(MockData.getUser());

        //Put our MockUser into the memstore and register to locator
        testGw.getMemStore().create("account", "users", gson.toJson(users));
        testGw.getMemStore().create("users", user.getUsername(), gson.toJson(user));
        locator.register(PsGateway.class, testGw);


        //Not required.
        //dActivityRule.launchActivity(new Intent());

        //Store currently logged in user in our context (simulate login)
        Context appContext = getTargetContext();
        SharedPreferences mPrefs=appContext.getSharedPreferences("account",Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditors=mPrefs.edit();
        prefEditors.putString("user",user.getUsername());
        prefEditors.commit();

        onView(withId(R.id.button)).perform(click());
        changeProfileDetail("Zahra","Hosseini@iastate.edu","Ames");
        onView(withId(R.id.btnSave))
                .perform(click());

        pressBack();

        onView(withId(R.id.button)).perform(click());




    }

    private void changeProfileDetail(String Name, String Email, String City){

        onView(withId(R.id.email))
                .perform( replaceText(Email));

        onView(withId(R.id.name))
                .perform(replaceText(Name));


        onView(withId(R.id.city))
                .perform(replaceText(City));

    }
}
