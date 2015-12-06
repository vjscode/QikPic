package com.tuts.vijay.qikpic.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.adapter.QikPicCursorAdapter;
import com.tuts.vijay.qikpic.db.QikPikContentProvider;
import com.tuts.vijay.qikpic.listener.GridViewItemClickListener;
import com.tuts.vijay.qikpic.listener.ScrollListener;

/**
 * Created by vijay on 10/6/15.
 */
public class QikPicGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private GridView mGridView;
    private QikPicCursorAdapter cusrsorAdapter;
    private QikPikContentProvider dataProvider;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

    private static final String[] PROJECTION = new String[] { "_id", "objectId", "image", "thumbnail", "createdAt",
            "updatedAt", "qikpicId"};

    private static final int LOADER_ID = 1;
    private String filterString = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos_grid, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridView);
        mGridView.setEmptyView(view.findViewById(R.id.emptyElement));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle b = getArguments();
        if (b != null) {
            //this.isSearch = b.getBoolean("isSearch", false);
            this.filterString = b.getString("q");
        }

        dataProvider = new QikPikContentProvider();

        String[] dataColumns = { "thumbnail" };
        int[] viewIDs = { R.id.gridImage };

        cusrsorAdapter = new QikPicCursorAdapter(getActivity(), R.layout.adapter_grid_item,
                null, dataColumns, viewIDs, 0);
        cusrsorAdapter.setType(0);
        mGridView.setOnScrollListener(new ScrollListener(cusrsorAdapter, dataProvider));
        mGridView.setAdapter(cusrsorAdapter);
        mCallbacks = this;
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, mCallbacks);
        mGridView.setOnItemClickListener(new GridViewItemClickListener(getActivity()));
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (filterString == null || filterString.equals("")) {
            return new android.support.v4.content.CursorLoader(getActivity(), QikPikContentProvider.CONTENT_URI,
                    PROJECTION, null, null, "updatedAt DESC");
        } else {
            return new android.support.v4.content.CursorLoader(getActivity(), QikPikContentProvider.CONTENT_URI,
                    PROJECTION, "tags LIKE ?", new String[]{"%" + filterString + "%"}, "updatedAt DESC");
        }
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor) {
        cusrsorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {
        cusrsorAdapter.swapCursor(null);
    }

}
