package edu.iastate.cs510.company2.socialpolling;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import edu.iastate.cs510.company2.activities.CreatePollActivity;
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
public class CreatePollTest {
    @Rule
    public ActivityTestRule<CreatePollActivity> rActivityRule = new ActivityTestRule<>(CreatePollActivity.class);

    @Test
    public void createNewPollTest(){
        Poll newPoll = MockData.getPoll();
        populateCreatePoll(newPoll);
        onView(withId(R.id.btnSend)).perform(click());
        System.out.println("test");
    }

    private void populateCreatePoll(Poll testPoll){
        onView(withId(R.id.yourQuestion)).perform(typeText(testPoll.getQuestion()));
        ArrayList<IPoll.Choice> choiceList = (ArrayList<IPoll.Choice>) testPoll.getChoices();
        onView(withId(R.id.choice1)).perform(typeText(choiceList.get(0).choice));
        onView(withId(R.id.choice2)).perform(typeText(choiceList.get(1).choice));
        onView(withId(R.id.choice3)).perform(typeText(choiceList.get(2).choice), closeSoftKeyboard());
    }
}
