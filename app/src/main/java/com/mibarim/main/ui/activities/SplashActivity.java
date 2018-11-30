package com.mibarim.main.ui.activities;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.BootstrapServiceProvider;
import com.mibarim.main.R;
import com.mibarim.main.authenticator.ActionBarAccountAuthenticatorActivity;
import com.mibarim.main.authenticator.LogoutService;
import com.mibarim.main.core.Constants;
import com.mibarim.main.events.NetworkErrorEvent;
import com.mibarim.main.events.UnAuthorizedErrorEvent;
import com.mibarim.main.models.TokenResponse;
import com.mibarim.main.models.UserInitialModel;
import com.mibarim.main.services.AuthenticateService;
import com.mibarim.main.services.UserInfoService;
import com.mibarim.main.util.SafeAsyncTask;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static com.mibarim.main.core.Constants.Auth.AUTHTOKEN_TYPE;
import static com.mibarim.main.core.Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE;

/**
 * Created by Hamed on 3/30/2016.
 */
public class SplashActivity extends ActionBarAccountAuthenticatorActivity {
    private static final String TAG = "SplashActivity";
    @Inject
    protected Bus bus;
    @Inject
    BootstrapServiceProvider serviceProvider;
    @Inject
    AuthenticateService authenticateService;
    @Inject
    UserInfoService userInfoService;
    @Inject
    LogoutService logoutService;


    @Bind(R.id.retry_btn)
    protected TextView retry_btn;
    @Bind(R.id.progressBar)
    protected ProgressBar progressBar;

    public static final String PARAM_GRANT_TYPE = "password";
    public static final String PARAM_REPONSE_TYPE = "token";
    private static final int USER_REQUEST_GET_ACCOUNTS = 220;

    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private Tracker mTracker;
    private String authToken;
    private String url;
    private UserInitialModel userInitialModel;
    private AccountManager accountManager;
    private Account theAccount;

    private String mobile;

    private String password;

    private TokenResponse loginResponse;


    private SafeAsyncTask<Boolean> authenticationTask;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Fabric.with(this, new Crashlytics());
        BootstrapApplication.component().inject(this);

        accountManager = AccountManager.get(this);

        BootstrapApplication application = (BootstrapApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("SplashActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Activity").setAction("SplashActivity").build());
        setContentView(R.layout.initial_splash);

        if (getIntent() != null && getIntent().getExtras() != null) {
            url = getIntent().getExtras().getString(Constants.GlobalConstants.URL);
        }

        ButterKnife.bind(this);

        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        retry_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    checkAuth();
                    return true;
                }
                return false;
            }
        });

        SharedPreferences prefs = this.getSharedPreferences(
                "com.mibarim.main", Context.MODE_PRIVATE);
        prefs.edit().putLong("FirstRidingShow", 0).apply();

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextStep();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }


    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }


    @Subscribe
    public void onNetworkErrorEvent(NetworkErrorEvent event) {
        retry();
    }

    @Subscribe
    public void onUnAuthorizedErrorEvent(UnAuthorizedErrorEvent event) {
        reLogin();
    }


    public void nextStep() {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mibarim.main", Context.MODE_PRIVATE);
        if (prefs.getInt("FirstLaunch", 0) != 1) {
            gotoMainActivity();
            //finishIt();
            return;
        } else {
            checkAuth();
        }
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//        Intent intent = new Intent(SplashActivity.this, MainCardActivity.class);
        //Intent intent = new Intent(MainActivity.this, HomeWorkStepActivity.class);
        if (url != null) {
            intent.putExtra(Constants.GlobalConstants.URL, url);
        }
        this.startActivity(intent);
        finishIt();
    }

    private void checkAuth() {
        retry_btn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                final AuthenticateService svc = serviceProvider.getService(SplashActivity.this);
                if (svc != null) {
                    authToken = serviceProvider.getAuthToken(SplashActivity.this);
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
                    retry();
                }
            }

            @Override
            protected void onSuccess(final Boolean hasAuthenticated) throws Exception {
                super.onSuccess(hasAuthenticated);
                //userHasAuthenticated = true;
                if (hasAuthenticated) {

                    getInitialInfoFromServer();
                } else {
                    gotoMainActivity();
                    //finishIt();
                }
            }
        }.execute();
    }

    private void getInitialInfoFromServer() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(SplashActivity.this);
                }
                userInitialModel = userInfoService.getUserInitialInfo(authToken);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                retry();
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                setInfoValues();
            }
        }.execute();
    }

    private void setInfoValues() {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mibarim.main", Context.MODE_PRIVATE);
        prefs.edit().putInt("FirstLaunch", 1).apply();
        if (userInitialModel.IsUserRegistered) {
            prefs.edit().putInt("UserInfoRegistered", 1).apply();
        } else {
            prefs.edit().putInt("UserInfoRegistered", 0).apply();
        }
        gotoMainActivity();
        //finishIt();
    }

    public void reLogin() {
        if (authenticationTask != null) {
            return;
        }
//        Toaster.showLong(TokenRefreshActivity.this, getString(R.string.access_error), R.drawable.toast_warn);
        authenticationTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {

                final AccountManagerFuture<Bundle> accountManagerFuture
                        = accountManager.getAuthTokenByFeatures(BOOTSTRAP_ACCOUNT_TYPE,
                        AUTHTOKEN_TYPE, new String[0], SplashActivity.this, null, null, null, null);
                authToken = accountManagerFuture.getResult().getString(KEY_AUTHTOKEN);
                accountManager.invalidateAuthToken(Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE, authToken);
                mobile = accountManagerFuture.getResult().getString(KEY_ACCOUNT_NAME);
                if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SplashActivity.this,
                            new String[]{Manifest.permission.GET_ACCOUNTS},
                            USER_REQUEST_GET_ACCOUNTS);
                }
                Account[] account = accountManager.getAccountsByType(BOOTSTRAP_ACCOUNT_TYPE);
                if (account[0] != null) {
                    theAccount = account[0];
                    password = accountManager.getPassword(theAccount);
                }

                loginResponse = authenticateService.authenticate(mobile, password, PARAM_GRANT_TYPE, PARAM_REPONSE_TYPE);
                if (loginResponse.access_token != null && loginResponse.access_token != "") {
                    authToken = loginResponse.access_token;
                    return true;
                }
                return false;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                Log.w(TAG,"omad1");
                // Retrofit Errors are handled inside of the {
                if (!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if (cause != null) {
                        Snackbar.make(findViewById(R.id.splash_activity_root), cause.getMessage(), Snackbar.LENGTH_LONG).show();
                        //Toaster.showLong(SplashActivity.this, cause.getMessage());
                    }
                } else {
                        Response res = ((RetrofitError) e).getResponse();
                        String body = new String(((TypedByteArray)res.getBody()).getBytes());
                        if (res.getStatus() == 400 && body.toLowerCase().contains("error")) {
                            logout();
                        }
                    retry();
                }
            }

            @Override
            public void onSuccess(final Boolean authSuccess) {
                onAuthenticationResult(authSuccess);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                authenticationTask = null;
            }
        };
        authenticationTask.execute();
    }

    private void retry() {
        Snackbar.make(findViewById(R.id.splash_activity_root), R.string.network_error, Snackbar.LENGTH_LONG).show();
        //Toaster.showLong(SplashActivity.this, getString(R.string.network_error), R.drawable.toast_error);
        retry_btn.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        //checkAuth();
    }

    public void onAuthenticationResult(final boolean result) {
        if (result) {
            finishLogin();
        } else {
            Snackbar.make(findViewById(R.id.splash_activity_root), loginResponse.error, Snackbar.LENGTH_LONG).show();
            logout();
            //Toaster.showLong(SplashActivity.this, loginResponse.error, R.drawable.toast_error);
        }
    }

    protected void finishLogin() {
        final Account account = new Account(mobile, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
        accountManager.invalidateAuthToken(Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE, authToken);
//        accountManager.addAccountExplicitly(account, password, null);
        accountManager.setAuthToken(account, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE, authToken);
        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_NAME, mobile);
        intent.putExtra(KEY_ACCOUNT_TYPE, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

        if (authToken != null) {
            intent.putExtra(KEY_AUTHTOKEN, authToken);
        }
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mibarim.main", Context.MODE_PRIVATE);
        if (loginResponse.isMobileConfirmed) {
            prefs.edit().putInt("MobileValidated", 1).apply();
        } else {
            prefs.edit().putInt("MobileValidated", 0).apply();
        }

        setAccountAuthenticatorResult(intent.getExtras());
        //setResult(RESULT_OK, intent);
        gotoMainActivity();
        //finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        return;
    }

    private void finishIt() {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void logout() {
        logoutService.logout(new Runnable() {
            @Override
            public void run() {
                gotoMainActivity();
            }
        });

    }

}
