package com.tuts.vijay.qikpic.module;

import com.tuts.vijay.qikpic.listener.ScrollListener;

import dagger.Component;

/**
 * Created by vijay on 1/5/16.
 */
@Component(modules={DownloadTaskModule.class})
public interface TaskComponent {
    void inject(ScrollListener listener);
}
