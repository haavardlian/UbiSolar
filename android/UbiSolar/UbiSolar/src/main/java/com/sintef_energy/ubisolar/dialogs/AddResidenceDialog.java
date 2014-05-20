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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.sintef_energy.ubisolar.IView.IPresenterCallback;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.database.energy.ResidenceModel;
import com.sintef_energy.ubisolar.presenter.ResidencePresenter;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;

/**
 * Created by Lars Erik on 03.05.2014.
 */
public class AddResidenceDialog extends DialogFragment {

    private TextView nameField, descriptionField;
    private EditText residentsField, areaField, zipCodeField;
    private ResidencePresenter residencePresenter;
    private Spinner energyClassSpinner;

    private View view;
    private ArrayAdapter<String> energyClassAdapter;
    private static final String TAG = AddResidenceDialog.class.getName();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            residencePresenter = ((IPresenterCallback) activity).getResidencePresenter();

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + TotalEnergyPresenter.class.getName());
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        view = inflater.inflate(R.layout.dialog_add_residence, null);

        /*Set up view*/
        nameField = (EditText) view.findViewById(R.id.dialog_add_residence_name);
        descriptionField = (EditText) view.findViewById(R.id.dialog_add_residence_description);
        residentsField = (EditText) view.findViewById(R.id.dialog_add_residence_residents);
        zipCodeField = (EditText) view.findViewById(R.id.dialog_add_residence_zip_code);
        areaField = (EditText) view.findViewById(R.id.dialog_add_residence_area);
        energyClassSpinner = (Spinner) view.findViewById(R.id.dialog_add_residence_spinner);


        energyClassAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                android.R.id.text1,
                getResources().getStringArray(R.array.energy_classes)
        );

        builder.setView(view)
                .setPositiveButton(R.string.addResidenceDialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /*Create new device model*/
                        ResidenceModel residenceModel = new ResidenceModel();

                        residenceModel.setHouseName(nameField.getText().toString());
                        residenceModel.setDescription(descriptionField.getText().toString());
                        residenceModel.setResidents(Integer.parseInt(residentsField.getText().toString()));
                        residenceModel.setArea(Integer.parseInt(areaField.getText().toString()));
                        residenceModel.setZipCode(Integer.parseInt(zipCodeField.getText().toString()));
                        residenceModel.setEnergyClass((char) energyClassSpinner.getSelectedItemPosition());

                        residencePresenter.addResidence(residenceModel, getActivity().getContentResolver());

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddResidenceDialog.this.getDialog().cancel();
                    }
                })
                .setTitle(R.string.device_add_title);

        energyClassSpinner.setAdapter(energyClassAdapter);

        return builder.create();

    }

}
