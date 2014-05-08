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
                .setPositiveButton("Post to Facebook", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Bundle param = new Bundle();
                        Request r;

                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        param.putString("message", caption.getText().toString());
                        param.putByteArray("picture", baos.toByteArray());
                        r = new Request(Session.getActiveSession(), "me/photos", param, HttpMethod.POST, new Request.Callback() {
                            @Override
                            public void onCompleted(Response response) {
                                Log.d("FACEBOOK", "Returned");
                            }
                        });
                        r.executeAsync();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        getDialog().cancel();
                    }
                })
                .setTitle("Share your progress");

        image = (ImageView) view.findViewById(R.id.shareImage);
        image.setImageBitmap(bitmap);

        caption = (EditText) view.findViewById(R.id.shareCaption);

        return builder.create();

    }

}
