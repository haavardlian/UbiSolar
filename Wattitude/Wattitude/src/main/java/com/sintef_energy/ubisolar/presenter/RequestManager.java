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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.ResponseDelivery;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

import java.io.File;
import java.util.concurrent.Executors;

/**
 * Created by Håvard on 25.03.2014.
 */
public class RequestManager {

    private static RequestManager mInstance;
    private RequestTipProxy mRequestTipProxy;
    private RequestFacebookProxy mRequestFacebookProxy;
    private RequestSyncProxy mRequestSyncProxy;

    private RequestQueue mRequestQueue;
    private RequestFriendsProxy mRequestFriendsProxy;

    private RequestManager(Context context) {
        mRequestQueue = newRequestQueue(context);
        mRequestSyncProxy = new RequestSyncProxy(mRequestQueue);
        mRequestTipProxy = new RequestTipProxy(mRequestQueue);
        mRequestFacebookProxy = new RequestFacebookProxy();
        mRequestFriendsProxy = new RequestFriendsProxy(mRequestQueue);
    }

    public RequestTipProxy doTipRequest() {
        return mRequestTipProxy;
    }

    public RequestSyncProxy doSyncRequest(){
        return mRequestSyncProxy;
    }

    public RequestFacebookProxy doFacebookRequest() {
        return mRequestFacebookProxy;
    }

    public RequestFriendsProxy doFriendRequest() { return  mRequestFriendsProxy; }

    // This method should be called first to do singleton initialization
    public static synchronized RequestManager getInstance(Context context) {
        if(mInstance == null)
            mInstance = new RequestManager(context);
        return mInstance;
    }

    public static synchronized RequestManager getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException(RequestManager.class.getSimpleName() +
                    " is not initialized, call getInstance(..) method first.");
        }
        return mInstance;
    }

    private static RequestQueue newRequestQueue(Context context) {
        File cacheDir = new File(context.getCacheDir(), "def_cahce_dir");
        HttpStack stack;
        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 9) {
            stack = new HurlStack();
        } else {
            // Prior to Gingerbread, HttpUrlConnection was unreliable.
            // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
            stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
        }

        Network network = new BasicNetwork(stack);
        int threadPoolSize = 10; // number of network dispatcher threads to create
        // pass Executor to constructor of ResponseDelivery object
        ResponseDelivery delivery = new ExecutorDelivery(Executors.newFixedThreadPool(threadPoolSize));
        // pass ResponseDelivery object as a 4th parameter for RequestQueue constructor
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network, threadPoolSize, delivery);
        queue.start();

        return queue;
    }

}
