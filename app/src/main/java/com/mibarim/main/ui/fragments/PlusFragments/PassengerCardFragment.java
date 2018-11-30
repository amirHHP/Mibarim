package com.mibarim.main.ui.fragments.PlusFragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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
import com.mibarim.main.adapters.PassengerRouteRecyclerAdapter;
import com.mibarim.main.authenticator.LogoutService;
import com.mibarim.main.core.ImageUtils;
import com.mibarim.main.data.UserData;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.ImageResponse;
import com.mibarim.main.models.Plus.PassRouteModel;
import com.mibarim.main.models.RouteResponse;
import com.mibarim.main.models.UserInfoModel;
import com.mibarim.main.models.enums.TripStates;
import com.mibarim.main.services.RouteResponseService;
import com.mibarim.main.services.UserInfoService;
import com.mibarim.main.ui.ThrowableLoader;
import com.mibarim.main.ui.activities.MainCardActivity;
import com.mibarim.main.ui.activities.UserInfoDetailActivity;
import com.mibarim.main.util.SafeAsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class PassengerCardFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<PassRouteModel>> {

    @Inject
    UserData userData;
    @Inject
    protected BootstrapServiceProvider serviceProvider;
    @Inject
    protected RouteResponseService routeResponseService;
    @Inject
    protected LogoutService logoutService;

    private int RELOAD_REQUEST = 1234;
    List<PassRouteModel> items;
    List<PassRouteModel> latest;
    private Tracker mTracker;
    private RouteResponse routeResponse;
    private ApiResponse suggestRouteResponse;

    private View mRecycler;
    private RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyView;
    //private ProgressBar mProgressView;
    private PassengerRouteRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int selectedRow;
    ItemTouchListener itemTouchListener;


    private UserInfoModel userInfoModel;
    private String authToken;
    @Inject
    UserInfoService userInfoService;


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
        /*mProgressView = (ProgressBar) mRecycler.findViewById(R.id.pb_loading);*/

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //showState(1);

        getUserInfoFromServer();

        itemTouchListener = new ItemTouchListener() {

            @Override
            public void onBookBtnClick(View view, int position) {
                if (getActivity() instanceof MainCardActivity) {
                    PassRouteModel selectedItem = ((PassRouteModel) items.get(position));
                    if (selectedItem.IsBooked) {

                        if (userInfoModel.UserImageId == null) {
//                            if (prefs.getInt("UserPhotoUploadedFirstTry", 2) != 1) {
                            Intent j = new Intent(getActivity(), UserInfoDetailActivity.class);
                            startActivity(j);
//                            }
                        }


                        ((MainCardActivity) getActivity()).gotoRidingActivity(selectedItem);

                    } else {
                        ((MainCardActivity) getActivity()).gotoPayActivity(selectedItem);
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


        return mRecycler;
    }

    public void refresh() {
        getLoaderManager().restartLoader(0, null, this);
        //showState(1);
        mAdapter = new PassengerRouteRecyclerAdapter(getActivity(), items, itemTouchListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTracker.setScreenName("PassengerCardFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Fragment").setAction("PassengerCardFragment").build());
        getLoaderManager().initLoader(0, null, this);
        //setEmptyText(R.string.no_routes);
    }

    /*public void showState(int showState) {
        mEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.GONE);
        //1 progress
        //2 show result list
        //3 no result
        switch (showState) {
            case 1:
                mProgressView.setVisibility(View.VISIBLE);
                break;
            case 2:
                mRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 3:
                mEmptyView.setVisibility(View.VISIBLE);
                break;
            default:
                mEmptyView.setVisibility(View.VISIBLE);
        }
    }*/

    @Override
    public Loader<List<PassRouteModel>> onCreateLoader(int id, Bundle args) {
        mEmptyView.setVisibility(View.GONE);

        mSwipeRefreshLayout.setRefreshing(true);
        items = new ArrayList<PassRouteModel>();
        return new ThrowableLoader<List<PassRouteModel>>(getActivity(), items) {
            @Override
            public List<PassRouteModel> loadData() throws Exception {
                latest = new ArrayList<PassRouteModel>();
                if (getActivity() instanceof MainCardActivity) {
                    Gson gson = new Gson();
                    PassRouteModel bookedTrip = null;
                    routeResponse = ((MainCardActivity) getActivity()).getRoute();
                    String authToken = ((MainCardActivity) getActivity()).getAuthToken();
                    if (getActivity() != null && authToken != null) {
                        suggestRouteResponse = routeResponseService.GetPassengerRoutes(authToken, 1);
                        if (suggestRouteResponse != null) {
                            for (String routeJson : suggestRouteResponse.Messages) {
                                PassRouteModel route = gson.fromJson(routeJson, PassRouteModel.class);
                                if (route.IsBooked) {
                                    bookedTrip = route;
                                }
                                latest.add(route);
                            }
                            if (bookedTrip != null) {
                                if (bookedTrip.TripState == TripStates.InPreTripTime.toInt() ||
                                        bookedTrip.TripState == TripStates.InRiding.toInt() ||
                                        bookedTrip.TripState == TripStates.InTripTime.toInt()) {
                                    ((MainCardActivity) getActivity()).gotoRidingActivity(bookedTrip);
                                } else {
                                    ((MainCardActivity) getActivity()).showRidingActivity(bookedTrip);
                                }
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
    public void onLoadFinished(Loader<List<PassRouteModel>> loader, List<PassRouteModel> data) {
        items = data;
        if (items.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        }
        // specify an adapter (see also next example)
        mAdapter = new PassengerRouteRecyclerAdapter(getActivity(), items, itemTouchListener);
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
    public void onLoaderReset(Loader<List<PassRouteModel>> loader) {

    }

    public interface ItemTouchListener {
        //public void onCardViewTap(View view, int position);
        public void onBookBtnClick(View view, int position);

        public void onSrcLinkClick(View view, int position);

        public void onDstLinkClick(View view, int position);
        //public void onUserImageClick(View view, int position);

    }

    private void getUserInfoFromServer() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(getActivity());
                }
                userInfoModel = userInfoService.getUserInfo(authToken);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                userData.insertUserInfo(userInfoModel);
                getImageById(userInfoModel.UserImageId, R.mipmap.ic_camera);
//                setInfoValues(userInfoModel.IsUserRegistered);
                //setEmail();
            }
        }.execute();
    }


    public Bitmap getImageById(String imageId, int defaultDrawableId) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), defaultDrawableId);
        if (imageId == null || imageId.equals("") || imageId.equals("00000000-0000-0000-0000-000000000000")) {
            return icon;
        }
        ImageResponse imageResponse = userData.imageQuery(imageId);
        if (imageResponse != null) {
            Bitmap b = ImageUtils.loadImageFromStorage(imageResponse.ImageFilePath, imageResponse.ImageId);
            if (b != null) {
                return b;
            } else {
                getImageFromServer(imageId);
            }
        } else {
            getImageFromServer(imageId);
        }
        return icon;
    }


    private void getImageFromServer(final String imageId) {
        new SafeAsyncTask<Boolean>() {
            ImageResponse imageResponse = new ImageResponse();
            Bitmap decodedByte;

            @Override
            public Boolean call() throws Exception {
                authToken = serviceProvider.getAuthToken(getActivity());
                imageResponse = userInfoService.GetImageById(authToken, imageId);
                if (imageResponse != null && imageResponse.Base64ImageFile != null) {
                    return true;
                }
                return false;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof android.os.OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
//                    finish();
                }
            }

            @Override
            protected void onSuccess(final Boolean imageLoaded) throws Exception {
                if (imageLoaded) {
                    byte[] decodedString = Base64.decode(imageResponse.Base64ImageFile, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    String path = ImageUtils.saveImageToInternalStorage(getContext(), decodedByte, imageResponse.ImageId);
//                    userData.insertImage(imageResponse, path);
                }
            }
        }.execute();
    }

    /*

    private void setInfoValues(boolean IsUserRegistered) {
        SharedPreferences prefs = getContext().getSharedPreferences(
                "com.mibarim.main", Context.MODE_PRIVATE);
        if (IsUserRegistered) {
            prefs.edit().putInt("UserInfoRegistered", 1).apply();
        } else {
            prefs.edit().putInt("UserInfoRegistered", 0).apply();
            final Intent i = new Intent(this, RegisterActivity.class);
            startActivityForResult(i, FINISH_USER_INFO);


        }


//        prefs.getInt("UserPhotoUploaded",2);


        if (userInfoModel.UserImageId == null) {
            if (prefs.getInt("UserPhotoUploadedFirstTry", 2) != 1) {
                Intent j = new Intent(this, UserInfoDetailActivity.class);
                startActivityForResult(j, USER_DETAIL_INFO_REQUEST_CODE);
            }
        }


    }
*/


}
