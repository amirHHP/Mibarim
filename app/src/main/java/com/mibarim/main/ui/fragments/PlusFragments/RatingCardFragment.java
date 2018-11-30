/*
package com.mibarim.main.ui.fragments.PlusFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.BootstrapServiceProvider;
import com.mibarim.main.R;
import com.mibarim.main.authenticator.LogoutService;
import com.mibarim.main.data.UserData;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.Plus.PassRouteModel;
import com.mibarim.main.models.RatingModel;
import com.mibarim.main.models.RouteResponse;
import com.mibarim.main.models.enums.TripStates;
import com.mibarim.main.services.RouteResponseService;
import com.mibarim.main.services.UserInfoService;
import com.mibarim.main.ui.ThrowableLoader;
import com.mibarim.main.ui.activities.MainCardActivity;
import com.mibarim.main.ui.activities.RatingActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class RatingCardFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<RatingModel>> {

    @Inject
    UserData userData;
    @Inject
    protected BootstrapServiceProvider serviceProvider;
    @Inject
    protected UserInfoService userInfoService;
    @Inject
    protected LogoutService logoutService;

    private int RELOAD_REQUEST = 1234;
    List<RatingModel> items;
    List<RatingModel> latest;
    private Tracker mTracker;
    private RouteResponse routeResponse;
    private int tripId;
    private ApiResponse ratingResponse;

    private View mRecycler;
    private RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyView;
    //private ProgressBar mProgressView;
    private RatingRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int selectedRow;
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
        mRecycler = inflater.inflate(R.layout.reload_card_list, null);

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
        */
/*mProgressView = (ProgressBar) mRecycler.findViewById(R.id.pb_loading);*//*


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //showState(1);

        itemTouchListener = new ItemTouchListener() {

            @Override
            public void onBookBtnClick(View view, int position) {
               */
/* if (getActivity() instanceof MainCardActivity) {
                    PassRouteModel selectedItem = ((PassRouteModel) items.get(position));
                    if (selectedItem.IsBooked) {
                        ((MainCardActivity) getActivity()).gotoRidingActivity(selectedItem);
                    } else {
                        ((MainCardActivity) getActivity()).gotoPayActivity(selectedItem);
                    }
                }*//*

            }

            @Override
            public void onSrcLinkClick(View view, int position) {
                */
/*if (getActivity() instanceof MainCardActivity) {
                    PassRouteModel selectedItem = ((PassRouteModel) items.get(position));
                    ((MainCardActivity) getActivity()).gotoWebView(selectedItem.SrcLink);
                }*//*

            }

            @Override
            public void onDstLinkClick(View view, int position) {
                */
/*if (getActivity() instanceof MainCardActivity) {
                    PassRouteModel selectedItem = ((PassRouteModel) items.get(position));
                    ((MainCardActivity) getActivity()).gotoWebView(selectedItem.DstLink);
                }*//*

            }
        };


        return mRecycler;
    }

    public void refresh() {
        getLoaderManager().restartLoader(0, null, this);
        //showState(1);
        mAdapter = new RatingRecyclerAdapter(getActivity(), items, itemTouchListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTracker.setScreenName("RatingCardFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Fragment").setAction("RatingCardFragment").build());
        getLoaderManager().initLoader(0, null, this);
        //setEmptyText(R.string.no_routes);
    }

    @Override
    public Loader<List<RatingModel>> onCreateLoader(int id, Bundle args) {

        mEmptyView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(true);
        items = new ArrayList<RatingModel>();
        return new ThrowableLoader<List<RatingModel>>(getActivity(), items) {
            @Override
            public List<RatingModel> loadData() throws Exception {
                latest = new ArrayList<RatingModel>();
                if (getActivity() instanceof RatingActivity) {
                    Gson gson = new Gson();
                    tripId = ((RatingActivity) getActivity()).getTripId();
                    String authToken = ((RatingActivity) getActivity()).getAuthToken();
                    if (getActivity() != null) {
                        ratingResponse = userInfoService.getRatings(authToken, tripId);
                        if (ratingResponse != null) {
                            for (String json :ratingResponse.Messages) {
                                RatingModel rateModel = gson.fromJson(json, RatingModel.class);
                                latest.add(rateModel);
                            }
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
    public void onLoadFinished(Loader<List<RatingModel>> loader, List<RatingModel> data) {
        items = data;
        if (items.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        }
        // specify an adapter (see also next example)
        mAdapter = new RatingRecyclerAdapter(getActivity(), items, itemTouchListener);
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
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<RatingModel>> loader) {

    }

    public interface ItemTouchListener {
        //public void onCardViewTap(View view, int position);
        public void onBookBtnClick(View view, int position);

        public void onSrcLinkClick(View view, int position);

        public void onDstLinkClick(View view, int position);
        //public void onUserImageClick(View view, int position);

    }


}
*/
