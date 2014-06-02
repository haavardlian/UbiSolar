/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
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
import android.widget.EditText;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.fragments.TipsFragment;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.presenter.RequestManager;

/**
 * Dialog for adding tips
 */
public class AddTipDialog extends DialogFragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.power_savers);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        View view = inflater.inflate(R.layout.dialog_add_tip, null);

        final EditText nameField = (EditText) view.findViewById(R.id.createTipDialogName);
        final EditText descriptionField = (EditText) view.findViewById(R.id.createTipDialogDescription);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.energy_saving_add_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        Tip tip = new Tip(0, nameField.getText().toString(), descriptionField.getText().toString(), 0, 0);

                        RequestManager.getInstance().doTipRequest().createTip(tip, getTargetFragment());
                        ((TipsFragment) getTargetFragment()).getAdapter().add(tip);
                        ((TipsFragment) getTargetFragment()).getAdapter().notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddTipDialog.this.getDialog().cancel();
                    }
                })
                .setTitle(R.string.energy_saving_add_title);

        return builder.create();
    }
}
