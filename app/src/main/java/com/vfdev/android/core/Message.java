package com.vfdev.android.core;

import android.location.Location;
import android.text.format.Time;

/**
 * Created by vfomin on 11/26/14.
 */
public class Message {


    private String mSenderId = null;
    private String[] mRecipients = null;
    private Time mDateTime = null;
    private Body mBody = null;

    public class Body {

        private Location mLocation=null;
        private String mText=null;

        public String getText() {
            return mText;
        }

        public void setText(String text) {
            mText = text;
        }

        public Location getLocation() {
            return mLocation;
        }

        public void setLocation(Location location) {
            mLocation = location;
        }

    }

    public String getSenderId() {
        return mSenderId;
    }

    public String[] getRecipients() {
        return mRecipients;
    }

    public Time getDateTime() {
        return mDateTime;
    }

    public Body getBody() {
        return mBody;
    }

    public void setSenderId(String mSenderId) {
        this.mSenderId = mSenderId;
    }

    public void setRecipients(String[] mRecipients) {
        this.mRecipients = mRecipients;
    }

    public void setDateTime(Time mDateTime) {
        this.mDateTime = mDateTime;
    }

    public void setBody(Body mBody) {
        this.mBody = mBody;
    }

}
