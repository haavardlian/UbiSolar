package com.sintef_energy.ubisolar.fragments.social;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;

import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sintef_energy.ubisolar.IView.IDeviceView;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.activities.DrawerActivity;
import com.sintef_energy.ubisolar.adapter.FriendAdapter;
//import com.sintef_energy.ubisolar.adapter.SocialMenuAdapter;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.dialogs.AddDeviceDialog;
import com.sintef_energy.ubisolar.dialogs.AddUsageDialog;
import com.sintef_energy.ubisolar.fragments.DefaultTabFragment;
//import com.sintef_energy.ubisolar.model.SocialMenuItem;
import com.sintef_energy.ubisolar.model.User;

import java.util.ArrayList;

/**
 * Created by perok on 2/11/14.
 */
public class SocialFragment extends DefaultTabFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String TAG = SocialFragment.class.getName();

    private View view;
    private ArrayList<User> friends;
    //TODO: swap User to String or SocialMenuItem

//    private ArrayList<SocialMenuItem> homeTabs;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SocialFragment newInstance(int sectionNumber) {
        SocialFragment fragment = new SocialFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SocialFragment() {
    }

    /**
     * The first call to a created fragment
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Callback to activity
        ((DrawerActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_social, container, false);
        friends = new ArrayList<User>();
        FriendAdapter friendAdapter = new FriendAdapter(getActivity(),R.layout.fragment_social_row, friends);
        final ListView friendsList = (ListView) view.findViewById(R.id.social_menu_list);
        friendsList.setAdapter(friendAdapter);

        friends.add(new User(this.getString(R.string.friends_header_title), getActivity().getResources().getDrawable(R.drawable.profile)));
        friends.add(new User(this.getString(R.string.similar_header_title), getActivity().getResources().getDrawable(R.drawable.heat)));
        friends.add(new User(this.getString(R.string.area_header_title), getActivity().getResources().getDrawable(R.drawable.profile)));

        friendAdapter.notifyDataSetChanged();

        friendsList.setClickable(true);
        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if(position == 0) {
//                    friendsList.setVisibility(View.GONE);
                }
                if(position == 1) {

                }
                if(position == 2) {

                }
            }
        });

        return view;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            // Restore last state for checked position.
        }
    }

    /*End lifecycle*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}