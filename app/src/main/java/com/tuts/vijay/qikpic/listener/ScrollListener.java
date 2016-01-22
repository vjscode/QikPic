package com.tuts.vijay.qikpic.listener;

import android.util.Log;
import android.widget.AbsListView;
import android.widget.CursorAdapter;

import com.tuts.vijay.qikpic.application.QikPicApplication;
import com.tuts.vijay.qikpic.async.DownloadTask;
import com.tuts.vijay.qikpic.db.QikPikContentProvider;
import com.tuts.vijay.qikpic.event.ScrollEvent;

import javax.inject.Inject;
import javax.inject.Provider;

import de.greenrobot.event.EventBus;

/**
 * Created by vijay on 9/20/15.
 */
public class ScrollListener implements AbsListView.OnScrollListener {

    @Inject
    Provider<DownloadTask> downloadTaskProvider;

    CursorAdapter adapter;
    boolean isLoading = false;

    public ScrollListener(CursorAdapter adapter, QikPikContentProvider provider) {
        this.adapter = adapter;
        QikPicApplication.getTaskComponent().inject(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        if (adapter == null)
            return ;

        Log.d("scroll", "first: " + firstVisibleItem + ", vis: " + visibleItemCount + ", total: " + totalItemCount);
        EventBus.getDefault().post(new ScrollEvent(firstVisibleItem, visibleItemCount));

        int l = visibleItemCount + firstVisibleItem;
        if (l >= totalItemCount && !isLoading) {
            isLoading = true;
            Log.d("test", "loading data!!!: " + l);
            DownloadTask dTask = createDownloadTask();
            dTask.execute(l);
        }
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }

    public DownloadTask createDownloadTask() {
        DownloadTask dt = downloadTaskProvider.get();
        dt.setScrollListener(this);
        return dt;
    }
}
