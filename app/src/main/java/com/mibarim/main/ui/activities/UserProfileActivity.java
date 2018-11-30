package com.mibarim.main.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.BootstrapServiceProvider;
import com.mibarim.main.R;
import com.mibarim.main.authenticator.LogoutService;
import com.mibarim.main.core.Constants;
import com.mibarim.main.core.ImageUtils;
import com.mibarim.main.data.UserData;
import com.mibarim.main.models.ImageResponse;
import com.mibarim.main.models.ScoreModel;
import com.mibarim.main.models.UserInfoModel;
import com.mibarim.main.services.UserInfoService;
import com.mibarim.main.util.SafeAsyncTask;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Arya on 1/20/2018.
 */

public class UserProfileActivity extends Activity {
    @Inject
    protected BootstrapServiceProvider serviceProvider;
    @Inject
    LogoutService getLogoutService;
    @Inject
    UserInfoService userInfoService;
    @Inject
    UserData userData;

    CircleImageView image_profile;
    TextView firstName_profile, lastName_profile, money_profile, mobile_profile, email_profile;
    private String authToken;
    private UserInfoModel userInfoModel;
    private Button charge, logOut;
    private ScoreModel scoreModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        BootstrapApplication.component().inject(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            authToken = getIntent().getExtras().getString(Constants.Auth.AUTH_TOKEN);
        }

        initialize();
        getUserInfo();

        charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, ChargeAccountActivity.class);
                intent.putExtra("currentCredit", money_profile.getText().toString());
                startActivity(intent);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserProfileActivity.this);
                alertDialogBuilder.setTitle("آیا می خواهید از حساب کاربری خود خارج شوید؟");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("بله",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        logout();
                                    }
                                })

                        .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    public void initialize() {

        image_profile = (CircleImageView) findViewById(R.id.image_profile);
        firstName_profile = (TextView) findViewById(R.id.firstName_profile);
        lastName_profile = (TextView) findViewById(R.id.lastName_profile);
        money_profile = (TextView) findViewById(R.id.money_profile);
        mobile_profile = (TextView) findViewById(R.id.phoneNumber_profile);
        email_profile = (TextView) findViewById(R.id.email_profile);
        charge = (Button) findViewById(R.id.account_charge);
        logOut = (Button) findViewById(R.id.logout_profile);


    }


    private void logout() {
        getLogoutService.logout(new Runnable() {
            @Override
            public void run() {
                finishIt();
            }
        });

    }

    protected void finishIt() {
        final Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    public void getUserInfo() {


        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(UserProfileActivity.this);
                }
                userInfoModel = userInfoService.getUserInfo(authToken);
                scoreModel = userInfoService.getUserScores(authToken);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                Toast.makeText(getApplicationContext(), "مشکلی در دریافت اطلاعات به وجود آمده است", Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                setInfo();
            }
        }.execute();
    }

    public void setInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.mibarim.main", Context.MODE_PRIVATE);
        firstName_profile.setText(userInfoModel.Name + " ");
        lastName_profile.setText(userInfoModel.Family);
        mobile_profile.setText(sharedPreferences.getString("UserMobile", ""));
        email_profile.setText(userInfoModel.Email);
        money_profile.setText(scoreModel.CreditMoneyString + " تومان");
        getImageById(userInfoModel.UserImageId);

    }

    public Bitmap getImageById(String imageId) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.account);
        if (imageId == null || imageId.equals("") || imageId.equals("00000000-0000-0000-0000-000000000000")) {
            image_profile.setImageBitmap(icon);
            return icon;
        }
        ImageResponse imageResponse = userData.imageQuery(imageId);
        if (imageResponse != null) {
            Bitmap b = ImageUtils.loadImageFromStorage(imageResponse.ImageFilePath, imageResponse.ImageId);
            if (b != null) {
                image_profile.setImageBitmap(b);
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
            }

            @Override
            protected void onSuccess(final Boolean imageLoaded) throws Exception {
                if (imageLoaded) {
//                    progressDialog.hide();
                    byte[] decodedString = Base64.decode(imageResponse.Base64ImageFile, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    image_profile.setImageBitmap(decodedByte);

                }
            }
        }.execute();
    }
}
