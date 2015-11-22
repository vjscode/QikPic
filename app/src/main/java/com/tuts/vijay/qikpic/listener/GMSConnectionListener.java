package com.tuts.vijay.qikpic.listener;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.tuts.vijay.qikpic.event.LocationEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by vijay on 11/21/15.
 */
public class GMSConnectionListener implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    public GMSConnectionListener() {
    }

    public void setmGoogleApiClient(GoogleApiClient googleApiClient) {
        this.mGoogleApiClient = googleApiClient;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        EventBus.getDefault().post(new LocationEvent(mLastLocation));
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        EventBus.getDefault().post(new LocationEvent(null));
    }
}
