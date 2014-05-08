package com.sintef_energy.ubisolar.drawer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.NavDrawerListAdapter;

/**
 * Created by Håvard on 02.04.14.
 */
public class DrawerHeader implements Item {
    private final String         name;

    public DrawerHeader(String name) {
        this.name = name;
    }

    @Override
    public int getViewType() {
        return NavDrawerListAdapter.RowType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.drawer_list_header, parent, false);
            // Do some initialization
        } else {
            view = convertView;
        }
        view.setClickable(false);
        view.setFocusable(false);

        TextView text = (TextView) view.findViewById(R.id.title);
        text.setText(name);

        return view;
    }
}
