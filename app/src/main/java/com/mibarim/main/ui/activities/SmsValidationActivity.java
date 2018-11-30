package com.mibarim.main.ui.activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.BootstrapServiceProvider;
import com.mibarim.main.R;
import com.mibarim.main.authenticator.LogoutService;
import com.mibarim.main.core.Constants;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.Password;
import com.mibarim.main.models.TokenResponse;
import com.mibarim.main.services.AuthenticateService;
import com.mibarim.main.services.RegisterService;
import com.mibarim.main.ui.TextWatcherAdapter;
import com.mibarim.main.util.SafeAsyncTask;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import timber.log.Timber;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;

/**
 * Created by Hamed on 2/27/2016.
 */
public class SmsValidationActivity extends AccountAuthenticatorActivity {
    private AccountManager accountManager;

    /**
     * PARAM_GRANT_TYPE
     */
    public static final String PARAM_GRANT_TYPE = "password";

    /**
     * PARAM_REPONSE_TYPE
     */
    public static final String PARAM_REPONSE_TYPE = "token";

//    private int CHECK_VALIDATE = 8888;
    private Tracker mTracker;
    private int NUMBER_SET = 9090;

    @Inject
    AuthenticateService authenticateService;
/*    @Inject
    RegisterService registerService;*/
    @Inject
    BootstrapServiceProvider serviceProvider;
    @Inject
    LogoutService getLogoutService;
    @Inject
    Bus bus;


    @Bind(R.id.validationCode)
    protected EditText validationCode;
    @Bind(R.id.counter)
    protected TextView counter;
    @Bind(R.id.sms_resend)
    protected TextView sms_resend;
    @Bind(R.id.voice_send)
    protected TextView voice_send;
    @Bind(R.id.mobile_confirm_btn)
    protected TextView mobile_confirm_btn;
    @Bind(R.id.link_login)
    protected TextView link_login;
    @Bind(R.id.mobile_no)
    protected TextView mobile_no;
    @Bind(R.id.wrong_number)
    protected TextView wrong_number;

    private SafeAsyncTask<Boolean> registerTask;

    private String authToken;
    private String mobileNo;
    private String password;
    private ApiResponse response;

    private TokenResponse loginResponse;

    private SafeAsyncTask<Boolean> authenticationTask;

    private final TextWatcher watcher = validationTextWatcher();

    private boolean isConfirmed;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        BootstrapApplication.component().inject(this);
        BootstrapApplication application = (BootstrapApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("SmsValidationActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Activity").setAction("SmsValidationActivity").build());
        goToMobileActivity();

        accountManager = AccountManager.get(this);

        setContentView(R.layout.sms_validation_activity);


        ButterKnife.bind(this);
        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

    }

    private void initScreen(){
        /*if (getIntent() != null && getIntent().getExtras() != null) {
            mobileNo = getIntent().getStringExtra("MobileNo");
            authToken = getIntent().getStringExtra("AuthToken");
        }*/
        mobile_no.setText(mobileNo);

//        mobile_number.setText(mobileNo);
        mobile_confirm_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    String vCode = validationCode.getText().toString();
                    confirmMobileNo(mobileNo, vCode);
                    return true;
                }
                return false;
            }
        });
        invisibleBtns();
        counter.setVisibility(View.VISIBLE);
        sms_resend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    invisibleBtns();
                    counter.setVisibility(View.VISIBLE);
                    secondWait();
                    sendSms(2);
                    return true;
                }
                return false;
            }
        });
        voice_send.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    invisibleBtns();
                    counter.setVisibility(View.VISIBLE);
                    thirdWait();
                    sendSms(3);
                    return true;
                }
                return false;
            }
        });
        wrong_number.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    goToMobileActivity();
                    return true;
                }
                return false;
            }
        });
        validationCode.addTextChangedListener(watcher);

        firstWait();
        sendSms(1);

    }

    private void goToMobileActivity() {
        final Intent intent = new Intent(SmsValidationActivity.this, MobileActivity.class);
        this.startActivityForResult(intent,NUMBER_SET);
    }

    private void invisibleBtns() {
        counter.setVisibility(View.GONE);
        sms_resend.setVisibility(View.GONE);
        voice_send.setVisibility(View.GONE);
        link_login.setVisibility(View.GONE);
        wrong_number.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void firstWait() {
        new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                counter.setText(millisUntilFinished / 1000 + " ثانیه تا ارسال پیامک... ");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                invisibleBtns();
                sms_resend.setVisibility(View.VISIBLE);
                wrong_number.setVisibility(View.VISIBLE);
            }

        }.start();
    }

    private void secondWait() {
        new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                counter.setText(millisUntilFinished / 1000 + " ثانیه تا ارسال مجدد پیامک ... ");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                invisibleBtns();
                voice_send.setVisibility(View.VISIBLE);
                wrong_number.setVisibility(View.VISIBLE);
            }

        }.start();
    }

    private void thirdWait() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                counter.setText(millisUntilFinished / 1000 + " ثانیه منتظر بمانید... ");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                invisibleBtns();
                link_login.setVisibility(View.VISIBLE);
                wrong_number.setVisibility(View.VISIBLE);
            }

        }.start();
    }


    private void sendSms(final int count) {

        registerTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                isConfirmed = authenticateService.sendValidateSms(authToken, mobileNo,count);
                return isConfirmed;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
            }

            @Override
            protected void onFinally() throws RuntimeException {
                registerTask = null;
            }
        };
        registerTask.execute();
    }

    /*private void sendConfirmSms() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:10008191"));
        intent.putExtra("sms_body", "1");
        startActivity(intent);
    }*/

    private void confirmMobileNo(final String mobileNo, final String vCode) {
        showProgress();

        registerTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                response = authenticateService.confirmMobileSms(authToken, mobileNo, vCode);
                if (response.Errors.size() == 0 && response.Status.equals("OK")) {
                    return true;
                }
                return false;
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
            Gson gson = new Gson();
            Password passwordModel = new Password();
            for (String json : response.Messages) {
                passwordModel = gson.fromJson(json, Password.class);
            }
            if (passwordModel.Confirmed) {
                password=passwordModel.Password;
                SharedPreferences prefs = this.getSharedPreferences(
                        "com.mibarim.main", Context.MODE_PRIVATE);
                prefs.edit().putInt("MobileValidated", 1).apply();
                if(passwordModel.IsUserRegistered){
                    prefs.edit().putInt("UserInfoRegistered", 1).apply();
                }else{
                    prefs.edit().putInt("UserInfoRegistered", 0).apply();
                }
                handleLogin();
            } else {
                Snackbar.make(findViewById(R.id.sms_validation_root),R.string.not_correct,Snackbar.LENGTH_LONG).show();
                //Toaster.showLong(SmsValidationActivity.this, getString(R.string.not_correct));
            }
        } else {
            Timber.d("onAuthenticationResult: failed to authenticate");
            Snackbar.make(findViewById(R.id.sms_validation_root),R.string.error_occured,Snackbar.LENGTH_LONG).show();
            //Toaster.showLong(SmsValidationActivity.this, getString(R.string.error_occured));
        }
    }

    public void handleLogin() {
        if (authenticationTask != null) {
            return;
        }
        showProgress();
        authenticationTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
//                return true;
                loginResponse = authenticateService.authenticate(mobileNo, password, PARAM_GRANT_TYPE, PARAM_REPONSE_TYPE);
                if (loginResponse.access_token != null && loginResponse.access_token != "") {
                    authToken = loginResponse.access_token;
                    return true;
                }
                return false;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                // Retrofit Errors are handled inside of the {
                if (!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if (cause != null) {
                        Snackbar.make(findViewById(R.id.sms_validation_root),cause.getMessage(),Snackbar.LENGTH_LONG).show();
                        //Toaster.showLong(SmsValidationActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean authSuccess) {
                onAuthenticationResult(authSuccess);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                authenticationTask = null;
            }
        };
        authenticationTask.execute();
    }

    public void onAuthenticationResult(final boolean result) {
        if (result) {
            finishLogin();
            SharedPreferences prefs = this.getSharedPreferences(
                    "com.mibarim.main", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("sentTokenToServer", false).apply();
            prefs.edit().putInt("FirstLaunch", 1).apply();
            if (loginResponse.isMobileConfirmed) {
                prefs.edit().putInt("MobileValidated", 1).apply();
            } else {
                prefs.edit().putInt("MobileValidated", 0).apply();
            }
            finishIt();
        } else {
            Timber.d("onAuthenticationResult: failed to authenticate");
            Snackbar.make(findViewById(R.id.sms_validation_root),R.string.error_occured,Snackbar.LENGTH_LONG).show();
            //Toaster.showLong(SmsValidationActivity.this, loginResponse.error, R.drawable.toast_error);
        }
    }

    protected void finishLogin() {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mibarim.main", Context.MODE_PRIVATE);
        prefs.edit().putString("UserMobile", mobileNo).apply();

        final Account account = new Account(mobileNo, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

        accountManager.addAccountExplicitly(account, password, null);
        accountManager.setAuthToken(account, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE, authToken);
    }

    protected void finishIt() {
        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_NAME, mobileNo);
        intent.putExtra(KEY_ACCOUNT_TYPE, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

//        if (authTokenType != null
//                && authTokenType.equals(Constants.Auth.AUTHTOKEN_TYPE)) {
//            intent.putExtra(KEY_AUTHTOKEN, authToken);
//        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NUMBER_SET && resultCode == RESULT_OK) {
            mobileNo = data.getStringExtra("MobileNo");
            initScreen();
        }
    }
    /*
        public void gotoMobileValidation(View view) {
            finishIt();
            *//*final Intent intent = new Intent(this, MobileValidationActivity.class);
        intent.putExtra("MobileNo", mobileNo);
        intent.putExtra("AuthToken", authToken);
        this.startActivityForResult(intent,CHECK_VALIDATE);*//*

    }
*/
    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                submitCode();
            }

        };
    }

    private void submitCode() {
        if (validationCode.length() >= 3) {
            confirmMobileNo(mobileNo, validationCode.getText().toString());
        }
    }

   /* private void logout() {
        getLogoutService.logout(new Runnable() {
            @Override
            public void run() {
                finishIt();

            }
        });

    }

    public void logout(View view) {
        logout();
    }*/
}
