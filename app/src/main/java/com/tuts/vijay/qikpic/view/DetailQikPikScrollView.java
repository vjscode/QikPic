package com.tuts.vijay.qikpic.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by vijay on 7/14/15.
 */
public class DetailQikPikScrollView extends ScrollView {

    boolean mScrollable = true;
    public DetailQikPikScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DetailQikPikScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailQikPikScrollView(Context context) {
        super(context);
    }

    public void setScrollable(boolean scrollable) {
        this.mScrollable = scrollable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mScrollable) return false;
        return super.onInterceptTouchEvent(ev);
    }
}
