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
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.BootstrapServiceProvider;
import com.mibarim.main.R;
import com.mibarim.main.events.NetworkErrorEvent;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.UserInfoModel;
import com.mibarim.main.services.AuthenticateService;
import com.mibarim.main.services.UserInfoService;
import com.mibarim.main.ui.HandleApiMessagesBySnackbar;
import com.mibarim.main.ui.TextWatcherAdapter;
import com.mibarim.main.util.SafeAsyncTask;
import com.mibarim.main.util.Strings;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import timber.log.Timber;

/**
 * Created by Hamed on 2/27/2016.
 */
public class RegisterActivity extends AccountAuthenticatorActivity {
    private AccountManager accountManager;

    /**
     * PARAM_GRANT_TYPE
     */
    public static final String PARAM_GRANT_TYPE = "password";

    /**
     * PARAM_REPONSE_TYPE
     */
    public static final String PARAM_REPONSE_TYPE = "token";

    private Tracker mTracker;

    @Inject
    AuthenticateService authenticateService;
    @Inject
    UserInfoService userInfoService;
    @Inject
    BootstrapServiceProvider serviceProvider;
    @Inject
    Bus bus;


    @Bind(R.id.radioSex)
    protected RadioGroup radioSex;
    @Bind(R.id.et_name)
    protected AutoCompleteTextView et_name;
    @Bind(R.id.et_family)
    protected AutoCompleteTextView et_family;
    @Bind(R.id.et_theCode)
    protected EditText et_theCode;
    @Bind(R.id.et_regEmail)
    protected AutoCompleteTextView et_regEmail;
    /*@Bind(R.id.et_regPassword)
    protected EditText regPasswordText;*/

    @Bind(R.id.b_register)
    protected AppCompatButton registerButton;

    private final TextWatcher watcher = validationTextWatcher();

    private SafeAsyncTask<Boolean> registerTask;

    private String authToken;

    private ApiResponse response;

    private String user_mobile ;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        BootstrapApplication.component().inject(this);
        BootstrapApplication application = (BootstrapApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("RegisterActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Activity").setAction("RegisterActivity").build());

        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        accountManager = AccountManager.get(this);

        setContentView(R.layout.register_activity);

        ButterKnife.bind(this);
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mibarim.main", Context.MODE_PRIVATE);
        user_mobile = prefs.getString("UserMobile", "");


        registerButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    UserInfoModel u = new UserInfoModel();
                    u.Name = et_name.getText().toString();
                    u.Family = et_family.getText().toString();
                    u.Email = et_regEmail.getText().toString();
                    u.Code = et_theCode.getText().toString();
                    int selectedId = radioSex.getCheckedRadioButtonId();
                    if (selectedId == R.id.woman_rdo) {
                        u.Gender = "2";
                    } else {
                        u.Gender = "1";
                    }
                    handleRegister(registerButton, u);
                    return true;
                }
                return false;
            }
        });

        et_name.addTextChangedListener(watcher);
        et_family.addTextChangedListener(watcher);
        et_regEmail.addTextChangedListener(watcher);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
        updateUIWithValidation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }



    @Override
    public void onBackPressed() {
        Snackbar.make(findViewById(R.id.register_activity_root),R.string.please_fill, Snackbar.LENGTH_LONG).show();
        //Toaster.showLong(RegisterActivity.this, getString(R.string.press_again_to_exit), R.drawable.toast_info);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.please_wait));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        return dialog;
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }

        };
    }

    @Subscribe
    public void onNetworkErrorEvent(NetworkErrorEvent event) {
        Snackbar.make(findViewById(R.id.register_activity_root),R.string.please_fill, Snackbar.LENGTH_LONG).show();
        //Toaster.showLong(RegisterActivity.this, getString(R.string.network_error), R.drawable.toast_warn);
    }


    private void updateUIWithValidation() {
        boolean populated = populated(et_regEmail) && populated(et_name) && populated(et_family);
        registerButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    public void handleRegister(final View view, final UserInfoModel u) {
        if (registerTask != null) {
            return;
        }
        showProgress();
        registerTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                if (authToken == null) {
                    authToken = serviceProvider.getAuthToken(RegisterActivity.this);
                }
                response = userInfoService.registerUser(authToken, u);
                if (response.Errors.size() == 0 && response.Status.equals("OK")) {
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
                        Snackbar.make(findViewById(R.id.register_activity_root),cause.getMessage(), Snackbar.LENGTH_LONG).show();
                        //Toaster.showLong(RegisterActivity.this, cause.getMessage());
                    }
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
            prefs.edit().putInt("UserInfoRegistered", 1).apply();
            Intent i=getIntent();
            setResult(RESULT_OK,i);
            finish();
        } else {
            Timber.d("onAuthenticationResult: failed to authenticate");
            new HandleApiMessagesBySnackbar(findViewById(R.id.register_activity_root), response).showMessages();
        }
    }

    public void gotoRules(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://mibarim.com/terms-policy"));
        startActivity(browserIntent);
    }
}
