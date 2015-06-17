package com.tuts.vijay.qikpic.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.Utils.Constants;
import com.tuts.vijay.qikpic.view.AspectRatioImageView;

/**
 * Created by vijay on 5/30/15.
 */
public class QikPicSearchParseQueryAdapter extends ParseQueryAdapter<ParseObject> {

    private Context mContext;
    private int container;

    public QikPicSearchParseQueryAdapter(Context context, String className, final String tag) {
        super(context, new QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("QikPik");
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                query.whereEqualTo("tags", tag);
                query.addDescendingOrder("createdAt");
                return query;
            }
        });
        mContext = context;
        //Picasso.with(mContext).setIndicatorsEnabled(true);
        //Picasso.with(mContext).setLoggingEnabled(true);
    }

    public void setContainerType(int container) {
        this.container = container;
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
        //pic.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ParseFile f = null;
        if (container == Constants.CONTAINER_LIST) {
            f = object.getParseFile("image");
        } else if (container == Constants.CONTAINER_GRID) {
            f = object.getParseFile("thumbnail");
        }
        ((AspectRatioImageView)pic).setObjectId(object.getObjectId());
        Picasso.with(mContext).load(f.getUrl()).into(pic);
        return v;
    }

    static class ViewHolder {
        ParseImageView img;
    }
}
