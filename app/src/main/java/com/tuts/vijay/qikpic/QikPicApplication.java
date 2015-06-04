package com.tuts.vijay.qikpic;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by vijay on 5/26/15.
 */
public class QikPicApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initializeParse();
    }

    private void initializeParse() {
        Parse.initialize(this, "omNT20gmamXM11mTtdIFpYnSiKyLbZPaIMxFzqDI",
                "6Z8MfHJLE4UKhnPxkoiZYMXDqoMCVehkJi375Nut");

    }
}