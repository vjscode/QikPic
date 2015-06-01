package com.tuts.vijay.qikpic.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.tuts.vijay.qikpic.R;

/**
 * Created by vijay on 5/30/15.
 */
public class QikPicParseQueryAdapter extends ParseQueryAdapter<ParseObject> {
    public QikPicParseQueryAdapter(Context context, String className) {
        super(context, className);
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.adapter_list_item, null);
        }

        // Take advantage of ParseQueryAdapter's getItemView logic for
        // populating the main TextView/ImageView.
        // The IDs in your custom layout must match what ParseQueryAdapter expects
        // if it will be populating a TextView or ImageView for you.
        super.getItemView(object, v, parent);

        // Do additional configuration before returning the View.
        final ParseImageView pic = (ParseImageView) v.findViewById(R.id.image);
        pic.setParseFile(object.getParseFile("image"));
        pic.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        pic.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                //pic.setVisibility(View.VISIBLE);
            }
        });
        return v;
    }
}
