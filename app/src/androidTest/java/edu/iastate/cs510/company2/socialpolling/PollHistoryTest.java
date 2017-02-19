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

import java.util.HashSet;

import edu.iastate.cs510.company2.activities.PollHistoryActivity;
import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.infrastructure.ServiceRegistry;
import edu.iastate.cs510.company2.models.Poll;
import edu.iastate.cs510.company2.persistence.MemStoreGw;
import edu.iastate.cs510.company2.testsupport.MockData;
import edu.iastate.cs510.company2.testsupport.MockUser;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static junit.framework.Assert.assertEquals;

/**
 * Created by tianchen on 2016/11/3.
 */

@RunWith(AndroidJUnit4.class)
public class PollHistoryTest {
    public ServiceRegistry locator = ServiceRegistry.getInstance();
    private MemStoreGw testGw;
    Gson gson;
    private MockUser user;
    private Poll poll;

    @Rule
    public ActivityTestRule<PollHistoryActivity> hActivityRule = new ActivityTestRule<>(PollHistoryActivity.class);

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

        //Store currently logged in user in our context (simulate login)
        user = MockData.getUser();
        SharedPreferences mPrefs=appContext.getSharedPreferences("account",Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditors=mPrefs.edit();
        prefEditors.putString("user",user.getUsername());
        prefEditors.commit();
    }

    @Test
    public void showPollHistory(){
        poll = MockData.getPoll();
        HashSet<Poll> polls = new HashSet<>();
        polls.add(poll);

        testGw.getMemStore().create("polls", "pollsDetail", gson.toJson(polls));
        locator.register(PsGateway.class, testGw);

        hActivityRule.launchActivity(new Intent());
    }
}
