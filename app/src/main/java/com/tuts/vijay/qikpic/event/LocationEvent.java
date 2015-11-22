package com.tuts.vijay.qikpic.event;

import android.location.Location;

/**
 * Created by vijay on 11/21/15.
 */
public class LocationEvent {
    public Location loc;

    public LocationEvent(Location loc) {
        this.loc = loc;
    }
}
