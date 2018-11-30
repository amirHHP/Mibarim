package com.mibarim.main.core;


import retrofit.RequestInterceptor;

public class CustomRestAdapterRequestInterceptor implements RequestInterceptor {


    public CustomRestAdapterRequestInterceptor() {
    }

    @Override
    public void intercept(RequestFacade request) {

        // Add header to set content type of JSON
        request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        // Add request authentication token.
        //request.addHeader("Authorization", userAgentProvider.get());

        // Add the user agent to the request.
        //request.addHeader("User-Agent", userAgentProvider.get());


        // Add auth info for PARSE, normally this is where you'd add your auth info for this request (if needed).
//        request.addHeader(HEADER_PARSE_REST_API_KEY, PARSE_REST_API_KEY);
//        request.addHeader(HEADER_PARSE_APP_ID, PARSE_APP_ID);

    }
    /*
    private UserAgentProvider userAgentProvider;

    public RestAdapterRequestInterceptor(UserAgentProvider userAgentProvider) {
        this.userAgentProvider = userAgentProvider;
    }

    @Override
    public void intercept(RequestFacade request) {

        // Add header to set content type of JSON
        request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        // Add auth info for PARSE, normally this is where you'd add your auth info for this request (if needed).
        request.addHeader(HEADER_PARSE_REST_API_KEY, PARSE_REST_API_KEY);
        request.addHeader(HEADER_PARSE_APP_ID, PARSE_APP_ID);

        // Add the user agent to the request.
        request.addHeader("User-Agent", userAgentProvider.get());
    }*/
}

