package com.tuts.vijay.qikpic.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.tuts.vijay.qikpic.DetailActivity;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.views.AspectRatioImageView;

/**
 * Created by vijay on 6/5/15.
 */
public class ListViewItemClickListener implements AdapterView.OnItemClickListener {

    Context mContext;

    public ListViewItemClickListener(Context context) {
        this.mContext = context;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String objectId = ((AspectRatioImageView) view.findViewById(R.id.image)).getObjectId();
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra("id", objectId);
        mContext.startActivity(intent);
    }
}
