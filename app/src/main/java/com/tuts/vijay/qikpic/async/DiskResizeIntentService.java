package com.tuts.vijay.qikpic.async;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.tuts.vijay.qikpic.db.QikPikContentProvider;
import com.tuts.vijay.qikpic.event.ScrollEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by vijay on 11/17/15.
 */
public class DiskResizeIntentService extends IntentService {

    private int firstVisiblePos;
    private int numberOfVisItems;

    public DiskResizeIntentService() {
        super("DiskResizeIntentService");
        EventBus.getDefault().register(this);
        Log.d("test", "one");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("test", "two");

        //Dont resize when user is actively engaging with qikpics
        if (firstVisiblePos + numberOfVisItems >= 100) {
            return;
        }

        Log.d("test", "three");

        Cursor c = getContentResolver().query(QikPikContentProvider.CONTENT_URI,
                new String[]{"_id", "objectId", "qikpicId", "createdAt", "updatedAt"}, "draft=?", new String[]{"0"}, "updatedAt ASC");
        c.moveToNext();
        int count = c.getCount();

        //check count > 100
        if (count < 100) {
            Log.d("test", "four");
            return;
        }

        //remove
        int countToBeRemoved = count - 100;
        while (countToBeRemoved > 0) {
            getContentResolver().delete(QikPikContentProvider.CONTENT_URI, "qikpicId=?", new String[]{c.getColumnName(c.getColumnIndex("_id"))});
            countToBeRemoved--;
        }

        EventBus.getDefault().unregister(this);
    }

    public void onEvent(ScrollEvent event) {
        firstVisiblePos = event.firstVisiblePos;
        numberOfVisItems = event.numberOfVisItems;
    }

}
