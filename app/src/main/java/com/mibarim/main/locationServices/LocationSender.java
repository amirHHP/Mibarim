package com.mibarim.main.locationServices;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.mibarim.main.core.Constants;
import com.mibarim.main.util.DynamicJsonConverter;
import com.mibarim.main.util.SafeAsyncTask;

import retrofit.RestAdapter;

/**
 * Created by Hamed on 9/25/2015.
 */
public class LocationSender extends Service {
    static final String TAG = "LocationSender";


    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public LocationSender getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationSender.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        if (intent!=null && intent.getStringExtra("Mobile") != null) {
            {
                String mobile = intent.getStringExtra("Mobile");
                double latitude = intent.getDoubleExtra("Latitude", 0);
                double longitude = intent.getDoubleExtra("Longitude", 0);
                SendLocation(mobile,longitude,latitude);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void SendLocation(String mobile, double longitude, double latitude) {
        String longitudeVal = String.valueOf(longitude);
        String latitudeVal = String.valueOf(latitude);
        SendLocationToServer(mobile, longitudeVal, latitudeVal);
        //sendingTask.execute(URL, longitudeVal, latitudeVal);
    }


    private void SendLocationToServer(final String mobile, final String longitude, final String latitude) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (isInternetOn()) {
                    RestAdapter rest = new RestAdapter.Builder()
                            .setEndpoint(Constants.Http.URL_BASE)
                            .setLogLevel(RestAdapter.LogLevel.FULL)
                            .setConverter(new DynamicJsonConverter())
                            .build();
                    /*TripService tripService= new TripService(rest);
                    tripService.sendUserLocation(mobile, latitude, longitude);*/
                    return true;
                }
                return false;
            }


            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                Log.d(TAG, "Exception" + e.toString());
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
            }
        }.execute();
    }

    public final boolean isInternetOn() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
