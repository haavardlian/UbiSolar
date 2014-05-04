package com.sintef_energy.ubisolar.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.model.ResidenceAttributes;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;
import com.sintef_energy.ubisolar.presenter.RequestManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by baier on 5/4/14.
 */
public class CompareSettingsDialog extends DialogFragment {
        private View view = null;
private ResidenceAttributes resAttr;
    private CheckBox area, location, residents, energy;
    PreferencesManager pref = PreferencesManager.getInstance();



    @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout

            view = inflater.inflate(R.layout.dialog_filter_compare, null);

            area = (CheckBox)view.findViewById(R.id.areaCheckBox);
            location = (CheckBox)view.findViewById(R.id.locationCheckBox);
            energy = (CheckBox)view.findViewById(R.id.energyCheckBox);
            residents = (CheckBox)view.findViewById(R.id.resCheckBox);

            area.setChecked(pref.getComparisonSizeChecked());
            location.setChecked(pref.getComparisonAreaChecked());
            energy.setChecked(pref.getComparisonEnergyChecked());
            residents.setChecked(pref.getComparisonResidentsChecked());


            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            pref.setComparisonSizeChecked(area.isChecked());
                            pref.setComparisonEnergyChecked(energy.isChecked());
                            pref.setComparisonResidentsChecked(residents.isChecked());
                            pref.setComparisonAreaChecked(location.isChecked());

                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CompareSettingsDialog.this.getDialog().cancel();
                        }
                    })
                    .setTitle(R.string.comparison_setting);


            AlertDialog alertDialog = builder.create();



            return alertDialog;

        }
    }


