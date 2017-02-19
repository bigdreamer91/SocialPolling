package edu.iastate.cs510.company2.socialpolling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.iastate.cs510.company2.activities.MainActivity;
import edu.iastate.cs510.company2.gateway.CreateMsg;
import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.gateway.ReadMsg;
import edu.iastate.cs510.company2.gateway.Record;
import edu.iastate.cs510.company2.infrastructure.ServiceRegistry;
import edu.iastate.cs510.company2.models.IPoll;
import edu.iastate.cs510.company2.models.Poll;
import edu.iastate.cs510.company2.models.User;
import edu.iastate.cs510.company2.persistence.MemStoreGw;
import edu.iastate.cs510.company2.testsupport.MockData;
import edu.iastate.cs510.company2.testsupport.MockUser;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by Greeshma Reddy on 12/3/2016.
 */

@RunWith(AndroidJUnit4.class)

public class CustomizeHomePageTest {
    private MemStoreGw testGw;
    Gson gson;
    private MockUser user;
    public ServiceRegistry locator = ServiceRegistry.getInstance();

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init(){
        gson = new Gson();
        testGw = new MemStoreGw();

    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = getTargetContext();
        assertEquals("edu.iastate.cs510.company2.socialpolling", appContext.getPackageName());
        user = MockData.getUser();
        SharedPreferences mPrefs=appContext.getSharedPreferences("account",Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditors=mPrefs.edit();
        prefEditors.putString("user",user.getUsername());
        prefEditors.commit();
    }

    @Test
    public void test(){
        user = MockData.getUser();
        HashSet<User> users = new HashSet<>();
        users.add(MockData.getUser());

        //Put our MockUser into the memstore and register to locator
        testGw.getMemStore().create("account", "users", gson.toJson(users));
        testGw.getMemStore().create("users", user.getUsername(), gson.toJson(user));
        locator.register(PsGateway.class, testGw);
        MockUser user = MockData.getUser();
        Poll newPoll = MockData.getPoll();
        createMemStore(newPoll);
        newPoll.setQuestion("Which sport is good to watch?");
        newPoll.setCategory("Sports");
        newPoll.setCreator(user.getUsername());
        createMemStore(newPoll);
        newPoll.setQuestion("Which political debate is good?");
        newPoll.setCategory("Politics");
        newPoll.setCreator(user.getUsername());
        createMemStore(newPoll);
        newPoll.setQuestion("Macro economics?");
        newPoll.setCategory("Economics");
        newPoll.setCreator(user.getUsername());
        createMemStore(newPoll);
        mActivityRule.launchActivity(new Intent());
        try {
            sleepWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.floatingActionButton)).perform(click());
        Poll poll = MockData.getPoll();
        populateCreatePoll(poll);
        onView(withId(R.id.btnSend)).perform(click());
    }

    private void sleepWait() throws InterruptedException {
        Thread.sleep(2000);
    }

    private void createMemStore(Poll testPoll){
        Gson gson = new Gson();
        final List<Record> pollRecords = new ArrayList<>();
        ReadMsg readMessage = new ReadMsg("http://iastate.510.com/SocialPolling", "Polls", "key");

        PsGateway gw = locator.getService(PsGateway.class);
        PsGateway.Response response = gw.send(readMessage);
        if(response.getStatus() != PsGateway.Status.success){
            //FAILED TO SEND MESSAGE
        }
        PsGateway.CbResponse fullReply = null;
        if (response instanceof PsGateway.CbResponse){
            fullReply = (PsGateway.CbResponse) response;
            pollRecords.addAll(fullReply.getPayload());
        }
        String jSon = gson.toJson(testPoll);
        CreateMsg createNewUserMsg = new CreateMsg("http://iastate.510.com/SocialPolling", "Polls", "key", jSon);
        gw.send(createNewUserMsg);
    }

    private void populateCreatePoll(Poll testPoll){
        onView(withId(R.id.yourQuestion)).perform(typeText(testPoll.getQuestion()));
        ArrayList<IPoll.Choice> choiceList = (ArrayList<IPoll.Choice>) testPoll.getChoices();
        onView(withId(R.id.choice1)).perform(typeText(choiceList.get(0).choice));
        onView(withId(R.id.choice2)).perform(typeText(choiceList.get(1).choice));
        onView(withId(R.id.choice3)).perform(typeText(choiceList.get(2).choice), closeSoftKeyboard());
        onView(withId(R.id.categories_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Social"))).perform(click());
    }
}

