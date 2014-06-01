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

package com.sintef_energy.ubisolar.utils;

/**
 * Created by perok on 06.05.14.
 */

import android.view.View;

/**
 * This class allows a single click and prevents multiple clicks on
 * the same button in rapid succession. Setting unclickable is not enough
 * because click events may still be queued up.
 *
 * Override onOneClick() to handle single clicks. Call reset() when you want to
 * accept another click.
 */
public abstract class OnOneOffClickListener implements View.OnClickListener {
    private boolean clickable = true;
    private final Object sObject = new Object();

    /**
     * Override onOneClick() instead.
     */
    @Override
    public final void onClick(View v) {
        synchronized (sObject) {
            if (clickable) {
                clickable = false;
                onOneClick(v);
                reset(); // uncomment this line to reset automatically
            }
        }
    }

    /**
     * Override this function to handle clicks.
     * reset() must be called after each click for this function to be called
     * again.
     * @param v
     */
    public abstract void onOneClick(View v);

    /**
     * Allows another click.
     * TODO: Synchronized? Reset is auto atm. Can cause problems?
     */
    public synchronized void reset() {
        clickable = true;
    }
}
