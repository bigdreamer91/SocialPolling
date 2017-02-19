package edu.iastate.cs510.company2.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.gateway.ReadMsg;
import edu.iastate.cs510.company2.gateway.Record;
import edu.iastate.cs510.company2.infrastructure.ServiceRegistry;
import edu.iastate.cs510.company2.models.User;
import edu.iastate.cs510.company2.persistence.MemStoreGw;
import edu.iastate.cs510.company2.socialpolling.R;
import edu.iastate.cs510.company2.testsupport.MockData;
import edu.iastate.cs510.company2.testsupport.MockUser;
import edu.iastate.cs510.company2.utility.Password;

/**
 * A very basic login screen with NO authentication
 */
public class LoginActivity extends AppCompatActivity{

    //Key string constant for our "extra" that we will send between activities
    //Constant contains package name to ensure uniqueness when apps talk between each other
    //We can make this much shorter but this is considered convention
    public ServiceRegistry locator = ServiceRegistry.getInstance();
    //TODO: Move this to a static reference somewhere so all activities reference same gson object?
    public Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(locator.getService(PsGateway.class) == null){
            MemStoreGw testGw = new MemStoreGw();
            HashSet<User> users = new HashSet<>();
            MockUser user = MockData.getUser();
            users.add(user);
            testGw.getMemStore().create("account", "users", gson.toJson(users));
            testGw.getMemStore().create("users", user.getUsername(), gson.toJson(user));
            locator.register(PsGateway.class, testGw);
            //locator.register(PsGateway.class, new MemStoreGw());
        }
        setContentView(R.layout.activity_login);
    }


    /** Called when the user clicks the Send button
     * because of this in the xml:
     * android:onClick="signIn"
     */
    public void signIn(View view) throws Exception{

        //I don't know why they want to return a list of records instead of a List of blobs. But this is what we have to work with
        //For most things we will probably only expect 1 item in our list (think of 1 row in JDBC coming back in ResultSet)
        //Holds retrieved records from async callback
        final List<Record> userRecords = new ArrayList<>();
        //TODO: Move read message creation to wrapper class, and create method like
        //      GatewayHelper.readAllUsers() that will make calls for us.. and handle response etc..
        ReadMsg readMessage = new ReadMsg("http://iastate.510.com/SocialPolling", "account", "users");
        //This is currently completely unnecessary because the memstore doesn't actually do anything asynchronously
        //And for some reason it checks for a callback and doesn't return a cbResponse if it has one but doesn't do anything to return a cbRsponse..
//        readMessage.setCallBack(new PsGateway.Callback() {
//            @Override
//            public void deliver(PsGateway.CbResponse deferredResponse) {
//                userRecords.addAll(deferredResponse.getPayload());
//            }
//        });
        PsGateway gw = locator.getService(PsGateway.class);
        PsGateway.Response response = gw.send(readMessage);
//        if(response.getStatus() != PsGateway.Status.success){
//            //FAILED TO SEND MESSAGE
//        }
        PsGateway.CbResponse fullReply = null;

        //This will go away because we need to wait for the actual callback response, memstore just happens to return a CbResponse type
        if (response instanceof PsGateway.CbResponse){
            fullReply = (PsGateway.CbResponse) response;
            userRecords.addAll(fullReply.getPayload());
        }



        //get json "blob" from call back response
        String json;// = userRecords.get(0).pLoad;
        HashSet<User> users;
        if(userRecords.size() == 1){
            json = userRecords.get(0).pLoad;
            Type type = new TypeToken<HashSet<User>>(){}.getType();
            users = gson.fromJson(json,type);
        }
        else{
            //There are no registered users so I already know this user is a faker
            Toast.makeText(getApplicationContext(), "Your email is not registered, please register first.", Toast.LENGTH_SHORT).show();
            return;
        }

        //Get context of a ui element
        EditText emailText = (EditText) findViewById(R.id.input_email);
        //Get the text out of the EditText object
        String email = emailText.getText().toString();
        String username = "";

        EditText passText = (EditText) findViewById(R.id.input_password);
        String password = passText.getText().toString();
        User loginUser = new User(email);
        if(users != null && users.contains(loginUser)) {
            for(User user : users){
                if(user.equals(loginUser)){

                    //Encrypt the entered password and compare with the stored salted password
                    if(!Password.encryptPassword(password,user.getSalt()).equals(user.getPassword())){
                        Toast.makeText(getApplicationContext(), "Invalid username or password. Please try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    username = user.getUsername();
                }
            }
            //Build intent.
            //This allows us to send information between activities/fragments
            Intent intent = new Intent(this, MainActivity.class); //Designate MainActivity as intended

            //Save current user context
            SharedPreferences mPrefs = getSharedPreferences("account", MODE_PRIVATE);
            String currentUsername = mPrefs.getString("user", "User not found.");


            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.putString("user", username);
            prefsEditor.commit();

            //launch new activity
            startActivity(intent);
        }else{
            //TODO: Make error same as invalid username/pass...Do we really want to let just anyone know if a given email has an account or not?
            // This is exposing information about our data to the public - also fix above
            Toast.makeText(getApplicationContext(), "Your email is not registered, please register first.", Toast.LENGTH_SHORT).show();

        }
    }

    public void createUser(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}

