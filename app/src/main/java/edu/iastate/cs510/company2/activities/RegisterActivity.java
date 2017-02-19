package edu.iastate.cs510.company2.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.iastate.cs510.company2.fragments.DatePickerFragment;
import edu.iastate.cs510.company2.gateway.CreateMsg;
import edu.iastate.cs510.company2.gateway.Message;
import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.gateway.ReadMsg;
import edu.iastate.cs510.company2.gateway.Record;
import edu.iastate.cs510.company2.gateway.UpdateMsg;
import edu.iastate.cs510.company2.infrastructure.ServiceRegistry;
import edu.iastate.cs510.company2.models.User;
import edu.iastate.cs510.company2.persistence.MemStoreGw;
import edu.iastate.cs510.company2.socialpolling.R;
import edu.iastate.cs510.company2.utility.Password;

import static edu.iastate.cs510.company2.socialpolling.R.id.username;

/**
 * Created by David on 10/11/2016.
 */
public class RegisterActivity extends AppCompatActivity {
    //TODO: Move this to a static reference somewhere so all activities reference same gson object?
    public Gson gson = new Gson();
    public ServiceRegistry locator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locator  = ServiceRegistry.getInstance();
        if(locator.getService(PsGateway.class) == null) {
            locator.register(PsGateway.class, new MemStoreGw());
        }
        setContentView(R.layout.activity_register);

        Spinner spinner = (Spinner) findViewById(R.id.genderSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Spinner spinnerPrefCatChoiceOne = (Spinner) findViewById(R.id.preferredCategorySpinnerChoiceOne);
        ArrayAdapter<CharSequence> adapterPrefCatOne = ArrayAdapter.createFromResource(this, R.array.preferred_categories_array, android.R.layout.simple_spinner_item);
        adapterPrefCatOne.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrefCatChoiceOne.setAdapter(adapterPrefCatOne);

        Spinner spinnerPrefCatChoiceTwo = (Spinner) findViewById(R.id.preferredCategorySpinnerChoiceTwo);
        ArrayAdapter<CharSequence> adapterPrefCatTwo = ArrayAdapter.createFromResource(this, R.array.preferred_categories_array, android.R.layout.simple_spinner_item);
        adapterPrefCatTwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrefCatChoiceTwo.setAdapter(adapterPrefCatTwo);

        Spinner spinnerPrefCatChoiceThree = (Spinner) findViewById(R.id.preferredCategorySpinnerChoiceThree);
        ArrayAdapter<CharSequence> adapterPrefCatThree = ArrayAdapter.createFromResource(this, R.array.preferred_categories_array, android.R.layout.simple_spinner_item);
        adapterPrefCatThree.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrefCatChoiceThree.setAdapter(adapterPrefCatThree);

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void createUser(View view){

        final List<Record> userRecords = new ArrayList<>();
        //TODO: Move read message creation to wrapper class, and create method like
        //      GatewayHelper.readAllUsers() that will make calls for us.. and handle response etc..
        ReadMsg readMessage = new ReadMsg("http://iastate.510.com/SocialPolling", "account", "users");
        //This is currently completely unnecessary because the memstore doesn't actually do anything asynchronously
        //        readMessage.setCallBack(new PsGateway.Callback() {
        //            @Override
        //            public void deliver(PsGateway.CbResponse deferredResponse) {
        //                userRecords.addAll(deferredResponse.getPayload());
        //            }
        //        });
        PsGateway gw = locator.getService(PsGateway.class);
        PsGateway.Response response = gw.send(readMessage);
        if(response.getStatus() != PsGateway.Status.success){
            //FAILED TO SEND MESSAGE
        }
        PsGateway.CbResponse fullReply = null;
        if (response instanceof PsGateway.CbResponse){
            fullReply = (PsGateway.CbResponse) response;
            userRecords.addAll(fullReply.getPayload());
        }

        //get json "blob" from call back response
        HashSet<User> users;
        String allUsersJson;
        String newUserJson;
        Type userSetType = new TypeToken<HashSet<User>>(){}.getType();
        Type userType = new TypeToken<User>(){}.getType();
        //I know I should only have 1 blob with this topic/key
        if(userRecords.size() == 1){
            allUsersJson = userRecords.get(0).pLoad;
            users = gson.fromJson(allUsersJson, userSetType);
        }
        else{
            //Never registered users before
            users = new  HashSet<>();
        }
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        //Create our new user and add it to the list
        User newUser = generateUserFromUI();//new User(username, email, password);
        if(newUser == null){
            CharSequence text = "You must fill out all information. Please check your form and try again.";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        if(users==null || users.isEmpty()){
            //This is the first time anything has been saved to "users" preferences
            // So we must create it.
            users = new  HashSet<>();
        }
        else{
            System.out.println("Got from users:/n" + users);
        }

        if(users.contains(newUser) || userNameTaken(users, newUser)){
            CharSequence text = "That username or email is already taken. Please choose another.";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else{
            users.add(newUser);
            allUsersJson = gson.toJson(users, userSetType);
            newUserJson = gson.toJson(newUser, userType);
            //System.out.println("Putting into 'users' inside shared preference account:\n" + allUsers);
            //Make sure never more than 1 in cluster
            Message updateAllUserMsg;
            if(users.size()==1){
                updateAllUserMsg = new CreateMsg("http://iastate.510.com/SocialPolling", "account", "users", allUsersJson);
            }
            else{
                //HARDCODE 1 because we always update this json never add more than 0 record to this cluster
                updateAllUserMsg = new UpdateMsg("http://iastate.510.com/SocialPolling", "account", "users", "0", allUsersJson);
            }

            CreateMsg createNewUserMsg = new CreateMsg("http://iastate.510.com/SocialPolling", "users", newUser.getUsername(), newUserJson);

            //Todo: check status
            gw.send(updateAllUserMsg);
            gw.send(createNewUserMsg);

            //Save current user context
            SharedPreferences mPrefs = getSharedPreferences("account", MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.putString("user", newUser.getUsername());
            prefsEditor.commit();

            //I don't agree with the product owner that this should auto login.
            //Should need to wait for async call to complete and account to be "Created"
            //also may want to require confirmation email link be followed by user to prevent bots etc
            Intent intent = new Intent(this, MainActivity.class);
            //Put the information in the intent
            //intent.putExtra(LoginActivity.USERNAME, newUser.getUsername());
            //launch new activity
            startActivity(intent);
        }

    }

    private User generateUserFromUI(){
        //Pull Info from UI
        EditText userText = (EditText) findViewById(username);
        String username = userText.getText().toString().trim();

        EditText emailText = (EditText) findViewById(R.id.email);
        String email = emailText.getText().toString().trim();

        EditText passwordText = (EditText) findViewById(R.id.password);
        String password = passwordText.getText().toString().trim();

        EditText nameText = (EditText) findViewById(R.id.name);
        String name = nameText.getText().toString().trim();

        EditText birthdayText = (EditText) findViewById(R.id.birthday);
        String birthday = birthdayText.getText().toString().trim();

        Spinner genderText = (Spinner) findViewById(R.id.genderSpinner);
        String gender = genderText.getSelectedItem().toString().trim();

        EditText cityText = (EditText) findViewById(R.id.city);
        String city = cityText.getText().toString().trim();

        EditText countryText = (EditText) findViewById(R.id.country);
        String country = countryText.getText().toString().trim();

        ArrayList<String> categories = new ArrayList<String>();
        Spinner prefSpinnerOne = (Spinner) findViewById(R.id.preferredCategorySpinnerChoiceOne);
        String prefChoiceOne = prefSpinnerOne.getSelectedItem().toString().trim();

        Spinner prefSpinnerTwo = (Spinner) findViewById(R.id.preferredCategorySpinnerChoiceTwo);
        String prefChoiceTwo = prefSpinnerTwo.getSelectedItem().toString().trim();

        Spinner prefSpinnerThree = (Spinner) findViewById(R.id.preferredCategorySpinnerChoiceThree);
        String prefChoiceThree = prefSpinnerThree.getSelectedItem().toString().trim();

        categories.add(prefChoiceOne);
        categories.add(prefChoiceTwo);
        categories.add(prefChoiceThree);

        //Really good validation technique, trust me...
        if(username == null || username.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty() ||
                name == null || name.isEmpty() || birthday == null || birthday.isEmpty() || gender == null || gender.isEmpty() ||
                city == null || city.isEmpty() || country == null || country.isEmpty()){
            return null;

        }

        // Generate salt & encrypt salted password
        String salt="";
        String saltedPassword="";
        try {

            salt = Password.generateSalt();
            saltedPassword = Password.encryptPassword(password, salt);

        }catch(Exception e){
            e.printStackTrace();
        }

        // System.out.println(saltedPassword  +   "  ");
        return new User(username, email, saltedPassword, salt, name, birthday, gender, city, country, categories);
    }

    private boolean userNameTaken(HashSet<User> users, User newUser) {
        for(User user  : users){
            if(user.getUsername().equals(newUser.getUsername())){
                return true;
            }
        }
        return false;
    }
}
