package com.mibarim.main;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.telephony.TelephonyManager;

import com.mibarim.main.authenticator.ApiKeyProvider;
import com.mibarim.main.authenticator.LogoutService;
import com.mibarim.main.authenticator.LogoutServiceImpl;
import com.mibarim.main.core.PostFromAnyThreadBus;
import com.mibarim.main.data.UserData;
import com.mibarim.main.services.AuthenticateService;
import com.mibarim.main.core.Constants;
import com.mibarim.main.core.RestAdapterRequestInterceptor;
import com.mibarim.main.core.RestErrorHandler;
import com.mibarim.main.core.UserAgentProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mibarim.main.services.RegisterService;
import com.mibarim.main.services.RouteRequestService;
import com.mibarim.main.services.RouteResponseService;
import com.mibarim.main.services.UserImageService;
import com.mibarim.main.services.UserInfoService;
import com.mibarim.main.util.DynamicJsonConverter;
//import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module
public class BootstrapModule {
    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new PostFromAnyThreadBus();
    }

    @Provides
    @Singleton
    LogoutService provideLogoutService(final Context context, final AccountManager accountManager) {
        return new LogoutServiceImpl(context, accountManager);
    }


    @Provides
    AuthenticateService provideBootstrapService(RestAdapter restAdapter) {
        return new AuthenticateService(restAdapter);
    }

    @Provides
    RegisterService provideRegisterService(RestAdapter restAdapter) {
        return new RegisterService(restAdapter);
    }

    @Provides
    RouteRequestService provideRouteService(RestAdapter restAdapter) {
        return new RouteRequestService(restAdapter);
    }

    @Provides
    UserInfoService provideUserInfoService(RestAdapter restAdapter) {
        return new UserInfoService(restAdapter);
    }


    @Provides
    RouteResponseService provideRouteResponseService(RestAdapter restAdapter) {
        return new RouteResponseService(restAdapter);
    }

    @Provides
    UserImageService provideUserImageService(Bus bus) {
        return new UserImageService(new RestErrorHandler(bus));
    }

    @Provides
    UserData provideUserdata(final Context context) {
        return new UserData(context);
    }


    @Provides
    BootstrapServiceProvider provideBootstrapServiceProvider(RestAdapter restAdapter, ApiKeyProvider apiKeyProvider) {
        return new BootstrapServiceProviderImpl(restAdapter, apiKeyProvider);
    }

    @Provides
    ApiKeyProvider provideApiKeyProvider(AccountManager accountManager) {
        return new ApiKeyProvider(accountManager);
    }

    @Provides
    Gson provideGson() {
        /**
         * GSON instance to use for all request  with date format set up for proper parsing.
         * <p/>
         * You can also configure GSON with different naming policies for your API.
         * Maybe your API is Rails API and all json values are lower case with an underscore,
         * like this "first_name" instead of "firstName".
         * You can configure GSON as such below.
         * <p/>
         *
         * public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd")
         *         .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
         */
        return new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd").create();
    }

    @Provides
    RestErrorHandler provideRestErrorHandler(Bus bus) {
        return new RestErrorHandler(bus);
    }

    @Provides
    UserAgentProvider providesUserAgentProvider(ApplicationInfo appInfo, PackageInfo packageInfo, TelephonyManager telephonyManager, ClassLoader classLoader) {
        return new UserAgentProvider(appInfo, packageInfo, telephonyManager, classLoader);
    }

    /*@Provides
    UserTokenProvider providesTokenAgentProvider(ApiKeyProvider apiKeyProvider) {
        return new UserTokenProvider(apiKeyProvider);
    }*/

    @Provides
    RestAdapterRequestInterceptor provideRestAdapterRequestInterceptor(UserAgentProvider userAgentProvider) {
        return new RestAdapterRequestInterceptor();
    }

    @Provides
    RestAdapter provideRestAdapter(RestErrorHandler restErrorHandler, RestAdapterRequestInterceptor restRequestInterceptor, Gson gson) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(120, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(120, TimeUnit.SECONDS);

        return new RestAdapter.Builder()
                .setEndpoint(Constants.Http.URL_BASE)
                .setErrorHandler(restErrorHandler)
                .setRequestInterceptor(restRequestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new DynamicJsonConverter())
                .setClient(new OkClient(okHttpClient))
                .build();
    }


}
