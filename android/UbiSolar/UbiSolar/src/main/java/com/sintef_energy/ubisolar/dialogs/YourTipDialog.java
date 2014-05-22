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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.Session;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.YourTipAdapter;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.model.TipRating;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;
import com.sintef_energy.ubisolar.presenter.RequestManager;

/**
 * Dialog displaying the option to remove or share a tip under your tips
 */
public class YourTipDialog extends DialogFragment {
    private Tip tip;
    private YourTipAdapter yourTipAdapter;

    public YourTipDialog(Tip tip, YourTipAdapter yourTipAdapter) {
        this.tip = tip;
        this.yourTipAdapter = yourTipAdapter;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.power_savers);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        View view = inflater.inflate(R.layout.dialog_tip, null);
        builder.setView(view)
                // Add action buttons
                .setNegativeButton(getString(R.string.energy_saving_your_tip_remove),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        yourTipAdapter.remove(tip);
                        yourTipAdapter.notifyDataSetChanged();
                        PreferencesManager.getInstance().removeSubscribedTip(tip);
                    }
                })
                .setTitle(tip.getName());

        if(Session.getActiveSession().isOpened()) {
            builder.setPositiveButton(getString(R.string.energy_saving_your_tip_share),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                RequestManager.getInstance().doFacebookRequest().postMessage(getTargetFragment(),
                        tip.getDescription(), tip.getName());
                }
            });
        }

        TextView descriptionField = (TextView) view.findViewById(R.id.tipDialogDescription);
        RatingBar ratingField = (RatingBar) view.findViewById(R.id.tipDialogRatingBar);

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
            }
        });
        return builder.create();

    }
}
