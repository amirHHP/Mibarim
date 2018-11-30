package com.mibarim.main.ui.activities;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.BootstrapServiceProvider;
import com.mibarim.main.R;
import com.mibarim.main.authenticator.LogoutService;
import com.mibarim.main.services.AuthenticateService;
import com.mibarim.main.services.RegisterService;
import com.mibarim.main.util.SafeAsyncTask;
import com.mibarim.main.util.Toaster;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Hamed on 2/27/2016.
 */
public class MobileValidationActivity extends AccountAuthenticatorActivity {
    private AccountManager accountManager;

    /**
     * PARAM_GRANT_TYPE
     */
    public static final String PARAM_GRANT_TYPE = "password";

    /**
     * PARAM_REPONSE_TYPE
     */
    public static final String PARAM_REPONSE_TYPE = "token";

    @Inject
    AuthenticateService authenticateService;
    @Inject
    RegisterService registerService;
    @Inject
    LogoutService getLogoutService;
    @Inject
    BootstrapServiceProvider serviceProvider;
    @Inject
    Bus bus;


    @Bind(R.id.mobile_number)
    protected TextView mobile_number;
    @Bind(R.id.mobile_confirm_btn)
    protected TextView mobile_confirm_btn;
    @Bind(R.id.send_sms_btn)
    protected TextView send_sms_btn;

    private SafeAsyncTask<Boolean> registerTask;

    private String authToken;
    private String mobileNo;

    private Tracker mTracker;

    private boolean isConfirmed;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        BootstrapApplication.component().inject(this);

        BootstrapApplication application = (BootstrapApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("MobileValidationActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Activity").setAction("MobileValidationActivity").build());


        accountManager = AccountManager.get(this);

        setContentView(R.layout.validation_activity);

        if (getIntent() != null && getIntent().getExtras() != null) {
            mobileNo = getIntent().getStringExtra("MobileNo");
            authToken = getIntent().getStringExtra("AuthToken");
        }


        ButterKnife.bind(this);

        mobile_number.setText(mobileNo);
        mobile_confirm_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    confirmMobileNo(mobileNo);
                    return true;
                }
                return false;
            }
        });
        send_sms_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    sendConfirmSms();
                    return true;
                }
                return false;
            }
        });
    }

    private void sendConfirmSms() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:10008191"));
        intent.putExtra("sms_body", "1");
        startActivity(intent);
    }

    private void confirmMobileNo(final String mobileNo) {
        showProgress();

        registerTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                isConfirmed = authenticateService.confirmMobile(authToken, mobileNo);
                return isConfirmed;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                // Retrofit Errors are handled inside of the {
//                if (!(e instanceof RetrofitError)) {
//                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
//                    if (cause != null) {
//                        Toaster.showLong(MobileValidationActivity.this, cause.getMessage());
//                    }
//                }
            }

            @Override
            public void onSuccess(final Boolean authSuccess) {
                onRegisterResult(authSuccess);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                registerTask = null;
            }
        };
        registerTask.execute();
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.please_wait));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            public void onCancel(final DialogInterface dialog) {
//                if (registerTask != null) {
//                    registerTask.cancel(true);
//                }
//            }
//        });
        return dialog;
    }


    /**
     * Show progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void showProgress() {
        showDialog(0);
    }

    /**
     * Hide progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void hideProgress() {
        dismissDialog(0);
    }

    public void onRegisterResult(final boolean result) {
        if (result) {
            SharedPreferences prefs = this.getSharedPreferences(
                    "com.mibarim.main", Context.MODE_PRIVATE);
            prefs.edit().putInt("MobileValidated", 1).apply();
            finishIt();
        } else {
            Timber.d("onAuthenticationResult: failed to authenticate");
            Toaster.showLong(MobileValidationActivity.this, getString(R.string.not_confirmed));
        }
    }

    protected void finishIt() {
        final Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
    private void logout() {
        getLogoutService.logout(new Runnable() {
            @Override
            public void run() {
                finishIt();

            }
        });

    }



    public void logout(View view) {
        logout();
    }
}
