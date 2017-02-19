package edu.iastate.cs510.company2.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import edu.iastate.cs510.company2.socialpolling.R;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        String formattedMonth = "" + month +1;
        String formattedDayOfMonth = "" + day;

        if(month < 10){

            formattedMonth = "0" + month;
        }
        if(day < 10){

            formattedDayOfMonth = "0" + day;
        }
        ((TextView) getActivity().findViewById(R.id.birthday)).setText(formattedMonth + "/" + formattedDayOfMonth + "/" + year);
    }
}