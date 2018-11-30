package com.mibarim.main.ui.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.R;
import com.mibarim.main.adapters.RatingAdapter;
import com.mibarim.main.core.Constants;
import com.mibarim.main.core.ImageUtils;
import com.mibarim.main.data.UserData;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.ImageResponse;
import com.mibarim.main.models.RatingModel;
import com.mibarim.main.services.UserInfoService;
import com.mibarim.main.ui.BootstrapActivity;
import com.mibarim.main.util.SafeAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;

import static com.mibarim.main.core.Constants.GlobalConstants.RAINTG_LIST_TAG;


/**
 * Created by Alireza on 9/16/2017.
 */


public class RatingActivity extends BootstrapActivity {

    ArrayList<RatingModel> ratingModels;
    ListView listView;
    private static RatingAdapter adapter;
    private String authToken;
    private ArrayList<RatingModel> ratingModelList = new ArrayList<>();

//    private ArrayList<RatingModel> model = new ArrayList<>();


    ApiResponse apiResponse;
    @Inject
    UserInfoService userInfoService;
    @Inject
    UserData userData;

    private ProgressDialog progressDialog;

    /*@Bind(R.id.confirm_button)
    Button confirmButton;*/

    FrameLayout footerLayout;

    Button confirmButton;
    int counter;
    int numberOfNonNullImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_activity);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        BootstrapApplication.component().inject(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);


        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        counter = 0;


        if (getIntent() != null && getIntent().getExtras() != null) {
            authToken = getIntent().getExtras().getString(Constants.Auth.AUTH_TOKEN);


            ApiResponse apiResponse = (ApiResponse) getIntent().getExtras().getSerializable(RAINTG_LIST_TAG);

            Gson gson = new GsonBuilder().create();
            for (String json : apiResponse.Messages) {
                ratingModelList.add(gson.fromJson(json, RatingModel.class));
            }


            numberOfNonNullImages = 0;

            for (int i = 0; i < ratingModelList.size(); i++) {
                String id = ratingModelList.get(i).getImageId();
                if (id != null) {
                    numberOfNonNullImages++;
                }
            }

            for (int i = 0; i < ratingModelList.size(); i++) {
                String id = ratingModelList.get(i).getImageId();
                if (id != null) {
                    getImageById(id, i);
//                    numberOfNonNullImages++;
                } else {
                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_blank_profile_photo);
                    ratingModelList.get(i).setImageBitmap(icon);
                }
            }

            if (numberOfNonNullImages == 0) {
                listView = (ListView) findViewById(R.id.list);
                adapter = new RatingAdapter(ratingModelList, RatingActivity.this);
                listView.setAdapter(adapter);
                View v = listView.getChildAt(0);
                progressDialog.hide();
            }
        }


        listView = (ListView) findViewById(R.id.list);
        adapter = new RatingAdapter(ratingModelList, RatingActivity.this);
        listView.setAdapter(adapter);


//        getTheRatingsFromServer();
//        progressDialog.show();






        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                RatingModel ratingModel = ratingModels.get(position);

//                int ratingInt = (int) ratingModelList.get(position).ratingBar.getRating();
//                ratingModelList.get(position).setRate(ratingInt);


//                Snackbar.make(view, ratingModelList.getName() + "\n" + ratingModelList.getRatingBar(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
            }
        });*/

//        View v = getLayoutInflater().inflate(R.layout.button_under_rating_layout, null);


        footerLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.button_under_rating_layout, null);

        confirmButton = (Button) footerLayout.findViewById(R.id.confirm_button1);


        listView.addFooterView(footerLayout);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < ratingModelList.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("Name", ratingModelList.get(i).getName());
                        jsonObject.put("Family", ratingModelList.get(i).getFamily());
                        jsonObject.put("UserUId", ratingModelList.get(i).getUserUId());
                        jsonObject.put("RateDescription", ratingModelList.get(i).getRateDescription());
                        jsonObject.put("Rate", ratingModelList.get(i).getRate());
                        jsonObject.put("Presence", ratingModelList.get(i).getPresence());
                        jsonObject.put("RateId", ratingModelList.get(i).getRateId());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsonArray.put(jsonObject);

                }

                int atLeastOneIsNotEated = 0;

                for (int i = 0; i < ratingModelList.size(); i++) {
                    if (ratingModelList.get(i).getPresence() == 1 && ratingModelList.get(i).getRate() == 0)
                        atLeastOneIsNotEated++;
                }

                if (atLeastOneIsNotEated == 0) {

                    String ratingListString = jsonArray.toString();
                    sendTheListToServer(ratingListString);
                } else {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(RatingActivity.this);
                    builder.setMessage("لطفا به همسفران رای دهید!");
                    builder.show();

                }


            }
        });


/*
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                JSONObject jResult = new JSONObject();
//                jResult.putOpt("last_sync_date", lastSyncDateTime);

                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < ratingModelList.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("Name", ratingModelList.get(i).getName());
                        jsonObject.put("Family", ratingModelList.get(i).getFamily());
                        jsonObject.put("UserUId", ratingModelList.get(i).getUserUId());
                        jsonObject.put("RateDescription", ratingModelList.get(i).getRateDescription());
                        jsonObject.put("Rate", ratingModelList.get(i).getRate());
                        jsonObject.put("Presence", ratingModelList.get(i).getPresence());
                        jsonObject.put("RateId", ratingModelList.get(i).getRateId());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsonArray.put(jsonObject);

                }

                int atLeastOneIsNotRated = 0;

                for (int i = 0; i < ratingModelList.size(); i++) {
                    if (ratingModelList.get(i).getPresence() == 1 && ratingModelList.get(i).getRate() == 0)
                        atLeastOneIsNotRated++;
                }

                if (atLeastOneIsNotRated == 0) {

                    String ratingListString = jsonArray.toString();
                    sendTheListToServer(ratingListString);
                } else {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(RatingActivity.this);
                    builder.setMessage("لطفا به همسفران رای دهید!");
                    builder.show();

                }


            }
        });
*/


    }



/*

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Id", _masterId);
            obj.put("Name", _name);
            obj.put("Category", _category);
        } catch (JSONException e) {
            trace("DefaultListItem.toString JSONException: " + e.getMessage());
        }
        return obj;
    }
*/


    private void getTheRatingsFromServer() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                /*if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(UserDocumentsUploadActivity.this);
                }*/

//                progressDialog.show();
                apiResponse = userInfoService.getRatings(authToken, "");

//                ratingModelList = new ArrayList<RatingModel>();


//                ApiResponse myResponse = routeResponseService.GetStationRoutes(1);
                //Gson gson = new Gson();
                Gson gson = new GsonBuilder().create();
                for (String json : apiResponse.Messages) {
                    ratingModelList.add(gson.fromJson(json, RatingModel.class));
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
//                makeAllProgressBarsInvisible();
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
//                listView = (ListView) findViewById(R.id.list);
//                adapter = new RatingAdapter(ratingModelList, RatingActivity.this);
//
//                listView.setAdapter(adapter);
//                Toast.makeText(RatingActivity.this, "پیغام موفقیت آمیز", Toast.LENGTH_LONG).show();


                for (int i = 0; i < ratingModelList.size(); i++) {
                    String id = ratingModelList.get(i).getImageId();
                    if (id != null)
                        getImageById(id, i);
                    else {
                        listView = (ListView) findViewById(R.id.list);
                        adapter = new RatingAdapter(ratingModelList, RatingActivity.this);
                        listView.setAdapter(adapter);
                        View v = listView.getChildAt(0);
//                        v.findViewById()
                    }
                }
                progressDialog.hide();


//                String i = ratingModelList.get(0).getImageId();
//                getImageById(i);

            }
        }.execute();
    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
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
    }

*/

    public Bitmap getImageById(String imageId, int i) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_camera);
        if (imageId == null || imageId.equals("") || imageId.equals("00000000-0000-0000-0000-000000000000")) {
            ratingModelList.get(i).setImageBitmap(icon);
            return icon;
        }
        ImageResponse imageResponse = userData.imageQuery(imageId);
        if (imageResponse != null) {
            Bitmap b = ImageUtils.loadImageFromStorage(imageResponse.ImageFilePath, imageResponse.ImageId);
            if (b != null) {
                return b;
            } else {
                getImageFromServer(imageId, i);
            }
        } else {
            getImageFromServer(imageId, i);
        }
        return icon;
    }


    private void getImageFromServer(final String imageId, final int i) {
//        progressDialog.show();
        new SafeAsyncTask<Boolean>() {
            ImageResponse imageResponse = new ImageResponse();

            @Override
            public Boolean call() throws Exception {
                //String token = serviceProvider.getAuthToken(UserImageUploadActivity.this);
                imageResponse = userInfoService.GetImageById(authToken, imageId);
                if (imageResponse != null && imageResponse.Base64ImageFile != null) {
                    return true;
                }

                return false;

            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
//                progressDialog.hide();
                if (e instanceof android.os.OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
//                    finish();
                }
                progressDialog.hide();
                Toast.makeText(RatingActivity.this, R.string.error_message, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onSuccess(final Boolean imageLoaded) throws Exception {
                if (imageLoaded) {
//                    progressDialog.hide();
                    byte[] decodedString = Base64.decode(imageResponse.Base64ImageFile, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    String path = ImageUtils.saveImageToInternalStorage(getApplicationContext(), decodedByte, imageResponse.ImageId);
//                    userData.insertImage(imageResponse, path);
//                    imageToUpload.setImageURI();
//                    imageToUpload.setImageBitmap(decodedByte);
//                    progressBar.setVisibility(View.GONE);

                    ratingModelList.get(i).setImageBitmap(decodedByte);


//                    Toast.makeText(getBaseContext(), "عکس دریافت شد از سرور!", Toast.LENGTH_LONG).show();

//                    if (i == ratingModelList.size() - 1) {
                    counter++;

                    if (counter == numberOfNonNullImages)
                        adapter = new RatingAdapter(ratingModelList, RatingActivity.this);
                    listView.setAdapter(adapter);
//                    }
//                    listView = (ListView) findViewById(R.id.list);

                    progressDialog.hide();
//                    Toast.makeText(RatingActivity.this, "لیست با موفقیت از سرور دریافت شد!", Toast.LENGTH_LONG).show();
/*

                    hideProgress();
                    if (imageLoaded) {
                        gotoBankPayPage(paymentDetailModel);
                    }
*/

//                    new HandleApiMessagesBySnackbar(parentLayout, response).showMessages();
//                    setImage(imageResponse);
                }
            }
        }.execute();
    }


    private void sendTheListToServer(final String ratingList) {
//        progressDialog.show();
        new SafeAsyncTask<Boolean>() {
            ImageResponse imageResponse = new ImageResponse();

            @Override
            public Boolean call() throws Exception {
                //String token = serviceProvider.getAuthToken(UserImageUploadActivity.this);
//                imageResponse = userInfoService.GetImageById(authToken, imageId);
                userInfoService.setRatings(authToken, ratingList);
//                if (imageResponse != null && imageResponse.Base64ImageFile != null) {
//                    return true;
//                }

                return false;

            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
//                progressDialog.hide();
                if (e instanceof android.os.OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
//                    finish();
                }
                progressDialog.hide();
                Toast.makeText(getBaseContext(), R.string.error_message, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onSuccess(final Boolean imageLoaded) throws Exception {


//                    Toast.makeText(getBaseContext(), "عکس دریافت شد از سرور!", Toast.LENGTH_LONG).show();
                progressDialog.hide();
                Toast.makeText(getBaseContext(), "لیست فرستاده شد به سرور", Toast.LENGTH_LONG).show();
                finish();

            }
        }.execute();
    }


}
