package com.sintef_energy.ubisolar.drawer;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.NavDrawerListAdapter;

/**
 * Created by Håvard on 02.04.14.
 */
public class DrawerItem implements Item {
    private String         str1;
    private String         str2;

    private DrawerHolder holder;

    public DrawerItem(String title, String count) {
        this.str1 = title;
        this.str2 = count;
    }

    public DrawerItem(String title) {
        this.str1 = title;
    }

    @Override
    public int getViewType() {
        return NavDrawerListAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        holder = new DrawerHolder();

        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.drawer_list_item, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        holder.text = (TextView) view.findViewById(R.id.title);
        holder.count = (TextView) view.findViewById(R.id.counter);
        holder.text.setText(str1);

        setCount(str2);

        return view;
    }

    public void setTitle(String str1) {
        this.str1 = str1;
    }

    public void setCount(String str){
        str2 = str;

        if(holder == null)
            return;

        if(holder.count == null)
            return;

        if(str != null && str.length() > 0) {
            holder.count.setVisibility(View.VISIBLE);
            holder.count.setText(str);
        }
        else
            holder.count.setVisibility(View.GONE);
    }

    static class DrawerHolder{
        TextView text;
        TextView count;
    }
}
