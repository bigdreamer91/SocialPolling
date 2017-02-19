package edu.iastate.cs510.company2.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import edu.iastate.cs510.company2.models.Poll;
import edu.iastate.cs510.company2.models.User;
import edu.iastate.cs510.company2.persistence.MemStoreGw;
import edu.iastate.cs510.company2.socialpolling.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String currentUsername;
    private ListView lv;
    private PollListAdapter adapter1;
    Button search;
    public ServiceRegistry locator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locator  = ServiceRegistry.getInstance();
        if(locator.getService(PsGateway.class) == null) {
            locator.register(PsGateway.class, new MemStoreGw());
        }
        setContentView(R.layout.activity_main);

        //Create a new TextView with our message
        TextView textView = new TextView(this);
        textView.setTextSize(30);
        SharedPreferences mPrefs = getSharedPreferences("account", MODE_PRIVATE);
        currentUsername = mPrefs.getString("user", "User not found."); //TODO: Use same static variable as CreatePollActivity (and all others)
        if(currentUsername!=null){
            textView.setTextColor(Color.WHITE);
            textView.setText("Hello " + currentUsername + ", Welcome to Social Polling!");

        }

        //Add/Inject the TextView into our layout. Cast to ViewGroup to expose addView
        //ViewGroup is a superclass of all layouts and exposes addView()
        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_main);
        layout.addView(textView);

        //TODO: Why doing it this way? Just add onClick attribute to the View in the xml and then rename the method to that..
        search = (Button) findViewById(R.id.search1);
        search.setOnClickListener(this);


        lv = (ListView)findViewById(R.id.listview);

        String line;
        Gson gson=new Gson();

        // reading the userRecord saved in memstore
        final List<Record> userRecord = new ArrayList<>();
        ReadMsg readMessageUser = new ReadMsg("http://iastate.510.com/SocialPolling", "users", currentUsername);
        PsGateway gwUser = locator.getService(PsGateway.class);
        PsGateway.Response responseUser = gwUser.send(readMessageUser);
        PsGateway.CbResponse fullReplyUser;

        if (responseUser instanceof PsGateway.CbResponse){
            fullReplyUser = (PsGateway.CbResponse) responseUser;
            userRecord.addAll(fullReplyUser.getPayload());
        }

        ArrayList<String> preferredCategories = null;

        if(userRecord.size()==1){
            JsonReader userReader = new JsonReader(new StringReader(userRecord.get(0).pLoad));
            User userobj = gson.fromJson(userReader,User.class);
            preferredCategories = new ArrayList<String>();
            preferredCategories = userobj.getPreferredCategories();
        }
        else{
            return;
        }

        // reading the polls in the memstore
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

        ArrayList<Poll> pollclassList = new ArrayList<Poll>(); // final list of polls with preferred categories first followed by other categories that will be displayed in mainactivity
        ArrayList<Poll> priorityList = new ArrayList<Poll>(); // priority list keeps list of polls matching the preferred categories
        ArrayList<Poll> tempList = new ArrayList<Poll>(); // list keeps all the polls not matching the preferred categories

        for(int i=pollRecords.size()-1;i>=0;i--){
            JsonReader reader = new JsonReader(new StringReader(pollRecords.get(i).pLoad));
            reader.setLenient(true);
            Poll pollObj=gson.fromJson(reader,Poll.class);
            if(preferredCategories.contains(pollObj.getCategory())){
                priorityList.add(pollObj);
            }
            else{
                tempList.add(pollObj);
            }
        }

        pollclassList.addAll(priorityList);
        pollclassList.addAll(tempList);

        adapter1 = new PollListAdapter(this,R.layout.poll_list_view,pollclassList);
        lv.setAdapter(adapter1);
        lv.setClickable(true);

        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(position>=0)
                {
                    Intent intent = new Intent(MainActivity.this, VotingPollActivity.class);
                    intent.putExtra("Position", position);
                    startActivity(intent);
                }
            }
        });
    }

    public void createPoll(View view){
        Intent intent = new Intent(this, CreatePollActivity.class);
        startActivity(intent);
    }
    public void profileDetail(View view){
        Intent intent = new Intent(this, ProfileDetailActivity.class);
        startActivity(intent);
    }

    public void onClick(View view){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }



};