package com.sintef_energy.ubisolar.utils;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Håvard on 25.03.2014.
 */
public class RequestManager {

    private static RequestManager instance;
    private RequestProxy mRequestProxy;

    private RequestManager(Activity activity) {
        mRequestProxy = new RequestProxy(activity);
    }
    public RequestProxy doRequest() {
        return mRequestProxy;
    }

    // This method should be called first to do singleton initialization
    public static synchronized RequestManager getInstance(Activity activity) {
        if (instance == null) {
            instance = new RequestManager(activity);
        }
        return instance;
    }

    public static synchronized RequestManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(RequestManager.class.getSimpleName() +
                    " is not initialized, call getInstance(..) method first.");
        }
        return instance;
    }
}
