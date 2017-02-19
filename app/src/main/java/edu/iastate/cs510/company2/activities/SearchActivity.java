package edu.iastate.cs510.company2.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import edu.iastate.cs510.company2.socialpolling.R;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{

    Button button;
    String value;

    EditText editText;
    Spinner spinner;
    String Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        button = (Button) findViewById(R.id.search2);
        button.setOnClickListener(this);

       //editText= (EditText) findViewById(R.id.cat);

        spinner = (Spinner) findViewById(R.id.spinner2);

        String[] categories = {"Entertainment" , "Politics", "Social" , "Sports" , "Science",
                                "Economy", "Technology", "Leisure"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Text = spinner.getSelectedItem().toString();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Text = parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    public void onClick(View view){
        Intent intent = new Intent(this, DisplaySearchActivity.class);
        intent.putExtra("cat",Text);
        startActivity(intent);
    }


};


