package com.tuts.vijay.qikpic.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuts.vijay.qikpic.R;

/**
 * Created by vijay on 9/19/15.
 */
public class QikPicCursorAdapter extends SimpleCursorAdapter {
    int type = 0;

    //diaply options for cache
    private DisplayImageOptions displayImageOptions;

    public QikPicCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = null;
        if (type == 0) {
            view = View.inflate(context, R.layout.adapter_grid_item, null);
        } else {
            view = View.inflate(context, R.layout.adapter_list_item, null);
        }
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String fullFile = cursor.getString(cursor.getColumnIndex("image"));
        String thumbnail = cursor.getString(cursor.getColumnIndex("thumbnail"));

        ImageView imgView = null;
        Log.d("test", "thumbnail: " + thumbnail);

        if (type == 0) {
            imgView = (ImageView) view.findViewById(R.id.gridImage);
            imgView.setTag(cursor.getString(cursor.getColumnIndex("qikpicId")));
            //Picasso.with(context).load(("file:" + thumbnail)).into(imgView);
            //imgView.setImageURI(Uri.parse("file:" + thumbnail));
            ImageLoader.getInstance().displayImage("file://" + thumbnail, imgView, displayImageOptions);
        } else {
            imgView = (ImageView) view.findViewById(R.id.listImage);
            imgView.setTag(cursor.getString(cursor.getColumnIndex("qikpicId")));
            //Picasso.with(context).load(("file:" + fullFile)).into(imgView);
            //imgView.setImageURI(Uri.parse("file:" + fullFile));
            ImageLoader.getInstance().displayImage("file://" + fullFile, imgView, displayImageOptions);
        }
    }

    public void setType(int type) {
        this.type = type;
    }
}
