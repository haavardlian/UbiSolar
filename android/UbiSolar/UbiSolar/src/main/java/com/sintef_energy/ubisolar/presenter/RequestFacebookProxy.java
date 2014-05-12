package com.sintef_energy.ubisolar.presenter;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;

/**
 * Created by Håvard on 07.05.14.
 */
public class RequestFacebookProxy {

    public void postPicture(final Fragment fragment, String caption, byte[] image) {
        Bundle param = new Bundle();
        param.putString("message", caption);
        param.putByteArray("picture", image);
        RequestAsyncTask requestTask = new Request(Session.getActiveSession(), "me/photos", param, HttpMethod.POST, new Request.Callback() {
            @Override
            public void onCompleted(final Response response) {

                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (response.getError() == null)
                            Toast.makeText(fragment.getActivity(), "Posted to Facebook", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(fragment.getActivity(), "Error posting to Facebook", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).executeAsync();
    }


    public void postMessage(final Fragment fragment, String message, String name) {
        Bundle param = new Bundle();
        param.putString("message", name + " - " + message);
        RequestAsyncTask requestTask = new Request(Session.getActiveSession(), "me/feed", param, HttpMethod.POST, new Request.Callback() {
            @Override
            public void onCompleted(final Response response) {
                if(response.getError() == null)
                    Log.d("FACEBOOK", "Message posted");
                else
                    Log.d("FACEBOOK", "Error");

            }
        }).executeAsync();
    }


    public void getFriends(Request.Callback callback) {
        Bundle param = new Bundle();
        param.putString("fields", "name, installed");
        new Request(Session.getActiveSession(), "me/friends", param, HttpMethod.GET, callback).executeAsync();
    }

    public void populateFeed(final Adapter adapter) {
        Request.Callback callback = new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                //Call server for friend feed
            }
        };

        getFriends(callback);
    }

}
