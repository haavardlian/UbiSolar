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
import com.google.gson.JsonObject;
import com.sintef_energy.ubisolar.adapter.FriendAdapter;
import com.sintef_energy.ubisolar.fragments.CompareFriendsListFragment;
import com.sintef_energy.ubisolar.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Håvard on 07.05.14.
 */
public class RequestFacebookProxy {

    Session session;

    public RequestFacebookProxy(Session session) {
        this.session = session;
    }

    public void postPicture(final Fragment fragment, String caption, byte[] image) {
        Bundle param = new Bundle();
        param.putString("message", caption);
        param.putByteArray("picture", image);
        RequestAsyncTask requestTask = new Request(session, "me/photos", param, HttpMethod.POST, new Request.Callback() {
            @Override
            public void onCompleted(final Response response) {
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.getError() == null)
                            Toast.makeText(fragment.getActivity(), "Posted to Facebook", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(fragment.getActivity(), "Error posting to Facebook", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).executeAsync();
    }

    private void getFriends(Request.Callback callback) {
        Bundle param = new Bundle();
        param.putString("fields", "name, installed");
        new Request(Session.getActiveSession(), "me/friends", param, HttpMethod.GET, callback).executeAsync();
    }

    public void populateFriendList(final FriendAdapter friendAdapter) {
        Request.Callback callback = new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                try {
                    JSONArray friends = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
                    friendAdapter.clear();
                    for(int i = 0; i < friends.length(); i++) {
                        JSONObject friend = friends.getJSONObject(i);
                        if(friend.getBoolean("installed"))
                            friendAdapter.add(new User(friend.getLong("id"), friend.getString("name")));
                    }

                    friendAdapter.notifyDataSetChanged();
                } catch(Exception e) {
                    
                }
            }
        };

        getFriends(callback);
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
