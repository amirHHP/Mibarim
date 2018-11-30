package com.mibarim.main.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.R;
import com.mibarim.main.R.id;
import com.mibarim.main.R.layout;
import com.mibarim.main.R.string;
import com.mibarim.main.events.NetworkErrorEvent;
import com.mibarim.main.ui.activities.RegisterActivity;
import com.mibarim.main.events.WrongCredentialErrorEvent;
import com.mibarim.main.models.TokenResponse;
import com.mibarim.main.services.AuthenticateService;
import com.mibarim.main.core.Constants;
import com.mibarim.main.ui.TextWatcherAdapter;
import com.mibarim.main.ui.activities.SmsValidationActivity;
import com.mibarim.main.util.SafeAsyncTask;
import com.mibarim.main.util.Toaster;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import retrofit.RetrofitError;
import timber.log.Timber;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

/**
 * Activity to register the user against an API
 */
public class AuthenticatorActivity extends ActionBarAccountAuthenticatorActivity {

    /**
     * PARAM_CONFIRM_CREDENTIALS
     */
//    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";

    /**
     * PARAM_PASSWORD
     */
    public static final String PARAM_PASSWORD = "password";

    /**
     * PARAM_GRANT_TYPE
     */
    public static final String PARAM_GRANT_TYPE = "password";

    /**
     * PARAM_REPONSE_TYPE
     */
    public static final String PARAM_REPONSE_TYPE = "token";
    /**
     * PARAM_USERNAME
     */
    public static final String PARAM_USERNAME = "username";

    /**
     * PARAM_AUTHTOKEN_TYPE
     */
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    private AccountManager accountManager;

    @Inject
    AuthenticateService authenticateService;
    @Inject
    Bus bus;

    @Bind(id.et_mobile)
    protected AutoCompleteTextView loginMobile;
    /*@Bind(id.et_password)
    protected EditText loginPassword;*/
    @Bind(id.b_signin)
    protected AppCompatButton signInButton;

    private final TextWatcher watcher = validationTextWatcher();

    private SafeAsyncTask<Boolean> authenticationTask;
    private String authToken;
//    private String authTokenType;

    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password to be changed on the device.
     */
//    private Boolean confirmCredentials = false;

    private String mobile;

    private String password;

    private int RegisterActivityResponse = 777;
    private int ConfirmResponse = 7777;

    private TokenResponse loginResponse;
    /**
     * In this instance the token is simply the sessionId returned from Parse.com. This could be a
     * oauth token or some other type of timed token that expires/etc. We're just using the parse.com
     * sessionId to prove the example of how to utilize a token.
     */
    private String token;

    /**
     * Was the original caller asking for an entirely new account?
     */
    protected boolean requestNewAccount = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        SharedPreferences prefs = this.getSharedPreferences(
                "com.mibarim.main", Context.MODE_PRIVATE);
        if (prefs.getString("UserMobile", "").equals("")) {
            gotoRegister();
        }
        BootstrapApplication.component().inject(this);

        accountManager = AccountManager.get(this);

        final Intent intent = getIntent();
        mobile = intent.getStringExtra(PARAM_USERNAME);
//        authTokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
//        confirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS, false);

        //requestNewAccount = mobile == null;

        setContentView(layout.login_activity);

        ButterKnife.bind(this);

        /*loginMobile.setAdapter(new ArrayAdapter<String>(this,
                simple_dropdown_item_1line, userEmailAccounts()));*/

        /*loginPassword.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && signInButton.isEnabled()) {
                    handleLogin();
                    return true;
                }
                return false;
            }
        });

        loginPassword.setOnEditorActionListener(new OnEditorActionListener() {

            public boolean onEditorAction(final TextView v, final int actionId,
                                          final KeyEvent event) {
                if (actionId == IME_ACTION_DONE && signInButton.isEnabled()) {
                    handleLogin();
                    return true;
                }
                return false;
            }
        });*/

        loginMobile.addTextChangedListener(watcher);
        //loginPassword.addTextChangedListener(watcher);

        signInButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    handleLogin();
                    return true;
                }
                return false;
            }
        });

//        final TextView signUpText = (TextView) findViewById(id.tv_signup);
//        signUpText.setMovementMethod(LinkMovementMethod.getInstance());
//        signUpText.setText(Html.fromHtml(getString(string.label_login)));
    }

    /*private List<String> userEmailAccounts() {
        final Account[] accounts = accountManager.getAccountsByType("com.google");
        final List<String> emailAddresses = new ArrayList<String>(accounts.length);
        for (final Account account : accounts) {
            emailAddresses.add(account.name);
        }
        return emailAddresses;
    }*/

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }

        };
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

    private void updateUIWithValidation() {
        final boolean populated = populated(loginMobile);
        signInButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(string.please_wait));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            public void onCancel(final DialogInterface dialog) {
//                if (authenticationTask != null) {
//                    authenticationTask.cancel(true);
//                }
//            }
//        });
        return dialog;
    }

    @Subscribe
    public void onWrongCredentialErrorEvent(WrongCredentialErrorEvent wrongCredentialErrorEvent) {
        // Could not authorize for some reason.
        Toaster.showLong(AuthenticatorActivity.this, getString(R.string.message_bad_credentials), R.drawable.toast_error);
    }

    @Subscribe
    public void onNetworkErrorEvent(NetworkErrorEvent event) {
        Toaster.showLong(AuthenticatorActivity.this, getString(R.string.network_error), R.drawable.toast_warn);
    }



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Handles onClick event on the Submit button. Sends username/password to
     * the server for authentication.
     * <p/>
     * Specified by android:onClick="handleLogin" in the layout xml
     *
     */
    public void handleLogin() {
        if (authenticationTask != null) {
            return;
        }

        if (!requestNewAccount) {
            mobile = loginMobile.getText().toString();
            //password = loginPassword.getText().toString();
        }

        showProgress();

        authenticationTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
//                return true;
                loginResponse = authenticateService.authenticate(mobile, "salam", PARAM_GRANT_TYPE, PARAM_REPONSE_TYPE);
                if (loginResponse.access_token != null && loginResponse.access_token != "") {
                    token = loginResponse.access_token;
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
                        Toaster.showLong(AuthenticatorActivity.this, cause.getMessage());
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

    /**
     * Called when response is received from the server for confirm credentials
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller.
     *
     * @param result
     */
//    protected void finishConfirmCredentials(final boolean result) {
//        final Account account = new Account(mobile, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
//        accountManager.setPassword(account, password);
//
//        final Intent intent = new Intent();
//        intent.putExtra(KEY_BOOLEAN_RESULT, result);
//        setAccountAuthenticatorResult(intent.getExtras());
//        setResult(RESULT_OK, intent);
//        finish();
//    }

    /**
     * Called when response is received from the server for authentication
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller. Also sets
     * the authToken in AccountManager for this account.
     */

    protected void finishLogin() {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mibarim.main", Context.MODE_PRIVATE);
        prefs.edit().putString("UserMobile", mobile).apply();

        final Account account = new Account(mobile, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

        authToken = token;

        //if (requestNewAccount) {
        accountManager.addAccountExplicitly(account, password, null);
        accountManager.setAuthToken(account, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE, authToken);
//        } else {
//            accountManager.setPassword(account, password);
//        }
    }

    protected void finishIt() {
        if (mobile == null) {
            SharedPreferences prefs = this.getSharedPreferences(
                    "com.mibarim.main", Context.MODE_PRIVATE);
            mobile = prefs.getString("UserMobile", "");
        }
        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_NAME, mobile);
        intent.putExtra(KEY_ACCOUNT_TYPE, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

//        if (authTokenType != null
//                && authTokenType.equals(Constants.Auth.AUTHTOKEN_TYPE)) {
//            intent.putExtra(KEY_AUTHTOKEN, authToken);
//        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

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

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param result
     */
    public void onAuthenticationResult(final boolean result) {
        if (result) {
            finishLogin();
            SharedPreferences prefs = this.getSharedPreferences(
                    "com.mibarim.main", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("sentTokenToServer", false).apply();
            if (loginResponse.isMobileConfirmed) {
                prefs.edit().putInt("MobileValidated", 1).apply();
                finishIt();
            } else {
                prefs.edit().putInt("MobileValidated", 0).apply();
                gotoMobileValidationActivity();
            }

        } else {
            Timber.d("onAuthenticationResult: failed to authenticate");
            Toaster.showLong(AuthenticatorActivity.this, loginResponse.error, R.drawable.toast_error);

            /*if (requestNewAccount) {
                Toaster.showLong(AuthenticatorActivity.this,
                        string.message_auth_failed_new_account);
            } else {
                Toaster.showLong(AuthenticatorActivity.this,
                        string.message_auth_failed);
            }*/
        }
    }

    private void gotoMobileValidationActivity() {
        final Intent intent = new Intent(AuthenticatorActivity.this, SmsValidationActivity.class);
        intent.putExtra("MobileNo", mobile);
        intent.putExtra("AuthToken", token);
        AuthenticatorActivity.this.startActivityForResult(intent, ConfirmResponse);
    }

    public void gotoRegister(View view) {
        gotoRegister();
    }

    private void gotoRegister() {
        final Intent intent = new Intent(AuthenticatorActivity.this, RegisterActivity.class);
        AuthenticatorActivity.this.startActivityForResult(intent, RegisterActivityResponse);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RegisterActivityResponse && resultCode == RESULT_OK && data != null) {
            requestNewAccount = true;
            mobile = data.getStringExtra(Constants.Auth.REG_MOBILE);
            password = data.getStringExtra(Constants.Auth.REG_PASSWORD);
            handleLogin();
        } else if (requestCode == ConfirmResponse && resultCode == RESULT_OK) {
            finishIt();
        }
    }
}
