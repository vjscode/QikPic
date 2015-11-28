package com.tuts.vijay.qikpic.event;

import android.location.Location;

/**
 * Created by vijay on 11/28/15.
 */
public class MediaStorePicEvent {
    public String timeStamp;
    public Location photoLocation;
    public String picturePath;

    public MediaStorePicEvent(String ts, Location loc, String picturePath) {
        this.timeStamp = ts;
        this.photoLocation = loc;
        this.picturePath = picturePath;
    }
}
