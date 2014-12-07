package com.vfdev.android.svoyakompaiika;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ConversationsFragment extends Fragment {

    private static final String TAG = ConversationsFragment.class.getName();
    private static final String TITLE="Title";

    // UI
    @InjectView(R.id.conversations) ListView mConversationsView;
    private SimpleCursorAdapter mConversationsAdapter;


    public static ConversationsFragment newInstance(String title) {
        ConversationsFragment fragment = new ConversationsFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }
    public ConversationsFragment() {
        // Required empty public constructor
    }


    // ------- Fragment methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated");

        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        mContactsAdapter=new SimpleCursorAdapter(
//                getActivity(),
//                R.layout.layout_contact_item,
//                null,
//                FROM_COLUMNS, TO_IDS,
//                0);
//        mContactsView.setAdapter(mContactsAdapter);
//        mContactsView.setOnItemClickListener(this);
//
//        // Initializes the loader
//        getLoaderManager().initLoader(LOADER_ID_PHONE_CONTACTS, null, this);

    }

    // -------- Other methods


}