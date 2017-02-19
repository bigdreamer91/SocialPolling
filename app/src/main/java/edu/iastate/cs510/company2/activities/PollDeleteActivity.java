package edu.iastate.cs510.company2.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.gateway.ReadMsg;
import edu.iastate.cs510.company2.gateway.Record;
import edu.iastate.cs510.company2.infrastructure.ServiceRegistry;
import edu.iastate.cs510.company2.models.IPoll;
import edu.iastate.cs510.company2.models.Poll;
import edu.iastate.cs510.company2.persistence.MemStoreGw;
import edu.iastate.cs510.company2.socialpolling.R;

/**
 * Created by swf94 on 12/25/2016.
 */

public class PollDeleteActivity extends AppCompatActivity {
    private ListView myListView;
    private PollDeleteAdapter listAdapter;
    private String username;
    public ServiceRegistry locator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locator  = ServiceRegistry.getInstance();
        if(locator.getService(PsGateway.class) == null) {
            locator.register(PsGateway.class, new MemStoreGw());
        }
        setContentView(R.layout.activity_poll_history);

        Bundle b = getIntent().getExtras();
        int c=b.getInt("Position");

        //Create a text space to put page name here
        TextView textView = new TextView(this);
        textView.setTextSize(20);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_poll_history);
        textView.setText("My Poll History");
        //Add page name to layout
        layout.addView(textView);

        Context context = PollDeleteActivity.this;
        myListView = (ListView)findViewById(R.id.listview);
        Poll pollObj;

        //get username from Shared preferences
        SharedPreferences mPrefs = getSharedPreferences("account", MODE_PRIVATE);
        username = mPrefs.getString("user", "User not found.");

        try {
            Gson gson=new Gson();
            ArrayList<String> pollsMessage = new ArrayList<String>();
            ArrayList<String> jSonList = new ArrayList<String>();
            ArrayList<Poll> pollclassList = new ArrayList<Poll>();

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
            c = pollRecords.size()-c-1;
                JsonReader reader = new JsonReader(new StringReader(pollRecords.get(c).pLoad));
                reader.setLenient(true);
                pollObj=gson.fromJson(reader,Poll.class);

                if(pollObj.getCreator().equals(username)) {
                    String line1 = pollObj.getQuestion().toString() + "\n";
                    for (IPoll.Choice choice : pollObj.getChoices())
                        line1 += Integer.toString(choice.votes) + " votes  " + choice.choice.toString() + "\n";
                    line1 += pollObj.getCategory() + "\n";
                    line1 += "Created at " + pollObj.getCreated() + "\n";
                    pollsMessage.add(line1);
                    pollclassList.add(pollObj);
                }

            listAdapter = new PollDeleteAdapter(this,R.layout.poll_delete_view,pollclassList,c);
            myListView.setAdapter(listAdapter);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
