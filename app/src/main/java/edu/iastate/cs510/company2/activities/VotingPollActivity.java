package edu.iastate.cs510.company2.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.content.Context;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import android.os.Environment;
import java.lang.Object;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.io.*;
import android.widget.Toast;
import android.widget.ListAdapter;

import edu.iastate.cs510.company2.models.IPoll;
import edu.iastate.cs510.company2.models.Poll;
import edu.iastate.cs510.company2.socialpolling.R;


public class VotingPollActivity extends AppCompatActivity {

    public int p;
    int counter = -1;

    private RadioGroup choice;
    private Button sub;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        int c = 0;
        if(b!=null) {
            if(b.containsKey("Position")){
                c = b.getInt("Position");
            }
        }

        setContentView(R.layout.activity_voting_poll);
        displayOutput(c);

        choice = (RadioGroup) findViewById(R.id.choices);
        sub = (Button) findViewById(R.id.button2);

        sub.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                for (int i = 0; i < choice.getChildCount(); i++) {
                    RadioButton r = (RadioButton) choice.getChildAt(i);
                    if (r.isChecked()) {
                        SharedPreferences myChoice = getSharedPreferences("mychoice", MODE_PRIVATE);
                        SharedPreferences.Editor editor = myChoice.edit();
                        editor.putString("pollID", Integer.toString(R.id.yourQuestion));
                        editor.putString("choiceID", Integer.toString(r.getId()));
                        editor.commit();

                        //String temp = Integer.toString(R.id.yourQuestion) + "  " + Integer.toString(r.getId());
                        Toast.makeText(VotingPollActivity.this, r.getText(), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void displayOutput(int c) {
        File file = new File(this.getExternalFilesDir(null).getAbsolutePath(), "pollfilenew1");

        StringBuilder text = new StringBuilder();

        ArrayList<String> radioStr = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            Gson gson = new Gson();
            //ArrayList<String> pollsList = new ArrayList<String>();
            //counter = # of polls;
            while ((line = br.readLine()) != null) {
                counter++;
                if (counter == c) {
                    JsonReader reader = new JsonReader(new StringReader(line));
                    reader.setLenient(true);
                    Poll pollObj = gson.fromJson(reader, Poll.class);
                    String line1 = pollObj.getQuestion().toString() + "\n";
                    text.append(line1 + '\n');
                    for (IPoll.Choice choice : pollObj.getChoices()) {
                        line1 += choice.choice.toString() + "\n";
                        radioStr.add(choice.choice.toString());
                    }
                    //pollsList.add(line1);

                    //text.append(line1 + '\n');

                    //text.append('\n');
                }

            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error reading file!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        TextView output = (TextView) findViewById(R.id.textView2);
        // Assuming that 'output' is the id of your TextView.
        output.setText(text);

        for (int i = 0; i < radioStr.size(); i++) {
            output = (TextView) this.findViewById(getResources().getIdentifier("radioButton" + (i + 5), "id", getPackageName()));
            output.setText(radioStr.get(i));
        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("VotingPoll Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}



