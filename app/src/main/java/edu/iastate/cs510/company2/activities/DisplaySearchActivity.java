package edu.iastate.cs510.company2.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
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

public class DisplaySearchActivity extends AppCompatActivity {

    TextView textView;
    private ListView lv;
    private ArrayAdapter<String> listAdapter ;
    private PollListAdapter adapter1;
    StringBuilder text;
    public ServiceRegistry locator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locator  = ServiceRegistry.getInstance();
        if(locator.getService(PsGateway.class) == null) {
            locator.register(PsGateway.class, new MemStoreGw());
        }
        setContentView(R.layout.activity_display_search);

        text=new StringBuilder();
        textView = (TextView) findViewById(R.id.textView8);
        Intent intent = getIntent();
        String mString = intent.getStringExtra("cat");

        //textView.setText(mString);
        Context context = DisplaySearchActivity.this;
        String line, line1;
        Gson gson = new Gson();
        ArrayList<String> pollsList = new ArrayList<String>();
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

        for (int i = pollRecords.size() - 1; i >= 0; i--) {
            JsonReader reader = new JsonReader(new StringReader(pollRecords.get(i).pLoad));
            reader.setLenient(true);
            Poll pollObj = gson.fromJson(reader, Poll.class);
            pollclassList.add(pollObj);
            String cat= pollObj.getCategory();
            if(mString.equals(cat)) {
                line1 = pollObj.getQuestion().toString() + "\n";
                for (IPoll.Choice choice : pollObj.getChoices())
                    line1 += choice.choice.toString() + "\n";
                line1 += pollObj.getCategory() + "\n";
                line1 += "Created by: " + pollObj.getCreator() + " at " + pollObj.getCreated() + "\n";
                text.append(line1 + '\n');
            }
        }
        textView.setTextColor(Color.WHITE);
        textView.setText(text);
    }
}
