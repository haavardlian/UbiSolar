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
