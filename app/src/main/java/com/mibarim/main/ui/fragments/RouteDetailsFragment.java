package com.mibarim.main.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.R;
import com.mibarim.main.adapters.RouteDetailsAdapter;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.Plus.PassRouteModel;
import com.mibarim.main.models.UserInfoModel;
import com.mibarim.main.services.RouteResponseService;
import com.mibarim.main.ui.ThrowableLoader;
import com.mibarim.main.ui.activities.MainActivity;
import com.mibarim.main.ui.activities.MainCardActivity;
import com.mibarim.main.ui.activities.UserInfoDetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class RouteDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<PassRouteModel>> {

    // class declarations
    @Inject
    protected RouteResponseService routeResponseService;

    // variable declarations
    List<PassRouteModel> items;
    List<PassRouteModel> latest;
    private ApiResponse suggestRouteResponse;
    private RouteDetailsAdapter mAdapter;
    private RecyclerView mRecyclerView;
    ItemTouchListener itemTouchListener;
    private UserInfoModel userInfoModel;
    private RecyclerView.LayoutManager mLayoutManager;
    LinearLayout emptyLayout;
    Button cancelTripButton;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Button returnToRouteDetailsFragmentButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                                "com.mibarim.main", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putInt("AllowBackButton", 0).apply();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        mRecycler = inflater.inflate(R.layout.reload_card_list, null);


        Bundle bundle = getArguments();
        String srcText = "DUMMY ACTION";
//        Bitmap imageBitmap = null;
        String transText = "";
        String transTextDest = "";
        String destText = "";
        String transitionName = "";

        if (bundle != null) {
            transitionName = bundle.getString("TRANS_NAME");
            destText = bundle.getString("DESTINATION_TEXT");
            srcText = bundle.getString("SOURCE_TEXT");
//            imageBitmap = bundle.getParcelable("IMAGE");
            transText = bundle.getString("TRANS_TEXT");
            transTextDest = bundle.getString("DEST_TRANS");
        }

        getActivity().setTitle(srcText);
        View view = inflater.inflate(R.layout.route_details_fragment, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.details_list);

        emptyLayout = (LinearLayout) view.findViewById(R.id.empty_layout);


        cancelTripButton = (Button) view.findViewById(R.id.cancel_trip_button);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.m_swipe_refresh_layout);

        returnToRouteDetailsFragmentButton = (Button) view.findViewById(R.id.return_to_route_filters_fragment);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        returnToRouteDetailsFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                        "com.mibarim.main", Context.MODE_PRIVATE);
                sharedPreferences.edit().putInt("AllowBackButton", 1).apply();
                ((MainActivity) getActivity()).removeCurrentFragmentAndAddRouteFilterFragment();

//                ((MainActivity) getActivity()).showFloatingActionButton();

            }
        });


        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);



        cancelTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelOrNotDialog();



            }
        });

        itemTouchListener = new RouteDetailsFragment.ItemTouchListener() {

            @Override
            public void onBookBtnClick(View view, int position) {
                if (getActivity() instanceof MainActivity) {
                    PassRouteModel selectedItem = ((PassRouteModel) items.get(position));
                    if (selectedItem.IsBooked) {

                        if (userInfoModel.UserImageId == null) {
//                            if (prefs.getInt("UserPhotoUploadedFirstTry", 2) != 1) {
                            Intent j = new Intent(getActivity(), UserInfoDetailActivity.class);
                            startActivity(j);
//                            }
                        }


                        ((MainActivity) getActivity()).gotoRidingActivity(selectedItem);

                    } else {
                        ((MainActivity) getActivity()).gotoPayActivity(selectedItem);
                    }
                }
            }

            @Override
            public void onSrcLinkClick(View view, int position) {
                if (getActivity() instanceof MainCardActivity) {
                    PassRouteModel selectedItem = ((PassRouteModel) items.get(position));
                    ((MainCardActivity) getActivity()).gotoWebView(selectedItem.SrcLink);
                }
            }

            @Override
            public void onDstLinkClick(View view, int position) {
                if (getActivity() instanceof MainCardActivity) {
                    PassRouteModel selectedItem = ((PassRouteModel) items.get(position));
                    ((MainCardActivity) getActivity()).gotoWebView(selectedItem.DstLink);
                }
            }
            /*@Override
            public void onCardViewTap(View view, int position) {
                //PassRouteModel selectedItem = ((PassRouteModel) items.get(position));
                *//*String srcLat = latest.get(position).SrcLatitude;
                String srcLng = latest.get(position).SrcLongitude;
                String dstLat = latest.get(position).DstLatitude;
                String dstLng = latest.get(position).DstLongitude;
                PathPoint pathRoute= latest.get(position).PathRoute;*//*
                *//*((SuggestRouteActivity) getActivity()).setSelectedRoute(selectedItem);
                ((SuggestRouteActivity) getActivity()).setRouteSrcDst(srcLat, srcLng, dstLat, dstLng,pathRoute, position);*//*
                //((SuggestRouteCardActivity) getActivity()).gotoTripProfile(selectedItem);

            }*/

            /*@Override
            public void onUserImageClick(View view, int position) {
                *//*PassRouteModel model = items.get(position);
                ContactModel contactModel = new ContactModel();
                //dirty coding-It's temporary Used
                contactModel.ContactId = model.TripId;
                contactModel.Name = model.Name;
                contactModel.Family = model.Family;
                contactModel.UserImageId = model.UserImageId;*//*
                //((SuggestRouteActivity) getActivity()).goToContactActivity(contactModel);

            }*/

        };


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.findViewById(R.id.listImage).setTransitionName(transitionName);
            view.findViewById(R.id.source_name).setTransitionName(transText);
            view.findViewById(R.id.destination_name).setTransitionName(transTextDest);
        }

//        ((ImageView) view.findViewById(R.id.listImage)).setImageBitmap(imageBitmap);
        ((TextView) view.findViewById(R.id.source_name)).setText(srcText);
        ((TextView) view.findViewById(R.id.destination_name)).setText(destText);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<PassRouteModel>> onCreateLoader(int id, Bundle args) {
//        mEmptyView.setVisibility(View.GONE);

        mSwipeRefreshLayout.setRefreshing(true);
        items = new ArrayList<PassRouteModel>();
        return new ThrowableLoader<List<PassRouteModel>>(getActivity(), items) {
            @Override
            public List<PassRouteModel> loadData() throws Exception {
                latest = new ArrayList<PassRouteModel>();
                if (getActivity() instanceof MainActivity) {
                    Gson gson = new Gson();
                    PassRouteModel bookedTrip = null;
//                    routeResponse = ((MainCardActivity) getActivity()).getRoute();
                    String authToken = ((MainActivity) getActivity()).getAuthToken();
                    if (getActivity() != null && authToken != null) {

                        long filterId = ((MainActivity) getActivity()).getChosenFilter();
                        suggestRouteResponse = routeResponseService.GetPassengerTrip(authToken, filterId);
                        if (suggestRouteResponse != null) {
                            for (String routeJson : suggestRouteResponse.Messages) {
                                PassRouteModel route = gson.fromJson(routeJson, PassRouteModel.class);
                                /*if (route.IsBooked) {
                                    bookedTrip = route;
                                }*/
                                latest.add(route);
                            }
                            /*if (bookedTrip != null) {
                                if (bookedTrip.TripState == TripStates.InPreTripTime.toInt() ||
                                        bookedTrip.TripState == TripStates.InRiding.toInt() ||
                                        bookedTrip.TripState == TripStates.InTripTime.toInt()) {
                                    ((MainCardActivity) getActivity()).gotoRidingActivity(bookedTrip);
                                }else{
                                    ((MainCardActivity) getActivity()).showRidingActivity(bookedTrip);
                                }
                            }*/
                        }
                    }
                }
                if (latest != null) {
                    return latest;
                } else {
                    return Collections.emptyList();
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<PassRouteModel>> loader, List<PassRouteModel> data) {

        mSwipeRefreshLayout.setRefreshing(false);
        items = data;
        if (!items.get(0).IsBooked) {
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            PassRouteModel model = items.get(0);
            ((MainActivity) getActivity()).gotoRidingActivity(model);
        }



        /*mAdapter = new RouteDetailsAdapter(getActivity(), items, itemTouchListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new SwipeableRecyclerViewTouchListener(mRecyclerView,
                new SwipeableRecyclerViewTouchListener.SwipeListener() {
                    @Override
                    public boolean canSwipeLeft(int position) {
                        return false;
                    }

                    @Override
                    public boolean canSwipeRight(int position) {
                        return false;
                    }

                    @Override
                    public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                    }

                    @Override
                    public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                    }
                }));
        mSwipeRefreshLayout.setRefreshing(false);*/
    }

    @Override
    public void onLoaderReset(Loader<List<PassRouteModel>> loader) {

    }


    public interface ItemTouchListener {
        //public void onCardViewTap(View view, int position);
        public void onBookBtnClick(View view, int position);

        public void onSrcLinkClick(View view, int position);

        public void onDstLinkClick(View view, int position);
        //public void onUserImageClick(View view, int position);

    }


    public void showCancelOrNotDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.cancel_card)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        /*SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                                "com.mibarim.main", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putInt("AllowBackButton", 1).apply();*/

                        long filterId = ((MainActivity) getActivity()).getChosenFilter();
                        ((MainActivity) getActivity()).cancelTrip(filterId);

                        ((MainActivity) getActivity()).removeRouteDetailsFragment();

                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                }).show();

    }

    public void refresh(){
        getLoaderManager().restartLoader(0, null, this);
    }

}
