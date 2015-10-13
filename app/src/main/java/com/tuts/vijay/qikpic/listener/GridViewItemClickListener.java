package com.tuts.vijay.qikpic.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.activity.DetailActivity;

/**
 * Created by vijay on 6/5/15.
 */
public class GridViewItemClickListener implements AdapterView.OnItemClickListener {

    Context mContext;

    public GridViewItemClickListener(Context context) {
        this.mContext = context;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String objectId = null;
        Object tag = view.findViewById(R.id.gridImage).getTag();
        if (tag != null) {
            objectId = tag.toString();
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra("oldOrNew", objectId);
            mContext.startActivity(intent);
        }
    }
}
