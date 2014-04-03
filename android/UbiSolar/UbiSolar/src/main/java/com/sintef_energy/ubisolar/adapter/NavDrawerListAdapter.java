package com.sintef_energy.ubisolar.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.drawer.Item;
import com.sintef_energy.ubisolar.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by perok on 20.03.14.
 */
public class NavDrawerListAdapter extends ArrayAdapter<Item> {
    private LayoutInflater mInflater;
    private List<Item> items;
    public enum RowType {
        LIST_ITEM, HEADER_ITEM
    }

    public NavDrawerListAdapter(Context context, List<Item> items) {
        super(context, 0, items);
        this.items = items;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(mInflater, convertView);
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public boolean isEnabled(int position) {
        if(items.get(position).getViewType() == RowType.HEADER_ITEM.ordinal())
            return false;
        else
            return true;
    }
}
