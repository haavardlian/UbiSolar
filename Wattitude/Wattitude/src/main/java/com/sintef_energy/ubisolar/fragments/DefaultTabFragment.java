/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.sintef_energy.ubisolar.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.sintef_energy.ubisolar.activities.DrawerActivity;

/**
 * Created by perok on 20.03.14.
 *
 * Parent object for fragments
 */
public abstract class DefaultTabFragment extends Fragment {

    protected static final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume(){
        super.onResume();

        ((DrawerActivity) getActivity()).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        ((DrawerActivity) getActivity()).restoreActionBar();
    }

    /**
     * A grand and ugly hack. UsageFragment uses a ViewPager, and it's inner Fragment uses the ActionBar
     * menu. This causes an issue when changing tabs that the ViewPager does not remove the .
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
