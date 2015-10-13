package com.tuts.vijay.qikpic.async;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.tuts.vijay.qikpic.db.QikPikContentProvider;
import com.tuts.vijay.qikpic.listener.ScrollListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by vijay on 9/20/15.
 */
public class DownloadTask extends AsyncTask<Integer, Void, Void> {

    Context context;
    ScrollListener listener;
    public DownloadTask(Context context, ScrollListener listener) {
        this.listener = listener;
        this.context = context;
    }

    public void fetch(int skip) {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("QikPik");
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.setLimit(5);
            query.addDescendingOrder("updatedAt");
            query.setSkip(skip);
            List<ParseObject> parseObjects = query.find();
            for (ParseObject po : parseObjects) {
                Log.d("test", "objectId: " + po.getObjectId());
                String[] filePaths = saveFiles(po);
                ContentValues values = createContentValues(po, filePaths);
                context.getContentResolver().insert(QikPikContentProvider.CONTENT_URI, values);
            }
        } catch (ParseException pe) {
            Log.d("test", "pe: " + pe);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d("test", "postexecute");
        listener.setLoading(false);
        super.onPostExecute(aVoid);
    }

    private ContentValues createContentValues(ParseObject po, String[] filePaths) {
        ContentValues values = new ContentValues();
        values.put("objectId", po.getObjectId());
        values.put("image", filePaths[0]);
        values.put("userId", po.getParseUser("user").getObjectId());
        values.put("createdAt", po.getCreatedAt().getTime());
        values.put("updatedAt", po.getCreatedAt().getTime());
        values.put("tags", getTagsAsString(po.getList("tags")));
        values.put("thumbnail", filePaths[1]);
        values.put("draft", 0);
        values.put("qikpicId", po.getString("qikpicId"));
        return values;
    }

    private String getTagsAsString(List<Object> tags) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            String str = tags.get(i).toString();
            sb.append(str+",");
        }
        if (sb.length() > 1) {
            sb = sb.deleteCharAt(sb.lastIndexOf(","));
        }
        return sb.toString();
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        fetch(integers[0]);
        return null;
    }

    private String[] saveFiles(ParseObject po) {
        String[] result = new String[2];
        try {
            Context c = context.getApplicationContext();
            String filesPath = c.getFilesDir().getPath();
            File full = new File(filesPath, "full");
            if (!full.exists()) {
                if (!full.mkdir()) {
                    Log.e("test", "could not create directory");
                }
            }
            File thumbnail = new File(filesPath, "thumbnail");
            if (!thumbnail.exists()) {
                if (!thumbnail.mkdir()) {
                    Log.e("test", "could not create directory");
                }
            }
            try {
                FileOutputStream fos = new FileOutputStream(full.getPath() + "/full_" + po.getObjectId() + ".jpg");
                fos.write(po.getParseFile("image").getData());
                fos.close();
                result[0] = full.getPath() + "/full_" + po.getObjectId() + ".jpg";
                fos = new FileOutputStream(thumbnail.getPath() + "/thumbnail_" + po.getObjectId() + ".jpg");
                fos.write(po.getParseFile("thumbnail").getData());
                fos.close();
                result[1] = thumbnail.getPath() + "/thumbnail_" + po.getObjectId() + ".jpg";
                return result;
            } catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
