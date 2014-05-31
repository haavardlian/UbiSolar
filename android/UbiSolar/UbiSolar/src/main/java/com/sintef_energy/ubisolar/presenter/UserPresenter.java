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

package com.sintef_energy.ubisolar.presenter;

import android.content.ContentResolver;
import android.util.Log;

import com.sintef_energy.ubisolar.Interfaces.IUserView;
import com.sintef_energy.ubisolar.database.energy.EnergyDataSource;
import com.sintef_energy.ubisolar.database.energy.UserModel;

import java.util.ArrayList;

/**
 * Created by Lars Erik on 04.05.2014.
 */
public class UserPresenter {


    private static final String TAG = UserPresenter.class.getName();
    private ArrayList<IUserView> userModelListeners;


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
