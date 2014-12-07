package com.vfdev.android.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;


import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/** Singleton */
public class DBHandler {

    private static final String TAG = DBHandler.class.getName();
    private static DBHandler mInstance = new DBHandler();
    private DBHandlerPrivate mHandlerPrivate=null;
    private DBConf mConf=null;


    // ------- Singleton init methods
    static public DBHandler getInstance() {
        return mInstance;
    }

    public void init(Context context, String xmlDBConfFile) {

        // Initialize DBConf
        Serializer serializer = new Persister();
        try {
            mConf = serializer.read(DBConf.class, context.getAssets().open(xmlDBConfFile));
        } catch (Exception e) {
            Log.e(TAG, "Failed to deserialize DBConf : " + e.getMessage());
            return;
        }

        Log.d(TAG, mConf.printSelf());
        mHandlerPrivate = new DBHandlerPrivate(context);
    }

    private DBHandler() {
    }




    // -------- Class methods

    /*
    public Cursor getAllDataFromTable(String tableName) {

        if (mHandlerPrivate == null) {
            return null;
        }
        // Field names can be null -> all fields are given, however it is discouraged
        // http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html
        String[] fieldNames = null;
        return mHandlerPrivate.getReadableDatabase().query(tableName,
                fieldNames,
                null,
                null,
                null,
                null,
                null);
    }

    public Cursor getAllDataFromTable(String tableName, String[] fieldNames) {

        if (mHandlerPrivate == null) {
            return null;
        }
        // Field names can be null -> all fields are given, however it is discouraged
        // http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html
        return mHandlerPrivate.getReadableDatabase().query(tableName,
                fieldNames,
                null,
                null,
                null,
                null,
                null);
    }


    public Cursor getDataOnRowIdFromTable(long id, String tableName) {
        if (mHandlerPrivate == null) {
            return null;
        }
        return mHandlerPrivate.getReadableDatabase().query(
                true,
                tableName,
                null, // field names
                GeoDBConf.COMMON_KEY_ID + "=" + String.valueOf(id),
                null,
                null,
                null,
                null,
                null
        );

    }


    public long createDataInTable(String tableName, ContentValues data) {
        SQLiteDatabase db = mHandlerPrivate.getWritableDatabase();
        return db.insert(tableName, null, data);
    }


    public boolean updateDataInTable(String tableName, long id, ContentValues data) {
        SQLiteDatabase db = mHandlerPrivate.getWritableDatabase();
        return db.update(
                tableName,
                data,
                GeoDBConf.COMMON_KEY_ID + "=" + String.valueOf(id),
                null) > 0;

    }

    public boolean deleteDataInTable(String tableName, long id)
    {
        SQLiteDatabase db = mHandlerPrivate.getWritableDatabase();
        return db.delete(
                tableName,
                GeoDBConf.COMMON_KEY_ID + "=" + String.valueOf(id),
                null) > 0;
    }


    public boolean deleteDataInTable(String tableName, long[] ids)
    {
        SQLiteDatabase db = mHandlerPrivate.getWritableDatabase();
        String values = "";
        for (int i=0; i<ids.length-1; i++) {
            long id = ids[i];
            values += String.valueOf(id) + ", ";
        }
        values += String.valueOf(ids[ids.length-1]);
        return db.delete(
                tableName,
                GeoDBConf.COMMON_KEY_ID + " in (" + values + ")",
                null) > 0;
    }
    */

    public SQLiteDatabase getDb() {
        if (mHandlerPrivate == null) return null;
        return mHandlerPrivate.getWritableDatabase();
    }

    public String[] getTablenames() {
        return mConf.getTablenames();
    }


    /// Method to rewrite DB table data : delete all DB rows and insert new rows
//    public long rewriteDataInTable(String tableName, ContentValues data) {
//        if (mHandlerPrivate == null) {
//            return -1;
//        }
//        SQLiteDatabase db = mHandlerPrivate.getWritableDatabase();
//        db.delete(tableName, null, null);
//        return db.insert(tableName, null, data);
//    }

    /// Method to insert new data in DB table
    public long insertDataInTable(String tableName, ContentValues data) {
        if (mHandlerPrivate == null) {
            return -1;
        }
        SQLiteDatabase db = mHandlerPrivate.getWritableDatabase();
        return db.insert(tableName, null, data);
    }

    public Cursor getDataFromTable(String tableName,
                                   String[] columns,
                                   String selection,
                                   String[] selectionArgs,
                                   String orderBy) {
        if (mHandlerPrivate == null) {
            return null;
        }
        return mHandlerPrivate.getReadableDatabase().query(
                true,
                tableName,
                columns, // field names
                selection, // selection
                selectionArgs, // args of selection
                null, // groupBy
                null, // having
                orderBy, // orderBy
                null // limit
        );
    }

    // ----- SQLiteOpenHelper extension
    private class DBHandlerPrivate extends SQLiteOpenHelper {

        public DBHandlerPrivate(Context context) {
            super(context, mConf.getDbName(), null, mConf.getDbVersion());
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "onCreate db");
            for (int i=0;i<mConf.getTableCount();i++){
                db.execSQL(mConf.createTableQuery(i));
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "onUpgrade db from " + String.valueOf(oldVersion) + " to " + String.valueOf(newVersion));
            for (int i=0;i<mConf.getTableCount();i++){
                db.execSQL(mConf.dropTableQuery(i));
            }
            onCreate(db);
        }

    }
}