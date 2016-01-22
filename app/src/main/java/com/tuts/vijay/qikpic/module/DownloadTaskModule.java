package com.tuts.vijay.qikpic.module;

import com.tuts.vijay.qikpic.async.DownloadTask;

import dagger.Module;
import dagger.Provides;

/**
 * Created by vijay on 1/5/16.
 */
@Module
public class DownloadTaskModule {
    @Provides
    public DownloadTask providesDownloadTask() {
        return new DownloadTask();
    }
}
