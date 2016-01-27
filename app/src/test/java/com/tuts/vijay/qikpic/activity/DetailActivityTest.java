package com.tuts.vijay.qikpic.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.MenuItem;

import com.tuts.vijay.qikpic.BuildConfig;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.application.TestQikPicApplication;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;

import static org.robolectric.Shadows.shadowOf;

/**
 * Created by vijay on 1/27/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, application = TestQikPicApplication.class)
public class DetailActivityTest {
    @Test
    public void launchEditPhoto() throws Exception {
        Intent detailActivityIntent = getDetailActivityIntent();
        Activity activity = Robolectric.buildActivity(DetailActivity.class).withIntent(detailActivityIntent).create().get();
        MenuItem item = new RoboMenuItem(R.id.action_edit);
        activity.onOptionsItemSelected(item);

        Intent expectedIntent = new Intent("aviary.intent.action.EDIT");
        Intent actualIntent = shadowOf(activity).getNextStartedActivity();
        boolean result = expectedIntent.getAction() == actualIntent.getAction();
        Assert.assertEquals(true, result);
    }

    private Intent getDetailActivityIntent() {
        Intent i = new Intent(RuntimeEnvironment.application, DetailActivity.class);
        i.putExtra("oldOrNew", "new");
        i.putExtra("uri", Uri.parse("/data/data/com.tuts.vijay.qikpic/files/full/full_RZTbbZZut1.jpg"));
        i.putExtra("thumbnailname", 999);
        i.putExtra("location", (Parcelable) null);
        return i;
    }
}