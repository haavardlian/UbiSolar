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
import android.widget.RatingBar;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.fragments.EnergySavingTabFragment;
import com.sintef_energy.ubisolar.fragments.TipsFragment;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.model.TipRating;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;
import com.sintef_energy.ubisolar.presenter.RequestManager;

public class TipDialog extends DialogFragment {

    private View view;
    private Tip tip;
    private TextView descriptionField;
    private RatingBar ratingField;

    public TipDialog(Tip tip) {
        this.tip = tip;
    }

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

        view = inflater.inflate(R.layout.dialog_tip, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(getString(R.string.energy_saving_move_tip), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ((EnergySavingTabFragment)getTargetFragment().getTargetFragment())
                                .getAdapter().getYourFragment().getAdapter().add(tip);

                        ((EnergySavingTabFragment)getTargetFragment().getTargetFragment())
                                .getAdapter().getYourFragment().getAdapter().notifyDataSetChanged();
                        PreferencesManager.getInstance().addSubscribedTip(tip);
                    }
                })
                .setNegativeButton(getString(R.string.energy_saving_close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        getDialog().cancel();
                    }
                })
                .setTitle(tip.getName());

        descriptionField = (TextView) view.findViewById(R.id.tipDialogDescription);
        ratingField = (RatingBar) view.findViewById(R.id.tipDialogRatingBar);

        descriptionField.setText(tip.getDescription());
        ratingField.setRating(tip.getAverageRating());
        ratingField.setIsIndicator(false);
        ratingField.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ratingBar.setRating(v);
                tip.setAverageRating((int)v);
                TipRating rating = new TipRating(0, tip.getId(), (short)v, 1);
                RequestManager.getInstance().doTipRequest().createRating(rating, getTargetFragment());
                ((TipsFragment) getTargetFragment()).getAdapter().notifyDataSetChanged();
            }
        });

        return builder.create();

    }
}
