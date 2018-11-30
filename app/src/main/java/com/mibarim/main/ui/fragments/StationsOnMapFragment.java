package com.mibarim.main.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.mibarim.main.R;
import com.mibarim.main.models.MyItem;
import com.mibarim.main.models.StationModel;
import com.mibarim.main.ui.activities.StationsOnMapActivity;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alireza on 10/7/2017.
 */

public class StationsOnMapFragment extends Fragment implements OnMapReadyCallback {

    MapView mapView;

    GoogleMap myGoogleMap;

    Context context;

    List<StationModel> mainStationsList;

//    List<SubStationModel> substationsList;

    private ClusterManager<MyItem> mClusterManager;

    List<MyItem> items;

    long selectedId;

    int stat;

    GoogleApiClient mGoogleApiClient;

//    LatLng latLng;

//    Marker currLocationMarker;

//    LocationRequest mLocationRequest;

    int LOCATION_PERMISSION_REQUEST = 23423;


    public StationsOnMapFragment() {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        stat = ((StationsOnMapActivity) getActivity()).getState();

        try {
            MapsInitializer.initialize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //bind the views to their layouts
        View rootView = inflater.inflate(R.layout.stations_on_map_fragment, container, false);

        mapView = (MapView) rootView.findViewById(R.id.map_view);

        mapView.onCreate(savedInstanceState);

        mapView.onResume();


        mapView.getMapAsync(this);


        // latitude and longitude

        double latitude = 17.385044;
        double longitude = 78.486671;

        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("Hello Maps");


        // Changing marker icon
//        marker.icon(BitmapDescriptorFactory
//                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker

        /*myGoogleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(17.385044, 78.486671)).zoom(12).build();
        myGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));*/

        // Perform any camera updates here
        return rootView;


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        myGoogleMap = googleMap;


        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(35.724851, 51.399822));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(9);

        myGoogleMap.moveCamera(center);
        myGoogleMap.animateCamera(zoom);


        items = new ArrayList<>();

        mainStationsList = ((StationsOnMapActivity) getActivity()).getMainStations();

//        substationsList = ((StationsOnMapActivity) getActivity()).getSubStations();


        /*if (stat == 0) {
            for (int i = 0; i < substationsList.size(); i++) {
                double lat = Double.parseDouble(substationsList.get(i).StLat);
                double lon = Double.parseDouble(substationsList.get(i).StLng);
                String title = substationsList.get(i).StAdd;

                long subStationIdLong = substationsList.get(i).StationId;

                long origMainStId = substationsList.get(i).MainStationId;
                long origSubStId = substationsList.get(i).StationId;

                String subStationIdString = Long.toString(subStationIdLong);

                LatLng latLng = new LatLng(lat, lon);
//            myGoogleMap.addMarker(new MarkerOptions().position(latLng).title(title));

                MyItem myItem = new MyItem(lat, lon, title, origMainStId, origSubStId);
                items.add(myItem);
            }


        }*/

        addListToItems();

/*        for (int i = 0; i < mainStationsList.size(); i++) {
            double lat = Double.parseDouble(mainStationsList.get(i).StLat);
            double lon = Double.parseDouble(mainStationsList.get(i).StLng);
            String title = mainStationsList.get(i).Name;

            long mainStationIdLong = mainStationsList.get(i).MainStationId;
            String mainStationIdString = Long.toString(mainStationIdLong);

            LatLng latLng = new LatLng(lat, lon);
//            myGoogleMap.addMarker(new MarkerOptions().position(latLng).title(title));

            MyItem myItem = new MyItem(lat, lon, title, mainStationIdLong);
            items.add(myItem);

        }*/


        mClusterManager = new ClusterManager<MyItem>(getActivity(), myGoogleMap);
        myGoogleMap.setOnCameraIdleListener(mClusterManager);

        mClusterManager.setRenderer(new OwnIconRendered(getActivity(), myGoogleMap, mClusterManager));

        mClusterManager.addItems(items);


        LatLng sydney = new LatLng(-34, 151);
//        myGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        /*myGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String snippet = marker.getSnippet();

                selectedId = Long.parseLong(snippet);


                ((StationsOnMapActivity) getActivity()).setDestStationId(selectedId);

                return false;
            }
        });*/


        myGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//                Toast.makeText(getActivity(),"touched",Toast.LENGTH_LONG).show();
                ((StationsOnMapActivity) getActivity()).setDestMainStationId(-1);
            }
        });


        myGoogleMap.setOnMarkerClickListener(mClusterManager);


        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                myGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        cluster.getPosition(), (float) Math.floor(myGoogleMap.getCameraPosition().zoom + 1)), 300,
                        null);

                return true;

            }
        });

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem myItem) {

//                String snippet = myItem.getSnippet();

                if (stat == 0) {
                    selectedId = myItem.getMainStationId();
                    ((StationsOnMapActivity) getActivity()).setOriginMainStationId(selectedId);


                }

                if (stat == 1) {
                    selectedId = myItem.getMainStationId();
                    ((StationsOnMapActivity) getActivity()).setDestMainStationId(selectedId);
                }


                return false;
            }
        });

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[
            // permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            myGoogleMap.setMyLocationEnabled(true);

            ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);

        } else {

            myGoogleMap.setMyLocationEnabled(true);
        }


    }


    class OwnIconRendered extends DefaultClusterRenderer<MyItem> { // this class is for overriding the marker icon

        public OwnIconRendered(Context context, GoogleMap map,
                               ClusterManager<MyItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
            BitmapDescriptor iconBitmap = bitmapDescriptorFromVector(getActivity(), R.drawable.ic_custom_marker);
//            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.custom_marker);
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            markerOptions.icon(iconBitmap);
            markerOptions.snippet(item.getSnippet());
            markerOptions.title(item.getTitle());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                myGoogleMap.setMyLocationEnabled(true);
            }

        }
    }

    public void addListToItems() {
        for (int i = 0; i < mainStationsList.size(); i++) {
            double lat = Double.parseDouble(mainStationsList.get(i).StLat);
            double lon = Double.parseDouble(mainStationsList.get(i).StLng);
            String title = mainStationsList.get(i).Name;

            long mainStationIdLong = mainStationsList.get(i).MainStationId;
            String mainStationIdString = Long.toString(mainStationIdLong);

            LatLng latLng = new LatLng(lat, lon);
//            myGoogleMap.addMarker(new MarkerOptions().position(latLng).title(title));

            MyItem myItem = new MyItem(lat, lon, title, mainStationIdLong);
            items.add(myItem);

        }
    }
}
