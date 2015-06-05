package com.tuts.vijay.qikpic.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.tuts.vijay.qikpic.R;

/**
 * Created by vijay on 5/30/15.
 */
public class QikPicParseQueryAdapter extends ParseQueryAdapter<ParseObject> {

    Context mContext;
    public QikPicParseQueryAdapter(Context context, String className) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("QikPik");
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                return query;
            }
        });
        mContext = context;
        //Picasso.with(mContext).setIndicatorsEnabled(true);
        //Picasso.with(mContext).setLoggingEnabled(true);
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
        pic.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ParseFile f = object.getParseFile("image");
        Picasso.with(mContext).load(f.getUrl()).into(pic);
        return v;
    }

    static class ViewHolder {
        ParseImageView img;
    }
}
