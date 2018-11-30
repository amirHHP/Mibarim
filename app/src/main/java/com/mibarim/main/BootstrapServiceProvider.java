package com.mibarim.main;

import android.accounts.AccountsException;
import android.app.Activity;

import com.mibarim.main.services.AuthenticateService;

import java.io.IOException;

public interface BootstrapServiceProvider {
    AuthenticateService getService(Activity activity) throws IOException, AccountsException;

    String getAuthToken(Activity activity) throws IOException, AccountsException;

    void invalidateAuthToken();
}
