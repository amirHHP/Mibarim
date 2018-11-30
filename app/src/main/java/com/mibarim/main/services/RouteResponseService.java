package com.mibarim.main.services;

import com.mibarim.main.RestInterfaces.GetRouteResponseService;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.util.Strings;

import retrofit.RestAdapter;

/**
 * Created by Hamed on 3/10/2016.
 */
public class RouteResponseService {

    private RestAdapter restAdapter;

    public RouteResponseService(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    public RestAdapter getRestAdapter() {
        return restAdapter;
    }

    private GetRouteResponseService getService() {
        return getRestAdapter().create(GetRouteResponseService.class);
    }

    public ApiResponse GetRoutes(String authToken) {
        ApiResponse res = getService().GetUserRoutes("Bearer " + authToken,"7");//7 is for retrofit problem
        return res;
    }

    public ApiResponse GetRouteSuggests(String authToken, long routeId) {
        ApiResponse res = getService().GetRouteSuggest("Bearer " + authToken, routeId);
        return res;
    }

    public ApiResponse GetPassengerRoutes(String authToken, long filterId) {
        ApiResponse res = getService().GetPassengerRoutes("Bearer " + authToken, filterId);
        return res;
    }


    public ApiResponse GetRouteSimilarSuggests(String authToken, long contactId) {
        ApiResponse res = getService().GetRouteSimilarSuggests("Bearer " + authToken, contactId);
        return res;
    }

    public ApiResponse setTripPoint(String authToken, String lat, String lng, long tripId, int tripState) {
        ApiResponse res = getService().SetTripPoint("Bearer " + authToken, lat, lng, tripId, tripState);
        return res;
    }

    public boolean cancelTrip(String authToken, int tripId) {
        boolean res = getService().cancelTrip("Bearer " + authToken, tripId);
        return res;
    }

    public ApiResponse GetMainStations(String authToken) {
        ApiResponse res = getService().GetMainStations("Bearer " + authToken, 1);
        return res;
    }

    public ApiResponse GetAllSubStations(String authToken) {
        ApiResponse res = getService().GetAllSubStations("Bearer " + authToken, "testtext");
        return res;
    }

    public ApiResponse GetSubStations(String authToken, long mainStationId) {
        ApiResponse res = getService().GetSubStations("Bearer " + authToken, mainStationId);
        return res;
    }


    public ApiResponse GetFilters(String authToken) {
        ApiResponse res = getService().GetFilters("Bearer " + authToken, "testtext");
        return res;
    }

    public ApiResponse GetTimes(String authToken, long filterId) {
        ApiResponse res = getService().GetTimes("Bearer " + authToken, filterId);
        return res;
    }


    public ApiResponse GetPassengerTrip(String authToken, long filterId) {
        ApiResponse res = getService().GetPassengerTrip("Bearer " + authToken, filterId);
        return res;
    }



}
