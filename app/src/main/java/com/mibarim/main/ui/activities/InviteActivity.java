package com.mibarim.main.ui.activities;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.os.OperationCanceledException;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.BootstrapServiceProvider;
import com.mibarim.main.R;
import com.mibarim.main.core.Constants;
import com.mibarim.main.data.UserData;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.InviteModel;
import com.mibarim.main.services.UserInfoService;
import com.mibarim.main.ui.BootstrapActivity;
import com.mibarim.main.ui.fragments.PlusFragments.InviteFragment;
import com.mibarim.main.util.SafeAsyncTask;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;


/**
 * Initial activity for the application.
 * * <p/>
 * If you need to remove the authentication from the application please see
 */
public class InviteActivity extends BootstrapActivity {

    @Inject
    UserInfoService userInfoService;
    @Inject
    BootstrapServiceProvider serviceProvider;
    @Inject
    UserData userData;


    private Toolbar toolbar;
    private String authToken;
    private Tracker mTracker;
    private InviteModel inviteModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            authToken = getIntent().getExtras().getString(Constants.Auth.AUTH_TOKEN);
        }

        BootstrapApplication application = (BootstrapApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("RidingActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Activity").setAction("InviteActivity").build());

        setContentView(R.layout.container_activity);

        // View injection with Butterknife
        ButterKnife.bind(this);

        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (Build.VERSION.SDK_INT >= 17) {
                actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu_forward);
            }
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        getInviteFromServer();
        //initScreen();
    }

    private void initScreen() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new InviteFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        finishIt();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishIt();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void finishIt() {
        Intent i= getIntent();
        setResult(RESULT_OK,i);
        finish();
    }


    public void ShareInvite(String link) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //shareIntent.putExtra(Intent.EXTRA_TEXT, imageUri.toString());
        shareIntent.putExtra(Intent.EXTRA_TEXT,link);
        shareIntent.setType("text/plain");

        startActivity(Intent.createChooser(shareIntent, "لینک دعوت را به اشتراک بگذارید"));
    }

    public InviteModel getInviteCode() {
        return inviteModel;
    }

    public void getInviteFromServer() {
        showProgress();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(InviteActivity.this);
                }
                inviteModel = userInfoService.getInvite(authToken);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                hideProgress();
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                //userData.insertInvite(inviteModel);
                if(inviteModel.InviteCode !=null && !inviteModel.InviteCode.equals("")){
                    //userData.insertInvite(inviteModel);
                    initScreen();
                    //reloadInviteCode();
                }
                hideProgress();
            }
        }.execute();
    }

    /*private void reloadInviteCode() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_container);
        ((InviteFragment) fragment).reloadInvite();
    }*/


    /**
     * Hide progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void hideProgress() {
        dismissDialog(0);
    }

    /**
     * Show progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void showProgress() {
        showDialog(0);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.please_wait));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            public void onCancel(final DialogInterface dialog) {
//            }
//        });
        return dialog;
    }


}
