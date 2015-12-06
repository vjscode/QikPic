package com.tuts.vijay.qikpic.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by vijay on 12/5/15.
 */
@Module
public class AppContextModule {

    Application mApplication;

    public AppContextModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Context providesApplication() {
        return mApplication;
    }
}