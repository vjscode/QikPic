package com.tuts.vijay.qikpic.application;

import com.flurry.android.FlurryAgent;
import com.parse.Parse;

/**
 * Created by vijay on 5/26/15.
 */
public class QikPicApplication extends android.app.Application {
    private static final String FLURRY_APIKEY = "";
    private static final String PARSE_APP_ID = "";
    private static final String PARSE_CLIENT_KEY = "";

    @Override
    public void onCreate() {
        super.onCreate();
        initializeParse();
        //initializeFlurry();
    }

    private void initializeFlurry() {
        // configure Flurry
        FlurryAgent.setLogEnabled(true);
        // init Flurry
        FlurryAgent.init(this, FLURRY_APIKEY);
    }

    private void initializeParse() {
        Parse.enableLocalDatastore(this);
        //Add your parse credentials here
        Parse.initialize(this, PARSE_APP_ID,
                PARSE_CLIENT_KEY);

    }
}
