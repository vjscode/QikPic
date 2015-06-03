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
            ViewHolder holder = new ViewHolder();
            holder.img = (ParseImageView) v.findViewById(R.id.image);
            v.setTag(holder);
        }

        super.getItemView(object, v, parent);


        ViewHolder h = (ViewHolder) v.getTag();

        final ParseImageView pic = h.img;
        pic.setParseFile(object.getParseFile("image"));
        pic.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        pic.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
            }
        });
        object.pinInBackground();
        return v;
    }

    static class ViewHolder {
        ParseImageView img;
    }
}
