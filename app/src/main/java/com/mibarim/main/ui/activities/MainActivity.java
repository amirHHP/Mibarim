package com.mibarim.main.ui.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.os.OperationCanceledException;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.BootstrapServiceProvider;
import com.mibarim.main.R;
import com.mibarim.main.core.Constants;
import com.mibarim.main.core.ImageUtils;
import com.mibarim.main.data.UserData;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.ImageResponse;
import com.mibarim.main.models.InviteModel;
import com.mibarim.main.models.Plus.PassRouteModel;
import com.mibarim.main.models.UserInfoModel;
import com.mibarim.main.services.AuthenticateService;
import com.mibarim.main.services.HelloService;
import com.mibarim.main.services.RouteRequestService;
import com.mibarim.main.services.RouteResponseService;
import com.mibarim.main.services.UserInfoService;
import com.mibarim.main.ui.BootstrapActivity;
import com.mibarim.main.ui.HandleApiMessages;
import com.mibarim.main.ui.HandleApiMessagesBySnackbar;
import com.mibarim.main.ui.fragments.FabFragment;
import com.mibarim.main.ui.fragments.RouteDetailsFragment;
import com.mibarim.main.ui.fragments.RouteFilterFragment;
import com.mibarim.main.ui.fragments.SuggestedTimesFragment;
import com.mibarim.main.util.SafeAsyncTask;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

import static com.mibarim.main.core.Constants.GlobalConstants.ALLOW_BACK_BUTTON;
import static com.mibarim.main.core.Constants.GlobalConstants.ROUTE_FILTER_FRAGMENT_TAG;

public class MainActivity extends BootstrapActivity {

    private int appVersion = 0;
    private String authToken;
    private UserInfoModel userInfoModel;
    private InviteModel inviteModel;
    private ApiResponse apiResponse;
    private String url;
    public long filterId;
    private ApiResponse suggestRouteResponse;
    private ApiResponse cancelFilterapiResponse;
    private ApiResponse deleteFilterapiResponse;
    List<PassRouteModel> passengerTripModel;
    ProgressDialog progressDialog;
    private View parentLayout;

    NumberPicker hourPicker;
    NumberPicker minutePicker;
    int selectedRouteHour;
    int selectedRouteMin;


    // constants
    private int FINISH_USER_INFO = 5649;
    private int SEARCH_STATION_REQUEST_CODE = 7464;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private int RIDING_ACTIVITY_REQUEST_CODE = 5669;
    private int FINISH_PAYMENT = 5659;


    ImageView invite_btn;
    ImageView upload_btn;
    ImageView testButton;
    private Toolbar toolbar;
    String googletoken = "";
    String oneSignaltoken = "";

    @Inject
    UserInfoService userInfoService;
    @Inject
    protected BootstrapServiceProvider serviceProvider;
    @Inject
    UserData userData;
    @Inject
    RouteResponseService routeResponseService;

    @Inject
    protected RouteRequestService routeRequestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);

        BootstrapApplication application = (BootstrapApplication) getApplication();
        setContentView(R.layout.main_activity);

        parentLayout = findViewById(R.id.main_activity_parent);
        ButterKnife.bind(this);

        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage(getText(R.string.please_wait));


//        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
//        setSupportActionBar(toolbar);
//        invite_btn = (ImageView) toolbar.findViewById(R.id.invite_btn);
//        checkAuth();
        //initScreen();
//        upload_btn = (ImageView) toolbar.findViewById(R.id.upload_btn);

//        user_panel = (ImageView) toolbar.findViewById(R.id.test_button);

        checkAuth();

        runningService();

    }

    public void runningService() {

        Calendar cur_cal = Calendar.getInstance();
        cur_cal.setTimeInMillis(System.currentTimeMillis());
        cur_cal.add(Calendar.SECOND, 90);
        Intent intent = new Intent(MainActivity.this, HelloService.class);
        PendingIntent pi = PendingIntent.getService(MainActivity.this, 0, intent, 0);
        AlarmManager alarm_manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm_manager.set(AlarmManager.RTC, cur_cal.getTimeInMillis(), pi);
        alarm_manager.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), 30 * 60 * 1000, pi);

//        Intent intent = new Intent(this,HelloService.class);
//        startService(intent);
    }


    private void checkAuth() {
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                final AuthenticateService svc = serviceProvider.getService(MainActivity.this);
                if (svc != null) {
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                    return true;
                }
                return false;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
                    finish();
                }
            }

            @Override
            protected void onSuccess(final Boolean hasAuthenticated) throws Exception {
                super.onSuccess(hasAuthenticated);
                //userHasAuthenticated = true;
                initScreen();
                sendRegistrationToServer();
            }
        }.execute();
    }

    private void sendRegistrationToServer() {
        if (checkPlayServices()) {
            final InstanceID instanceID = InstanceID.getInstance(this);
            new SafeAsyncTask<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    googletoken = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
                    oneSignaltoken=status.getSubscriptionStatus().getUserId();
                    return true;
                }

                @Override
                protected void onException(final Exception e) throws RuntimeException {
                    super.onException(e);
                    sendTokenToServer();
                }

                @Override
                protected void onSuccess(final Boolean state) throws Exception {
                    super.onSuccess(state);
                    sendTokenToServer();
                }
            }.execute();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }


    private void sendTokenToServer() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                }
                userInfoService.SaveGoogleToken(authToken, googletoken,oneSignaltoken);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
            }
        }.execute();
    }


    private void initScreen() {
        checkVersion();
        getUserInfoFromServer();
        getInviteFromServer();

        getTheRatingsFromServer();

        RouteFilterFragment routeFilterFragment = new RouteFilterFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_activity, routeFilterFragment, ROUTE_FILTER_FRAGMENT_TAG)
                .commit();


        fragmentManager.beginTransaction()
                .add(R.id.main_container, new FabFragment(), "FabFragment")
                .commit();

//        showFloatingActionButton();

        /*fragmentManager.beginTransaction()
                .add(R.id.main_container, new PassengerCardFragment())
                .commit();*/

//        fragmentManager.beginTransaction()
//                .add(R.id.main_container, new FabFragment(), "FabFragment")
//                .commit();

        if (url != null) {
            gotoWebView(url);
        }
        /*invite_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    gotoInviteActivity();
                    return true;
                }
                return false;
            }
        });

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent upload_intent = new Intent(MainActivity.this, UserInfoDetailActivity.class);
                upload_intent.putExtra(Constants.Auth.AUTH_TOKEN, authToken);
                startActivity(upload_intent);
            }
        });


        user_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                intent.putExtra(Constants.Auth.AUTH_TOKEN, authToken);
//                startActivity(intent);
            }
        });*/
    }

    private void checkVersion() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                ApiResponse ver = userInfoService.AppVersion();
                if (ver.Messages.size() > 0 && ver.Messages.get(0) != null) {
                    String version = ver.Messages.get(0);
                    appVersion = Integer.valueOf(version);
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                if (appVersion > getVersion()) {
                    showUpdateDialog(getString(R.string.update_msg));
                }
            }
        }.execute();
    }

    public int getVersion() {
        int v = 1000;
        try {
            v = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return v;
    }

    private void showUpdateDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setPositiveButton("باشه", dialogClickListener).setNegativeButton("بستن برنامه", dialogClickListener).show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.update_link)));
                    startActivity(browserIntent);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    finishAffinity();
                    break;
            }
        }
    };

    private void getUserInfoFromServer() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                }
                userInfoModel = userInfoService.getUserInfo(authToken);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                userData.insertUserInfo(userInfoModel);
                getImageById(userInfoModel.UserImageId, R.mipmap.ic_camera);
                setInfoValues(userInfoModel.IsUserRegistered);
                //setEmail();
            }
        }.execute();
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

    private void setInfoValues(boolean IsUserRegistered) {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mibarim.main", Context.MODE_PRIVATE);
        if (IsUserRegistered) {
            prefs.edit().putInt("UserInfoRegistered", 1).apply();
        } else {
            prefs.edit().putInt("UserInfoRegistered", 0).apply();
            final Intent i = new Intent(this, RegisterActivity.class);
            startActivityForResult(i, FINISH_USER_INFO);


        }


//        prefs.getInt("UserPhotoUploaded",2);


        /*if (userInfoModel.UserImageId == null) {
            if (prefs.getInt("UserPhotoUploadedFirstTry", 2) != 1) {
                Intent j = new Intent(this, UserInfoDetailActivity.class);
                startActivityForResult(j, USER_DETAIL_INFO_REQUEST_CODE);
            }
        }*/


    }

    private void getImageFromServer(final String imageId) {
        new SafeAsyncTask<Boolean>() {
            ImageResponse imageResponse = new ImageResponse();
            Bitmap decodedByte;

            @Override
            public Boolean call() throws Exception {
                authToken = serviceProvider.getAuthToken(MainActivity.this);
                imageResponse = userInfoService.GetImageById(authToken, imageId);
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

    public void getInviteFromServer() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                }
                inviteModel = userInfoService.getInvite(authToken);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                userData.insertInvite(inviteModel);
            }
        }.execute();
    }

    private void getTheRatingsFromServer() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                /*if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(UserDocumentsUploadActivity.this);
                }*/

//                progressDialog.show();
                apiResponse = userInfoService.getRatings(authToken, "");

//                ratingModelList = new ArrayList<RatingModel>();

//                if (mainStationsApiResponse.Count > 0)


//                ApiResponse myResponse = routeResponseService.GetStationRoutes(1);
                //Gson gson = new Gson();
                /*Gson gson = new GsonBuilder().create();
                for (String json : mainStationsApiResponse.Messages) {
                    ratingModelList.add(gson.fromJson(json, RatingModel.class));
                }*/
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
//                makeAllProgressBarsInvisible();
//                progressDialog.hide();
                Toast.makeText(MainActivity.this, R.string.error_message, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);


                if (apiResponse.Count > 0) {
                    Intent intent = new Intent(MainActivity.this, RatingActivity.class);
                    intent.putExtra(Constants.Auth.AUTH_TOKEN, authToken);

                    intent.putExtra(Constants.GlobalConstants.RAINTG_LIST_TAG, apiResponse);

                    startActivity(intent);
                }
            }
        }.execute();
    }

    public void gotoWebView(String link) {
        Intent i = new Intent(MainActivity.this, WebViewActivity.class);
        i.putExtra("URL", link);
        startActivity(i);
    }

    public void gotoInviteActivity() {
        Intent intent = new Intent(this, InviteActivity.class);
        intent.putExtra(Constants.Auth.AUTH_TOKEN, authToken);
        this.startActivity(intent);
    }


    public String getAuthToken() {
        return authToken;
    }

    public void chosenFilter(long id) {
        filterId = id;
    }

    public long getChosenFilter() {
        return filterId;
    }

    public void goToSuggestTimes() {

    }


    public void removeCurrentFragmentAndAddRouteFilterFragment() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_activity);
        if (fragment != null) {
//            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            showFloatingActionButton();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_activity, new RouteFilterFragment())
                    .commit();

        }
    }


    public void deleteTheRoute(final long filterId) {
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {


                deleteFilterapiResponse = routeRequestService.deleteFilter(authToken, filterId); // GetFilters in here
                /*final AuthenticateService svc = serviceProvider.getService(MainActivity.this);
                if (svc != null) {
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                    return true;
                }*/

                return false;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
                    finish();
                }
            }

            @Override
            protected void onSuccess(final Boolean hasAuthenticated) throws Exception {
                super.onSuccess(hasAuthenticated);
                //userHasAuthenticated = true;
                new HandleApiMessages(MainActivity.this, deleteFilterapiResponse).showMessages();
                addRouteFilterFragment();
//                sendRegistrationToServer();
            }
        }.execute();
    }


    public void cancelTrip(final long filterId) {

        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {


                cancelFilterapiResponse = routeRequestService.cancelFilter(authToken, filterId); // GetFilters in here
                /*final AuthenticateService svc = serviceProvider.getService(MainActivity.this);
                if (svc != null) {
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                    return true;
                }*/

                return false;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
                    finish();
                }
            }

            @Override
            protected void onSuccess(final Boolean hasAuthenticated) throws Exception {
                super.onSuccess(hasAuthenticated);
                new HandleApiMessages(MainActivity.this, cancelFilterapiResponse).showMessages();
                //new HandleApiMessagesBySnackbar(parentLayout,cancelFilterapiResponse);
                //userHasAuthenticated = true;
//                initScreen();
//                RouteFilterFragment fragment = (RouteFilterFragment) getSupportFragmentManager().findFragmentByTag(ROUTE_FILTER_FRAGMENT_TAG);
//                fragment.refresh();

                addRouteFilterFragment();

//                sendRegistrationToServer();
            }
        }.execute();

    }


    public void showSuggestTimeDialog(long filterId) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.time_picker_dialog, null);

        View titleLayout = inflater.inflate(R.layout.timepicker_dialog_custom_title, null);

        hourPicker = (NumberPicker) alertLayout.findViewById(R.id.hour);
        minutePicker = (NumberPicker) alertLayout.findViewById(R.id.minute);

        LinearLayout hourUp = (LinearLayout) alertLayout.findViewById(R.id.add_to_hours);
        LinearLayout hourDown = (LinearLayout) alertLayout.findViewById(R.id.reduce_from_hours);

        LinearLayout minuteUp = (LinearLayout) alertLayout.findViewById(R.id.add_to_minutes);
        LinearLayout minuteDown = (LinearLayout) alertLayout.findViewById(R.id.reduce_from_minutes);
        final TextView stateOfTheDay = (TextView) alertLayout.findViewById(R.id.state_of_the_day);


        hourDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int val = hourPicker.getValue();
                    int temp = 28 - val;
                    temp = temp - 1;
                    if (temp == 24) {
                        temp = (val + 1) % 23 + 4;
                    }
                    if (temp == 4)
                        temp = 23;
                    if (temp > 12 && temp <= 23) {
                        stateOfTheDay.setText(getString(R.string.afternoon));
                    }
                    if (temp < 12 && temp >= 5)
                        stateOfTheDay.setText(getString(R.string.morning));

                    if (temp == 12)
                        stateOfTheDay.setText(getString(R.string.noon));

                    hourPicker.setValue(val + 1);

                }
                return true;
            }
        });
        hourUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int val = hourPicker.getValue();
                    int temp = 28 - val;
                    temp = temp + 1;
                    if (temp == 24)
                        temp = (val + 1) % 23 + 4;

                    if (temp > 12 && temp <= 23) {
                        stateOfTheDay.setText(getString(R.string.afternoon));
                    }
                    if (temp < 12 && temp >= 5)
                        stateOfTheDay.setText(getString(R.string.morning));
                    if (temp == 12)
                        stateOfTheDay.setText(getString(R.string.noon));
                    hourPicker.setValue(val - 1);
                }
                return true;
            }
        });

        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                int current = 28 - i1;
                if (current == 12)
                    stateOfTheDay.setText("ظهر");
                if (current < 12 && current >= 5)
                    stateOfTheDay.setText("صبح");
                if (current > 12 && current <= 23)
                    stateOfTheDay.setText("بعد از ظهر");
            }
        });


        minuteDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int val = minutePicker.getValue();
                    minutePicker.setValue(val + 1);
                }
                return true;
            }
        });
        minuteUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int val = minutePicker.getValue();
                    minutePicker.setValue(val - 1);
                }
                return true;
            }
        });


        //String[] hourValues = new String[]{"5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        String[] hourValues = new String[]{"23", "22", "21", "20", "19", "18", "17", "16", "15", "14", "13", "12", "11", "10", "9", "8", "7", "6", "5"};


        String[] hourValues2 = new String[19];

        for (int i = 23; i >= 5; i--) {
//            hourValues2[23-i] = String.valueOf(i);
            hourValues2[23 - i] = String.format("%02d", i);
        }

        String[] minuteValues = new String[]{"45", "30", "15", "00"};
        hourPicker.setMinValue(5);
        hourPicker.setMaxValue(23);

        hourPicker.setWrapSelectorWheel(true);

        int hour = 6; // = selectedRouteTrip.TimingHour;
        int minute = 0; // = selectedRouteTrip.TimingMin;
        if (hour < 5) {
            hour = 5;
        }
        if (hour > 23) {
            hour = 23;
        }
        //int hourIndex=hour - 5;
        int hourIndex = 28 - hour;
        if (hourIndex == 24) {
            hourIndex = (hour + 1) % 23 + 4;
        }
        if (hour > 12 && hour <= 23) {
            stateOfTheDay.setText(getString(R.string.afternoon));
        }
        if (hour < 12 && hour >= 5)
            stateOfTheDay.setText(getString(R.string.morning));
        if (hour == 12)
            stateOfTheDay.setText(getString(R.string.noon));
        hourPicker.setValue(hourIndex);
        int minIndex = 4;
        if (minute >= 45) {
            minIndex = 1;
        } else if (minute >= 30) {
            minIndex = 2;
        } else if (minute >= 15) {
            minIndex = 3;
        }
        minutePicker.setMinValue(1);
        minutePicker.setMaxValue(4);

        minutePicker.setDisplayedValues(minuteValues);
        //minutePicker.setValue(1);
        minutePicker.setValue(minIndex);
        minutePicker.setWrapSelectorWheel(true);

        //hourPicker.setValue(1);
        hourPicker.setDisplayedValues(hourValues2);
        hourPicker.setValue(0);
        int currentHourValue = 28 - hourPicker.getValue();
        if (currentHourValue == 12)
            stateOfTheDay.setText("ظهر");
        if (currentHourValue < 12 && currentHourValue >= 5)
            stateOfTheDay.setText("صبح");
        if (currentHourValue > 12 && currentHourValue <= 23)
            stateOfTheDay.setText("بعدازظهر");

        TextView title = new TextView(this);
// You Can Customise your Title here
        title.setText("Custom");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(View.TEXT_ALIGNMENT_CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("انتخاب زمان");
        builder.setCustomTitle(titleLayout);
        builder.setView(alertLayout);
        builder.setCancelable(false);
        builder.setPositiveButton("تایید", TimeDialogClickListener)
                .setNegativeButton("بیخیال", TimeDialogClickListener).show();
    }


    DialogInterface.OnClickListener TimeDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(
                            "com.mibarim.main", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putInt(ALLOW_BACK_BUTTON, 1).apply();
//                    int seatPickerVal = hourPicker.getValue();
                    selectedRouteHour = 28 - hourPicker.getValue();
                    int minValue = minutePicker.getValue();
                    switch (minValue) {
                        case 0:
                            selectedRouteMin = 0;
                            break;
                        case 1:
                            selectedRouteMin = 45;
                            break;
                        case 2:
                            selectedRouteMin = 30;
                            break;
                        case 3:
                            selectedRouteMin = 15;
                            break;
                    }

                    sendSuggestedFilterToServer(filterId, selectedRouteHour, selectedRouteMin);


//                    setEmptySeats();

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
//                    refreshList();
                    break;
            }
        }
    };


    public void sendSuggestedFilterToServer(final long filterId, final int hour, final int min) {

        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {

//                authToken = ((MainActivity) getActivity()).getAuthToken();
                ApiResponse setRes = routeRequestService.setSuggestedFilter(authToken, filterId, hour, min);


                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
//                progressDialog.hide();
//                Toast.makeText(SearchStationActivity.this, "خطا در انتخاب مسیر", Toast.LENGTH_LONG).show();
//                hideProgress();
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);

                initScreen();

//                hideProgress();
//                new HandleApiMessagesBySnackbar(parentLayout, setRes).showMessages();
//                Gson gson = new Gson();
//                FilterModel filterModel = new FilterModel();
//                for (String shareJson : setRes.Messages) {
//                    filterModel = gson.fromJson(shareJson, FilterModel.class);
//                }
//                if (filterModel.FilterId > 0) {
////                    progressDialog.hide();
////                    finishIt();
//                }
            }
        }.execute();


    }


    public void gotoRouteLists() {
//        Intent intent = new Intent(this, StationRouteListActivity.class);
//        this.startActivityForResult(intent, ROUTESELECTED);

        Intent intent = new Intent(this, SearchStationActivity.class);
        intent.putExtra(Constants.Auth.AUTH_TOKEN, authToken);
        startActivityForResult(intent, SEARCH_STATION_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEARCH_STATION_REQUEST_CODE && resultCode == RESULT_OK) {
//            initScreen();
            filterId = data.getLongExtra("Filter_Id", -1);
            if (filterId != -1) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.main_activity, new SuggestedTimesFragment(), "SuggestedTimesFragmentTag")
                        .commit();

                SharedPreferences sharedPreferences = this.getSharedPreferences(
                        "com.mibarim.main", Context.MODE_PRIVATE);
                sharedPreferences.edit().putInt("AllowBackButton", 0).apply();

                hideFloatingActionButton();


            }

        }


        if (requestCode == RIDING_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            removeCurrentFragmentAndAddRouteFilterFragment();
        }
    }


    @Override
    public void onBackPressed() {
        final SuggestedTimesFragment fragment = (SuggestedTimesFragment) getSupportFragmentManager().findFragmentByTag("SuggestedTimesFragmentTag");

        SharedPreferences sharedPreferences = this.getSharedPreferences(
                "com.mibarim.main", Context.MODE_PRIVATE);


        int allowBackButton = sharedPreferences.getInt(ALLOW_BACK_BUTTON, -1);
        // zero means "back_button_not_allowed" , 1 means "back_button_allowed"

        if (allowBackButton == 0) {

        } else {
            showFloatingActionButton();
            super.onBackPressed();
        }

    }


    public void hideFloatingActionButton() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("FabFragment");
        ((FabFragment) fragment).hideTheFab();
    }


    public void showFloatingActionButton() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("FabFragment");

        ((FabFragment) fragment).showTheFab();

    }

    public void addRouteFilterFragment() {
        RouteFilterFragment routeFilterFragment = new RouteFilterFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_activity, routeFilterFragment, ROUTE_FILTER_FRAGMENT_TAG)
                .commit();
        showFloatingActionButton();
    }

    public void goToImageUploadActivity() {
        Intent upload_intent = new Intent(MainActivity.this, UserInfoDetailActivity.class);
        upload_intent.putExtra(Constants.Auth.AUTH_TOKEN, authToken);
        startActivity(upload_intent);
    }

    public void goToUserProfileActivity(){
        Intent userPanel = new Intent(MainActivity.this, UserProfileActivity.class);
        userPanel.putExtra(Constants.Auth.AUTH_TOKEN, authToken);
        startActivity(userPanel);
    }


    public void previewDialog(final long filterId, String message, final boolean cancelOrDelete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (cancelOrDelete) {
                            deleteTheRoute(filterId);
                        } else {
                            cancelTrip(filterId);
                        }


                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                }).show();
    }

    public void removeRouteDetailsFragment() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_activity);
        if (fragment != null) {
//            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_activity, new RouteFilterFragment())
                    .commit();

            showFloatingActionButton();
        }
    }


    public void gotoRidingActivity(PassRouteModel dm) {
        Intent intent = new Intent(this, RidingActivity.class);
        intent.putExtra(Constants.GlobalConstants.PASS_ROUTE_MODEL, dm);
        intent.putExtra(Constants.Auth.AUTH_TOKEN, authToken);
        startActivityForResult(intent, RIDING_ACTIVITY_REQUEST_CODE);
    }

    public void gotoPayActivity(final PassRouteModel selectedItem) {
        Intent intent = new Intent(this, PayActivity.class);
        intent.putExtra(Constants.GlobalConstants.PASS_ROUTE_MODEL, selectedItem);
        intent.putExtra(Constants.Auth.AUTH_TOKEN, authToken);
        this.startActivityForResult(intent, FINISH_PAYMENT);
    }

    public void getPassengetTripFromServer() {

        progressDialog.show();

        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {


                Gson gson = new Gson();
                PassRouteModel bookedTrip = null;
//                    routeResponse = ((MainCardActivity) getActivity()).getRoute();
                passengerTripModel = new ArrayList<>();

                long mFilterId = getChosenFilter();
                suggestRouteResponse = routeResponseService.GetPassengerTrip(authToken, mFilterId);
                if (suggestRouteResponse != null) {
                    for (String routeJson : suggestRouteResponse.Messages) {
                        PassRouteModel route = gson.fromJson(routeJson, PassRouteModel.class);
                        passengerTripModel.add(route);
                    }
                            /*if (bookedTrip != null) {
                                if (bookedTrip.TripState == TripStates.InPreTripTime.toInt() ||
                                        bookedTrip.TripState == TripStates.InRiding.toInt() ||
                                        bookedTrip.TripState == TripStates.InTripTime.toInt()) {
                                    ((MainCardActivity) getActivity()).gotoRidingActivity(bookedTrip);
                                }else{
                                    ((MainCardActivity) getActivity()).showRidingActivity(bookedTrip);
                                }
                            }*/
                }


                return false;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);


            }

            @Override
            protected void onSuccess(final Boolean hasAuthenticated) throws Exception {

                progressDialog.hide();
                if (!passengerTripModel.get(0).IsBooked) {
                    addRouteDetailsFragment();
                } else {
                    PassRouteModel model = passengerTripModel.get(0);
                    gotoRidingActivity(model);
                }

//                super.onSuccess(hasAuthenticated);
//                //userHasAuthenticated = true;
//                initScreen();
//                sendRegistrationToServer();
            }
        }.execute();


    }


    public void addRouteDetailsFragment() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_activity);
        if (fragment != null) {
//            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_activity, new RouteDetailsFragment())
                    .commit();


        }
    }


}
