package edu.iastate.cs510.company2.activities;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.gateway.ReadMsg;
import edu.iastate.cs510.company2.gateway.Record;
import edu.iastate.cs510.company2.gateway.UpdateMsg;
import edu.iastate.cs510.company2.infrastructure.ServiceRegistry;
import edu.iastate.cs510.company2.models.User;
import edu.iastate.cs510.company2.persistence.MemStoreGw;
import edu.iastate.cs510.company2.socialpolling.R;

public class ProfileDetailActivity extends AppCompatActivity {
    public Gson gson = new Gson();
    public ServiceRegistry locator = ServiceRegistry.getInstance();
    private User currentUser;
    private User originalUser;
    private PsGateway gw;
    String currentUsername;
    int currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(locator.getService(PsGateway.class) == null){
            locator.register(PsGateway.class, new MemStoreGw());
        }
        setContentView(R.layout.activity_profile_detail);
        Spinner spinner = (Spinner) findViewById(R.id.genderSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        SharedPreferences mPrefs = getSharedPreferences("account", MODE_PRIVATE);
        currentUsername = mPrefs.getString("user", "User not found om preferemces. Can't happen if register is correct");

        ReadMsg readMessage = new ReadMsg("http://iastate.510.com/SocialPolling", "users", currentUsername);
        gw = locator.getService(PsGateway.class);
        PsGateway.Response response = gw.send(readMessage);
        PsGateway.CbResponse fullReply;

        List<Record> userRecord = new ArrayList<>();
        if (response instanceof PsGateway.CbResponse){
            fullReply = (PsGateway.CbResponse) response;
            userRecord.addAll(fullReply.getPayload());
        }
        String userJson;// = userRecords.get(0).pLoad;

        if(userRecord.size() == 1){
            userJson = userRecord.get(0).pLoad;
            currentUserId = userRecord.get(0).index;
            Type userType = new TypeToken<User>(){}.getType();
            currentUser = gson.fromJson(userJson,userType);
            originalUser = gson.fromJson(userJson,userType);
            setupUI();
        }
        else{
            //Can't get to login activity without user being in DB.
            return;
        }


    }

    private void setupUI() {
        TextView userText = (TextView) findViewById(R.id.textViewUser);
        userText.setTextColor(Color.WHITE);
        userText.setText(currentUser.getUsername());

        EditText emailText = (EditText) findViewById(R.id.email);
        emailText.setText(currentUser.getEmail());
        EditText nameText = (EditText) findViewById(R.id.name);
        nameText.setText(currentUser.getName());
        EditText birthdayText = (EditText) findViewById(R.id.birthday);
        birthdayText.setText(currentUser.getBirthdate());
        Spinner genderText = (Spinner) findViewById(R.id.genderSpinner);
        genderText.setSelection(((ArrayAdapter<String>)genderText.getAdapter()).getPosition(currentUser.getGender()));
        EditText cityText = (EditText) findViewById(R.id.city);
        cityText.setText(currentUser.getCity());
        EditText countryText = (EditText) findViewById(R.id.country);
        countryText.setText(currentUser.getCountry());
    }

    private void updateUserFromUI(User user){
        //Pull Info from UI

        EditText emailText = (EditText) findViewById(R.id.email);
        String email = emailText.getText().toString().trim();

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


        if(email != null && !email.isEmpty()){
            user.setEmail(email);
        }

        if(country != null && !country.isEmpty()){
            user.setCountry(country);
        }
        if(name != null && !name.isEmpty()){
            user.setName(name);
        }

        if(city != null && !city.isEmpty()){
            user.setCity(city);
        }

        if(gender != null && !gender.isEmpty()){
            user.setGender(gender);
        }
        if(birthday != null && !birthday.isEmpty()){
            user.setBirthdate(birthday);
        }





    }
    public void saveUserDetails(View view){
        ReadMsg readAllUsersMessage = new ReadMsg("http://iastate.510.com/SocialPolling", "account", "users");
        PsGateway gw = locator.getService(PsGateway.class);

        //TODO: Refactor for callback, can't assume CbResponse
        PsGateway.Response response = gw.send(readAllUsersMessage);
        PsGateway.CbResponse fullReply;
        List<Record> userRecords = new ArrayList<>();
        if (response instanceof PsGateway.CbResponse){
            fullReply = (PsGateway.CbResponse) response;
            userRecords.addAll(fullReply.getPayload());
        }
        HashSet<User> allUsers = new HashSet<>();
        String allUsersJson;
        if(userRecords.size() == 1){
            allUsersJson = userRecords.get(0).pLoad;
            Type userSetType = new TypeToken<HashSet<User>>(){}.getType();
            allUsers = gson.fromJson(allUsersJson,userSetType);
        }
        allUsers.remove(originalUser);
        updateUserFromUI(currentUser);

        allUsers.add(currentUser);

        UpdateMsg updateAllUserMsg =  new UpdateMsg("http://iastate.510.com/SocialPolling", "account", "users", "0", gson.toJson(allUsers));
        UpdateMsg updateCurrentUser = new UpdateMsg("http://iastate.510.com/SocialPolling", "users", currentUsername, "" + currentUserId, gson.toJson(currentUser));

        gw.send(updateAllUserMsg);
        gw.send(updateCurrentUser);



    }
    public void PollHistory(View view){
        Intent intent = new Intent(this, PollHistoryActivity.class);
        startActivity(intent);
    }

    public void passwordChange(View view) throws Exception{
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }
}
