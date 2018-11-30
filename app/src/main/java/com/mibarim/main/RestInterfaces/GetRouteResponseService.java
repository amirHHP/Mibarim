package com.mibarim.main.RestInterfaces;

import com.mibarim.main.core.Constants;
import com.mibarim.main.models.ApiResponse;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by Hamed on 3/10/2016.
 */
public interface GetRouteResponseService {
    @POST(Constants.Http.URL_GET_ROUTE)
    @FormUrlEncoded
    ApiResponse GetUserRoutes(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                      @Field("UserId") String Id);


    @POST(Constants.Http.SUGGEST_ROUTE_URL)
    @FormUrlEncoded
    ApiResponse GetRouteSuggest(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                        @Field("RouteRequestId") long Id);
    @POST(Constants.Http.GET_PASSENGER_ROUTE_URL)
    @FormUrlEncoded
    ApiResponse GetPassengerRoutes(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                @Field("FilteringId") long Id);

    @POST(Constants.Http.SIMILAR_SUGGEST_ROUTE_URL)
    @FormUrlEncoded
    ApiResponse GetRouteSimilarSuggests(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                         @Field("ContactId") long Id);

    @POST(Constants.Http.URL_SET_TRIP_LOCATION)
    @FormUrlEncoded
    ApiResponse SetTripPoint(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                             @Field("SrcLatitude") String lat,
                             @Field("SrcLongitude") String lng,
                             @Field("TripId") long tripId,
                             @Field("TripState") int tripState
    );

    @POST(Constants.Http.URL_CANCEL_RESERVE)
    @FormUrlEncoded
    boolean cancelTrip(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                             @Field("TripId") long tripId
    );

    @POST(Constants.Http.GET_MAIN_STATIONS_URL)
    @FormUrlEncoded
    ApiResponse GetMainStations(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                @Field("None") long Id);

    @POST(Constants.Http.GET_ALL_SUB_STATIONS_URL)
    @FormUrlEncoded
    ApiResponse GetAllSubStations(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                  @Field("TestText") String Id);

    @POST(Constants.Http.GET_SUB_STATIONS_URL)
    @FormUrlEncoded
    ApiResponse GetSubStations(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                               @Field("MainStationId") long Id);

    @POST(Constants.Http.GET_FILTERS)
    @FormUrlEncoded
    ApiResponse GetFilters(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                               @Field("testtext") String testtext);

    @POST(Constants.Http.GET_TIMES)
    @FormUrlEncoded
    ApiResponse GetTimes(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                           @Field("FilterId") long filterId);

    @POST(Constants.Http.GET_PASSENGER_TRIP_URL)
    @FormUrlEncoded
    ApiResponse GetPassengerTrip(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                   @Field("FilteringId") long Id);

}
