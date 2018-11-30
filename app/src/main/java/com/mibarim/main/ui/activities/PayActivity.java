package com.mibarim.main.ui.activities;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.BootstrapServiceProvider;
import com.mibarim.main.R;
import com.mibarim.main.core.Constants;
import com.mibarim.main.data.UserData;
import com.mibarim.main.models.ApiResponse;
import com.mibarim.main.models.InviteModel;
import com.mibarim.main.models.Plus.PassRouteModel;
import com.mibarim.main.models.Plus.PaymentDetailModel;
import com.mibarim.main.models.ScoreModel;
import com.mibarim.main.services.RouteRequestService;
import com.mibarim.main.services.UserInfoService;
import com.mibarim.main.ui.BootstrapActivity;
import com.mibarim.main.ui.HandleApiMessagesBySnackbar;
import com.mibarim.main.ui.fragments.PlusFragments.InviteFragment;
import com.mibarim.main.ui.fragments.PlusFragments.PayFragment;
import com.mibarim.main.util.SafeAsyncTask;

import javax.inject.Inject;

import butterknife.ButterKnife;


/**
 * Initial activity for the application.
 * * <p/>
 * If you need to remove the authentication from the application please see
 */
public class PayActivity extends BootstrapActivity {

    @Inject
    UserInfoService userInfoService;
    @Inject
    BootstrapServiceProvider serviceProvider;
    @Inject
    RouteRequestService routeRequestService;

    @Inject
    UserData userData;

    private View parentLayout;
    private Toolbar toolbar;
    private String authToken;
    private Tracker mTracker;
    private InviteModel inviteModel;
    private PassRouteModel passRouteModel;
    private ScoreModel scoreModel;
    protected ApiResponse response;
    private PaymentDetailModel paymentDetailModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            authToken = getIntent().getExtras().getString(Constants.Auth.AUTH_TOKEN);
            passRouteModel = (PassRouteModel) getIntent().getExtras().getSerializable(Constants.GlobalConstants.PASS_ROUTE_MODEL);
        }
        BootstrapApplication application = (BootstrapApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("PayActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Activity").setAction("PayActivity").build());

        setContentView(R.layout.container_activity);

        parentLayout = findViewById(R.id.container_activity_root);

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
        initScreen();
    }

    private void initScreen() {
        getUserScore();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, new PayFragment())
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
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

    public void BookSeat(final long tripId, final String discountCode, final long chargeAmount, final long seatPrice, final long credit) {
        showProgress();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    authToken = serviceProvider.getAuthToken(PayActivity.this);
                }
                paymentDetailModel = routeRequestService.bookRequest(authToken,tripId,discountCode,chargeAmount,seatPrice,credit);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof android.os.OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
//                    finish();
                }
                hideProgress();
            }

            @Override
            protected void onSuccess(final Boolean succees) throws Exception {
                hideProgress();
                if (succees) {
                    gotoBankPayPage(paymentDetailModel);
                }
                //new HandleApiMessagesBySnackbar(parentLayout, response).showMessages();
                //finishIt();
            }
        }.execute();
    }

    private void gotoBankPayPage(PaymentDetailModel paymentDetailModel) {
        if (paymentDetailModel.State == 100) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentDetailModel.BankLink));
            startActivity(browserIntent);
        } else if(paymentDetailModel.State == 200){
            Intent i=getIntent();
            setResult(RESULT_OK,i);
            finish();
        }else{
            Snackbar.make(parentLayout, R.string.payment_error, Snackbar.LENGTH_LONG).show();
        }
    }
    public PassRouteModel getPassRouteModel(){
        return passRouteModel;
    }

    public void getUserScore() {
        showProgress();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                scoreModel = userInfoService.getUserScores(authToken);
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
                hideProgress();
                setUserScore();
            }
        }.execute();
    }

    private void setUserScore() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if (fragment != null) {
            ((PayFragment)fragment).setCredit(scoreModel);
        }
    }

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

    public void submitDiscountCode(final String discountCode,final long chargeAmount,final long seatPrice) {
        showProgress();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                response = userInfoService.submitDiscount(authToken,discountCode,chargeAmount,seatPrice);
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
                hideProgress();
                if (state) {
                    new HandleApiMessagesBySnackbar(parentLayout, response).showMessages();
                    if(response.Messages.size()>0){
                        for (String msg: response.Messages){
                            Snackbar.make(parentLayout,msg,Snackbar.LENGTH_LONG).show();
                            getUserScore();
                            clearCode();
                        }
                    }
                }
            }
        }.execute();
    }

    public void clearCode() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if (fragment != null) {
            ((PayFragment)fragment).ClearCode();
        }
    }
}
