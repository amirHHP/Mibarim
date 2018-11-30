package com.mibarim.main.ui.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.R;
import com.mibarim.main.R.id;
import com.mibarim.main.R.string;
import com.mibarim.main.authenticator.ActionBarAccountAuthenticatorActivity;
import com.mibarim.main.events.NetworkErrorEvent;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.services.RegisterService;
import com.mibarim.main.ui.HandleApiMessagesBySnackbar;
import com.mibarim.main.util.SafeAsyncTask;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import timber.log.Timber;

/**
 * Activity to register the user against an API
 */
public class MobileActivity extends ActionBarAccountAuthenticatorActivity {

    @Inject
    RegisterService registerService;
    @Inject
    Bus bus;

    @Bind(id.et_mobile)
    protected AutoCompleteTextView loginMobile;
    @Bind(id.b_signin)
    protected AppCompatButton signInButton;
    /*@Bind(R.id.b_register_ent)
    protected AppCompatButton entRegisterButton;
*/
    private ApiResponse response;

    private SafeAsyncTask<Boolean> registerTask;
    private String regMobile;
    private int NUMBER_AGAIN = 5577;
    private int FINISH_REGISTER_SERVICE = 4561;
    private Tracker mTracker;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        BootstrapApplication.component().inject(this);
        BootstrapApplication application = (BootstrapApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("MobileActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Activity").setAction("MobileActivity").build());


        setContentView(R.layout.mdobile_activity);

        ButterKnife.bind(this);

        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        SharedPreferences prefs = this.getSharedPreferences(
                "com.mibarim.main", Context.MODE_PRIVATE);
        regMobile = prefs.getString("UserMobile", "");
        loginMobile.setText(regMobile);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/IRANSans(FaNum)_Light.ttf");
        signInButton.setTypeface(font);
        signInButton.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    registerUser();
                    return true;
                }
                return false;
            }
        });

/*
        entRegisterButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent i = new Intent(MobileActivity.this, WebViewActivity.class);
                    i.putExtra("URL", "http://mibarimapp.com/coreapi/FanapLogin");
                    startActivityForResult(i, FINISH_REGISTER_SERVICE);
                    return true;
                }
                return false;
            }
        });
*/



        final Intent i = new Intent(this, HelpActivity.class);
        startActivity(i);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FINISH_REGISTER_SERVICE && resultCode == RESULT_OK) {
            /*Intent i=getIntent();
            setResult(RESULT_OK,i);
            finish();*/
            //relogin();
        }
    }


    private void registerUser() {
        if (registerTask != null) {
            return;
        }
        regMobile = loginMobile.getText().toString();

        showProgress();

        registerTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                response = registerService.register(regMobile);
                if (response.Errors.size() == 0 && response.Status.equals("OK")) {
                    return true;
                }
                return false;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if (!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if (cause != null) {
                        Snackbar.make(findViewById(R.id.mobile_activity_root), cause.getMessage(), Snackbar.LENGTH_LONG).show();
                        //Toaster.showLong(MobileActivity.this, cause.getMessage());
                    }
                } else {
                    Snackbar.make(findViewById(R.id.mobile_activity_root), R.string.error_server_connection, Snackbar.LENGTH_LONG).show();
                    //Toaster.showLong(MobileActivity.this, getString(R.string.network_error), R.drawable.toast_warn);
                }
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

    private void onRegisterResult(Boolean result) {
        if (result) {
            returnToSmsValidation();

        } else {
            Timber.d("onAuthenticationResult: failed to authenticate");
            new HandleApiMessagesBySnackbar(findViewById(R.id.mobile_activity_root), response).showMessages();
        }
    }

    private void returnToSmsValidation() {
        Intent intent = getIntent();
        intent.putExtra("MobileNo", regMobile);
        setResult(RESULT_OK, intent);
        finish();
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
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(string.please_wait));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(final DialogInterface dialog) {
                if (registerTask != null) {
                    registerTask.cancel(true);
                }
            }
        });
        return dialog;
    }


    @Subscribe
    public void onNetworkErrorEvent(NetworkErrorEvent event) {
        Snackbar.make(findViewById(R.id.mobile_activity_root), R.string.network_error, Snackbar.LENGTH_LONG).show();
        //Toaster.showLong(MobileActivity.this, getString(string.network_error), R.drawable.toast_warn);
    }



/*

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

*/

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
    public void onBackPressed() {

        return;
    }

}
