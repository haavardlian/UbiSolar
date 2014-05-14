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

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.fragments.EnergySavingTabFragment;
import com.sintef_energy.ubisolar.fragments.TipsFragment;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.model.TipRating;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;
import com.sintef_energy.ubisolar.presenter.RequestManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Håvard on 24.03.2014.
 */
public class TipDialog extends DialogFragment {

    private View view = null;
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
                        ((EnergySavingTabFragment)getTargetFragment().getTargetFragment()).getAdapter().getYourFragment().getAdapter().add(tip);
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                        SharedPreferences.Editor editor = sharedPref.edit();

                        Set<String> savedTips = sharedPref.getStringSet(PreferencesManager.SAVED_TIPS, new HashSet<String>());
                        for(String s : savedTips) {
                            if(Integer.valueOf(s) == tip.getId()) return;
                            Log.d("Saved tip id", s);
                        }
                        savedTips.add(String.valueOf(tip.getId()));
                        editor.putStringSet(PreferencesManager.SAVED_TIPS, savedTips);
                        editor.commit();
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
