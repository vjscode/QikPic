package com.tuts.vijay.qikpic.async;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tuts.vijay.qikpic.application.QikPicApplication;
import com.tuts.vijay.qikpic.db.QikPikContentProvider;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by vijay on 10/8/15.
 */
public class UploadTask extends AsyncTask<Void, Void, Void> {

    @Inject Context context;
    public UploadTask() {
        QikPicApplication.getAppContextComponent().inject(this);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //android.net.Uri uri, java.lang.String[] projection, java.lang.String selection, java.lang.String[] selectionArgs, java.lang.String sortOrder
        Cursor c = context.getContentResolver().query(QikPikContentProvider.CONTENT_URI,
                new String[]{"objectId", "qikpicId", "image", "userId", "createdAt", "updatedAt", "tags", "thumbnail", "lat", "lng"}, "draft=?", new String[]{"1"}, "updatedAt DESC");
        while (c.moveToNext()) {
            String objectId = c.getString(c.getColumnIndex("objectId"));
            if (objectId == null) {
                makeParseObject(
                        objectId,
                        c.getString(c.getColumnIndex("qikpicId")),
                        c.getString(c.getColumnIndex("image")),
                        c.getString(c.getColumnIndex("userId")),
                        c.getString(c.getColumnIndex("createdAt")),
                        c.getString(c.getColumnIndex("updatedAt")),
                        c.getString(c.getColumnIndex("tags")),
                        c.getString(c.getColumnIndex("thumbnail")),
                        c.getString(c.getColumnIndex("lat")),
                        c.getString(c.getColumnIndex("lng")));
            } else {
                ParseQuery<ParseObject> pq = ParseQuery.getQuery("QikPik");
                pq.whereEqualTo("objectId", objectId);
                try {
                    List<ParseObject> list = pq.find();
                    if (list.size() > 0) {
                        ParseObject row = list.get(0);
                        ParseFile[] imgFile = createParseFilesFromFile(c.getString(c.getColumnIndex("image")),
                                c.getString(c.getColumnIndex("thumbnail")), c.getString(c.getColumnIndex("userId")));
                        row.put("tags", loadTags(c.getString(c.getColumnIndex("tags"))));
                        row.put("image", imgFile[0]);
                        row.put("thumbnail", imgFile[1]);
                        row.save();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void updateLocal(String objectId, String qikpicId) {
        ContentValues values = new ContentValues();
        values.put("draft", 0);
        Log.d("test", "oid: " + objectId);
        values.put("objectId", objectId);
        int count = context.getContentResolver().update(QikPikContentProvider.CONTENT_URI,
                values, "qikpicId=?", new String[]{qikpicId});
        Log.d("test", "updated rows: " + count);
    }

    private void makeParseObject(String objectId, String qikpicId, String image, String userId, String createdAt,
                                 String updateAt, String tags, String thumbnail, String lat, String lng) {

            ParseFile[] imageFiles = createParseFilesFromFile(image, thumbnail, userId);

            //thumbnailPFile.save();
            ParseUser pu = ParseUser.getCurrentUser();
            final ParseObject po = new ParseObject("QikPik");
            if (objectId != null) {
                po.put("objectId", objectId);
            }
            po.put("qikpicId", qikpicId);
            po.put("image", imageFiles[0]);

            po.put("user", pu);
            po.put("created", Long.valueOf(createdAt));
            po.put("updated", Long.valueOf(updateAt));
            po.put("tags", loadTags(tags));
            po.put("thumbnail", imageFiles[1]);
            if (lat != null && lng != null) {
                po.put("lat", lat);
                po.put("lng", lng);
            }
            po.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    updateLocal(po.getObjectId(), po.getString("qikpicId"));

                }
            });
    }

    private ParseFile[] createParseFilesFromFile(String image, String thumbnail, String userId) {
        ParseFile[] result = new ParseFile[2];
        try {
            //read image file
            File imageFile = new File(image);
            FileInputStream fis = new FileInputStream(imageFile);
            byte[] imgBytes = IOUtils.toByteArray(fis);
            ParseFile imgPFile = new ParseFile("full_" + userId + ".jpg", imgBytes);
            //imgPFile.save();

            //read thumbnail
            File thumbnailFile = new File(thumbnail);
            fis = new FileInputStream(thumbnailFile);
            byte[] thumbnailBytes = IOUtils.toByteArray(fis);
            ParseFile thumbnailPFile = new ParseFile("thumbnail_" + userId + ".jpg", thumbnailBytes);


            result[0] = imgPFile;
            result[1] = thumbnailPFile;
        }  catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return result;
    }

    private void updateParseObject(ParseObject po) {

    }


    private ArrayList<String> loadTags(String tagStr) {
        ArrayList<String> resList = new ArrayList<String>();
        String[] tags = tagStr.split(",");
        for (String tag : tags) {
            if (!tag.equals("")) {
                resList.add(tag);
            }
        }
        return resList;
    }
}
