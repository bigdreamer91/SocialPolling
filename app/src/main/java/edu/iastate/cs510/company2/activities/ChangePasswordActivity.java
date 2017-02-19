package edu.iastate.cs510.company2.activities;

import android.content.Intent;
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

/**
 * Created by Dhaval on 11/16/2016.
 */

public class ChangePasswordActivity extends AppCompatActivity {

   // String currentUsername;
  //  public final static String DEFAULT_USER_NOT_FOUND = "[INVALID_USER]";
    public ServiceRegistry locator;
    public Gson gson = new Gson();

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         locator = ServiceRegistry.getInstance();

         if (locator.getService(PsGateway.class) == null) {
             locator.register(PsGateway.class, new MemStoreGw());
         }
         setContentView(R.layout.activity_change_password);
     }

    public void changePassword(View view) throws Exception{

        final List<Record> userRecords = new ArrayList<>();
        ReadMsg readMessage = new ReadMsg("http://iastate.510.com/SocialPolling", "account", "users");

        PsGateway gw = locator.getService(PsGateway.class);
        PsGateway.Response response = gw.send(readMessage);
        PsGateway.CbResponse fullReply = null;

        if(response.getStatus() != PsGateway.Status.success){
            //FAILED TO SEND MESSAGE
        }
        if (response instanceof PsGateway.CbResponse){
            fullReply = (PsGateway.CbResponse) response;
            userRecords.addAll(fullReply.getPayload());
        }

        //Get Current user
        String json;
        HashSet<User> users;
        if(userRecords.size() == 1){
            json = userRecords.get(0).pLoad;
            Type type = new TypeToken<HashSet<User>>(){}.getType();
            users = gson.fromJson(json,type);
        }
        else{
            //Impossible to reach here, just in case if it happens...
            Toast.makeText(getApplicationContext(), "Your email is not registered, please register first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the context UI
        EditText emailText = (EditText) findViewById(R.id.input_email_id);
        String email = emailText.getText().toString().trim();

        EditText oldPasswordText = (EditText) findViewById(R.id.input_old_passwd);
        String oldPassword = oldPasswordText.getText().toString().trim();

        EditText newPasswordText = (EditText) findViewById(R.id.input_new_passwd);
        String newPassword = newPasswordText.getText().toString().trim();

        EditText confirmNewPasswordText = (EditText) findViewById(R.id.input_confirm_passwd);
        String confirmNewPassword = confirmNewPasswordText.getText().toString().trim();

        if(!validateUIFields(email, oldPassword, newPassword,confirmNewPassword)){
            return;
        }

        // If all the fields are entered correctly then go ahead and change the password
        User currentUser = new User(email);

        if(users != null && users.contains(currentUser)) {
            for (User user : users) {
                if (user.equals(currentUser)) {

                    //Encrypt the entered old password and compare with the stored salted password
                    if (!Password.encryptPassword(oldPassword, user.getSalt()).equals(user.getPassword())) {
                        Toast.makeText(getApplicationContext(), "Enter the correct existing password. Please try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // set the new password  [Note: we are not changing the salt here]
                    user.setPassword(Password.encryptPassword(newPassword, user.getSalt()));
                    break;
                }
            }
        }
        else{
            // Should never occur
            Toast.makeText(getApplicationContext(), "Your email is not registered, please register first.", Toast.LENGTH_SHORT).show();
            return;
        }

        Type type = new TypeToken<HashSet<User>>(){}.getType();
        json = gson.toJson(users, type);

        //Update all users
        Message updateAllUsers = new UpdateMsg("http://iastate.510.com/SocialPolling", "account", "users", "0", json);
        gw.send(updateAllUsers);

        Toast.makeText(getApplicationContext(), "Password successfully changed.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean validateUIFields(String email, String oldPassword, String newPassword, String confirmNewPassword) {

        if( email == null || email.isEmpty()){
            Toast.makeText(getApplicationContext(), "Your email can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(oldPassword == null || oldPassword.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter old Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(newPassword == null || newPassword.isEmpty()){
            Toast.makeText(getApplicationContext(), "Your new password can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(confirmNewPassword == null || confirmNewPassword.isEmpty()){
            Toast.makeText(getApplicationContext(), "Password confirmation field can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!newPassword.equals(confirmNewPassword)){
              //  System.out.print(newPassword + " - "+ confirmNewPassword);
                Toast.makeText(getApplicationContext(), "Confirm your new password correctly", Toast.LENGTH_SHORT).show();
                return false;
        }

        return true;
    }
}
