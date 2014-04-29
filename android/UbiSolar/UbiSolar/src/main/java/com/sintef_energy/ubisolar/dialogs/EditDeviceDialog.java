package com.sintef_energy.ubisolar.dialogs;

import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import com.sintef_energy.ubisolar.presenter.DevicePresenter;

/**
 * Created by pialindkjolen on 29.04.14.
 */
public class EditDeviceDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private DevicePresenter devicePresenter;


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
