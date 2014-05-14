package com.sintef_energy.ubisolar.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.presenter.RequestManager;

import java.io.ByteArrayOutputStream;

/**
 * Created by Håvard on 30.04.14.
 */
public class ShareDialog extends DialogFragment {

    private ImageView image;
    private EditText caption;
    private Bitmap bitmap;
    private View view;

    public ShareDialog(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        view = inflater.inflate(R.layout.dialog_share, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(getString(R.string.share_post), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ByteArrayOutputStream image = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, image);
                        RequestManager.getInstance().doFacebookRequest().postPicture(getTargetFragment(), caption.getText().toString(), image.toByteArray());

                    }
                })
                .setNegativeButton(getString(R.string.share_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        getDialog().cancel();
                    }
                })
                .setTitle(getString(R.string.share_title));

        image = (ImageView) view.findViewById(R.id.shareImage);
        image.setImageBitmap(bitmap);

        caption = (EditText) view.findViewById(R.id.shareCaption);

        return builder.create();

    }

}
