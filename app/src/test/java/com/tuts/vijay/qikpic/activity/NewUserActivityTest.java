package com.tuts.vijay.qikpic.activity;

import android.app.Activity;
import android.widget.Button;

import com.tuts.vijay.qikpic.BuildConfig;
import com.tuts.vijay.qikpic.R;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NewUserActivityTest {

    @Test
    public void onCreate_InitialUI() throws Exception {
        System.out.print("hello");
        Activity activity = Robolectric.buildActivity(NewUserActivity.class).create().get();
        Button setUp = (Button) shadowOf(activity).findViewById(R.id.signup_button);
        Button logIn = (Button) shadowOf(activity).findViewById(R.id.login_button);
        Assert.assertEquals(setUp.getText(), "Signup");
        Assert.assertEquals(logIn.getText(), "Login");
    }

    @Test
    public void testVisibility() throws Exception {
        Activity activity = Robolectric.buildActivity(NewUserActivity.class).create().start().resume().visible().get();
        Button setUp = (Button) shadowOf(activity).findViewById(R.id.signup_button);
        Button logIn = (Button) shadowOf(activity).findViewById(R.id.login_button);
        Assert.assertEquals(setUp.getVisibility(), 0);
        Assert.assertEquals(logIn.getVisibility(), 0);
    }
}