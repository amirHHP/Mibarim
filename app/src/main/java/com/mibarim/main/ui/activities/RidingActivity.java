package com.mibarim.main.ui.activities;


import android.accounts.OperationCanceledException;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.ArrayRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.BootstrapServiceProvider;
import com.mibarim.main.R;
import com.mibarim.main.authenticator.TokenRefreshActivity;
import com.mibarim.main.core.Constants;
import com.mibarim.main.core.ImageUtils;
import com.mibarim.main.core.LocationService;
import com.mibarim.main.data.UserData;
import com.mibarim.main.events.UnAuthorizedErrorEvent;
import com.mibarim.main.locationServices.GoogleLocationService;
import com.mibarim.main.models.Location;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.ImageResponse;
import com.mibarim.main.models.Plus.PassRouteModel;
import com.mibarim.main.models.enums.TripStates;
import com.mibarim.main.services.AuthenticateService;
import com.mibarim.main.services.RouteResponseService;
import com.mibarim.main.services.UserInfoService;
import com.mibarim.main.ui.BootstrapActivity;
import com.mibarim.main.ui.HandleApiMessages;
import com.mibarim.main.ui.fragments.MapFragment;
import com.mibarim.main.ui.fragments.RouteFilterFragment;
import com.mibarim.main.util.SafeAsyncTask;
import com.squareup.otto.Subscribe;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

//import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;


/**
 * Initial activity for the application.
 * * <p/>
 * If you need to remove the authentication from the application please see
 */
public class RidingActivity extends BootstrapActivity {
    static final String TAG = "RidingActivity";

    @Inject
    BootstrapServiceProvider serviceProvider;
    @Inject
    protected RouteResponseService routeResponseService;
    @Inject
    UserInfoService userInfoService;
    @Inject
    UserData userData;


    @Bind(R.id.station_add)
    protected TextView station_add;
    @Bind(R.id.carString)
    protected TextView carString;
    @Bind(R.id.username)
    protected TextView username;
    /*
        @Bind(R.id.enable_text)
        protected TextView enable_text;
    */
    @Bind(R.id.trip_time)
    protected TextView trip_time;
    @Bind(R.id.call_driver)
    protected TextView call_driver;
    @Bind(R.id.cancel_trip)
    protected TextView cancel_trip;
    @Bind(R.id.userimage)
    protected BootstrapCircleThumbnail userimage;
    /* @Bind(R.id.call_btn)
     protected AppCompatButton call_btn;*/
    @Bind(R.id.map_container)
    protected FrameLayout map_container;
    @Bind(R.id.map_container_web)
    protected WebView map_container_web;
    @Bind(R.id.pay)
    protected TextView pay;


    Intent googleServiceIntent;
    Intent serviceIntent;
    GoogleLocationService mService;
    boolean mBound = false;
    private Tracker mTracker;
    //private TextView cancel_btn;
    private ImageView support;

    private String authToken;
    private int RELOAD_REQUEST = 1234;
    private Toolbar toolbar;
    private PassRouteModel passTripModel;
    private PassRouteModel cancelPassTripModel;
    private int delay = 10000;
    private ApiResponse tripApiResponse;
    private ApiResponse canceltripApiResponse;
    private ApiResponse endTripApiResponse;
    private PassRouteModel tripResponse;
    private int endTripResult;
    private int LocationServiceInUse = 1;// GOOGLE=1 LOCAL_SERVICE=2
    private ImageResponse imageResponse;
    private AlertDialog gpsAlert;
    private Location driverLocation;
    private int FINISH_PAYMENT = 5659;
    //private int stationDistance = 500;


    private Handler mHandler;
    private Runnable mRunnable;
    private Handler endHandler;
    private Runnable endRunnable;
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);

        SharedPreferences sharedPreferences = getSharedPreferences(
                "com.mibarim.main", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("AllowBackButton", 1).apply();

        if (getIntent() != null && getIntent().getExtras() != null) {
            authToken = getIntent().getExtras().getString(Constants.Auth.AUTH_TOKEN);
            passTripModel = (PassRouteModel) getIntent().getExtras().getSerializable(Constants.GlobalConstants.PASS_ROUTE_MODEL);

            //if this page is shown for trip. don't show it again before trip
            SharedPreferences prefs = this.getSharedPreferences(
                    "com.mibarim.main", Context.MODE_PRIVATE);
            prefs.edit().putLong("FirstRidingShow", passTripModel.TripId).apply();
        }

        BootstrapApplication application = (BootstrapApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("RidingActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Activity").setAction("RidingActivity").build());


        setContentView(R.layout.riding_activity);

        // View injection with Butterknife
        ButterKnife.bind(this);

        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }


        toolbar = (Toolbar) findViewById(R.id.ride_toolbar);
        setSupportActionBar(toolbar);
        //cancel_btn = (TextView) toolbar.findViewById(R.id.trip_cancel);
        support = (ImageView) toolbar.findViewById(R.id.support);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (Build.VERSION.SDK_INT >= 17) {
                actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu_forward);
            }
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        serviceIntent = new Intent(this, LocationService.class);
        googleServiceIntent = new Intent(this, GoogleLocationService.class);
        map_container_web.setVisibility(View.GONE);
        map_container.setVisibility(View.GONE);
        call_driver.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (passTripModel.TripState == TripStates.Scheduled.toInt()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RidingActivity.this);
                        builder.setMessage(getString(R.string.enable_call)).setPositiveButton("باشه", dialogClickListener).show();
                    } else {
                        call(passTripModel.MobileNo);
                        //call_driver.setEnabled(false);
                    }
                    return true;
                }
                return false;
            }
        });
        cancel_trip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cancelPassTripModel = passTripModel;
                    showDialog(getString(R.string.sure_disable));
                    return true;
                }
                return false;
            }
        });
        support.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    call("02128426784");
                    return true;
                }
                return false;
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPayActivity(passTripModel);
            }
        });
        driverLocation = new Location();
        initScreen();
    }


    private void initScreen() {
        station_add.setText(passTripModel.SrcAddress);
        carString.setText(passTripModel.CarString + " " + passTripModel.CarPlate);
        username.setText(passTripModel.Name + " " + passTripModel.Family);
        trip_time.setText(passTripModel.TimingString);
        userimage.setImageBitmap(getImageById(passTripModel.UserImageId, R.mipmap.ic_user_black));
        if (isPlayServicesConfigured()) {
            map_container.setVisibility(View.VISIBLE);
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.map_container, new MapFragment())
                    .commitAllowingStateLoss();
        } else {
            map_container_web.setVisibility(View.VISIBLE);
            map_container_web.loadUrl(passTripModel.SrcLink);
            map_container_web.getSettings().setDomStorageEnabled(true);
            map_container_web.getSettings().setJavaScriptEnabled(true);
        }
        /*if (passTripModel.TripState == TripStates.InPreTripTime.toInt() ||
                passTripModel.TripState == TripStates.InRiding.toInt() ||
                passTripModel.TripState == TripStates.InTripTime.toInt()) {
            call_driver.setEnabled(true);
        }else{
            call_driver.setEnabled(false);
        }*/
        periodicReLoading();
        locationReloading();
        finishRiding();
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finishIt();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishIt();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mHandler.removeCallbacks(mRunnable);
        }
        return super.onKeyUp(keyCode, event);
    }*/

    private void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setPositiveButton("بله", cancelDialogClickListener).setNegativeButton("نه", cancelDialogClickListener).show();
    }

    public void call(String tel) {
        sendTripPoint(TripStates.PassengerCall.toInt());
        if (tel != null) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + tel));
            startActivity(intent);
        }
    }

    private void sendTripPoint(final int tripState) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                tripApiResponse = routeResponseService.setTripPoint(authToken, getLocation().lat, getLocation().lng, passTripModel.TripId, tripState);
                if (tripApiResponse != null) {
                    for (String tripJson : tripApiResponse.Messages) {
                        tripResponse = new Gson().fromJson(tripJson, PassRouteModel.class);
                    }
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                //hideProgress();
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                if (tripResponse.SrcLatitude != null) {
                    driverLocation.lat = tripResponse.SrcLatitude;
                    driverLocation.lng = tripResponse.SrcLongitude;
                }
            }
        }.execute();
    }


    private void periodicReLoading() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                reloadThread();
                mHandler.postDelayed(this, 20000);
            }
        };
        worker.schedule(mRunnable, 10, TimeUnit.SECONDS);
    }

    private void finishRiding() {
        endRunnable = new Runnable() {
            @Override
            public void run() {
                finishIt();
            }
        };
        worker.schedule(endRunnable, 10, TimeUnit.MINUTES);
    }


    public void reloadThread() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean isMsgSubmitted) throws Exception {
                super.onSuccess(isMsgSubmitted);
                locationReloading();
            }
        }.execute();

    }


    private void checkNetwork() {
        if (!isNetworkAvailable()) {
            if (gpsAlert == null || !gpsAlert.isShowing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String msg = "لطفا ارتباط اینترنت را فعال کنید";
                builder.setMessage(msg).setPositiveButton("باشه", dataClickListener);
                gpsAlert = builder.create();
                gpsAlert.show();
            }
        }
    }

    DialogInterface.OnClickListener dataClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    private void showTripInfo() {
        Location loc = getLocation();
        if (loc != null) {
            showOnMap(loc);
            sendTripPoint(passTripModel.TripState);
        }
    }

    private void showStation() {
        Location loc = new Location();
        loc.lat = null;
        loc.lng = null;
        driverLocation.lat = passTripModel.SrcLatitude;
        driverLocation.lng = passTripModel.SrcLongitude;
        showOnMap(loc);
    }

    private void showOnMap(Location loc) {
        if (isPlayServicesConfigured()) {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.map_container);
            if (fragment != null) {
                ((MapFragment) fragment).setStation(driverLocation.lat, driverLocation.lng, loc.lat, loc.lng);
            }
        }
    }

    private void locationReloading() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = "لطفا مکان یاب تلفن همراه را روشن کنید";
        if (passTripModel.TripState == TripStates.InPreTripTime.toInt() ||
                passTripModel.TripState == TripStates.InRiding.toInt() ||
                passTripModel.TripState == TripStates.InTripTime.toInt()) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                android.location.Location location = LocationService.getLocationManager(this).getLocation();
                if (location != null) {
                    if (gpsAlert != null && gpsAlert.isShowing()) {
                        gpsAlert.dismiss();
                    }
                    showTripInfo();
                    checkNetwork();
                }
            /*else {
                if (gpsAlert == null || !gpsAlert.isShowing()) {
                    builder.setMessage(msg).setPositiveButton("باشه", gpsClickListener);
                    gpsAlert = builder.create();
                    gpsAlert.show();
                }
            }*/

            } else {
                if (gpsAlert == null || !gpsAlert.isShowing()) {
                    builder.setMessage(msg).setPositiveButton("باشه", gpsClickListener);
                    gpsAlert = builder.create();
                    gpsAlert.show();
                }
            }
        } else {
            showStation();
        }
    }

    DialogInterface.OnClickListener gpsClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    turnOnGps();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    //finishAffinity();
                    break;
            }
        }
    };

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    //finishAffinity();
                    break;
            }
        }
    };

    DialogInterface.OnClickListener cancelDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    cancelTrip(cancelPassTripModel);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    //finishAffinity();
                    break;
            }
        }
    };


    /**
     * Hide progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void hideProgress() {
        dismissDialog(0);
    }

    /**
     * Show progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void showProgress() {
        showDialog(0);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.please_wait));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    public void turnOnGps() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void startLocationService() {
        boolean IsGoogleServiceSupported = false;
        if (isPlayServicesConfigured()) {
            //bind to google location
            try {
                startService(googleServiceIntent);
                bindService(googleServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
                IsGoogleServiceSupported = true;
                LocationServiceInUse = 1;
            } catch (Exception e) {
                IsGoogleServiceSupported = false;
            }
        } else {
            IsGoogleServiceSupported = false;
        }
        if (!IsGoogleServiceSupported) {
            //bind to manual location provider
            startService(serviceIntent);
            bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
            LocationServiceInUse = 2;
        }
    }

    public void stopService() {
        unbindService(mConnection);
        if (LocationServiceInUse == 1) {
            stopService(googleServiceIntent);
        } else if (LocationServiceInUse == 2) {
            stopService(serviceIntent);
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GoogleLocationService.LocalBinder binder = (GoogleLocationService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private boolean isPlayServicesConfigured() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getApplicationContext());
        if (status == ConnectionResult.SUCCESS)
            return true;
        else {
            Log.d(TAG, "Error connecting with Google Play services. Code: " + String.valueOf(status));
            return false;
        }
    }

    public Location getLocation() {
        Location point = new Location();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            android.location.Location location = LocationService.getLocationManager(this).getLocation();
            if (location != null) {
                point.lat = String.valueOf(location.getLatitude());
                point.lng = String.valueOf(location.getLongitude());
            }
        }
        return point;
    }

    public void gotoMyLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            android.location.Location location = LocationService.getLocationManager(this).getLocation();
            if (location != null) {
                final FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.main_container);
                //((MainTripFragment) fragment).MoveMap(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
            }
        } else {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    public Bitmap getImageById(String imageId, int defaultDrawableId) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), defaultDrawableId);
        if (imageId == null || imageId.equals("") || imageId.equals("00000000-0000-0000-0000-000000000000")) {
            return icon;
        }
        ImageResponse imageResponse = userData.imageQuery(imageId);
        if (imageResponse != null) {
            Bitmap b = ImageUtils.loadImageFromStorage(imageResponse.ImageFilePath, imageResponse.ImageId);
            if (b != null) {
                return b;
            } else {
                getImageFromServer(imageId);
            }
        } else {
            getImageFromServer(imageId);
        }
        return icon;
    }

    private void getImageFromServer(final String imageId) {
        new SafeAsyncTask<Boolean>() {
            ImageResponse imageResponse = new ImageResponse();
            Bitmap decodedByte;

            @Override
            public Boolean call() throws Exception {
                String token = serviceProvider.getAuthToken(RidingActivity.this);
                imageResponse = userInfoService.GetImageById(token, imageId);
                if (imageResponse != null && imageResponse.Base64ImageFile != null) {
                    return true;
                }
                return false;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof android.os.OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
//                    finish();
                }
            }

            @Override
            protected void onSuccess(final Boolean imageLoaded) throws Exception {
                if (imageLoaded) {
                    byte[] decodedString = Base64.decode(imageResponse.Base64ImageFile, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    String path = ImageUtils.saveImageToInternalStorage(getApplicationContext(), decodedByte, imageResponse.ImageId);
                    userData.insertImage(imageResponse, path);
                }
            }
        }.execute();
    }


    private void cancelTrip(final PassRouteModel passTripModel) {
        showProgress();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return routeResponseService.cancelTrip(authToken, passTripModel.TripId);
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                hideProgress();
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                hideProgress();
                finishIt();
            }
        }.execute();
    }

    private void finishIt() {
        mHandler.removeCallbacks(mRunnable);
        LocationService.destroy(RidingActivity.this);
        Intent i = getIntent();
        setResult(RESULT_OK, i);
        finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void gotoPayActivity(final PassRouteModel selectedItem) {
        Intent intent = new Intent(this, PayActivity.class);
        intent.putExtra(Constants.GlobalConstants.PASS_ROUTE_MODEL, selectedItem);
        intent.putExtra(Constants.Auth.AUTH_TOKEN, authToken);
        this.startActivityForResult(intent, FINISH_PAYMENT);
    }





}
