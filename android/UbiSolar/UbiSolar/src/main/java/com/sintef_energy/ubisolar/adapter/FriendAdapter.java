package com.sintef_energy.ubisolar.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.model.User;

import java.util.List;

/**
 * Created by baier on 3/21/14.
 */
public class FriendAdapter extends ArrayAdapter<User> {

    protected Context context;
    protected int resource;
    List<User> users = null;

    public FriendAdapter(Context context, int resource, List<User> users) {
        super(context, resource);

        this.context = context;
        this.resource = resource;
        this.users = users;
    }

    @Override
    public void add(User object) {
        users.add(object);
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FriendHolder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new FriendHolder();
            holder.name = (TextView)row.findViewById(R.id.social_user_name);
            holder.profilePic = (ProfilePictureView)row.findViewById(R.id.social_profile_pic);

            row.setTag(holder);
        } else {
            holder = (FriendHolder)row.getTag();
        }

        if(!users.isEmpty()) {
            User user = users.get(position);
            holder.name.setText(user.getName());
        }
        return row;
    }

    static class FriendHolder {
        TextView name;
        ProfilePictureView profilePic;
    }

}
