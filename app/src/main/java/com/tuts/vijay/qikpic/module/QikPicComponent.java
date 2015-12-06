package com.tuts.vijay.qikpic.module;

import com.tuts.vijay.qikpic.async.DownloadTask;
import com.tuts.vijay.qikpic.async.LoadPicFromMediaStoreTask;
import com.tuts.vijay.qikpic.async.UploadTask;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by vijay on 12/5/15.
 */
@Singleton
@Component(modules={AppContextModule.class})
public interface QikPicComponent {
    void inject(DownloadTask downloadTask);
    void inject(UploadTask uploadTask);
    void inject(LoadPicFromMediaStoreTask loadPicFromMediaStoreTask);
}