package com.mibarim.main.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.R;
import com.mibarim.main.adapters.SubstationListAdapter;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.RouteResponse;
import com.mibarim.main.models.StationModel;
import com.mibarim.main.models.SubStationModel;
import com.mibarim.main.services.RouteResponseService;
import com.mibarim.main.ui.ThrowableLoader;
import com.mibarim.main.ui.activities.SearchStationActivity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Alireza on 10/9/2017.
 */

public class SubstationListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<SubStationModel>> {


    /*    @Inject
        UserData userData;
        @Inject
        protected BootstrapServiceProvider serviceProvider;*/
    @Inject
    protected RouteResponseService routeResponseService;
/*    @Inject
    protected LogoutService logoutService;*/

    private int RELOAD_REQUEST = 1234;
    List<SubStationModel> items;
    List<SubStationModel> latest;
    private Tracker mTracker;
    private RouteResponse routeResponse;
    private ApiResponse stationsResponse;

    private View mRecycler;
    private RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyView;
    private RelativeLayout substation_layout;
    //private ProgressBar mProgressView;
    private SubstationListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int selectedRow;
    private StationModel stationRouteModel;
    ItemTouchListener itemTouchListener;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
        // Obtain the shared Tracker instance.
        BootstrapApplication application = (BootstrapApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        mRecycler = inflater.inflate(R.layout.station_card_list, null);

        mRecyclerView = (RecyclerView) mRecycler.findViewById(android.R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRecycler.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mEmptyView = (TextView) mRecycler.findViewById(android.R.id.empty);
        mEmptyView.setVisibility(View.GONE);

        substation_layout = (RelativeLayout) mRecycler.findViewById(R.id.substation_layout);
        substation_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((SearchStationActivity) getActivity()).removeSubStation();
                    return true;
                }
                return false;
            }
        });
        /*mProgressView = (ProgressBar) mRecycler.findViewById(R.id.pb_loading);*/

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //showState(1);

        itemTouchListener = new ItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
                if (getActivity() instanceof SearchStationActivity) {
                    SubStationModel selectedItem = ((SubStationModel) items.get(position));

                    ((SearchStationActivity) getActivity()).setOriginSubStationId(selectedItem.StationId);

                    Toast.makeText(getActivity(), R.string.origin_selected , Toast.LENGTH_SHORT).show();
//                    ((SearchStationActivity) getActivity()).setFilter(selectedItem);
                    ((SearchStationActivity) getActivity()).removeSubStation();
                    ((SearchStationActivity) getActivity()).checkToSeeWhatToDo();

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


        return mRecycler;
    }

    public void refresh() {
        getLoaderManager().restartLoader(0, null, this);
        //showState(1);
        mAdapter = new SubstationListAdapter(getActivity(), items, itemTouchListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTracker.setScreenName("RoutesCardFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Fragment").setAction("RoutesCardFragment").build());
        getLoaderManager().initLoader(0, null, this);
        //setEmptyText(R.string.no_routes);
    }

    @Override
    public Loader<List<SubStationModel>> onCreateLoader(int id, Bundle args) {
        mEmptyView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(true);
        stationRouteModel = ((SearchStationActivity) getActivity()).getRoute();
        items = new ArrayList<SubStationModel>();
        return new ThrowableLoader<List<SubStationModel>>(getActivity(), items) {
            @Override
            public List<SubStationModel> loadData() throws Exception {
                latest = new ArrayList<SubStationModel>();
                Gson gson = new Gson();
                if (getActivity() != null) {


                    String authToken = ((SearchStationActivity) getActivity()).getToken();

//                    stationsResponse = routeResponseService.GetStations(stationRouteModel.MainStationId);

                    stationsResponse = routeResponseService.GetSubStations(authToken, stationRouteModel.MainStationId);


                    if (stationsResponse != null) {
                        for (String routeJson : stationsResponse.Messages) {
                            SubStationModel route = gson.fromJson(routeJson, SubStationModel.class);
                            latest.add(route);
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
    public void onLoadFinished(Loader<List<SubStationModel>> loader, List<SubStationModel> data) {
        items = data;
        /*if (items.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        }*/
        // specify an adapter (see also next example)
        mAdapter = new SubstationListAdapter(getActivity(), items, itemTouchListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new SwipeableRecyclerViewTouchListener(mRecyclerView,
                new SwipeableRecyclerViewTouchListener.SwipeListener() {
                    @Override
                    public boolean canSwipeLeft(int position) {
                        return true;
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
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<SubStationModel>> loader) {

    }

    public interface ItemTouchListener {
        public void onCardViewTap(View view, int position);
        //public void onBookBtnClick(View view, int position);
        //public void onUserImageClick(View view, int position);

    }


}
