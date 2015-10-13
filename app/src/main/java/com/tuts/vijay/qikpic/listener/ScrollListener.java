package com.tuts.vijay.qikpic.listener;

import android.content.Context;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.CursorAdapter;

import com.tuts.vijay.qikpic.async.DownloadTask;
import com.tuts.vijay.qikpic.db.QikPikContentProvider;

/**
 * Created by vijay on 9/20/15.
 */
public class ScrollListener implements AbsListView.OnScrollListener {
    CursorAdapter adapter;
    Context context;
    boolean isLoading = false;

    public ScrollListener(CursorAdapter adapter, QikPikContentProvider provider, Context context) {
        this.adapter = adapter;
        this.context = context;
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

        int l = visibleItemCount + firstVisibleItem;
        if (l >= totalItemCount && !isLoading) {
            isLoading = true;
            Log.d("test", "loading data!!!");
            new DownloadTask(context, this).execute(totalItemCount);
        }
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }
}
