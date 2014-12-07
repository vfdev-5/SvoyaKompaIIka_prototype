package com.vfdev.android.svoyakompaiika;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.vfdev.android.core.DBHandler;
import com.vfdev.android.core.SqlDBCursorLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    private static final String TAG = ContactsFragment.class.getName();


    // ----- Conversation recipient contacts
    private String [] mRecipientIds=null;


    // ----- Loader : Phone contacts, AppContacts from SQL app DB
    DBHandler mDBHandler;
    private static final String[] APP_CONTACTS_PROJECTION=null;
    private boolean avoidLoopingIfNoAppContacts=false;

    private static final int LOADER_ID_PHONE_CONTACTS=0;
    private static final int LOADER_ID_APP_CONTACTS=1;
    private static final String[] PHONE_CONTACTS_PROJECTION = {
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    private Cursor mPhoneContacts;


    // ------ Contacts view/adapter :
    /// Defines an array that contains column names to move from the Cursor to the ListView.
    private final static String[] FROM_COLUMNS = {
//            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
//            ContactsContract.CommonDataKinds.Phone.NUMBER
            "name",
            "phoneNumber"
    };
    /// Defines an array that contains resource ids for the layout views that get the
    // Cursor column contents.
    private final static int[] TO_IDS = {
            R.id.contactName,
            R.id.contactPhoneNumber
    };
    @InjectView(R.id.contacts) ListView mContactsView;
    private SimpleCursorAdapter mContactsAdapter;

    // ------- Fragment methods
    private static final String TITLE="Title";

    public static ContactsFragment newInstance(String title) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContactsAdapter=new SimpleCursorAdapter(
                getActivity(),
                R.layout.layout_contact_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        mContactsView.setAdapter(mContactsAdapter);
        mContactsView.setOnItemClickListener(this);

        mDBHandler = DBHandler.getInstance();

        // Initializes the loader <=> get phone contacts
        requestAppContacts();

    }





    @Override
    public void onDetach() {
        super.onDetach();
    }



    // ------ LoaderCallbacks<Cursor> methods
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        if (id == LOADER_ID_PHONE_CONTACTS) {
            return new CursorLoader(getActivity(),
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PHONE_CONTACTS_PROJECTION,
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY
            );
        } else if (id == LOADER_ID_APP_CONTACTS) {
            return new SqlDBCursorLoader(getActivity(),
                    mDBHandler.getDb(),
                    getString(R.string.app_contacts_db_table_name),
                    APP_CONTACTS_PROJECTION,
                    null,
                    null,
                    getString(R.string.app_contacts_orderBy_column_name)
            );
        }
        Log.e(TAG, "Loader id is not recognized!");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (loader.getId() == LOADER_ID_APP_CONTACTS) {

            if (data.getCount() == 0) {

                Log.d(TAG, "App DB is empty");
                /// avoid looping if no contacts found
                if (avoidLoopingIfNoAppContacts) {
                    return;
                }
                updateContacts();
                avoidLoopingIfNoAppContacts=true;

            } else {
                // Put the result Cursor in the adapter for the ListView
                Log.d(TAG, "Display app contacts");
                mRecipientIds = new String[data.getCount()];
                mContactsAdapter.swapCursor(data);
            }
        } else if (loader.getId() == LOADER_ID_PHONE_CONTACTS) {

            Log.d(TAG, "Search contact numbers in DB");
            mPhoneContacts=data;
            //  Verify if phone contacts are registered in Parse DB:
            ArrayList<String> contactNumbers = new ArrayList<String>();
            if (data != null && data.moveToFirst()) {
                int index = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int index2 = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
                while (data.moveToNext() && index > -1 && index2 > -1) {
                    contactNumbers.add(PhoneNumberUtils.stripSeparators(data.getString(index)));
                    Log.d(TAG, "_id, Number : " + data.getString(index2) + ", " + data.getString(index));
                }
            }
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereContainedIn(getString(R.string.parse_db_query_column_name),contactNumbers);
            query.findInBackground(new _FindCallback());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if (loader.getId() == LOADER_ID_APP_CONTACTS) {
            // Delete the reference to the existing Cursor
            mContactsAdapter.swapCursor(null);
        }
    }


    // ------ OnItemSelectedListener implementation
    @Override
    public void onItemClick (AdapterView<?> parent, View view, int position, long id) {

        if (mRecipientIds == null) {
            return;
        }

        boolean isChecked=mContactsView.getCheckedItemPositions().get(position);
        if (isChecked) {
            view.setBackgroundColor(getActivity().getResources().getColor(R.color.selected_item));
        } else {
            view.setBackgroundColor(getActivity().getResources().getColor(R.color.unselected_item));
        }

        Cursor cursor = (Cursor) mContactsView.getItemAtPosition(position);
        if (cursor != null && position >=0 && position < mRecipientIds.length) {
            if (isChecked) {
                String contact_id = cursor.getString(
                        // !!! NEED TO REPLACE '_id' BY PROPER VARIABLE
                        cursor.getColumnIndex("_id")
                );
                mRecipientIds[position]=contact_id;
            } else {
                mRecipientIds[position]=null;
            }
        } else {
            Log.e(TAG, "onItemClick : ListItem is not Cursor. " +
                    "Position = " + position + ". RecipientIds.length = " + mRecipientIds.length);
        }

    }

    // ------ FindCallback<ParseUser> extension
    class _FindCallback extends FindCallback<ParseUser> {

        @Override
        public void done (List< ParseUser > objects, ParseException e) {
            Log.d(TAG, "FindCallback : done()");
            if (e == null) {
                if (objects.size() > 0) {
                    // The query was successful.
                    // get Phone contacts registered in Parse DB
                    // transform to AppContact and write into App DB
                    if (mPhoneContacts != null && mPhoneContacts.moveToFirst()) {

                        mDBHandler.getDb().delete(getString(R.string.app_contacts_db_table_name), null, null);
                        mDBHandler.getDb().beginTransaction();

                        int indexID = mPhoneContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
                        int indexPhoneNumber = mPhoneContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int indexUserName = mPhoneContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);

                        while (mPhoneContacts.moveToNext() && indexUserName > -1 && indexPhoneNumber > -1) {

//                            INSERT INTO table_name (column1,column2,column3,...)
//                            VALUES (value1,value2,value3,...);


                            // !!! NEED TO REPLACE '_id,phoneNumber,name' BY PROPER VARIABLE
                            String sql = "INSERT INTO "+
                                    getString(R.string.app_contacts_db_table_name) +
                                    " (_id,phoneNumber,name) " +
                                    " VALUES (?,?,?);";
                            SQLiteStatement statement = mDBHandler.getDb().compileStatement(sql);

                            String number = PhoneNumberUtils.stripSeparators(mPhoneContacts.getString(indexPhoneNumber));
                            for (ParseUser user : objects) {
                                if (number.equals(user.getUsername())) {
                                    statement.clearBindings();
                                    statement.bindLong(1,mPhoneContacts.getInt(indexID));
                                    statement.bindString(2,mPhoneContacts.getString(indexPhoneNumber));
                                    statement.bindString(3,mPhoneContacts.getString(indexUserName));
                                    statement.execute();
                                    break;
                                }
                            }
                        }
                        mDBHandler.getDb().setTransactionSuccessful();
                        mDBHandler.getDb().endTransaction();
                    }
                    // release temp variables :
                    mPhoneContacts=null;
                    // request App Contacts
                    requestAppContacts();

                } else {
                    Log.d(TAG,"FindCallback : objects.size() = " + objects.size());
                }
            } else {
                // Something went wrong.
                Toast.makeText(getActivity().getApplicationContext(),
                        "Something is wrong : " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
                Log.e(TAG, "FindCallback : Something is wrong : " + e.getMessage());
            }
        }
    }



    // ------ Other methods ----------------

    /// Method to request contacts from App DB using Loaders
    protected void requestAppContacts() {
        getLoaderManager().restartLoader(LOADER_ID_APP_CONTACTS, null, this);
    }

    /// Method to request phone contacts using Loaders
    protected void requestPhoneContacts() {
        getLoaderManager().restartLoader(LOADER_ID_PHONE_CONTACTS, null, this);
    }

    /// Method to update app contacts: get phone contacts -> verify in Parse DB => write result in App DB
    public void updateContacts() {
        getLoaderManager().restartLoader(LOADER_ID_PHONE_CONTACTS, null, this);
    }


    /// Method to get recipients for new conversation
    public ArrayList<String> getRecipientIds() {

        ArrayList<String> out = new ArrayList<String>();
        String debugOut="";
        for (String id : mRecipientIds) {
            if (id!=null) {
                out.add(id);
                debugOut+=id + ", ";
            }
        }
        Log.d(TAG, "Recipient ids : " + debugOut);
        return out;
    }

    /// Method to reset selected recipients
    public void resetRecipientIds() {
        mRecipientIds=null;
        for (int i=0;i<mContactsView.getCount();i++) {
            mContactsView.setItemChecked(i,false);
        }
    }


}
