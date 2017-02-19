package edu.iastate.cs510.company2.socialpolling;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import edu.iastate.cs510.company2.activities.CreatePollActivity;
import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.infrastructure.ServiceRegistry;
import edu.iastate.cs510.company2.models.IPoll;
import edu.iastate.cs510.company2.models.Poll;
import edu.iastate.cs510.company2.persistence.MemStoreGw;
import edu.iastate.cs510.company2.testsupport.MockData;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;

/**
 * Created by Dhaval on 12/1/2016.
 */


@RunWith(AndroidJUnit4.class)
public class CreatePollOptionalTest {

    public ServiceRegistry locator;
    public Gson gson;
    private MemStoreGw testGw;

    @Before
    public void init(){
        locator = edu.iastate.cs510.company2.infrastructure.ServiceRegistry.getInstance();
        gson = new Gson();
        testGw = new MemStoreGw();
    }
    @Rule
    public ActivityTestRule<CreatePollActivity> rActivityRule = new ActivityTestRule<>(CreatePollActivity.class);

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = getTargetContext();
        assertEquals("edu.iastate.cs510.company2.socialpolling", appContext.getPackageName());
    }

    @Test
    public void createPollTest() throws Exception{
        Poll newPoll = MockData.getPollWithImageAndLink();
        String json = gson.toJson(newPoll);
        testGw.getMemStore().create("Polls", "key", json);
        locator.register(PsGateway.class, testGw);
        enterCreatePollData(newPoll);
     //   testingImage(newPoll);
        onView(withId(R.id.btnSend)).perform(click());
}

    private void enterCreatePollData(Poll testPoll){
        onView(withId(R.id.yourQuestion)).perform(clearText()).perform(typeText(testPoll.getQuestion()));
        ArrayList<IPoll.Choice> choiceList = (ArrayList<IPoll.Choice>) testPoll.getChoices();
        onView(withId(R.id.choice1)).perform(clearText()).perform(typeText(choiceList.get(0).choice));
        onView(withId(R.id.choice2)).perform(clearText()).perform(typeText(choiceList.get(1).choice));
        onView(withId(R.id.choice3)).perform(clearText()).perform(typeText(choiceList.get(2).choice), closeSoftKeyboard());
        onView(withId(R.id.LinkeditText)).perform(clearText()).perform(typeText(testPoll.getLink()), closeSoftKeyboard());

    }


   /* public static void testingImage(Poll testPoll) throws  Exception {
        Exception returnException = null;
        Intent resultData = new Intent();
        byte[] decodedBytes = Base64.decode(testPoll.getImageString(), 0);
        Bitmap bitmap =  BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        String path = MediaStore.Images.Media.insertImage(getTargetContext().getContentResolver(), bitmap, "Test Image", null);
        Uri testUri =  Uri.parse(path);
        resultData.setData(testUri);

        Intents.init();
        try {
            Matcher<Intent> expectedIntent = hasAction(Intent.ACTION_GET_CONTENT);
            intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));
            onView(withId(R.id.attachImage)).perform(click());
            intended(expectedIntent);
        }
        catch (Exception e) {
            returnException = e;
        }
        finally {
            Intents.release();
        }

        if (returnException != null) {
            throw returnException;
        }
    }
    */

}
