package com.tuts.vijay.qikpic.async;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.tuts.vijay.qikpic.db.QikPikContentProvider;

/**
 * Created by vijay on 11/15/15.
 */
public class DiskResizeTask extends AsyncTask<Void, Void, Void> {

    private Context context;

    public DiskResizeTask(Context context) {
        this.context = context;
    }
    @Override
    protected Void doInBackground(Void... voids) {

        Cursor c = context.getContentResolver().query(QikPikContentProvider.CONTENT_URI,
                new String[]{"_id", "objectId", "qikpicId", "createdAt", "updatedAt"}, "draft=?", new String[]{"0"}, "updatedAt ASC");
        c.moveToNext();
        int count = c.getCount();

        //check count > 100
        if (count < 100) {
            return null;
        }

        //remove
        int countToBeRemoved = count - 100;
        while (countToBeRemoved > 0) {
            context.getContentResolver().delete(QikPikContentProvider.CONTENT_URI, "qikpicId=?", new String[]{c.getColumnName(c.getColumnIndex("_id"))});
            countToBeRemoved--;
        }

        return null;
    }
}
