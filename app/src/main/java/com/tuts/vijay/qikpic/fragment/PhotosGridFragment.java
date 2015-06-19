package com.tuts.vijay.qikpic.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.tuts.vijay.qikpic.ActivityInteraction;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.Utils.Constants;
import com.tuts.vijay.qikpic.adapter.QikPicParseQueryAdapter;
import com.tuts.vijay.qikpic.adapter.QikPicSearchParseQueryAdapter;
import com.tuts.vijay.qikpic.listener.GridViewItemClickListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotosGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PhotosGridFragment extends Fragment implements ActivityInteraction, ParseQueryAdapter.OnQueryLoadListener {

    private ParseQueryAdapter<ParseObject> mAdapter;
    private GridView mGridView;
    private boolean isSearch = false;
    private String searchTag;

    public static PhotosGridFragment newInstance(String param1, String param2) {
        PhotosGridFragment fragment = new PhotosGridFragment();
        return fragment;
    }
    public PhotosGridFragment() {
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
        View view = inflater.inflate(R.layout.fragment_photos_grid, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridView);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            this.isSearch = b.getBoolean("isSearch", false);
            this.searchTag = b.getString("q");
        }
        if (isSearch) {
            mAdapter = new QikPicSearchParseQueryAdapter(getActivity(), "QikPik", searchTag);
            QikPicSearchParseQueryAdapter searchAdapter = (QikPicSearchParseQueryAdapter)mAdapter;
            searchAdapter.setContainerType(Constants.CONTAINER_GRID);
            searchAdapter.addOnQueryLoadListener(this);
        } else {
            mAdapter = new QikPicParseQueryAdapter(getActivity(), "QikPik");
            QikPicParseQueryAdapter queryAdapter = (QikPicParseQueryAdapter)mAdapter;
            queryAdapter.setContainerType(Constants.CONTAINER_GRID);
            queryAdapter.addOnQueryLoadListener(this);
        }

        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new GridViewItemClickListener(getActivity()));
    }

    @Override
    public void loadObjects() {
        mAdapter.loadObjects();
    }

    @Override
    public void onLoading() {
        getActivity().findViewById(R.id.loadingProgress).setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.GONE);
    }

    @Override
    public void onLoaded(List list, Exception e) {
        getActivity().findViewById(R.id.loadingProgress).setVisibility(View.GONE);
        mGridView.setVisibility(View.VISIBLE);
    }
}
