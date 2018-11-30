
package com.mibarim.main;

import android.accounts.AccountsException;
import android.app.Activity;

import com.mibarim.main.authenticator.ApiKeyProvider;
import com.mibarim.main.services.AuthenticateService;

import java.io.IOException;

import retrofit.RestAdapter;

/**
 * Provider for a {@link AuthenticateService} instance
 */
public class BootstrapServiceProviderImpl implements BootstrapServiceProvider {

    private RestAdapter restAdapter;
    private ApiKeyProvider keyProvider;
    private String authToken;

    public BootstrapServiceProviderImpl(RestAdapter restAdapter, ApiKeyProvider keyProvider) {
        this.restAdapter = restAdapter;
        this.keyProvider = keyProvider;
    }

    /**
     * Get service for configured key provider
     * <p/>
     * This method gets an auth key and so it blocks and shouldn't be called on the main thread.
     *
     * @return bootstrap service
     * @throws IOException
     * @throws AccountsException
     */
    @Override
    public AuthenticateService getService(final Activity activity)
            throws IOException, AccountsException {
        // The call to keyProvider.getAuthKey(...) is what initiates the login screen. Call that now.
        authToken=keyProvider.getAuthKey(activity);

        return new AuthenticateService(restAdapter);
    }

    public String getAuthToken(final Activity activity) throws IOException, AccountsException {
        if(authToken==null){
            authToken=keyProvider.getAuthKey(activity);
        }
        return authToken;
    }
    public void invalidateAuthToken(){
        authToken=null;
    }
}
