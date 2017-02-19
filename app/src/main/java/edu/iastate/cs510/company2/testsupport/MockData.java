package edu.iastate.cs510.company2.testsupport;

import java.util.ArrayList;
import java.util.Collection;

import edu.iastate.cs510.company2.models.IPoll;
import edu.iastate.cs510.company2.models.Poll;
import edu.iastate.cs510.company2.utility.Password;

/**
 * Created by Guru on 10/26/2016.
 */

public class MockData {

    static public Poll getPoll(){
        Collection<IPoll.Choice> choices = new ArrayList<>();
        IPoll.Choice red = new IPoll.Choice("Red", 3);
        IPoll.Choice green = new IPoll.Choice("Green", 4);
        IPoll.Choice blue = new IPoll.Choice("Blue", 5);
        choices.add(red);
        choices.add(green);
        choices.add(blue);
        return new Poll("NumberOneUser", "General", "What is your favorite color?", choices);
    }

    static public Poll getPollWithImageAndLink(){
        Collection<IPoll.Choice> choices = new ArrayList<>();
        IPoll.Choice red = new IPoll.Choice("Red", 3);
        IPoll.Choice green = new IPoll.Choice("Green", 4);
        IPoll.Choice blue = new IPoll.Choice("Blue", 5);
        choices.add(red);
        choices.add(green);
        choices.add(blue);

        String testImageBase64 = "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 IpoAAAAASUVORK5CYII=";
        return new Poll("NumberOneUser", "General", "What is your favorite color?", choices, testImageBase64, "SomeDummyLink");
    }

    static public MockUser getUser() {
        String salt  = Password.generateSalt();
        String salted;
        String password = "uncleben";
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Entertainment");
        categories.add("Technology");
        categories.add("Sports");
        try{
            salted =  Password.encryptPassword(password, salt);
        }catch(Exception e){
            return null;
        }

        return new MockUser("spiderman", "spiderman@marvel.com", salted, salt, password, "Peter Parker", "10/15", "Male", "New York", "United States",categories);
    }

}
