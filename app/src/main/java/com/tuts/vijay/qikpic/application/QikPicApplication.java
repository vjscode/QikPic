package com.tuts.vijay.qikpic.application;

import com.parse.Parse;

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
        //Add your parse credentials here
        Parse.initialize(this, "",
                "");

    }
}