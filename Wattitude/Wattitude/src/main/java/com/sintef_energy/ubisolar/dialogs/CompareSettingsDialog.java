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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;

/**
 * Dialog to allow the user to filter on parameters
 */
public class CompareSettingsDialog extends DialogFragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final PreferencesManager preferenceManager = PreferencesManager.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        View view = inflater.inflate(R.layout.dialog_filter_compare, null);

        final CheckBox area = (CheckBox) view.findViewById(R.id.areaCheckBox);
        final CheckBox location = (CheckBox) view.findViewById(R.id.locationCheckBox);
        final CheckBox energy = (CheckBox) view.findViewById(R.id.energyCheckBox);
        final CheckBox residents = (CheckBox) view.findViewById(R.id.resCheckBox);

        area.setChecked(preferenceManager.getComparisonAreaChecked());
        location.setChecked(preferenceManager.getComparisonLocationChecked());
        energy.setChecked(preferenceManager.getComparisonEnergyChecked());
        residents.setChecked(preferenceManager.getComparisonResidentsChecked());


        builder.setView(view)
                // Add action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        preferenceManager.setComparisonAreaChecked(area.isChecked());
                        preferenceManager.setComparisonEnergyChecked(energy.isChecked());
                        preferenceManager.setComparisonResidentsChecked(residents.isChecked());
                        preferenceManager.setComparisonLocationChecked(location.isChecked());

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CompareSettingsDialog.this.getDialog().cancel();
                    }
                })
                .setTitle(R.string.comparison_setting);
        return builder.create();
    }
}


