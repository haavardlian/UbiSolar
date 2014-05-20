/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.sintef_energy.ubisolar.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import com.sintef_energy.ubisolar.IView.IDateCallback;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by perok on 12.03.14.
 *
 * A date picker dialog.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle args) {
        // Use the current date as the default date in the picker

        int year;
        int month;
        int day;

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
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);

        // Max date is now.
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user

        IDateCallback target = (IDateCallback)getTargetFragment();

        if(target != null)
            target.setDate(year, month, day);
    }
}
