package com.mibarim.main.core;

import com.mibarim.main.events.NetworkErrorEvent;
import com.mibarim.main.events.RestAdapterErrorEvent;
import com.mibarim.main.events.UnAuthorizedErrorEvent;
import com.mibarim.main.events.WrongCredentialErrorEvent;
import com.squareup.otto.Bus;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

public class RestErrorHandler implements ErrorHandler {

    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int INVALID_LOGIN_PARAMETERS = 101;

    private Bus bus;

    public RestErrorHandler(Bus bus) {
        this.bus = bus;
    }

    @Override
    public Throwable handleError(RetrofitError cause) {
        if (cause != null) {
            if (cause.isNetworkError()) {
                bus.post(new NetworkErrorEvent(cause));
            } else if (isUnAuthorized(cause)) {
                bus.post(new UnAuthorizedErrorEvent(cause));
            } else if (wrongCredential(cause)) {
                bus.post(new WrongCredentialErrorEvent(cause));
            } else {
                bus.post(new RestAdapterErrorEvent(cause));
            }
        }

        // Example of how you'd check for a unauthorized result
        // if (cause != null && cause.getStatus() == 401) {
        //     return new UnauthorizedException(cause);
        // }

        // You could also put some generic error handling in here so you can start
        // getting analytics on error rates/etc. Perhaps ship your logs off to
        // Splunk, Loggly, etc

        return cause;
    }

    private boolean wrongCredential(RetrofitError cause) {
        if (cause.getResponse().getStatus() == HTTP_BAD_REQUEST) {
            final ApiError err = (ApiError) cause.getBodyAs(ApiError.class);
            return true;
        }
        return false;
    }

    /**
     * If a user passes an incorrect username/password combo in we could
     * get a unauthorized error back from the API. On parse.com this means
     * we get back a HTTP 404 with an error as JSON in the body as such:
     * <p/>
     * {
     * code: 101,
     * error: "invalid login parameters"
     * }
     * <p/>
     * }
     * <p/>
     * Therefore we need to check for the 101 and the 404.
     *
     * @param cause The initial error.
     * @return
     */
    private boolean isUnAuthorized(RetrofitError cause) {
        boolean authFailed = false;

        if (cause.getResponse().getStatus() == HTTP_UNAUTHORIZED) {
            authFailed = true;
        }

        return authFailed;
    }
}
