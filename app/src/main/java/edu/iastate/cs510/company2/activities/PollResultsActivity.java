package edu.iastate.cs510.company2.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.net.URL;
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
 * Created by geethanjalijeevanatham on 10/28/16.
 */

public class PollResultsActivity extends AppCompatActivity {

    public ServiceRegistry locator;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        locator  = ServiceRegistry.getInstance();
        if(locator.getService(PsGateway.class) == null) {
            locator.register(PsGateway.class, new MemStoreGw());
        }
        Bundle b = getIntent().getExtras();
        int c=b.getInt("Position");
        setContentView(R.layout.poll_results);
        displayOutput(c);
    }

    public void displayOutput(int c){
        Context context = PollResultsActivity.this;
        try {
            String line;
            Gson gson=new Gson();
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
            int size = pollRecords.size();
            size = size - 1;

            JsonReader reader = new JsonReader(new StringReader(pollRecords.get(size-c).pLoad));
            reader.setLenient(true);
            Poll pollObj=gson.fromJson(reader,Poll.class);
            TextView question = (TextView)findViewById(R.id.questionPollResults);
            TextView category = (TextView)findViewById(R.id.categoryResults);
            TextView created = (TextView)findViewById(R.id.createdResults);
            TextView choice1 = (TextView)findViewById(R.id.choice1Results);
            TextView choice2 = (TextView)findViewById(R.id.choice2Results);
            TextView choice3 = (TextView)findViewById(R.id.choice3Results);
            TextView votes1 = (TextView)findViewById(R.id.choice1VotesResults);
            TextView votes2 = (TextView)findViewById(R.id.choice2VotesResults);
            TextView votes3 = (TextView)findViewById(R.id.choice3VotesResults);
            question.setText(pollObj.getQuestion());
            category.setText(pollObj.getCategory());
            created.setText(pollObj.getCreator());
            ArrayList<IPoll.Choice> choices = (ArrayList<IPoll.Choice>) pollObj.getChoices();
            choice1.setText(choices.get(0).choice.toString());
            choice2.setText(choices.get(1).choice.toString());
            choice3.setText(choices.get(2).choice.toString());
            votes1.setText(String.valueOf(choices.get(0).votes));
            votes2.setText(String.valueOf(choices.get(1).votes));
            votes3.setText(String.valueOf(choices.get(2).votes));


            byte[] decodedBytes = Base64.decode(pollObj.getImageString(), 0);
            Bitmap bitmap =  BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(bitmap);

            Button link = (Button) findViewById(R.id.pollLink);
            //link.setText("Link");
            Uri url = Uri.parse(pollObj.getLink());
            final Intent intent = new Intent(Intent.ACTION_VIEW, url);
            intent.setData(url);
            link.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    startActivity(intent);
                }
            });
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
