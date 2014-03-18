package com.sintef_energy.ubisolar.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import com.sintef_energy.ubisolar.IView.IDateCallback;

import java.util.Calendar;

/**
 * Created by perok on 12.03.14.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle args) {
        // Use the current date as the default date in the picker

        int year = 0;
        int month = 0;
        int day = 0;

        if(args == null) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        else {
            year = args.getInt("year");
            month = args.getInt("month");
            day = args.getInt("day");
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user

        IDateCallback target = (IDateCallback)getTargetFragment();

        if(target != null)
            target.setDate(year, month, day);
    }
}
