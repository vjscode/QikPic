package com.tuts.vijay.qikpic.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.tuts.vijay.qikpic.ActivityInteraction;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.adapter.QikPicParseQueryAdapter;
import com.tuts.vijay.qikpic.listener.ListViewItemClickListener;

public class PhotosListFragment extends Fragment implements ActivityInteraction {

    private OnFragmentInteractionListener mListener;
    private ParseQueryAdapter<ParseObject> mAdapter;
    private ListView mListView;

    // TODO: Rename and change types of parameters
    public static PhotosListFragment newInstance(String param1, String param2) {
        PhotosListFragment fragment = new PhotosListFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PhotosListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setListAdapter(mAdapter);
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

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
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
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new QikPicParseQueryAdapter(getActivity(), "QikPik");//ParseQueryAdapter<ParseObject>(this, "QikPik");
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new ListViewItemClickListener(getActivity()));
    }

    @Override
    public void loadObjects() {
        mAdapter.loadObjects();
    }
}
