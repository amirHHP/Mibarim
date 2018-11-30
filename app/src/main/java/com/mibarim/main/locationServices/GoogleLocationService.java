package com.mibarim.main.locationServices;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Hamed on 9/25/2015.
 */
public class GoogleLocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    static final String TAG = "GoogleLocationService";
    private static final long UPDATE_INTERVAL_SECOND = 30;
    private static final long FASTEST_INTERVAL_SECOND = 15;
    GoogleApiClient mGoogleApiClient;
    Intent locationSenderServiceIntent;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    long mLastUpdateTime;


    @Override
    public boolean onUnbind(Intent intent) {
        stopLocationUpdates();
        return super.onUnbind(intent);
    }

    private boolean _isConnected = false;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(this, "google Service Location Changed", Toast.LENGTH_SHORT).show();
        mLastLocation = location;
        mLastUpdateTime = Calendar.getInstance().getTimeInMillis();
        SendLocationInfo(location);
    }


    public class LocalBinder extends Binder {
        public GoogleLocationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return GoogleLocationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(this, "google Service create", Toast.LENGTH_SHORT).show();
        buildGoogleApiClient();
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Toast.makeText(this, "google Service Connected", Toast.LENGTH_SHORT).show();
        _isConnected = true;
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        mLastUpdateTime = Calendar.getInstance().getTimeInMillis();
        if (mGoogleApiClient.isConnected()) {
            createLocationRequest();
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "google Service Connection Failed" + connectionResult, Toast.LENGTH_LONG).show();
    }

    public Location getLastLocation() {
        if (mLastLocation != null) {
            return mLastLocation;
        }
        return null;
    }

    public boolean IsConnected() {
        return _isConnected;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_SECOND * 1000);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL_SECOND * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        //Toast.makeText(this, "google Service Update Request created", Toast.LENGTH_SHORT).show();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    private void SendLocationInfo(Location location) {
        Intent intent = new Intent("LOCATION_RECEIVER_INTENT");
        intent.putExtra("Latitude",location.getLatitude());
        intent.putExtra("Longitude",location.getLongitude());
        sendBroadcast(intent);
    }
}
