package com.tuts.vijay.qikpic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseUser;


public class DispatchActivity extends Activity {

    public DispatchActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
            //startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "log in", Toast.LENGTH_SHORT).show();
        } else {
            // Start and intent for the logged out activity
            //startActivity(new Intent(this, WelcomeActivity.class));
            Toast.makeText(this, "log out", Toast.LENGTH_SHORT).show();
        }
    }

}