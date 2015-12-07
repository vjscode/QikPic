package com.tuts.vijay.qikpic.application;

import com.flurry.android.FlurryAgent;
import com.parse.Parse;
import com.tuts.vijay.qikpic.module.AppContextModule;
import com.tuts.vijay.qikpic.module.DaggerQikPicComponent;
import com.tuts.vijay.qikpic.module.QikPicComponent;

/**
 * Created by vijay on 5/26/15.
 */
public class QikPicApplication extends android.app.Application {
    private static final String FLURRY_APIKEY = "";
    private static final String PARSE_APP_ID = "";
    private static final String PARSE_CLIENT_KEY = "";

    private static QikPicComponent qikPicComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeParse();
        //initializeFlurry();
        qikPicComponent = DaggerQikPicComponent.builder()
                .appContextModule(new AppContextModule(this))
                .build();
    }

    private void initializeFlurry() {
        // configure Flurry
        FlurryAgent.setLogEnabled(true);
        // init Flurry
        FlurryAgent.init(this, FLURRY_APIKEY);
    }

    private void initializeParse() {
        //Parse.enableLocalDatastore(this);
        //Add your parse credentials here
        Parse.initialize(this, PARSE_APP_ID,
                PARSE_CLIENT_KEY);

    }

    public static QikPicComponent getAppContextComponent() {
        return qikPicComponent;
    }
}
