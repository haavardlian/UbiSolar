package com.sintef_energy.ubisolar.adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.dialogs.YourDialog;
import com.sintef_energy.ubisolar.model.Tip;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Håvard on 20.03.14.
 */
public class YourAdapter extends ArrayAdapter<Tip> {
    Context context;
    FragmentManager fragmentManager;
    int layoutResourceId;
    List<Tip> data = null;
    SharedPreferences sharedPreferences;
    List<String> list;
    SharedPreferences.Editor editor;

    public YourAdapter(Context context, int layoutResourceId, ArrayList<Tip> data, FragmentManager fragmentManager) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
        this.fragmentManager = fragmentManager;
        this.sharedPreferences = context.getSharedPreferences("com.sintef_energy.ubisolar", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Activity getActivity() {
        return (Activity) context;
    }

    @Override
    public void add(Tip object) {
        if(!data.contains(object)) {
            data.add(object);
            Gson gson = new Gson();
            String json = gson.toJson(object, Tip.class);
            
            notifyDataSetChanged();
        }
    }

    @Override
    public void remove(Tip object) {
        if(data.contains(object)) {
            data.remove(object);
            sharedPreferences.edit().putInt("Tip"+object.getId(), 0).apply();
            notifyDataSetChanged();
        }
    }

    @Override
    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    @Override
    public Tip getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final TipHolder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TipHolder();
            holder.name = (TextView)row.findViewById(R.id.yourTipsRowName);
            holder.checked = (CheckBox) row.findViewById(R.id.yourTipsRowChecked);

            row.setTag(holder);
        } else {
            holder = (TipHolder)row.getTag();
        }

        if(!data.isEmpty()) {
            Tip tip = data.get(position);
            holder.name.setText(tip.getName());
        }


        row.findViewById(R.id.yourTipsRowChecked).setOnClickListener(new View.OnClickListener() {

            private boolean isChecked = false;
            @Override
            public void onClick(View view) {
                isChecked = ! isChecked;
                holder.checked.setChecked(isChecked);
            }
        });

        row.findViewById(R.id.yourTipsRowName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YourDialog dialog = new YourDialog(YourAdapter.this.getItem(position), YourAdapter.this);
                dialog.show(fragmentManager, "yourDialog");
            }
        });

        return row;
    }

    static class TipHolder {
        TextView name;
        CheckBox checked;
    }
}
