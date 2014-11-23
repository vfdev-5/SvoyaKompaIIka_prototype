package com.vfdev.android.usersmanaging_with_parse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.vfdev.android.svoyakompaiika.R;

public class LoginSignUpWithTelNumActivity extends Activity {

    private final static String TAG = LoginSignUpWithTelNumActivity.class.getName();

    private TelephonyManager mTelephonyManager = null;

    private EditText mPhoneNumberET = null;
    private EditText mPasswordET = null;
    private EditText mPasswordAgainET = null;

    private ProgressDialog mProgressDialog = null;

    // ------ Inner classes :
    private class _SignUpCallback extends SignUpCallback {
        @Override
        public void done(ParseException e) {
            if (e != null) {
                if (e.getCode() == ParseException.USERNAME_TAKEN) {
                    // Try to login :
                    login();
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.ErrorMsg) +
                                    e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            } else {
                mProgressDialog.dismiss();
                finishActivity();
            }
        }
    }

    private class _LogInCallback extends LogInCallback {
        @Override
        public void done(ParseUser user, ParseException e) {

            if (e != null) {
                if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                    // Verification :
                    verification();
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.ErrorMsg) +
                                    e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            } else {
                mProgressDialog.dismiss();
                finishActivity();
            }
        }
    }

    // ------ Activity methods -------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up_with_tel_num);

        mPhoneNumberET = (EditText) findViewById(R.id.phoneNumber);
        mPasswordET = (EditText) findViewById(R.id.password);
        mPasswordAgainET = (EditText) findViewById(R.id.passwordAgain);

        mProgressDialog = new ProgressDialog(this);

        mTelephonyManager = (TelephonyManager) getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);

        setupPhoneInfo();

    }


    // ------- Other class methods --------

    // Method to get required info to register user
    protected void setupPhoneInfo() {

        Log.i(TAG, "setupPhoneInfo");

        if (mPhoneNumberET.getText().length() == 0 ||
                mPasswordET.getText().length() == 0) {

            String phoneNumber = getDigits(mTelephonyManager.getLine1Number());
            String deviceId = mTelephonyManager.getDeviceId();

            if (!phoneNumber.isEmpty()) {
                mPhoneNumberET.setText(phoneNumber);
            }

            if (deviceId.isEmpty()) {
                mPasswordET.setVisibility(EditText.VISIBLE);
                mPasswordAgainET.setVisibility(EditText.VISIBLE);
            } else {
                mPasswordET.setText(deviceId);
                mPasswordAgainET.setText(deviceId);
            }
        }
        // Show : number + password
        Toast.makeText(this,
                mPhoneNumberET.getText() + " : " + mPasswordET.getText(),
                Toast.LENGTH_LONG).show();

    }

    /// Method to finish activity with RESULT_OK
    void finishActivity() {
        Intent i = new Intent();
        setResult(RESULT_OK,i);
        finish();
    }

    protected String getDigits(String rawPhoneNumber) {
        if (rawPhoneNumber.contains("+")){
            return rawPhoneNumber.replace("+", "");
        }
        return rawPhoneNumber;
    }

    // Register user
    protected void signup() {

        String userID = mPhoneNumberET.getText().toString().trim();
        String password = mPasswordET.getText().toString().trim();
        String passwordAgain = mPasswordAgainET.getText().toString().trim();

        if (!checkUserIdPassword(userID, password, passwordAgain)) {
            return;
        }

        // 1) sign up :
        ParseUser user = new ParseUser();
        user.setUsername(userID);
        user.setPassword(password);

        // Call the Parse signup method -> see callback
        user.signUpInBackground(new _SignUpCallback());
    }

    // Login
    protected void login() {

        String userID = mPhoneNumberET.getText().toString().trim();
        String password = mPasswordET.getText().toString().trim();
        String passwordAgain = mPasswordAgainET.getText().toString().trim();

        if (!checkUserIdPassword(userID, password, passwordAgain)) {
            return;
        }
        ParseUser.logInInBackground(userID, password, new _LogInCallback());
    }

    // verification
    protected void verification() {

        mProgressDialog.dismiss();
        Toast.makeText(this, "NEED TO VERIFY YOUR PHONE NUMBER", Toast.LENGTH_LONG).show();

    }

    // Check user/password info :
    protected boolean checkUserIdPassword(String userID, String password, String passwordAgain) {

        // check phone number : number of digits (==11):
        if (userID.length() != 11) {
            Toast.makeText(this,
                    getString(R.string.ErrorMsg) +
                            getString(R.string.PhoneNumErr),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        // check if password is not null and equal to passwordAgain :
        if (password.length() == 0) {
            Toast.makeText(this,
                    getString(R.string.Password) + " " +
                            getString(R.string.BlankFieldError),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (!passwordAgain.equals(password)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.PasswordsNotEqual),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    // Register button is clicked
    public void onRegisterClicked(View view) {
        Log.i(TAG, "onRegisterClicked");

        // Set up a progress dialog and try to login/signup
        mProgressDialog.setMessage(getString(R.string.ProgressMsg));
        mProgressDialog.show();
        signup();

    }


}
