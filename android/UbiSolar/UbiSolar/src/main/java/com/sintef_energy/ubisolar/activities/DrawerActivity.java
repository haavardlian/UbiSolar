package com.sintef_energy.ubisolar.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.sintef_energy.ubisolar.IView.IPresenterCallback;

import com.sintef_energy.ubisolar.adapter.ComparisonAdapter;
import com.sintef_energy.ubisolar.drawer.DrawerItem;
import com.sintef_energy.ubisolar.drawer.Item;
import com.sintef_energy.ubisolar.fragments.AddUsageFragment;
import com.sintef_energy.ubisolar.fragments.DeviceFragment;
import com.sintef_energy.ubisolar.fragments.EnergySavingTabFragment;
import com.sintef_energy.ubisolar.fragments.HomeFragment;
import com.sintef_energy.ubisolar.fragments.ProfileFragment;
import com.sintef_energy.ubisolar.fragments.social.CompareFragment;
import com.sintef_energy.ubisolar.fragments.social.ComparisonSettingsFragment;
import com.sintef_energy.ubisolar.model.NavDrawerItem;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;
import com.sintef_energy.ubisolar.presenter.DevicePresenter;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;
import com.sintef_energy.ubisolar.utils.Global;
import com.sintef_energy.ubisolar.R;
import com.sintef_energy.ubisolar.fragments.NavigationDrawerFragment;
import com.sintef_energy.ubisolar.fragments.UsageFragment;
import com.sintef_energy.ubisolar.utils.RequestManager;

import java.util.Arrays;

/**
 * The main activity.
 * Handles all the presenters, internet RequestManager, account data, authentication, and logic for the application.
 * TODO: WeakReference presenters?
 */
public class DrawerActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        IPresenterCallback{

    private static final String TAG = DrawerActivity.class.getName();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private String[] titleNames;

    /**
     * Presenters
     */
    private TotalEnergyPresenter mTotalEnergyPresenter;
    private DevicePresenter devicePresenter;

    private FacebookSessionStatusCallback mFacebookSessionStatusCallback;

    private PreferencesManager mPrefManager;

    // Constants
    // The authority for the sync adapter's content provider
    public static String AUTHORITY;
    // An account type, in the form of a domain name
    public static String ACCOUNT_TYPE;
    // The account name
    public static String ACCOUNT;
    // Instance fields
    private Account mAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        //We want to use the progress bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        //Create RequestManager instance
        RequestManager.getInstance(this);
        /* Set up the presenters */

        /*UsagePresenter*/
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MONTH, 8);
        mTotalEnergyPresenter = new TotalEnergyPresenter();
        //mTotalEnergyPresenter.loadEnergyData(getContentResolver(),
        //        0,
        //        calendar.getTimeInMillis());


        titleNames = getResources().getStringArray(R.array.nav_drawer_items);
        setContentView(R.layout.activity_usage);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        /* DevicePresenter */
        devicePresenter = new DevicePresenter();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        /* Update UX on backstack change *//*
        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        // Update your UI here.
                        getFragmentManager().
                    }
                });*/


        /* Session data */
        mFacebookSessionStatusCallback = new FacebookSessionStatusCallback();

        /* Setup preference manager */
        PreferencesManager.initializeInstance(getApplicationContext());
        mPrefManager = PreferencesManager.getInstance();

        /* Setup dummy account */
        AUTHORITY = getResources().getString(R.string.provider_authority_energy);
        ACCOUNT_TYPE = getResources().getString(R.string.auth_account_type);
        ACCOUNT = getResources().getString(R.string.app_name);

        mAccount = CreateSyncAccount(this);

        /* The same as ticking allow sync */
        //ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);

        /* Request a sync operation */
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true); //Do sync regardless of settings
        //bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true); //Force sync immediately
        ContentResolver.requestSync(mAccount, AUTHORITY, bundle);

        // Extra logging for debug
        if(Global.DEVELOPER_MADE)
            Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        startFacebookLogin(savedInstanceState);

        /* Start developer mode after app has been setup
         * Lots of StrictMode violations are done in startup anyways. */
        developerMode(Global.DEVELOPER_MADE);
    }

    /**
     * The view has been created.
     * Authentication will be handled here.
     */
    @Override
    public void onStart(){
        super.onStart();
        Session.getActiveSession().addCallback(mFacebookSessionStatusCallback);

        //Updates the UI based on logged in state
        changeNavdrawerSessionsView(Global.loggedIn);
    }


    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(mFacebookSessionStatusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        //FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = null;

        boolean logout = false;

        switch (position){
            case 0:
                fragment = HomeFragment.newInstance(position);
                break;
            case 2:
                fragment = UsageFragment.newInstance(position);
                break;
            case 3:
                fragment = CompareFragment.newInstance(position);
                break;
            case 5:
                fragment = DeviceFragment.newInstance(position);
                break;
            case 6:
                fragment = AddUsageFragment.newInstance(position);
                break;
            case 8:
                fragment = EnergySavingTabFragment.newInstance(position);
                break;
            case 10:
                fragment = ProfileFragment.newInstance(position);
                break;
            case 11:
                fragment = ComparisonSettingsFragment.newInstance(position);
                break;
            case 12:
                logout = true;
                break;
            default:
                break;
        }

        if(fragment != null) //todo: Add to backstack? Or add null?
            addFragment(fragment, false, true, titleNames[position]);
            //fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        else if(logout) {
            onClickLogin();
        }
        else
            Log.e(TAG, "Error creating fragment from navigation drawer.");
    }

    /**
     * Helper method to add fragments to the view.
     */
    public void addFragment(Fragment fragment, boolean animate, boolean addToBackStack, String tag) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if (animate) {/*
            ft.setCustomAnimations(android.R.anim.fragment_from_right,
                    R.anim.fragment_from_left, R.anim.fragment_from_right,
                    R.anim.fragment_from_left);*/
        }
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        //ft.add(R.id.container, fragment);
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    public void onSectionAttached(int number) {
        if(number < titleNames.length)
            mTitle = titleNames[number];
        else
            Log.e(TAG, "Attaching to section number that does not exist: " + number);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * Changes the nav drawer text and Global loggedIn state.
     *
     * @param state State of facebook session
     */
    private void changeNavdrawerSessionsView(boolean state){
        Global.loggedIn = state;

        DrawerItem item = (DrawerItem)mNavigationDrawerFragment.getNavDrawerItem(12);

        if (Global.loggedIn) //TODO: if Session.GetActiveSession().isOpened?
            item.setTitle("Log out");
        else
            item.setTitle("Log in");

        mNavigationDrawerFragment.getNavDrawerListAdapter().notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.global, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public TotalEnergyPresenter getmTotalEnergyPresenter() {
        return mTotalEnergyPresenter;
    }

    @Override
    public DevicePresenter getDevicePresenter() { return devicePresenter; }

    /* Login logic */

    /**
     * Handles the startup authentication.
     *
     * Authenticates to facebook. If no authentication data is present, then
     * nothing happens. The user must explicitly say that he wants to login.
     *
     * @param savedInstanceState Saved instance data
     */
    private void startFacebookLogin(Bundle savedInstanceState){


        // start Facebook Login
        // This will _only_ log in if the user is logged in from before.
        // To log in, the user must choose so himself from the menu.
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, mFacebookSessionStatusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(mFacebookSessionStatusCallback));
            }
        }
    }

    /**
     * Handles login and logout from facebook, when the user clicks the login/ lgout button.
     * What done is based on Global.isloggedIn state.
     *
     * Updates state, and UI accordingly.
     *
     */
    private void onClickLogin() {
        /* User wants to log out */
        if (Global.loggedIn){
            callFacebookLogout(getApplicationContext());
        }
        /* User wants to log in */
        /* Is there internet? */
        else if(!isNetworkOn(getApplicationContext())){
            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
        /* There is internet */
        else {
            Session session = Session.getActiveSession();
            if (session == null) {
                // start Facebook Login
                // This will _only_ log in if the user is logged in from before.
                // To log in, the user must choose so himself from the menu.
                Session.openActiveSession(this, true, mFacebookSessionStatusCallback);
            }
            else if (!session.isOpened() && !session.isClosed()) {
                session.openForRead(new Session.OpenRequest(this)
                        .setPermissions(Arrays.asList("basic_info"))
                        .setCallback(mFacebookSessionStatusCallback));
            } else {
                Session.openActiveSession(this, true, mFacebookSessionStatusCallback);
            }
        }
    }

    /**
     * Logout From Facebook
     */
    private void callFacebookLogout(Context context) {
        Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
                //clear your preferences if saved
            }
        } else {
            session = new Session(context);
            Session.setActiveSession(session);

            session.closeAndClearTokenInformation();
            //clear your preferences if saved
        }

        changeNavdrawerSessionsView(false);
        mPrefManager.clearFacebookSessionData();
    }


    private class FacebookSessionStatusCallback implements Session.StatusCallback {
        private final String TAG = FacebookSessionStatusCallback.class.getName();

        // callback when session changes state
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            //User is logged in
            if (session.isOpened()) {
                Log.v(DrawerActivity.TAG,"Facebook logged in.");

                /* Set session data */
                mPrefManager.setAccessToken(session.getAccessToken());
                mPrefManager.setKeyAccessTokenExpires(session.getExpirationDate());

                //SessionState.

                Toast.makeText(getBaseContext(), "Logged in through facebook", Toast.LENGTH_LONG).show();
                changeNavdrawerSessionsView(true);

                /* How to do the callback?
                 * isNetworkOn: Only request the user data when user logs in
                 * Handle the response or user object: Can get cached data
                 * -> Is it needed? Data should only be fetched, when the possibility for new data is there.
                 *  Don't need to store cached data.
                 *
                 */
                if(isNetworkOn(getApplicationContext()))
                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if(response.getConnection() != null || response.getIsFromCache() != false) {

                                mPrefManager.setKeyFacebookUid(user.getId());
                                Log.v(DrawerActivity.TAG, "USER ID: " + user.getId());
                            } else {
                                Log.e(TAG, "No DATA");
                            }
                        }
                    }).executeAsync();
           }
            // User is logged out
            else if (session.isClosed()) {
                Log.v(DrawerActivity.TAG, "Facebook logged out.");
                changeNavdrawerSessionsView(false);
            } else
                Log.v(DrawerActivity.TAG, "Facebook status is fishy");
        }
    }

    /**
     * Helper class to check if app has internet connection.
     * @param context
     * @return
     */
    public boolean isNetworkOn(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }


    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            Log.v(TAG, "CreateSyncAccount successful");
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */

            Log.v(TAG, "CreateSyncAccount failed: Most probably because account already exists.");
        }

        return newAccount;
    }

    /**
     * Debug with strict mode
     */
    private void developerMode(boolean devMode){
        if(devMode){
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .penaltyLog()
            .build());
            StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .penaltyLog()
            .penaltyDeath()
            .build());
        }
    }

}
