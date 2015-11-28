package com.tuts.vijay.qikpic.async;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.tuts.vijay.qikpic.event.MediaStorePicEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.greenrobot.event.EventBus;

/**
 * Created by vijay on 11/28/15.
 */
public class LoadPicFromMediaStoreTask extends AsyncTask<Uri, Void, Cursor> {

    private Context mContext;
    private String[] filePathColumn = { MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.LATITUDE, MediaStore.Images.ImageColumns.LONGITUDE};
    public LoadPicFromMediaStoreTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected Cursor doInBackground(Uri... uris) {
        Cursor cursor = mContext.getContentResolver().query(uris[0],
                filePathColumn, null, null, null);
        return cursor;
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        super.onPostExecute(cursor);
        cursor.moveToFirst();

        int columndataIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columndataIndex);
        Log.d("test", "picturePath: " + picturePath);

        int columndateIndex = cursor.getColumnIndex(filePathColumn[1]);
        long datePic = cursor.getLong(columndateIndex);
        Log.d("test", "date: " + datePic);
        String picTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(datePic));

        int columnlatIndex = cursor.getColumnIndex(filePathColumn[2]);
        double latPic = cursor.getDouble(columnlatIndex);
        Log.d("test", "date: " + latPic);

        int columnlngIndex = cursor.getColumnIndex(filePathColumn[3]);
        double lngPic = cursor.getDouble(columnlngIndex);
        Log.d("test", "date: " + lngPic);
        cursor.close();
        Location picLocation = new Location("");
        picLocation.setLatitude(latPic);
        picLocation.setLongitude(latPic);

        MediaStorePicEvent mediaStorePicEvent = new MediaStorePicEvent(picTimeStamp, picLocation,
                picturePath);
        EventBus.getDefault().post(mediaStorePicEvent);
    }
}
