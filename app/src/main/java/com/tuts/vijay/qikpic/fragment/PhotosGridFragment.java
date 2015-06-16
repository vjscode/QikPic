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
import com.tuts.vijay.qikpic.listener.GridViewItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotosGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PhotosGridFragment extends Fragment implements ActivityInteraction {

    private ParseQueryAdapter<ParseObject> mAdapter;
    private GridView mGridView;

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
        mAdapter = new QikPicParseQueryAdapter(getActivity(), "QikPik");//ParseQueryAdapter<ParseObject>(this, "QikPik");
        ((QikPicParseQueryAdapter)mAdapter).setContainerType(Constants.CONTAINER_GRID);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new GridViewItemClickListener(getActivity()));
    }

    @Override
    public void loadObjects() {
        mAdapter.loadObjects();
    }
}
