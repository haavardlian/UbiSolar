package com.sintef_energy.ubisolar.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.model.WallPost;
import com.sintef_energy.ubisolar.presenter.RequestManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Håvard on 20.03.14.
 */
public class WallAdapter extends ArrayAdapter<WallPost> {
    private Context context;
    private int layoutResourceId;
    private List<WallPost> data = null;
    private String[] messages;
    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public WallAdapter(Context context, int layoutResourceId, ArrayList<WallPost> data) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
        messages = context.getResources().getStringArray(R.array.wall_post_messages);
    }

    public Activity getActivity() {
        return (Activity) context;
    }

    @Override
    public void add(WallPost object) {
        data.add(object);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public WallPost getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        WallItemHolder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new WallItemHolder();
            holder.name = (TextView)row.findViewById(R.id.wall_item_name);
            holder.message = (TextView)row.findViewById(R.id.wall_item_text);
            holder.date = (TextView) row.findViewById(R.id.wall_item_date);
            holder.time = (TextView) row.findViewById(R.id.wall_item_time);

            row.setTag(holder);
        } else {
            holder = (WallItemHolder)row.getTag();
        }

        if(!data.isEmpty()) {
            WallPost post = data.get(position);
            Date date = new Date(post.getTimestamp() * 1000);
            //TODO: Swap Friend with actual friend name
            holder.message.setText(messages[post.getMessage()-1]);
            holder.date.setText(dateFormat.format(date));
            holder.time.setText(timeFormat.format(date));

            RequestManager.getInstance().doFacebookRequest().getFacebookName(post.getUserId(), holder.name);

        }
        return row;
    }

    static class WallItemHolder {
        TextView name;
        TextView message;
        TextView date;
        TextView time;
    }
}
