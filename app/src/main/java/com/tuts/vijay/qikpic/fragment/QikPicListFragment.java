
package com.tuts.vijay.qikpic.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tuts.vijay.qikpic.ActivityInteraction;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.adapter.QikPicCursorAdapter;
import com.tuts.vijay.qikpic.db.QikPikContentProvider;
import com.tuts.vijay.qikpic.listener.ListViewItemClickListener;
import com.tuts.vijay.qikpic.listener.ScrollListener;

/**
 * Created by vijay on 9/19/15.
 */

public class QikPicListFragment extends Fragment implements ActivityInteraction, LoaderManager.LoaderCallbacks<Cursor> {

    private OnFragmentInteractionListener mListener;
    private ListView mListView;
    private QikPicCursorAdapter cusrsorAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    QikPikContentProvider dataProvider;

    private static final String[] PROJECTION = new String[] { "_id", "objectId", "image", "thumbnail", "createdAt",
        "updatedAt", "qikpicId"};

    private static final int LOADER_ID = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QikPicListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void loadObjects() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), QikPikContentProvider.CONTENT_URI,
                PROJECTION, null, null, "updatedAt DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cusrsorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        cusrsorAdapter.swapCursor(null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos_list, container, false);
        mListView = (ListView) view.findViewById(R.id.listView);
        mListView.setEmptyView(view.findViewById(R.id.emptyElement));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataProvider = new QikPikContentProvider();

        String[] dataColumns = { "image" };
        int[] viewIDs = { R.id.listImage};

        cusrsorAdapter = new QikPicCursorAdapter(getActivity(), R.layout.adapter_list_item,
                null, dataColumns, viewIDs, 0);
        cusrsorAdapter.setType(1);
        mListView.setOnScrollListener(new ScrollListener(cusrsorAdapter, dataProvider, getActivity()));
        mListView.setAdapter(cusrsorAdapter);
        mCallbacks = this;
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, mCallbacks);
        mListView.setOnItemClickListener(new ListViewItemClickListener(getActivity()));
    }

}
