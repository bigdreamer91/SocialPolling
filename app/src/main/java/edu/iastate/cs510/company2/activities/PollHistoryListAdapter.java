package edu.iastate.cs510.company2.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.iastate.cs510.company2.gateway.DeleteMsg;
import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.gateway.ReadMsg;
import edu.iastate.cs510.company2.gateway.Record;
import edu.iastate.cs510.company2.infrastructure.ServiceRegistry;
import edu.iastate.cs510.company2.models.IPoll;
import edu.iastate.cs510.company2.models.Poll;
import edu.iastate.cs510.company2.persistence.MemStoreGw;
import edu.iastate.cs510.company2.socialpolling.R;

/**
 * Created by Greeshma Reddy on 10/31/2016.
 */

public class PollHistoryListAdapter extends ArrayAdapter<Poll> {

    private List<Poll> items;
    private Context context;
    private Context context1 = this.getContext();
    private int layoutResourceId;
    int temp = 0;
    public ServiceRegistry locator;


    public PollHistoryListAdapter(Context context, int resource) {
        super(context, resource);
    }
    public PollHistoryListAdapter(Context context, int layoutResourceID, List<Poll> items){
        super(context, layoutResourceID, items);
        this.items = items;
        this.context = context;
        this.layoutResourceId = layoutResourceID;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        locator  = ServiceRegistry.getInstance();
        if(locator.getService(PsGateway.class) == null) {
            locator.register(PsGateway.class, new MemStoreGw());
        }
        View row = convertView;
        PollHistoryListAdapter.PollViewHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new PollHistoryListAdapter.PollViewHolder();
        holder.myPoll = items.get(position);
        holder.createdByUser = (TextView)row.findViewById(R.id.createdbyUser);
        holder.categoryDisplay = (TextView)row.findViewById(R.id.categoryDisplay);
        holder.questionView = (TextView)row.findViewById(R.id.questionTextView);
        /*holder.choice1View = (TextView)row.findViewById(R.id.choice1TextView);
        holder.choice2View = (TextView)row.findViewById(R.id.choice2TextView);
        holder.choice3View = (TextView)row.findViewById(R.id.choice3TextView);
        holder.choice1VoteView = (TextView)row.findViewById(R.id.choice1VotesView);
        holder.choice2VoteView = (TextView)row.findViewById(R.id.choice2VotesView);
        holder.choice3VoteView = (TextView)row.findViewById(R.id.choice3VotesView);*/
        holder.overflowBtn = (Button)row.findViewById(R.id.overflowMenu);
        //holder.deleteButton = (Button)row.findViewById(R.id.deleteButton);

        holder.overflowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentRow = (View)v.getParent();
                LinearLayout lay1 = (LinearLayout)parentRow.getParent();
                ListView listview1 = (ListView)lay1.getParent();
                final int position = listview1.getPositionForView(parentRow);
                System.out.println(position);
                temp = position;
                PopupMenu popup = new PopupMenu(context,v);
                MenuInflater menuinflater = popup.getMenuInflater();
                menuinflater.inflate(R.menu.pollresults,popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        System.out.println("Item has been clicked");
                        Intent intent1 = new Intent(context1, PollDeleteActivity.class);
                        intent1.putExtra("Position",temp);
                        context1.startActivity(intent1);
                        return false;
                    }
                });
                popup.show();
            }
        });

      /*  holder.deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String line;
                Gson gson=new Gson();
                ArrayList<String> pollsList = new ArrayList<String>();
                ArrayList<String> jSonList = new ArrayList<String>();
                ArrayList<Poll> pollclassList = new ArrayList<Poll>();
                ArrayList<String> keysList = new ArrayList<String>();
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
                for(int i=0;i<pollRecords.size();i++)
                {
                    keysList.add(pollRecords.get(i).key);
                }
                for(int i=0;i<pollRecords.size();i++){
                    JsonReader reader = new JsonReader(new StringReader(pollRecords.get(i).pLoad));
                    reader.setLenient(true);
                    Poll pollObj=gson.fromJson(reader,Poll.class);
                    pollclassList.add(pollObj);
                    String line1=pollObj.getQuestion().toString()+"\n";
                    for(IPoll.Choice choice:pollObj.getChoices())
                        line1+=choice.choice.toString()+"\n";
                    line1+=pollObj.getCategory()+"\n";
                    line1+="Created: "+ pollObj.getCreator()+" at " + pollObj.getCreated()+"\n";
                    pollsList.add(line1);
                }
               /* String pollID=keysList.get(position);
                DeleteMsg deleteMessage=new DeleteMsg("http://iastate.510.com/SocialPolling", "Polls","key",keysList.size()-position-1+"",pollclassList.get(position));
                gw.send(deleteMessage);
                Intent intent = new Intent(context,MainActivity.class);
                context.startActivity(intent);

            }
        })*/;

        row.setTag(holder);
        setupItem(holder);
        return row;
    }



    private void setupItem(PollHistoryListAdapter.PollViewHolder holder) {
        holder.createdByUser.setText(holder.myPoll.getCreator());
        holder.categoryDisplay.setText(holder.myPoll.getCategory());
        holder.questionView.setText(holder.myPoll.getQuestion());
        //Collection<IPoll.Choice> choices = holder.myPoll.getChoices();
        ArrayList<IPoll.Choice> choices = (ArrayList<IPoll.Choice>) holder.myPoll.getChoices();
        /*holder.choice1View.setText(choices.get(0).choice.toString());
        holder.choice2View.setText(choices.get(1).choice.toString());
        holder.choice3View.setText(choices.get(2).choice.toString());
        holder.choice1VoteView.setText(String.valueOf(choices.get(0).votes));
        holder.choice2VoteView.setText(String.valueOf(choices.get(1).votes));
        holder.choice3VoteView.setText(String.valueOf(choices.get(2).votes));*/
    }

    public static class PollViewHolder {
        Poll myPoll;
        TextView createdByUser;
        TextView categoryDisplay;
        TextView questionView;
        /*TextView choice1View;
        TextView choice2View;
        TextView choice3View;
        TextView choice1VoteView;
        TextView choice2VoteView;
        TextView choice3VoteView;*/
        Button overflowBtn;
        //Button deleteButton;

    }
}
