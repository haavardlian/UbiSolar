package com.sintef_energy.ubisolar.presenter;

import android.content.ContentResolver;
import android.util.Log;

import com.sintef_energy.ubisolar.IView.IUserView;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.database.energy.UserModel;

import java.util.ArrayList;

/**
 * Created by Lars Erik on 04.05.2014.
 */
public class UserPresenter {


    private static final String TAG = UserPresenter.class.getName();
    ArrayList<IUserView> userModelListeners;


    public UserPresenter(){}

    //TODO: Implement storage
    public void registerListener(IUserView view){
        this.userModelListeners.add(view);
    }

    public void unregisterListener(IUserView view){
        this.userModelListeners.remove(view);
    }

    public void addUser(UserModel user, ContentResolver contentResolver){
        EnergyDataSource.insertUser(contentResolver, user);
        Log.d(TAG, "added user" + user.getName());
    }

    public void deleteUser(ContentResolver contentResolver, UserModel user) {

    }
}
