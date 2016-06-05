package com.tuts.vijay.qikpic.application;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.flurry.android.FlurryAgent;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.tuts.vijay.qikpic.module.AppContextModule;
import com.tuts.vijay.qikpic.module.DaggerQikPicComponent;
import com.tuts.vijay.qikpic.module.DaggerTaskComponent;
import com.tuts.vijay.qikpic.module.DownloadTaskModule;
import com.tuts.vijay.qikpic.module.QikPicComponent;
import com.tuts.vijay.qikpic.module.TaskComponent;

/**
 * Created by vijay on 5/26/15.
 */
public class QikPicApplication extends android.app.Application implements com.aviary.android.feather.sdk.IAviaryClientCredentials {
    private static final String FLURRY_APIKEY = "";
    private static final String PARSE_APP_ID = "7Iax9O5yMbDA28MSwr3IV0BjgbUC29sY5I8Ygir0";
    private static final String PARSE_CLIENT_KEY = "Awk0EA8r9oN9dRZAMTWJ1mJvy8cBoqAfweDIeuG1";

    private static final String CREATIVE_SDK_CLIENT_ID = "d61263b966ce4ddc81341e0574a1d1bd";
    private static final String CREATIVE_SDK_CLIENT_SECRET = "5e120d0c-b7f0-4036-80bb-d5011f01b2af";

    private static QikPicComponent qikPicComponent;
    protected static TaskComponent taskComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeParse();
        //initializeFlurry();
        qikPicComponent = DaggerQikPicComponent.builder()
                .appContextModule(new AppContextModule(this))
                .build();
        taskComponent = DaggerTaskComponent.builder()
                .downloadTaskModule((new DownloadTaskModule()))
                .build();
        initializeCreativeSDK();
        initializeLeakCanary();
        initImageLoader();
    }

    /*public static RefWatcher getRefWatcher(Context context) {
        QikPicApplication application = (QikPicApplication) context.getApplicationContext();
        return application.refWatcher;
    }*/

    //private RefWatcher refWatcher;


    private void initializeLeakCanary() {
        //refWatcher = LeakCanary.install(this);
    }

    private void initializeCreativeSDK() {
        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());
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

    public static TaskComponent getTaskComponent() {
        return taskComponent;
    }

    @Override
    public String getClientID() {
        return CREATIVE_SDK_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_CLIENT_SECRET;
    }

    @Override
    public String getBillingKey() {
        return "";
    }

    private void initImageLoader() {
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);
    }
}
