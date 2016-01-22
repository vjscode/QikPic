package com.tuts.vijay.qikpic.activity;

import android.widget.AbsListView;
import android.widget.CursorAdapter;

import com.tuts.vijay.qikpic.BuildConfig;
import com.tuts.vijay.qikpic.application.TestQikPicApplication;
import com.tuts.vijay.qikpic.async.DownloadTask;
import com.tuts.vijay.qikpic.listener.ScrollListener;
import com.tuts.vijay.qikpic.module.DaggerTaskComponent;
import com.tuts.vijay.qikpic.module.DownloadTaskModule;
import com.tuts.vijay.qikpic.module.TaskComponent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import dagger.Provides;

/**
 * Created by vijay on 1/2/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, application = TestQikPicApplication.class)
public class ScrollListenerTest {

    DownloadTask mockTask;
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockTask = Mockito.mock(DownloadTask.class);
        TaskComponent c = DaggerTaskComponent.builder()
                .downloadTaskModule(new DownloadTaskModule() {
                    @Provides
                    @Override
                    public DownloadTask providesDownloadTask() {
                        return mockTask;
                    }
                })
                .build();
        ((TestQikPicApplication) RuntimeEnvironment.application).setTestComponent(c);
    }

    @Test
    public void testDownloadTaskFired() {
        CursorAdapter mockCursorAdapter = Mockito.mock(CursorAdapter.class);
        ScrollListener lis = new ScrollListener(mockCursorAdapter, null);
        lis.onScroll(Mockito.mock(AbsListView.class),
                0, 0, 0);
        //check if fetch is called
        Mockito.verify(mockTask).execute(0);
    }

}
