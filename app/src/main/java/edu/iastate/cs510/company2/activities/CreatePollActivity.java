package edu.iastate.cs510.company2.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.iastate.cs510.company2.gateway.CreateMsg;
import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.gateway.ReadMsg;
import edu.iastate.cs510.company2.gateway.Record;
import edu.iastate.cs510.company2.infrastructure.ServiceRegistry;
import edu.iastate.cs510.company2.models.IPoll;
import edu.iastate.cs510.company2.models.Poll;
import edu.iastate.cs510.company2.persistence.MemStoreGw;
import edu.iastate.cs510.company2.socialpolling.R;


public class CreatePollActivity extends AppCompatActivity {
    String currentUsername;
    String imageBase64;
    String link;
    public ServiceRegistry locator = ServiceRegistry.getInstance();
    public final static String DEFAULT_USER_NOT_FOUND = "[INVALID_USER]"; //TODO: Move this to a utility/helper class and ensure a username can never match this.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locator  = ServiceRegistry.getInstance();
        if(locator.getService(PsGateway.class) == null) {
            locator.register(PsGateway.class, new MemStoreGw());
        }
        setContentView(R.layout.activity_create_poll);
        Spinner spinner = (Spinner) findViewById(R.id.categories_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void sendMessage(View view){

        //TODO: Remove this? We don't use it, why are we reading the polls at all, this is activity to create new poll
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

        //Get Current user
        SharedPreferences mPrefs = getSharedPreferences("account", MODE_PRIVATE);
        currentUsername = mPrefs.getString("user", DEFAULT_USER_NOT_FOUND);
        if(DEFAULT_USER_NOT_FOUND.equals(currentUsername)){
           // Log.ERROR("User variable not set in preferences. This shouldn't happpen. Verify Login/Registration Activity have executed");
            return;
        }
        Gson gson = new Gson();
        EditText question = (EditText) findViewById(R.id.yourQuestion);
        EditText choiceOne = (EditText) findViewById(R.id.choice1);
        EditText choiceTwo = (EditText) findViewById(R.id.choice2);
        EditText choiceThree = (EditText) findViewById(R.id.choice3);
        EditText link = (EditText) findViewById(R.id.LinkeditText);

        Collection<IPoll.Choice> choices = new ArrayList<>();
        IPoll.Choice red = new IPoll.Choice(choiceOne.getText().toString(), 0);
        IPoll.Choice green = new IPoll.Choice(choiceTwo.getText().toString(), 0);
        IPoll.Choice blue = new IPoll.Choice(choiceThree.getText().toString(), 0);
        String linkString=link.getText().toString();

        choices.add(red);
        choices.add(green);
        choices.add(blue);

        Spinner categoryspinner = (Spinner) findViewById(R.id.categories_spinner);
        String category = categoryspinner.getSelectedItem().toString().trim();
        
        Poll myPoll = new Poll(currentUsername,category, question.getText().toString(), choices, imageBase64,linkString);

        String json = gson.toJson(myPoll);

        //TODO: Why is the key literally key? Want to store by topic? Store by username? Both? Others?
        CreateMsg createNewUserMsg = new CreateMsg("http://iastate.510.com/SocialPolling", "Polls", "key", json);
        gw.send(createNewUserMsg);

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

    }

    public void getImage(View view){
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);

                ByteArrayOutputStream baos=new  ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
                byte [] b=baos.toByteArray();
                String encodedString=Base64.encodeToString(b, Base64.DEFAULT);
                imageBase64 = encodedString;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}