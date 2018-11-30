package com.mibarim.main.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.R;
import com.mibarim.main.adapters.SuggestedTimesAdapter;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.FilterModel;
import com.mibarim.main.models.FilterTimeModel;
import com.mibarim.main.services.RouteRequestService;
import com.mibarim.main.services.RouteResponseService;
import com.mibarim.main.ui.activities.MainActivity;
import com.mibarim.main.util.SafeAsyncTask;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.mibarim.main.core.Constants.GlobalConstants.ALLOW_BACK_BUTTON;

/**
 * Created by Alireza on 11/22/2017.
 */

public class SuggestedTimesFragment extends Fragment {

    @Inject
    RouteResponseService routeResponseService;
    @Inject
    RouteRequestService routeRequestService;


    private ApiResponse suggestedTimesResponse;
    String authToken;
    long filterId;
    List<FilterTimeModel> items;
    ListView suggestedTimesList;
    private ApiResponse setRes;
    String suggestedTimePrice;
    Button suggestTimeButton;
    FrameLayout footerView;
    SwipeRefreshLayout swipeRefreshLayout;
    Button proceedButton;

    int min;
    int hour;
    int itemSelected;

    NumberPicker hourPicker;
    NumberPicker minutePicker;
    boolean isManual;

    View alertLayout;
    View titleLayout;
    View temp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);

//        BootstrapApplication application = (BootstrapApplication) getApplication();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.suggested_times_fragment, container, false);


        alertLayout = inflater.inflate(R.layout.time_picker_dialog, null);
        titleLayout = inflater.inflate(R.layout.timepicker_dialog_custom_title, null);

        authToken = ((MainActivity) getActivity()).getAuthToken();
        filterId = ((MainActivity) getActivity()).getChosenFilter();

        suggestedTimesList = (ListView) view.findViewById(R.id.suggested_times_list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);


        footerView = (FrameLayout) inflater.inflate(R.layout.button_under_suggested_times_fragment, null);
        suggestTimeButton = (Button) footerView.findViewById(R.id.suggest_time);

        filterId = ((MainActivity) getActivity()).getChosenFilter();

        proceedButton = (Button) view.findViewById(R.id.proceed_button);

        itemSelected = 0;


        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Object selectedItem = suggestedTimesList.getSelectedItem();
//                suggestedTimesList.clearChoices();
//                suggestedTimesList.requestLayout();


                if (itemSelected == 0)
                    showDialog();
                else {



                    if (isManual) {
                        ((MainActivity) getActivity()).showSuggestTimeDialog(filterId);
                    } else {

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                                "com.mibarim.main", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putInt(ALLOW_BACK_BUTTON, 1).apply();

                        sendSuggestedFilterToServer(filterId, hour, min);
                    }
                }

            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                itemSelected = 0;
                getTimesFromServer();
            }
        });


        suggestTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showSuggestTimeDialog(filterId);
            }
        });

//        suggestedTimesList.addFooterView(footerView);

        items = new ArrayList<>();

        swipeRefreshLayout.setRefreshing(true);
        getTimesFromServer();


        suggestedTimesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FilterTimeModel model = items.get(position);


                /*SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                        "com.mibarim.main", Context.MODE_PRIVATE);
                sharedPreferences.edit().putInt(ALLOW_BACK_BUTTON, 1).apply();*/

//                ((MainActivity) getActivity()).showSuggestTimeDialog(filterId);


                view.setSelected(true);

                suggestedTimesList.setItemChecked(position, true); //

                view.setBackgroundColor(getResources().getColor(R.color.pressed_color));
                if (temp != null && temp!= view) {
                    temp.setBackgroundColor(getResources().getColor(R.color.white));
                }
                temp = view;


                itemSelected = 1;


                isManual = model.IsManual;

                hour = model.TimeHour;
                min = model.TimeMinute;


//                sendSuggestedFilterToServer(filterId, hour, min);


//                setRes = routeRequestService.setSuggestedFilter(authToken, model.FilterId, model.TimeHour, model.TimeMinute);
            }
        });


        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    private void getTimesFromServer() {
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {

                items = new ArrayList<>();

                suggestedTimesResponse = routeResponseService.GetTimes(authToken, filterId);
                Gson gson = new Gson();

                if (suggestedTimesResponse != null) {
                    for (String json : suggestedTimesResponse.Messages) {
                        FilterTimeModel timeModel = gson.fromJson(json, FilterTimeModel.class);
                                /*if (route.IsBooked) {
                                    bookedTrip = route;
                  Status = "BadRequest"              }*/
//                        if (!timeModel.IsManual)
                        items.add(timeModel);
                        if (timeModel.IsManual)
                            suggestedTimePrice = timeModel.PriceString;

                    }
                }


                return false;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);

                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
                // User cancelled the authentication process (back button, etc).
                // Since auth could not take place, lets finish this activity.
//                    finish();

            }

            @Override
            protected void onSuccess(final Boolean hasAuthenticated) throws Exception {
                super.onSuccess(hasAuthenticated);
                //userHasAuthenticated = true;
//                initScreen();
//                sendRegistrationToServer();
                prepareTheSuggestedTimesList();
            }
        }.execute();
    }

    public void prepareTheSuggestedTimesList() {
        SuggestedTimesAdapter timesAdapter = new SuggestedTimesAdapter(getActivity(), R.layout.suggested_times_list_item, items);
        suggestedTimesList.setAdapter(timesAdapter);

        suggestedTimesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        String text = (String) suggestTimeButton.getText();
        suggestTimeButton.setText(text + "(قیمت: " + suggestedTimePrice + ")");

        swipeRefreshLayout.setRefreshing(false);

    }


    public void sendSuggestedFilterToServer(final long id, final int hour, final int min) {

        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {

//                authToken = ((MainActivity) getActivity()).getAuthToken();
                setRes = routeRequestService.setSuggestedFilter(authToken, id, hour, min);


                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
//                progressDialog.hide();
//                Toast.makeText(SearchStationActivity.this, "خطا در انتخاب مسیر", Toast.LENGTH_LONG).show();
//                hideProgress();
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
//                hideProgress();
                ((MainActivity) getActivity()).removeCurrentFragmentAndAddRouteFilterFragment();
//                new HandleApiMessagesBySnackbar(parentLayout, setRes).showMessages();
//                Gson gson = new Gson();
//                FilterModel filterModel = new FilterModel();
//                for (String shareJson : setRes.Messages) {
//                    filterModel = gson.fromJson(shareJson, FilterModel.class);
//                }
//                if (filterModel.FilterId > 0) {
////                    progressDialog.hide();
////                    finishIt();
//                }


            }
        }.execute();


    }


    public void allowBackPressed() {

    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.suggested_times_dialog)
                .setPositiveButton(R.string.suggested_times_dialog_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        /*SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                                "com.mibarim.main", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putInt(ALLOW_BACK_BUTTON, 1).apply();*/

                        ((MainActivity) getActivity()).showSuggestTimeDialog(filterId);

                    }
                })
                .setNegativeButton(R.string.suggested_times_dialog_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                }).show();
    }


}
