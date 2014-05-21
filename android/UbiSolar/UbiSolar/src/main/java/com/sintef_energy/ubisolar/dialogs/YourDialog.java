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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.Session;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.YourAdapter;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.model.TipRating;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;
import com.sintef_energy.ubisolar.presenter.RequestManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Håvard on 24.03.2014.
 */
public class YourDialog extends DialogFragment {
    private View view = null;
    private Tip tip;
    private YourAdapter yourAdapter;
    private TextView descriptionField;
    private RatingBar ratingField;

    public YourDialog(Tip tip, YourAdapter yourAdapter) {
        this.tip = tip;
        this.yourAdapter = yourAdapter;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        view = inflater.inflate(R.layout.dialog_tip, null);
        builder.setView(view)
                // Add action buttons
                .setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        yourAdapter.remove(tip);
                        yourAdapter.notifyDataSetChanged();
                        PreferencesManager.getInstance().removeSubscribedTip(tip);
                    }
                })
                .setTitle(tip.getName());

        if(Session.getActiveSession().isOpened()) {
            builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                RequestManager.getInstance().doFacebookRequest().postMessage(getTargetFragment(),
                        tip.getDescription(), tip.getName());
                }
            });
        }

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
            }
        });
        return builder.create();

    }
}
