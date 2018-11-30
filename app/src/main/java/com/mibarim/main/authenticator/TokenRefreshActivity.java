package com.mibarim.main.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.R;
import com.mibarim.main.R.layout;
import com.mibarim.main.R.string;
import com.mibarim.main.core.Constants;
import com.mibarim.main.events.UnAuthorizedErrorEvent;
import com.mibarim.main.models.TokenResponse;
import com.mibarim.main.services.AuthenticateService;
import com.mibarim.main.ui.TextWatcherAdapter;
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
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static com.mibarim.main.core.Constants.Auth.AUTHTOKEN_TYPE;
import static com.mibarim.main.core.Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

/**
 * Activity to register the user against an API
 */
public class TokenRefreshActivity extends ActionBarAccountAuthenticatorActivity {

    /**
     * PARAM_CONFIRM_CREDENTIALS
     */
    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";

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


    private SafeAsyncTask<Boolean> authenticationTask;
    private String authToken;
    private String authTokenType;
    private Account theAccount;

    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password to be changed on the device.
     */
    private Boolean confirmCredentials = false;

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

        BootstrapApplication.component().inject(this);

        accountManager = AccountManager.get(this);

        setContentView(layout.token_refresh_activity);

        ButterKnife.bind(this);

        handleLogin();
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


    public void handleLogin() {
        if (authenticationTask != null) {
            return;
        }
//        Toaster.showLong(TokenRefreshActivity.this, getString(R.string.access_error), R.drawable.toast_warn);
        authenticationTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {

                final AccountManagerFuture<Bundle> accountManagerFuture
                        = accountManager.getAuthTokenByFeatures(BOOTSTRAP_ACCOUNT_TYPE,
                        AUTHTOKEN_TYPE, new String[0], TokenRefreshActivity.this, null, null, null, null);
                authToken=accountManagerFuture.getResult().getString(KEY_AUTHTOKEN);
                accountManager.invalidateAuthToken(Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE, authToken);
                mobile = accountManagerFuture.getResult().getString(KEY_ACCOUNT_NAME);
                Account[] account = accountManager.getAccountsByType(BOOTSTRAP_ACCOUNT_TYPE);
                if (account[0] != null) {
                    theAccount = account[0];
                    password = accountManager.getPassword(theAccount);
                }

                loginResponse = authenticateService.authenticate(mobile, password, PARAM_GRANT_TYPE, PARAM_REPONSE_TYPE);
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
                        Toaster.showLong(TokenRefreshActivity.this, cause.getMessage());
                    }
                }else {
                    finishIt();
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

    protected void finishLogin() {
        final Account account = new Account(mobile, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
        accountManager.invalidateAuthToken(Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE,authToken);
        authToken = token;
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
        setResult(RESULT_OK, intent);
        finish();
    }

    protected  void finishIt(){
        Intent intent=getIntent();
        setResult(RESULT_CANCELED, intent);
        Toaster.showLong(TokenRefreshActivity.this, getString(string.network_error), R.drawable.toast_error);
        finish();
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param result
     */
    public void onAuthenticationResult(final boolean result) {
        if (result) {
            finishLogin();
        } else {
            Timber.d("onAuthenticationResult: failed to authenticate");
            Toaster.showLong(TokenRefreshActivity.this, loginResponse.error, R.drawable.toast_error);
        }
    }
}
