package com.vfdev.android.svoyakompaiika;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.vfdev.android.usersmanaging_with_parse.LoginSignUpActivity;

public class MainActivity extends Activity {


    private static final int LOGINSIGNUP_REQUEST_CODE=0;
    private ParseUser mUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // custom initializations :
        Parse.initialize(this, getResources().getString(R.string.PARSE_APP_ID),
                getResources().getString(R.string.PARSE_CLIENT_KEY));


        // Check if there is a current user :
        dispatchAction();

    }


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
        }
        return super.onOptionsItemSelected(item);
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

    /// Method to dispatch the workflow : if there is current user -> setup main view otherwise LoginSignUp
    protected void dispatchAction() {
        // Check if there is a current user :
        mUser = ParseUser.getCurrentUser();
        if (mUser == null) {
            // no user -> start login/signup activity
            startLoginSignUpActivity();
        } else {
            setupMainView();
        }
    }

    /// Launch LoginSignUpActivity
    protected void startLoginSignUpActivity() {
        Intent i = new Intent(this, LoginSignUpActivity.class);
        startActivityForResult(i, LOGINSIGNUP_REQUEST_CODE);
    }

    /// Method to setup main view
    protected void setupMainView() {
        TextView userId = (TextView) findViewById(R.id.userId);
        userId.setText(mUser.getUsername());
    }


    /// Logout
    public void onLogoutClicked(View v) {
        mUser.logOut();
        dispatchAction();
    }


}
