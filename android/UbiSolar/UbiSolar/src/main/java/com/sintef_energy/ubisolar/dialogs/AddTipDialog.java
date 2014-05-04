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
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.presenter.RequestManager;

/**
 * Created by Håvard on 20.03.14.
 */
public class AddTipDialog extends DialogFragment {
    private View view = null;
    private TextView nameField, descriptionField;
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

        view = inflater.inflate(R.layout.dialog_add_tip, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.addTipDialog_buttonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Tip tip = new Tip(0, nameField.getText().toString(), descriptionField.getText().toString(), 0, 0);

                        RequestManager.getInstance().doTipRequest().createTip(tip);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddTipDialog.this.getDialog().cancel();
                    }
                })
                .setTitle(R.string.addTipDialog_title);



        nameField = (EditText) view.findViewById(R.id.createTipDialogName);
        descriptionField = (EditText) view.findViewById(R.id.createTipDialogDescription);


        AlertDialog alertDialog = builder.create();

        return alertDialog;

    }
}
