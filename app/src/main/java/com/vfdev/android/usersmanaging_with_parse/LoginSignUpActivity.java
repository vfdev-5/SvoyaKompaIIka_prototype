package com.vfdev.android.usersmanaging_with_parse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.vfdev.android.svoyakompaiika.R;

public class LoginSignUpActivity extends Activity {

    private static final String TAG=LoginSignUpActivity.class.getName();

    private EditText mUserId;
    private EditText mPassword;
    private EditText mPasswordAgain;
    private TextView mLoginSignUpSwitcher;
    private TextView mLoginSignUpAction;
    private TextView mForgotPasswordAction;


    private String [] mSwitcherOptions;
    private String [] mActionOptions;

    /// ??? RECODE : This should be done using mActionOptions, mSwitcherOptions
    private static final int SWITCHER_OPTION_SIGN_UP = 0;
    private static final int SWITCHER_OPTION_LOG_IN = 1;

    private static final int ACTION_OPTION_SIGN_UP = 0;
    private static final int ACTION_OPTION_LOG_IN = 1;
    private static final int ACTION_OPTION_SEND = 2;
    private int mState = -1;

    /// !!! RECODE


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        mUserId = (EditText) findViewById(R.id.userId);
        mPassword = (EditText) findViewById(R.id.password);
        mPasswordAgain = (EditText) findViewById(R.id.passwordAgain);
        mLoginSignUpSwitcher = (TextView) findViewById(R.id.loginSignUpSwitcher);
        mLoginSignUpAction = (TextView) findViewById(R.id.loginSignUpAction);
        mForgotPasswordAction = (TextView) findViewById(R.id.forgotPasswordAction);

        mSwitcherOptions = getResources().getStringArray(R.array.LoginSignUpSwitch);
        mActionOptions = getResources().getStringArray(R.array.LoginSignUpAction);

        // Login activity is initialized by default
        setupLoginActivity();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_sign_up, menu);
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


    /*
     *  Class public/private methods
     */

    protected void setupLoginActivity() {
        mState = ACTION_OPTION_LOG_IN;
        mLoginSignUpSwitcher.setText(mSwitcherOptions[SWITCHER_OPTION_SIGN_UP]);
        mLoginSignUpAction.setText(mActionOptions[mState]);
        mPasswordAgain.setVisibility(EditText.GONE);
        mForgotPasswordAction.setVisibility(TextView.VISIBLE);
    }

    protected void setupSignUpActivity() {
        mState=ACTION_OPTION_SIGN_UP;
        mLoginSignUpSwitcher.setText(mSwitcherOptions[SWITCHER_OPTION_LOG_IN]);
        mLoginSignUpAction.setText(mActionOptions[mState]);
        mPasswordAgain.setVisibility(EditText.VISIBLE);
        mForgotPasswordAction.setVisibility(TextView.GONE);

    }

    protected void setupForgotPasswordActivity() {
        mState=ACTION_OPTION_SEND;
        mLoginSignUpSwitcher.setText(mSwitcherOptions[SWITCHER_OPTION_LOG_IN]);
        mLoginSignUpAction.setText(mActionOptions[ACTION_OPTION_SEND]);
        mPassword.setVisibility(EditText.GONE);
        mPasswordAgain.setVisibility(EditText.GONE);
        mForgotPasswordAction.setVisibility(TextView.GONE);
    }

    /// Method to check inputs
    protected boolean checkInputs() {
        return false;
    }

    /// Callback when login / sign up switcher text edit is clicked
    public void onLoginSignUpSwitcherClicked(View view) {

        if (mState == ACTION_OPTION_SIGN_UP) {
//            Toast.makeText(this, "SWITCHER_OPTION_LOG_IN", Toast.LENGTH_SHORT).show();
            setupLoginActivity();
        } else if (mState == ACTION_OPTION_LOG_IN) {
//            Toast.makeText(this, "SWITCHER_OPTION_SIGN_UP", Toast.LENGTH_SHORT).show();
            setupSignUpActivity();
        }

    }


    /// Callback when login / sign up is actioned
    public void onLoginSignUpActionClicked(View view) {

        String userid = mUserId.getText().toString().trim();
        String password = null;
        String passwordAgain = null;

        // check if UserID:
        if (userid.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.Email) + " " +
                            getString(R.string.BlankFieldError),
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (mState == ACTION_OPTION_LOG_IN || mState == ACTION_OPTION_SIGN_UP) {
            Log.i(TAG, "LOG IN or SIGN UP is actioned");

            password = mPassword.getText().toString().trim();
            if (password.length() == 0) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.Password) + " " +
                                getString(R.string.BlankFieldError),
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (mState == ACTION_OPTION_SIGN_UP) {
                Log.i(TAG, "SIGN UP is actioned");
                passwordAgain = mPasswordAgain.getText().toString().trim();

                if (passwordAgain.length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.Password) + " " +
                                    getString(R.string.BlankFieldError),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (!passwordAgain.equals(password)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.PasswordsNotEqual),
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.ProgressMsg));
        dialog.show();

        if (mState == ACTION_OPTION_SEND) {
            Log.i(TAG, "SEND PWD actioned");

        } else if (mState == ACTION_OPTION_LOG_IN) {
            // Call the Parse login method
            ParseUser.logInInBackground(userid, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    dialog.dismiss();
                    if (e != null) {
                        // Show the error message
                        Toast.makeText(getApplicationContext(),
                                e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    } else {
                        finishActivity();
                    }
                }
            });

        } else if (mState == ACTION_OPTION_SIGN_UP) {

            ParseUser user = new ParseUser();
            user.setUsername(userid);
            user.setPassword(password);

            // Call the Parse signup method
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    dialog.dismiss();
                    if (e != null) {
                        // Show the error message
                        Toast.makeText(getApplicationContext(),
                                e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    } else {
                        finishActivity();
                    }
                }
            });
        }
    }


    /// Method to finish activity with RESULT_OK
    void finishActivity() {
        Intent i = new Intent();
        setResult(RESULT_OK,i);
        finish();
    }

    /// Callback when forgot password is actioned
    public void onForgotPasswordClicked(View view) {
//        Toast.makeText(this, "onForgotPasswordClicked", Toast.LENGTH_SHORT).show();
        setupForgotPasswordActivity();
    }


}
