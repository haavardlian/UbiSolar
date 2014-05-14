package com.sintef_energy.ubisolar.fragments;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ExpandableListView;

import com.sintef_energy.ubisolar.IView.IPresenterCallback;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.adapter.DeviceListAdapter;
import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.dialogs.EditDeviceDialog;
import com.sintef_energy.ubisolar.model.Device;
import com.sintef_energy.ubisolar.presenter.DevicePresenter;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;
import com.sintef_energy.ubisolar.utils.Utils;

import java.util.ArrayList;

/**
 * Created by perok on 2/11/14.
 */
public class DeviceFragment extends DefaultTabFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String TAG = DeviceFragment.class.getName();
    private View mRootview;
    private DevicePresenter devicePresenter;
    private ExpandableListView expListView;
    private DeviceListAdapter expListAdapter;
    private ArrayList<DeviceModel> devices;

    private DeviceModel mDevice;

    public static DeviceFragment newInstance(int sectionNumber) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public DeviceFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            devicePresenter = ((IPresenterCallback) getActivity()).getDevicePresenter();
             /*Line so we can delete test data easily*/
            //EnergyDataSource.deleteAll(getActivity().getContentResolver());
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement " +
                    TotalEnergyPresenter.class.getName());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_device, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_add_device:
                EditDeviceDialog addDeviceDialog = new EditDeviceDialog(getString(R.string.device_add_title));
                addDeviceDialog.show(getFragmentManager(), "addDevice");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        mRootview =  inflater.inflate(R.layout.fragment_device_expandablelist, container, false);

        expListView = (ExpandableListView) mRootview.findViewById(R.id.devicesListView);

        devices = new ArrayList<>();
        expListAdapter = new DeviceListAdapter(getActivity(), devices);
        setGroupIndicatorToRight();
        expListView.setAdapter(expListAdapter);

        registerForContextMenu(expListView);

        return mRootview;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                EnergyContract.Devices.CONTENT_URI,
                EnergyContract.Devices.PROJECTION_ALL,
                null,
                null,
                DeviceModel.DeviceEntry._ID + " ASC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        devices.clear();

        cursor.moveToFirst();
        if (cursor.getCount() != 0)
            do {
                DeviceModel model = new DeviceModel(cursor);
                devices.add(model);
            } while (cursor.moveToNext());

        expListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        devices.clear();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int child = ExpandableListView.getPackedPositionChild(info.packedPosition);
        Device device = expListAdapter.getChild(group, child);

        MenuInflater m = getActivity().getMenuInflater();
        menu.setHeaderTitle(device.getName());
        menu.setHeaderIcon(R.drawable.devices);
        m.inflate(R.menu.device_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info =
                (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int child = ExpandableListView.getPackedPositionChild(info.packedPosition);
        mDevice = expListAdapter.getChild(group, child);

        switch(item.getItemId()){
            case R.id.device_edit:
                EditDeviceDialog editDeviceDialog =
                        new EditDeviceDialog(mDevice, getString(R.string.device_edit_title));
                editDeviceDialog.show(getFragmentManager(), TAG);
                break;
            //TODO use Strings
            case R.id.device_delete:
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.device_dialog_delete))
                        .setMessage(Html.fromHtml(getString(R.string.device_delete_conf1) + " <b>" +
                                mDevice.getName() + "</b> " + getString(R.string.device_delete_conf2)))
                        .setPositiveButton(getString(R.string.device_ok), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri.Builder builer = EnergyContract.Devices.CONTENT_URI.buildUpon();
                                builer.appendPath("" + mDevice.getId());
                                getActivity().getContentResolver().delete(builer.build(), null, null);
                                devices.remove(mDevice);
                                Utils.makeShortToast(getActivity(),
                                        mDevice.getName() + " " + getString(R.string.device_toast_deleted));
                            }

                        })
                        .setNegativeButton(getString(R.string.device_cancel), null)
                        .show();

                this.expListAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }
}