package com.sintef_energy.ubisolar.presenter;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.sintef_energy.ubisolar.adapter.WallAdapter;
import com.sintef_energy.ubisolar.model.WallPost;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;

/**
 * Created by Håvard on 07.05.14.
 */
public class RequestFacebookProxy {

    public void postPicture(final Fragment fragment, String caption, byte[] image) {
        Bundle param = new Bundle();
        param.putString("message", caption);
        param.putByteArray("picture", image);
        RequestAsyncTask requestTask = new Request(Session.getActiveSession(), "me/photos",
                param, HttpMethod.POST, new Request.Callback() {
            @Override
            public void onCompleted(final Response response) {

                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (response.getError() == null) {
                            Toast.makeText(fragment.getActivity(),
                                    "Posted to Facebook", Toast.LENGTH_SHORT).show();

                            RequestManager.getInstance().doFriendRequest().createWallPost(
                                    new WallPost(0,
                                            Long.valueOf(PreferencesManager
                                                    .getInstance().getKeyFacebookUid()), 2,
                                            System.currentTimeMillis()/1000), fragment);
                        } else
                            Toast.makeText(fragment.getActivity(),
                                    "Error posting to Facebook", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).executeAsync();
    }


    public void postMessage(final Fragment fragment, String message, String name) {
        Bundle param = new Bundle();
        param.putString("message", name + " - " + message);
        RequestAsyncTask requestTask = new Request(Session.getActiveSession(), "me/feed", param,
                HttpMethod.POST, new Request.Callback() {
            @Override
            public void onCompleted(final Response response) {
                if(response.getError() == null) {
                    Log.d("FACEBOOK", "Message posted");
                    RequestManager.getInstance().doFriendRequest().createWallPost(
                            new WallPost(0,
                                    Long.valueOf(PreferencesManager
                                            .getInstance().getKeyFacebookUid()), 3,
                                    System.currentTimeMillis()/1000), fragment);
                } else
                    Log.d("FACEBOOK", "Error");

            }
        }).executeAsync();
    }


    public void getFriends(Request.Callback callback) {
        Bundle param = new Bundle();
        param.putString("fields", "name, installed");
        new Request(Session.getActiveSession(), "me/friends", param, HttpMethod.GET, callback).executeAsync();
    }

    public void populateFeed(final WallAdapter adapter, final Fragment fragment) {
        Request.Callback callback = new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                if(response.getError() != null)
                    return;
                try {
                    JSONArray friends = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
                    ArrayList<String> friendIds = new ArrayList<>();
                    for(int i = 0; i < friends.length(); i++) {
                        JSONObject friend = friends.getJSONObject(i);
                        if(friend.has("installed") && friend.getBoolean("installed"))
                            friendIds.add(friend.getString("id"));
                    }
                    RequestManager.getInstance().doFriendRequest().getWallUpdates(adapter, Long.valueOf(PreferencesManager.getInstance().getKeyFacebookUid()), fragment, StringUtils.join(friendIds, ','));

                } catch(Exception e) {

                }
            }
        };

        getFriends(callback);
    }

}
