package com.mibarim.main.ui.activities;

import android.animation.LayoutTransition;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.BootstrapServiceProvider;
import com.mibarim.main.R;
import com.mibarim.main.adapters.SearchCustomAdapter;
import com.mibarim.main.core.Constants;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.FilterModel;
import com.mibarim.main.models.StationModel;
import com.mibarim.main.models.SubStationModel;
import com.mibarim.main.models.SubmitResult;
import com.mibarim.main.services.RouteRequestService;
import com.mibarim.main.services.RouteResponseService;
import com.mibarim.main.ui.HandleApiMessagesBySnackbar;
import com.mibarim.main.ui.fragments.SubstationListFragment;
import com.mibarim.main.util.SafeAsyncTask;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.mibarim.main.core.Constants.GlobalConstants.ORIGIN_OR_DESTINATION_ID_INTENT_TAG;
import static com.mibarim.main.core.Constants.GlobalConstants.RAINTG_LIST_TAG;
import static com.mibarim.main.core.Constants.GlobalConstants.STATE_OF_ORIGIN_OR_DESTINATION_INTENT_TAG;


//import android.support.design.widget.BottomSheetBehavior;

public class SearchStationActivity extends AppCompatActivity {

    @Inject
    RouteResponseService routeResponseService;

    @Inject
    protected BootstrapServiceProvider serviceProvider;

    private ApiResponse setRes;

    @Inject
    RouteRequestService routeRequestService;

    private View parentLayout;


    private static final String SubStationFragment = "SubStationFragment";


    ArrayList<String> stationArrayList;
    //    ArrayList<DataModel> dataModels1;
    ListView mainStationsListView;
    ListView chooseFromMapListview;
    Button chooseFromMapButton;
//    private static CustomAdapter adapter;
//    private static CustomAdapter adapter1;

    private static SearchCustomAdapter mySearchCustomAdapter;

    LinearLayout destinationSearchLayout;
    LinearLayout sourceSearchLayout;

    LinearLayout searchBoxes;
    //    RelativeLayout stationListLayout;
    CoordinatorLayout rootLayout;

    LayoutTransition layoutTransition;

//    ScrollView listsLayout;

    LayoutInflater linflater;


    EditText searchEdittext;

    ImageView refreshImageview;

    ApiResponse mainStationsApiResponse;

    ApiResponse allSubStationsApiResponse;

    private String authToken;

    public List<StationModel> routeResponseList;

    public List<SubStationModel> substationResponseList;

    String[] stationsArray;

    List<String> stationsArrayList;

    Toolbar toolbar;

    private StationModel stationModel;

    StationModel selectedItem;

    public int state;

    long originStationId;

    long destinationStationId;

    long originMainStationId;

    FrameLayout footerLayout;

    Button suggestButton;


    ProgressDialog progressDialog;
    FilterModel filterModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_station);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        BootstrapApplication.component().inject(this);


        // we  set the initial value of these to -1 to ensure they are not set at first
        originStationId = destinationStationId = -1;
        originMainStationId = -2;


        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }


        if (getIntent() != null && getIntent().getExtras() != null) {
            authToken = getIntent().getExtras().getString(Constants.Auth.AUTH_TOKEN);


            ApiResponse apiResponse = (ApiResponse) getIntent().getExtras().getSerializable(RAINTG_LIST_TAG);


        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_forward_black_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getText(R.string.please_wait));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        progressDialog.show();
        getTheMainStationsFromServer();


//        searchEdittext.setWidth(refreshImageview.getWidth());


//        destinationSearchLayout = (LinearLayout) findViewById(R.id.destination_search_layout1);

        mainStationsListView = (ListView) findViewById(R.id.main_stations_listview);

        footerLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.button_under_main_search_list, null);
        suggestButton = (Button) footerLayout.findViewById(R.id.suggest_station_button);
        mainStationsListView.addFooterView(footerLayout);

//        chooseFromMapListview = (ListView) findViewById(R.id.choose_from_map_listview);
        chooseFromMapButton = (Button) findViewById(R.id.choose_from_map_button);


        sourceSearchLayout = (LinearLayout) findViewById(R.id.source_search_layout);


        refreshImageview = (ImageView) findViewById(R.id.refresh_imageview);

        searchEdittext = (EditText) findViewById(R.id.search_edittext);

        setState(0); // this method is for setting the state that the user is confronting with origin or destination
//        View bottomSheet = findViewById(R.id.bottom_sheet1);
//        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);


        stationArrayList = new ArrayList<>();

//        dataModels1 = new ArrayList<>();

        String[] values = new String[]{"abbas", "bkajsf", "cdsfd", "ddsfgng", "ccvbcne", "fcb", "g",
                "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "w", "x", "y", "z"};

        String[] values2 = new String[]{"Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2"};

        String[] chooseFromMapSingleValue = new String[]{"انتخاب از نقشه"};


//        stationArrayList.add("applepie");
//        stationArrayList.add("bananabread");
//        stationArrayList.add("cupcake");
//        stationArrayList.add("donut");
//        stationArrayList.add("eclair");
//        stationArrayList.add("froyo");
//        stationArrayList.add("gingerbread");
//        stationArrayList.add("honeycomb");
//        stationArrayList.add("icecandwich");
//        stationArrayList.add("jellyean");
//        stationArrayList.add("itkat");
//        stationArrayList.add("ollipop");
//        stationArrayList.add("arshmallow");
//        stationArrayList.add("ougat");
//        stationArrayList.add("reo");
//        stationArrayList.add("reo");
//        stationArrayList.add("reo");
//        stationArrayList.add("reo");
//        stationArrayList.add("reo");
//        stationArrayList.add("reo");
//        stationArrayList.add("reo");
//        stationArrayList.add("reo");
//        stationArrayList.add("reo");
//        stationArrayList.add("reo");
//        stationArrayList.add("reo");


//        dataModels1.add(new DataModel("انتخاب از نقشه"));
//        final ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
//                R.layout.search_row_item, R.id.station_name, values);

//        adapter = new CustomAdapter(stationArrayList,getApplicationContext(), values);

        final ArrayAdapter<String> chooseFromMapAdapter = new ArrayAdapter<String>(this,
                R.layout.choose_from_map_item, R.id.station_name, chooseFromMapSingleValue);


//        mainStationsListView.setAdapter(adapter3);


//
//        chooseFromMapListview.setAdapter(chooseFromMapAdapter);
//        chooseFromMapListview.setAdapter(chooseFromMapAdapter);


        searchBoxes = (LinearLayout) findViewById(R.id.search_boxes);

//        stationListLayout = (RelativeLayout) findViewById(R.id.station_list_layout);

        rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);

//        listsLayout = (ScrollView) findViewById(R.id.lists_layout);


        LayoutTransition transition = searchBoxes.getLayoutTransition();

        final FragmentManager fragmentManager = getSupportFragmentManager();


//        Fragment fragment = fragmentManager.findFragmentByTag("SourceDestinationFragmentTag");

//        ObjectAnimator animator = ObjectAnimator.ofFloat(
//                fragment,,0,200);


        // New capability as of Jellybean; monitor the container for *all* layout changes
        // (not just add/remove/visibility changes) and animate these changes as well.
        transition.enableTransitionType(LayoutTransition.CHANGING);
        linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View myView = linflater.inflate(R.layout.destination_search_layout, null);

        suggestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSuggestStation();

            }
        });


        mainStationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                List<StationModel> theItems = routeResponseList;
                List<StationModel> theItems = mySearchCustomAdapter.getItems();
                selectedItem = ((StationModel) theItems.get(position));


                int stat = getState();
                if (stat == 0) {
                    originStationId = selectedItem.MainStationId;
                    if (checkToSeeWhatToDo()) {
                        Toast.makeText(SearchStationActivity.this, R.string.origin_selected, Toast.LENGTH_SHORT).show();
                    }
//                    selectSubstation(selectedItem);
                }

                if (stat == 1) {
                    destinationStationId = selectedItem.MainStationId;
                    if (checkToSeeWhatToDo()) {
                        Toast.makeText(SearchStationActivity.this, R.string.destination_selected, Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });


        refreshImageview.setOnClickListener(new View.OnClickListener() {
            int j;

            @Override
            public void onClick(View view) {

                int status = getState();

                if (status == 0) {
                    setState(1);
                } else {
                    setState(0);
                }
            }
        });


        /*chooseFromMapListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Gson gson = new GsonBuilder().create();
                String jsonObject = gson.toJson(routeResponseList);

//                JsonArray jsonArray =  gson.toJsonTree(stationList).getAsJsonArray();

                int stat = getState();

                if (stat == 0) {
                    Intent intent = new Intent(SearchStationActivity.this, StationsOnMapActivity.class);
//                intent.putExtra("MainStationsIntentTag", jsonArray.toString());
                    intent.putExtra(Constants.GlobalConstants.MAIN_STATIONS_INTENT_TAG, mainStationsApiResponse);
                    intent.putExtra(Constants.GlobalConstants.STATE_OF_WHETHER_CHOOSING_ORIGIN_OR_DESTINATION, stat);
                    intent.putExtra(Constants.GlobalConstants.ORIGIN_MAIN_STATION_ID_INTENT_TAG, originStationId);
                    intent.putExtra(Constants.GlobalConstants.DESTINATION_MAIN_STATION_ID_INTENT_TAG, destinationStationId);

                    startActivityForResult(intent, 1432);

                }

                if (stat == 1) {
                    Intent intent = new Intent(SearchStationActivity.this, StationsOnMapActivity.class);
//                intent.putExtra("MainStationsIntentTag", jsonArray.toString());
                    intent.putExtra(Constants.GlobalConstants.MAIN_STATIONS_INTENT_TAG, mainStationsApiResponse);
                    intent.putExtra(Constants.GlobalConstants.STATE_OF_WHETHER_CHOOSING_ORIGIN_OR_DESTINATION, stat);
                    intent.putExtra(Constants.GlobalConstants.ORIGIN_MAIN_STATION_ID_INTENT_TAG, originStationId);
                    intent.putExtra(Constants.GlobalConstants.DESTINATION_MAIN_STATION_ID_INTENT_TAG, destinationStationId);
                    startActivityForResult(intent, 1432);
                }


            }
        });*/

        chooseFromMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int stat = getState();

                if (stat == 0) {
                    Intent intent = new Intent(SearchStationActivity.this, StationsOnMapActivity.class);
//                intent.putExtra("MainStationsIntentTag", jsonArray.toString());
                    intent.putExtra(Constants.GlobalConstants.MAIN_STATIONS_INTENT_TAG, mainStationsApiResponse);
                    intent.putExtra(Constants.GlobalConstants.STATE_OF_WHETHER_CHOOSING_ORIGIN_OR_DESTINATION, stat);
                    intent.putExtra(Constants.GlobalConstants.ORIGIN_MAIN_STATION_ID_INTENT_TAG, originStationId);
                    intent.putExtra(Constants.GlobalConstants.DESTINATION_MAIN_STATION_ID_INTENT_TAG, destinationStationId);

                    startActivityForResult(intent, 1432);

                }

                if (stat == 1) {
                    Intent intent = new Intent(SearchStationActivity.this, StationsOnMapActivity.class);
//                intent.putExtra("MainStationsIntentTag", jsonArray.toString());
                    intent.putExtra(Constants.GlobalConstants.MAIN_STATIONS_INTENT_TAG, mainStationsApiResponse);
                    intent.putExtra(Constants.GlobalConstants.STATE_OF_WHETHER_CHOOSING_ORIGIN_OR_DESTINATION, stat);
                    intent.putExtra(Constants.GlobalConstants.ORIGIN_MAIN_STATION_ID_INTENT_TAG, originStationId);
                    intent.putExtra(Constants.GlobalConstants.DESTINATION_MAIN_STATION_ID_INTENT_TAG, destinationStationId);
                    startActivityForResult(intent, 1432);
                }

            }
        });




        /*chooseFromMapListview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Gson gson = new GsonBuilder().create();
                String jsonObject =  gson.toJson(stationList);

//                JsonArray jsonArray =  gson.toJsonTree(stationList).getAsJsonArray();

                Intent intent = new Intent(SearchStationActivity.this, StationsOnMapActivity.class);
//                intent.putExtra("MainStationsIntentTag", jsonArray.toString());
                intent.putExtra(Constants.GlobalConstants.MAIN_STATIONS_INTENT_TAG, mainStationsApiResponse);
                startActivity(intent);
                startActivityForResult(intent,1432);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/


        searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mySearchCustomAdapter != null) {

                    String textBeingSearched = charSequence.toString();
                    mySearchCustomAdapter.getFilter().filter(textBeingSearched.replaceAll(" ", ""));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        parentLayout = findViewById(android.R.id.content);

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/




    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        try {
            int totalHeight = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        } catch (Exception ee) {

        }

    }


    private void getTheMainStationsFromServer() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                /*if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(UserDocumentsUploadActivity.this);
                }*/

//                progressDialog.show();
                mainStationsApiResponse = routeResponseService.GetMainStations(authToken);


                routeResponseList = new ArrayList<StationModel>();


//                ApiResponse myResponse = routeResponseService.GetStationRoutes(1);
                //Gson gson = new Gson();
                Gson gson = new GsonBuilder().create();
                for (String json : mainStationsApiResponse.Messages) {
                    routeResponseList.add(gson.fromJson(json, StationModel.class));
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                progressDialog.hide();
                Toast.makeText(getBaseContext(), R.string.error_message, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
//                userData.insertUserInfo(userInfoModel);
//                getImageById(userInfoModel.UserImageId, R.mipmap.ic_camera);
//                setInfoValues(userInfoModel.IsUserRegistered);
                //setEmail();

                //TODO move this to the onSuccess of the getImageFromServer
//                mainStationsListView = (ListView) findViewById(R.id.list);
//                adapter = new RatingAdapter(ratingModelList, RatingActivity.this);
//
//                mainStationsListView.setAdapter(adapter);

                stationsArrayList = new ArrayList<String>();

                int i;
                int n = routeResponseList.size();

                for (i = 0; i < n; i++) {
                    String name = routeResponseList.get(i).Name;
                    stationsArrayList.add(name);
                }

                stationsArray = new String[n];

                for (i = 0; i < n; i++) {
                    stationsArray[i] = stationsArrayList.get(i);
                }

                /*final ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(SearchStationActivity.this,
                        R.layout.search_row_item, R.id.station_name, stationsArray);*/


                mySearchCustomAdapter = new SearchCustomAdapter(SearchStationActivity.this, routeResponseList);

//                mainStationsListView.setAdapter(adapter3);

                mainStationsListView.setAdapter(mySearchCustomAdapter);

                setListViewHeightBasedOnChildren(mainStationsListView);

//                setListViewHeightBasedOnChildren(chooseFromMapListview);


//                progressDialog.hide();


//                String i = ratingModelList.get(0).getImageId();
//                getImageById(i);

                getTheSubstationsFromServer();

            }
        }.execute();
    }


    public void selectSubstation(final StationModel selectedItem) {
        StationModel stationRouteModel = selectedItem;
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.substation_card_fragment_holder, new SubstationListFragment(), SubStationFragment)
                .commit();
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }


    public void setRoute(final long destStId, final long originStId) {

//        progressDialog.show();

//        showProgress();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(SearchStationActivity.this);
                }
                setRes = routeRequestService.setFilter(authToken, destStId, originStId);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                progressDialog.hide();
                Toast.makeText(SearchStationActivity.this, "خطا در انتخاب مسیر", Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                progressDialog.hide();

                new HandleApiMessagesBySnackbar(parentLayout, setRes).showMessages();
                Gson gson = new Gson();

                filterModel = new FilterModel();
                for (String shareJson : setRes.Messages) {
                    filterModel = gson.fromJson(shareJson, FilterModel.class);
                }
                if (filterModel.FilterId > 0) {
                    progressDialog.hide();
                    finishIt();
                }
            }
        }.execute();
    }


    public StationModel getRoute() {
        return selectedItem;
    }

    private void finishIt() {
        final Intent intent = getIntent();
        intent.putExtra("Filter_Id", filterModel.FilterId);
        setResult(RESULT_OK, intent);
        finish();
    }


    public void removeSubStation() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(SubStationFragment);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
    }


    public String getToken() {
        return authToken;
    }


    public void setState(int i) {
        // 0 means the state is origin and 1 means that the state is the desttination
        state = i;
        if (i == 0) {
            searchEdittext.setHint("جست و جو برای مبدا");
            refreshImageview.animate().rotation(0);
            refreshImageview.setColorFilter(getResources().getColor(R.color.defining_route_origin_color));
//            chooseFromMapListview.setBackgroundColor(getResources().getColor(R.color.defining_route_origin_color));
            chooseFromMapButton.setBackgroundColor(getResources().getColor(R.color.defining_route_origin_color));
            chooseFromMapButton.setText("انتخاب مبدا از نقشه");
            if (routeResponseList != null) {
                mySearchCustomAdapter = new SearchCustomAdapter(SearchStationActivity.this, routeResponseList);
                mainStationsListView.setAdapter(mySearchCustomAdapter);
            }
            searchEdittext.setText("");

            String[] chooseFromMapSingleValue = new String[]{"انتخاب مبدا از نقشه"};
            final ArrayAdapter<String> chooseFromMapAdapter = new ArrayAdapter<String>(this,
                    R.layout.choose_from_map_item, R.id.station_name, chooseFromMapSingleValue);
//            chooseFromMapListview.setAdapter(chooseFromMapAdapter);
//            chooseFromMapListview.setAdapter(chooseFromMapAdapter);
        }

        if (i == 1) {
            searchEdittext.setHint("جست و جو برای مقصد");
            refreshImageview.animate().rotation(180);
            refreshImageview.setColorFilter(getResources().getColor(R.color.defining_route_destination_color));
//            chooseFromMapListview.setBackgroundColor(getResources().getColor(R.color.defining_route_destination_color));
            chooseFromMapButton.setBackgroundColor(getResources().getColor(R.color.defining_route_destination_color));
            chooseFromMapButton.setText("انتخاب مقصد از نقشه");
            if (routeResponseList != null) {
                mySearchCustomAdapter = new SearchCustomAdapter(SearchStationActivity.this, routeResponseList);
                mainStationsListView.setAdapter(mySearchCustomAdapter);
            }
            searchEdittext.setText("");

            String[] chooseFromMapSingleValue = new String[]{"انتخاب مقصد از نقشه"};
            final ArrayAdapter<String> chooseFromMapAdapter = new ArrayAdapter<String>(this,
                    R.layout.choose_from_map_item, R.id.station_name, chooseFromMapSingleValue);
//            chooseFromMapListview.setAdapter(chooseFromMapAdapter);
        }
    }


    public int getState() {
        return state;
    }


    public boolean checkToSeeWhatToDo() {

        if (originStationId >= 0 && destinationStationId >= 0 && originMainStationId == destinationStationId) {
            Toast.makeText(SearchStationActivity.this, "مبدا و مقصد نمی توانند یکی باشند", Toast.LENGTH_LONG).show();
            return false;
        }

        if (originStationId >= 0 && destinationStationId >= 0 && originMainStationId != destinationStationId) {
            setRoute(destinationStationId, originStationId);
            progressDialog.show();
//            progressDialog.hide();
            return false;
        }


        if (originStationId < 0 && destinationStationId > 0) { // means if the originsubstationid is not set

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setState(0); // then set the state to state: getting the originid
                }
            }, 500);


        }

        if (destinationStationId < 0 && originStationId > 0) { // means if the destinationstationid is not set
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setState(1); // then set the state to getting the destinationid
                }
            }, 500);

        }

        return true;
    }

    public void setOriginSubStationId(long i) {
        originStationId = i;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1432 && resultCode == RESULT_OK) {
            final long origOrDestId = data.getLongExtra(ORIGIN_OR_DESTINATION_ID_INTENT_TAG, -1);
            final int stat = data.getIntExtra(STATE_OF_ORIGIN_OR_DESTINATION_INTENT_TAG, -1);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (stat == 0) {
                        originStationId = origOrDestId;
                        checkToSeeWhatToDo();
                    }

                    if (stat == 1) {
                        destinationStationId = origOrDestId;
                        checkToSeeWhatToDo();
                    }
                }
            }, 500);


        }
    }

    public void getTheSubstationsFromServer() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                /*if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(UserDocumentsUploadActivity.this);
                }*/

//                progressDialog.show();
                allSubStationsApiResponse = routeResponseService.GetAllSubStations(authToken);


                substationResponseList = new ArrayList<SubStationModel>();


//                ApiResponse myResponse = routeResponseService.GetStationRoutes(1);
                //Gson gson = new Gson();
                Gson gson = new GsonBuilder().create();

                for (String json : allSubStationsApiResponse.Messages) {
                    substationResponseList.add(gson.fromJson(json, SubStationModel.class));
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);

                progressDialog.hide();
                Toast.makeText(getBaseContext(), R.string.error_message, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
//                userData.insertUserInfo(userInfoModel);
//                getImageById(userInfoModel.UserImageId, R.mipmap.ic_camera);
//                setInfoValues(userInfoModel.IsUserRegistered);
                //setEmail();

                //TODO move this to the onSuccess of the getImageFromServer
//                mainStationsListView = (ListView) findViewById(R.id.list);
//                adapter = new RatingAdapter(ratingModelList, RatingActivity.this);
//
//                mainStationsListView.setAdapter(adapter);

                stationsArrayList = new ArrayList<String>();

                int i;
                int n = routeResponseList.size();

                for (i = 0; i < n; i++) {
                    String name = routeResponseList.get(i).Name;
                    stationsArrayList.add(name);
                }

                stationsArray = new String[n];

                for (i = 0; i < n; i++) {
                    stationsArray[i] = stationsArrayList.get(i);
                }

                /*final ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(SearchStationActivity.this,
                        R.layout.search_row_item, R.id.station_name, stationsArray);
*/

                mySearchCustomAdapter = new SearchCustomAdapter(SearchStationActivity.this, routeResponseList);


//                mainStationsListView.setAdapter(adapter3);

                mainStationsListView.setAdapter(mySearchCustomAdapter);


                /*setListViewHeightBasedOnChildren(mainStationsListView);


                setListViewHeightBasedOnChildren(chooseFromMapListview);*/


//                progressDialog.hide();


//                String i = ratingModelList.get(0).getImageId();
//                getImageById(i);
                progressDialog.hide();

            }
        }.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // this is for handling the back button located in the toolbar
        switch (item.getItemId()) {
            case android.R.id.home:
                int stat = getState();
                if (stat == 0) {
                    finish();
                }
                if (stat == 1) {
                    setState(0);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) { // this is for handling the back button of the phone
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            int stat = getState();
            if (stat == 0) {
                finish();
            }
            if (stat == 1) {
                setState(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void gotoSuggestStation() {
        Intent intent = new Intent(SearchStationActivity.this, WebViewActivity.class);
        intent.putExtra("URL", "https://goo.gl/forms/igP3kx2E3ilzGYDf1");
        startActivity(intent);

    }
}
