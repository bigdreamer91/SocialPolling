package edu.iastate.cs510.company2.socialpolling;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import edu.iastate.cs510.company2.activities.PollHistoryActivity;
import edu.iastate.cs510.company2.activities.SearchActivity;
import edu.iastate.cs510.company2.gateway.CreateMsg;
import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.gateway.ReadMsg;
import edu.iastate.cs510.company2.gateway.Record;
import edu.iastate.cs510.company2.infrastructure.ServiceRegistry;
import edu.iastate.cs510.company2.models.Poll;
import edu.iastate.cs510.company2.persistence.MemStoreGw;
import edu.iastate.cs510.company2.testsupport.MockData;
import edu.iastate.cs510.company2.testsupport.MockUser;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.core.deps.guava.base.Predicates.instanceOf;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;


/**
 * Created by Ananth Radhakrishnan on 11/8/2016.
 */

@RunWith(AndroidJUnit4.class)
public class SearchPollTest {

    private MemStoreGw testGw;
    Gson gson;
    private MockUser user;
    private Poll poll;
    private Button button;
    private Activity activity;
    public ServiceRegistry locator = ServiceRegistry.getInstance();
    Spinner spinner;


    @Rule
    public ActivityTestRule<SearchActivity> hActivityRule = new ActivityTestRule<>(SearchActivity.class);

    @Before
    public void init(){
        gson = new Gson();
        testGw = new MemStoreGw();
        //spinner = (Spinner) spinner.findViewById(R.id.spinner2);
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = getTargetContext();
        assertEquals("edu.iastate.cs510.company2.socialpolling", appContext.getPackageName());

    }

    @Test
    public void searchPoll(){

        hActivityRule.launchActivity(new Intent());
        try {
            sleepWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] myArray =
                hActivityRule.getActivity().getResources()
                        .getStringArray(R.array.categories_array);

        int size = myArray.length;
        for (int i=0; i<size; i++) {

            onView(withId(R.id.spinner2)).perform(click());
            onData(is(myArray[i])).perform(click());
            onView(withId(R.id.search2)).perform(click());
            hActivityRule.launchActivity(new Intent());
        }
    }

    private void sleepWait() throws InterruptedException {
        Thread.sleep(2000);
    }


}
