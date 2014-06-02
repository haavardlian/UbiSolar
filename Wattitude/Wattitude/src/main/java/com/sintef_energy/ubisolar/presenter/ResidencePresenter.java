/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
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
import com.sintef_energy.ubisolar.database.energy.ResidenceModel;

import java.util.ArrayList;

/**
 * Created by Lars Erik on 03.05.2014.
 */
public class ResidencePresenter {

    private static final String TAG = ResidencePresenter.class.getName();

    public ResidencePresenter(){}

    //TODO: Implement storage

    /**
     * Currently only a stub method for adding residence.
     * @param residence
     * @param contentResolver
     */
    public void addResidence(ResidenceModel residence, ContentResolver contentResolver){
        Log.d(TAG,"added residence" + residence.getHouseName());

    }

    public void editResidence(ContentResolver contentResolver, ResidenceModel residence) {
    }
}
