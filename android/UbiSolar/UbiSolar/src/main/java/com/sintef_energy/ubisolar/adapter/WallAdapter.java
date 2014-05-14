package com.sintef_energy.ubisolar.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.model.Tip;
import com.sintef_energy.ubisolar.model.WallPost;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;

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
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public WallAdapter(Context context, int layoutResourceId, ArrayList<WallPost> data) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
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
        TipHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TipHolder();
            holder.message = (TextView)row.findViewById(R.id.wall_item_text);
            holder.timestamp = (TextView) row.findViewById(R.id.wall_item_date);

            row.setTag(holder);
        } else {
            holder = (TipHolder)row.getTag();
        }

        if(!data.isEmpty()) {
            WallPost post = data.get(position);
            //TODO: Swap Friend with actual friend name
            String message = "Friend" + " " +
                    row.getResources().getStringArray(R.array.wall_post_messages)[post.getMessage()];
            holder.message.setText(message);
            holder.timestamp.setText(df.format(new Date(post.getTimestamp() * 1000)));
        }
        return row;
    }

    static class TipHolder {
        TextView message;
        TextView timestamp;
    }
}
