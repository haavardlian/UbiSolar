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
import android.widget.RatingBar;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.adapter.YourAdapter;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.model.TipRating;
import com.sintef_energy.ubisolar.utils.RequestManager;

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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        view = inflater.inflate(R.layout.dialog_tip, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                })
                .setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        yourAdapter.remove(tip);
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
                RequestManager.getInstance().doRequest().createRating(rating);
            }
        });
        return builder.create();

    }
}
