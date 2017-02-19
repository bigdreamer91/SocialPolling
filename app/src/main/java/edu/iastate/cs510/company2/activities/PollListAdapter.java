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

import java.util.ArrayList;
import java.util.List;

import edu.iastate.cs510.company2.models.IPoll;
import edu.iastate.cs510.company2.models.Poll;
import edu.iastate.cs510.company2.socialpolling.R;

/**
 * Created by geethanjalijeevanatham on 10/24/16.
 */

public class PollListAdapter extends ArrayAdapter<Poll> {

    private List<Poll> items;
    private Context context;
    private Context context1 = this.getContext();
    private int layoutResourceId;
    int temp = 0;

    public PollListAdapter(Context context, int resource) {
        super(context, resource);
    }
    public PollListAdapter(Context context, int layoutResourceID, List<Poll> items){
        super(context, layoutResourceID, items);
        this.items = items;
        this.context = context;
        this.layoutResourceId = layoutResourceID;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PollViewHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new PollViewHolder();
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
        holder.voteButton = (Button)row.findViewById(R.id.voteButton);
        holder.overflowBtn = (Button)row.findViewById(R.id.overflowMenu);

        holder.voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentRow = (View)v.getParent();
                ListView listview1 = (ListView)parentRow.getParent();
                final int position = listview1.getPositionForView(parentRow);
                System.out.println(position);
                temp = position;
                Intent intent2 = new Intent(context1, NewVotingPollActivity.class);
                intent2.putExtra("Position",temp);
                context1.startActivity(intent2);
            }
        });

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
                        Intent intent1 = new Intent(context1, PollResultsActivity.class);
                        intent1.putExtra("Position",temp);
                        context1.startActivity(intent1);
                        return false;
                    }
                });
                popup.show();
            }
        });

        row.setTag(holder);
        setupItem(holder);
        return row;
    }



    private void setupItem(PollViewHolder holder) {
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
        Button voteButton;
        Button overflowBtn;

    }
}
