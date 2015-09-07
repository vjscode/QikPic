package com.tuts.vijay.qikpic.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.Utils.Constants;
import com.tuts.vijay.qikpic.view.AspectRatioImageView;

import java.io.File;

/**
 * Created by vijay on 8/8/15.
 */
public class QikPicParseLocalQueryAdapter extends ParseQueryAdapter {

    private Context mContext;
    private int container;

    public QikPicParseLocalQueryAdapter(Context context, String className) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery<ParseObject> queryLocal = new ParseQuery("QikPik");
                queryLocal.fromPin("qikpic_drafts");
                queryLocal.ignoreACLs();
                queryLocal.setLimit(5);
                //queryLocal.whereEqualTo("user", ParseUser.getCurrentUser());

                return queryLocal;
            }
        });
        mContext = context;
        Picasso.with(mContext).setIndicatorsEnabled(true);
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

        //super.getItemView(object, v, parent);


        ViewHolder h = (ViewHolder) v.getTag();

        final ParseImageView pic = h.img;
        //pic.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        Log.d("test", "parse: " + object.get("image"));
        if (((ParseObject)object).get("isDraft") != null) {
            Log.d("test", "from pin");
            String p = null;
            if (container == Constants.CONTAINER_LIST) {
                p = (String) object.get("image");
            } else if (container == Constants.CONTAINER_GRID) {
                p = (String) object.get("thumbnail");
            }
            if (p!= null) {
                ((AspectRatioImageView) pic).setObjectId(object.get("uuid").toString());
                Log.d("test", "p: " + p);
                File fi = new File(p);
                Log.d("test", "fi: " + fi);
                Picasso.with(mContext).load(fi).into(pic);
            }
        }
        return v;
    }

    static class ViewHolder {
        ParseImageView img;
    }
}
