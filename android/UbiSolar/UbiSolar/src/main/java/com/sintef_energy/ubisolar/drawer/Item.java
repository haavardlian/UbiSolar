package com.sintef_energy.ubisolar.drawer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Håvard on 02.04.14.
 */
public interface Item {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView, ViewGroup parent);
}
