package com.vfdev.android.svoyakompaiika;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseUser;
import com.vfdev.android.core.DBHandler;
import com.vfdev.android.usersmanaging_with_parse.LoginSignUpWithTelNumActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;


/*
* This activity contains ViewPager of Fragments with Tabs

* */
public class MainActivity extends Activity implements ActionBar.TabListener {

    private static final String TAG = MainActivity.class.getName();

    // UI
    private ViewPager mViewPager=null;
    private PagerAdapter mPagerAdapter=null;
    private ActionBar mActionBar=null;
    private ConversationsFragment mConversationFragment=null;
    private ContactsFragment mContactsFragment=null;

    // DB
    private DBHandler mDBHandler=null;

    // Login/Signup
    private static final int LOGINSIGNUP_REQUEST_CODE=0;
    private ParseUser mUser = null;

    // Contacts:
    private static final int PICK_CONTACTS_REQUEST_CODE=1;


    // ------ Activity methods -------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupPagerUI();

        setupDB();

        // custom initializations :
        Parse.initialize(this, getResources().getString(R.string.PARSE_APP_ID),
                getResources().getString(R.string.PARSE_CLIENT_KEY));

        // Check if there is a current user :
        dispatchAction();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOGINSIGNUP_REQUEST_CODE) {

            if (resultCode == RESULT_CANCELED) {
                finish();
            } else if (resultCode == RESULT_OK) {

                // Something to do
                dispatchAction();

            }
        }

    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume()");
        super.onResume();

    }

    // ------ Tabs handling -------
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    // ------ Menu handling -------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            logout();
        } else if (id == R.id.action_updateContacts) {
            mContactsFragment.updateContacts();
        } else if (id == R.id.action_newConversation) {
            ArrayList<String> recipientIds = mContactsFragment.getRecipientIds();
            if (recipientIds.isEmpty()) {
                Toast.makeText(this,
                        "Please choose at least one friend",
                        Toast.LENGTH_LONG).show();
            } else {
                createNewConversation(recipientIds);
            }
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//
//        if (mIsModifiedMenu) {
//            menu.findItem(R.id.action_add_point).setVisible(false);
//            menu.findItem(R.id.action_remove_points).setVisible(true);
//        } else {
//            menu.findItem(R.id.action_add_point).setVisible(true);
//            menu.findItem(R.id.action_remove_points).setVisible(false);
//        }
//
//        return super.onPrepareOptionsMenu(menu);
//    }


    // ------ other helping methods -------
    protected void initTabs() {

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            mActionBar.addTab(
                    mActionBar.newTab()
                            .setText(mPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }
    protected void setupPagerUI()
    {
        Log.i(TAG, "setupPagerUI()");
        // Set up the action bar.
        mActionBar = getActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mContactsFragment = ContactsFragment.newInstance(getString(R.string.contacts_fragment_title));
        mConversationFragment = ConversationsFragment.newInstance(getString(R.string.conversations_fragment_title));
        mPagerAdapter = new PagerAdapter(getFragmentManager());
        mPagerAdapter.appendFragment(mContactsFragment);
        mPagerAdapter.appendFragment(mConversationFragment);


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.i(TAG,"onPageSelected : " + String.valueOf(position));
                mActionBar.setSelectedNavigationItem(position);
            }
        });
        initTabs();
    }


    /// Method to setup DB
    protected void setupDB() {
        mDBHandler = DBHandler.getInstance();
        mDBHandler.init(this, "dbconf.xml");
    }

    /// Method to dispatch the workflow : if there is current user -> setup main view otherwise LoginSignUp
    protected void dispatchAction() {
        // Check if there is a current user :
        mUser = ParseUser.getCurrentUser();
        if (mUser == null) {
            // no user -> start login/signup activity
            startLoginSignUpActivity();
        }
    }

    /// Launch LoginSignUpActivity
    protected void startLoginSignUpActivity() {
        Intent i = new Intent(this, LoginSignUpWithTelNumActivity.class);
        startActivityForResult(i, LOGINSIGNUP_REQUEST_CODE);
    }

    protected void logout() {
        mUser.logOut();
        dispatchAction();
    }




    /// Create new conversation
    protected void createNewConversation(ArrayList<String> recipientIds) {

        Toast.makeText(this,
                "Create new conversation",
                Toast.LENGTH_SHORT
        ).show();




    }


}
