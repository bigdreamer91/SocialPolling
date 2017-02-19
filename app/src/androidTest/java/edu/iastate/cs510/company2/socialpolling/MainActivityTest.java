package edu.iastate.cs510.company2.socialpolling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import edu.iastate.cs510.company2.activities.MainActivity;
import edu.iastate.cs510.company2.gateway.CreateMsg;
import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.gateway.ReadMsg;
import edu.iastate.cs510.company2.gateway.Record;
import edu.iastate.cs510.company2.infrastructure.ServiceRegistry;
import edu.iastate.cs510.company2.models.IPoll;
import edu.iastate.cs510.company2.models.Poll;
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
 * Created by geethanjalijeevanatham on 11/15/16.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    public ServiceRegistry locator = ServiceRegistry.getInstance();

    @Rule
    public ActivityTestRule<MainActivity> rActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActTest() throws InterruptedException {
        Poll newPoll = MockData.getPoll();
        createMemStore(newPoll);
        rActivityRule.launchActivity(new Intent());
        SleepWait();
    }

    private void SleepWait() throws InterruptedException {
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
}
