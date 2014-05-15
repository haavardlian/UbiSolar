package com.sintef_energy.ubisolar.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.sintef_energy.ubisolar.IView.IPresenterCallback;
import com.sintef_energy.ubisolar.R;

import com.sintef_energy.ubisolar.database.energy.DeviceModel;
import com.sintef_energy.ubisolar.database.energy.EnergyContract;
import com.sintef_energy.ubisolar.drawer.DrawerItem;
import com.sintef_energy.ubisolar.fragments.AddUsageFragment;
import com.sintef_energy.ubisolar.fragments.DeviceFragment;
import com.sintef_energy.ubisolar.fragments.EnergySavingTabFragment;
import com.sintef_energy.ubisolar.fragments.HomeFragment;
import com.sintef_energy.ubisolar.fragments.NavigationDrawerFragment;
import com.sintef_energy.ubisolar.fragments.ProfileFragment;
import com.sintef_energy.ubisolar.fragments.UsageFragment;

import com.sintef_energy.ubisolar.fragments.CompareFragment;

import com.sintef_energy.ubisolar.model.WallPost;
import com.sintef_energy.ubisolar.preferences.PreferencesManager;
import com.sintef_energy.ubisolar.preferences.PreferencesManagerSync;
import com.sintef_energy.ubisolar.presenter.DevicePresenter;
import com.sintef_energy.ubisolar.presenter.RequestManager;
import com.sintef_energy.ubisolar.presenter.ResidencePresenter;
import com.sintef_energy.ubisolar.presenter.TotalEnergyPresenter;
import com.sintef_energy.ubisolar.presenter.UserPresenter;
import com.sintef_energy.ubisolar.utils.Global;

import com.sintef_energy.ubisolar.utils.Utils;

import java.util.Date;

/**
 * The main activity.
 * Handles all the presenters, internet RequestManager, account data, authentication, and logic for the application.
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

    /*
     * Presenters
     */
    private TotalEnergyPresenter mTotalEnergyPresenter;
    private DevicePresenter devicePresenter;
    private ResidencePresenter residencePresenter;
    private UserPresenter userPresenter;

    private FacebookSessionStatusCallback mFacebookSessionStatusCallback;

    private PreferencesManager mPrefManager;

    // constants
    /** The authority for the sync adapter's content provider */
    private static String AUTHORITY_PROVIDER;

    /** An account type for sync, in the form of a domain name*/
    private static String ACCOUNT_TYPE;

    // The account name
    private static String ACCOUNT;

    // Instance fields
    private Account mAccount;

    /** Number is random */
    private static final int LOGIN_CALL_ID = 231;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //We want to use the progress bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        Global.BROADCAST_NAV_DRAWER = getResources().getString(R.string.broadcast_nav_drawer_usage);

        //Create RequestManager instance
        try {
            RequestManager.getInstance();
        } catch(IllegalStateException e) {
            RequestManager.getInstance(getApplicationContext());
        }

        /* Setup preference manager */
        try {
            mPrefManager = PreferencesManager.getInstance();
        } catch (IllegalStateException ex) {
            mPrefManager = PreferencesManager.initializeInstance(getApplicationContext());
        }

        /* Set up the presenters */
        mTotalEnergyPresenter = new TotalEnergyPresenter();
        devicePresenter = new DevicePresenter();
        residencePresenter = new ResidencePresenter();
        userPresenter = new UserPresenter();

        titleNames = getResources().getStringArray(R.array.nav_drawer_items);
        setContentView(R.layout.activity_usage);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        /* Update UX on backstack change */
        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        // Update your UI here.
                        int count = getFragmentManager().getBackStackEntryCount();

                        if(count > 0)
                            mNavigationDrawerFragment.setNavDrawerToggle(false);
                        else
                            mNavigationDrawerFragment.setNavDrawerToggle(true);
                    }
               });

        /* Session data */
        mFacebookSessionStatusCallback = new FacebookSessionStatusCallback();

        /* Setup dummy account */
        AUTHORITY_PROVIDER = getResources().getString(R.string.provider_authority_energy);
        ACCOUNT_TYPE = getResources().getString(R.string.auth_account_type);
        ACCOUNT = getResources().getString(R.string.app_name);

        //mAccount = CreateSyncAccount(this);
        startFacebookLogin(savedInstanceState);

        /*
        * DEVELOPER SETTINGS
        */

        // Extra logging for debug
        if(Global.DEVELOPER_MADE)
            Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        //Creates the Total usage device if it does not already exist.
        Utils.createTotal(getContentResolver(), this);

        /* Start developer mode after app has been setup
         * Lots of StrictMode violations are done in startup anyways. */
        Utils.developerMode(getContentResolver(), Global.DEVELOPER_MADE, false);
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
    public void onDestroy(){
        super.onDestroy();
        cleanUpReferences();
    }

    /**
     * Everything is nulled out so the GC can collect the fragment instance.
     */
    private void cleanUpReferences(){
        mNavigationDrawerFragment = null;
        mTitle = null;
        titleNames = null;
        mTotalEnergyPresenter = null;
        devicePresenter = null;
        residencePresenter = null;
        mFacebookSessionStatusCallback = null;
        mPrefManager = null;
        AUTHORITY_PROVIDER = null;
        ACCOUNT_TYPE = null;
        ACCOUNT = null;
        mAccount = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

        if(requestCode == LOGIN_CALL_ID){
            if(resultCode == Activity.RESULT_OK) {
                Log.v(TAG, "Login was successful. Starting to attain session data.");

                startFacebookLogin(null);

                // Find the account
                Account[] accounts = getAccounts(getApplicationContext(), ACCOUNT_TYPE);
                for(Account account : accounts){
                    if(account.name.equals(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME))) {
                        mAccount = account;
                        break;
                    }
                }

                if(mAccount == null){
                    Log.e(TAG, "Account creation somehow failed to make an account.");
                    return;
                }

                /* The same as ticking allow sync */
                ContentResolver.setSyncAutomatically(mAccount, AUTHORITY_PROVIDER, true);

                /* Request a sync operation */
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true); //Do sync regardless of settings
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true); //Force sync immediately
                ContentResolver.requestSync(mAccount, AUTHORITY_PROVIDER, bundle);

                /* Update all -1 users to the current user id. */
                AccountManager accountManager =
                    (AccountManager) getApplicationContext().getSystemService(ACCOUNT_SERVICE);

                String facebookUID = accountManager.getUserData(mAccount, Global.DATA_FB_UID);

                ContentValues values = new ContentValues();
                values.put(DeviceModel.DeviceEntry.COLUMN_USER_ID, facebookUID);
                values.put(DeviceModel.DeviceEntry.COLUMN_LAST_UPDATED, System.currentTimeMillis()/1000L);

                getContentResolver().update(EnergyContract.Devices.CONTENT_URI,
                        values,
                        EnergyContract.Devices.COLUMN_USER_ID + "=?",
                        new String[]{"-1"});

                //Publish a post saying you started using Wattitude
                RequestManager.getInstance().doFriendRequest().createWallPost(
                        new WallPost(0, Long.valueOf(facebookUID), 1, System.currentTimeMillis() / 1000),
                        null);
            }
            else {
                Log.v(TAG, "Login failed");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
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
                logout = true;
                break;
            default:
                break;
        }

        if(fragment != null)
            addFragment(fragment, false, false, titleNames[position]);
            //fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        else if(logout) {
            onClickLogin();
        }
        else
            Log.e(TAG, "Error creating fragment from navigation drawer.");
    }

    /**
     * Helper method to add fragments to the view.
     *
     * @param fragment
     * @param animate
     * @param addToBackStack
     * @param tag
     */
    public void addFragment(Fragment fragment, boolean animate, boolean addToBackStack, String tag) {
        FragmentManager manager = getFragmentManager();

        /*
        for(int i = 0; i < manager.getBackStackEntryCount(); ++i) {
            manager.popBackStack();
        }*/

        FragmentTransaction ft = manager.beginTransaction();
        if (animate) {
            ft.setCustomAnimations(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right,
                    // Pop enter
                    android.R.anim.fade_in,
                    android.R.anim.fade_out);
        }


        if (addToBackStack) {
            ft.addToBackStack(tag);
        }

        ft.replace(R.id.container, fragment, tag);
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

        DrawerItem item = (DrawerItem)mNavigationDrawerFragment.getNavDrawerItem(11);

        if (Global.loggedIn) //TODO: if Session.GetActiveSession().isOpened?
            item.setTitle(getString(R.string.drawer_log_out));
        else
            item.setTitle(getString(R.string.drawer_log_in));

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
        switch(item.getItemId()){
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                if(getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public TotalEnergyPresenter getmTotalEnergyPresenter() {
        return mTotalEnergyPresenter;
    }

    @Override
    public DevicePresenter getDevicePresenter() { return devicePresenter; }

    @Override
    public ResidencePresenter getResidencePresenter() {return residencePresenter;}

    @Override
    public UserPresenter getUserPresenter() {return userPresenter;}
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
        mAccount = getAccount(getApplicationContext(), ACCOUNT_TYPE);

        // start Facebook Login
        // This will _only_ log in if the user is logged in from before.
        // To log in, the user must choose so himself from the menu.
        Session session = Session.getActiveSession();

        // We don't have a session
        if (session == null) {
            // We have session data
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, mFacebookSessionStatusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }

            // Try to open the session
            Session.setActiveSession(session);

            // Do we have cached tokens
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(mFacebookSessionStatusCallback));
            }
            // Use token from AccountManager
            else{
                if(mAccount == null){
                    Log.v(TAG, "startAppLogin account does not exist. Aborting");
                    return;
                }

                AccountManager accountManager =
                (AccountManager) getApplicationContext().getSystemService(ACCOUNT_SERVICE);
                String token = accountManager.getUserData(mAccount, Global.DATA_AUTH_TOKEN);
                String exprDate = accountManager.getUserData(mAccount, Global.DATA_EXPIRATION_DATE);

                if(token == null){
                    Log.v(TAG, "No account to login to.");
                    return;
                }

                migrateFbTokenToSession(token, new Date(Long.valueOf(exprDate)));
            }
        }
    }

    /**
     * This method should handle the migration process from a token with expire date
     * from the AccountManager to a Facebook session
     *
     * TODO: Don't know if this is the correct way to do it, or even necessary at all
     * @param token
     * @param exprDate
     */
    public void migrateFbTokenToSession(String token, Date exprDate) {
        AccessToken accessToken = AccessToken.createFromExistingAccessToken(
                token,
                exprDate,
                null, // How can we know this?
                AccessTokenSource.FACEBOOK_APPLICATION_NATIVE, // How can we know this?
                Global.FACEBOOK_PERMISSIONS); //Arrays.asList(Constants.FB_APP_PERMISSIONS));

        // Apply the new session
        Session.openActiveSessionWithAccessToken(getApplicationContext(), accessToken , new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if(session != null && session.isOpened()) {
                    Session.setActiveSession(session);
                }
            }
        });

    }

    /**
     * Handles login and logout from facebook, when the user clicks the login/ logout button.
     * What done is based on Global.isloggedIn state.
     *
     * Updates state, and UI accordingly.
     *
     */
    private void onClickLogin() {
        /* User wants to log out */
        if (Global.loggedIn){
            performLogout(getApplicationContext());
        }
        /* User wants to log in */
        /* Is there internet? */
        else if(!Utils.isNetworkOn(getApplicationContext())){
            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
        /* There is internet */
        else {

            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.setAction(Global.ACTION_LOGIN_FB);

            // Rest of the login logic is performed in onActivityResult
            startActivityForResult(i, LOGIN_CALL_ID);
                //session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, Arrays.asList("user_friends")));
        }
    }

    /**
     * Logout From the app
     */
    private void performLogout(Context context) {
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

        /* UPDATE VIEW */
        changeNavdrawerSessionsView(false);
        mPrefManager.clearFacebookSessionData();
        Utils.makeLongToast(getApplicationContext(), getResources().getString(R.string.fb_logout));

        /* REMOVE ACCOUNT */
        AccountManager accountManager =
                (AccountManager) context.getSystemService(ACCOUNT_SERVICE);

        Account[] accounts = accountManager.getAccounts();
        for(Account account : accounts){
            if(account.type.intern().equals(ACCOUNT_TYPE)){
                Log.v(TAG, "Removing account: " + account.name + " for authority: " + AUTHORITY_PROVIDER);
                accountManager.removeAccount(account, null, new Handler());
            }
        }

        /* REMOVE PREFERENCES*/
        PreferencesManagerSync preferencesManagerSync;
        try {
            preferencesManagerSync = PreferencesManagerSync.getInstance();
        }catch(IllegalStateException e){
            preferencesManagerSync = PreferencesManagerSync.initializeInstance(getApplicationContext());
        }

        preferencesManagerSync.clearAll();

        /* REMOVE DATA*/
        getContentResolver().delete(EnergyContract.Devices.CONTENT_URI, null, null);
        getContentResolver().delete(EnergyContract.Energy.CONTENT_URI, null, null);
   }

  private static Account[] getAccounts(Context context, String ACC_TYPE){
        AccountManager accountManager =
            (AccountManager) context.getSystemService(ACCOUNT_SERVICE);

        return accountManager.getAccountsByType(ACC_TYPE);
    }

    private static Account getAccount(Context context, String ACC_TYPE){
        AccountManager accountManager =
            (AccountManager) context.getSystemService(ACCOUNT_SERVICE);

        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);

        Account account = null;

        if(accounts.length > 0){
            account = accounts[0];
        }

        if(account == null)
            Log.v(TAG, "getAccount: No account found for: " + ACC_TYPE);
        else
            Log.v(TAG, "getAccount: Found account for: " + ACC_TYPE);

        return account;
    }

    private class FacebookSessionStatusCallback implements Session.StatusCallback {
        private final String TAG = FacebookSessionStatusCallback.class.getName();

        // callback when session changes state
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            //User is logged in
            if (session.isOpened()) {
                Log.v(DrawerActivity.TAG, "Facebook logged in.");

                Toast.makeText(getBaseContext(), getResources().getString(R.string.fb_login), Toast.LENGTH_LONG).show();
                changeNavdrawerSessionsView(true);

                /* How to do the callback?
                 * isNetworkOn: Only request the user data when user logs in
                 * Handle the response or user object: Can get cached data
                 * -> Is it needed? Data should only be fetched, when the possibility for new data is there.
                 *  Don't need to store cached data.
                 *
                 */
                if(Utils.isNetworkOn(getApplicationContext()))
                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if(response.getConnection() != null || response.getIsFromCache()) {
                                if(null != user.getFirstName())
                                    mPrefManager.setFacebookName(user.getFirstName() + " " +user.getLastName());
                                else
                                    mPrefManager.setFacebookName("Could not find Facebook name");
                                if(user.getLocation() != null) {
                                    if (null != user.getLocation().getCity())
                                        mPrefManager.setFacebookLocation(user.getLocation().getCity());
                                    else
                                        mPrefManager.setFacebookLocation("Could not find Facebook location");

                                    if (null != user.getLocation().getCountry())
                                        mPrefManager.setFacebookCountry(user.getLocation().getCountry());
                                    else
                                        mPrefManager.setFacebookCountry("Could not find Facebook country");
                                }
                                if(null != user.getBirthday())
                                    mPrefManager.setFacebookAge(user.getBirthday());
                                else
                                    mPrefManager.setFacebookAge("Could not find Facebook age");

                                mPrefManager.setKeyFacebookUid(user.getId());

                                Log.v(DrawerActivity.TAG, "USER ID: " + user.getId());

                            } else {
                                Log.e(TAG, "No facebook data return on newMeRequest.");
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
}
