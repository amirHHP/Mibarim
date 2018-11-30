package com.mibarim.main.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.R;
import com.mibarim.main.models.Plus.PaymentDetailModel;
import com.mibarim.main.services.UserInfoService;
import com.mibarim.main.util.SafeAsyncTask;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Arya on 1/20/2018.
 */

public class ChargeAccountActivity extends Activity implements View.OnClickListener {


    @Bind(R.id.charge_amount)
    EditText chargeAmountEditText;
    @Bind(R.id.amount1)
    Button amount1;
    @Bind(R.id.amount2)
    Button amount2;
    @Bind(R.id.amount3)
    Button amount3;
    @Bind(R.id.amount4)
    Button amount4;
    @Bind(R.id.payment_button)
    Button paymentButton;
    @Bind(R.id.clear_amount)
    ImageView clearAll;
    @Bind(R.id.current_credit)
    TextView currentCredit;

    @Inject
    UserInfoService userInfoService;
    PaymentDetailModel paymentModel;
    String mobileNumber, acceptedAmount;
    int amount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charge_account_layout);
        BootstrapApplication.component().inject(this);
        ButterKnife.bind(this);
        paymentModel = new PaymentDetailModel();
        amount1.setOnClickListener(this);
        amount2.setOnClickListener(this);
        amount3.setOnClickListener(this);
        amount4.setOnClickListener(this);
        clearAll.setOnClickListener(this);
        paymentButton.setOnClickListener(this);
        currentCredit.append("اعتبار : ");
        currentCredit.append(getIntent().getExtras().getString("currentCredit"));
    }

    private void payment() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.mibarim.main", Context.MODE_PRIVATE);
        mobileNumber = sharedPreferences.getString("UserMobile", "");
        final ProgressDialog dialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        dialog.setMessage("لطفا صبر کنید...");
        dialog.show();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                paymentModel = userInfoService.sendChargeAmount(mobileNumber, acceptedAmount);
                return true;
            }

            @Override
            protected void onSuccess(Boolean aBoolean) throws Exception {
                super.onSuccess(aBoolean);
                dialog.dismiss();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(paymentModel.BankLink));
                startActivity(i);
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
                dialog.dismiss();
                Toast.makeText(ChargeAccountActivity.this, getString(R.string.error_server_connection), Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        if (chargeAmountEditText.getText().toString().matches(""))
            amount = 0;
        else
            amount = Integer.parseInt(chargeAmountEditText.getText().toString());
        switch (v.getId()) {
            case R.id.amount1:
                amount += Integer.parseInt(amount1.getText().toString().replace(getString(R.string.toman), "").replaceAll("\\s+", ""));
                chargeAmountEditText.setText(String.valueOf(amount));
                break;
            case R.id.amount2:
                amount += Integer.parseInt(amount2.getText().toString().replace(getString(R.string.toman), "").replaceAll("\\s+", ""));
                chargeAmountEditText.setText(String.valueOf(amount));
                break;
            case R.id.amount3:
                amount += Integer.parseInt(amount3.getText().toString().replace(getString(R.string.toman), "").replaceAll("\\s+", ""));
                chargeAmountEditText.setText(String.valueOf(amount));
                break;
            case R.id.amount4:
                amount += Integer.parseInt(amount4.getText().toString().replace(getString(R.string.toman), "").replaceAll("\\s+", ""));
                chargeAmountEditText.setText(String.valueOf(amount));
                break;
            case R.id.clear_amount:
                chargeAmountEditText.setText("");
                break;
            case R.id.payment_button:
                if (amount < 500)
                    Toast.makeText(this, getString(R.string.low_charge_amount), Toast.LENGTH_SHORT).show();
                else {
                    acceptedAmount = String.valueOf(amount);
                    payment();
                }
                break;
        }
    }
}
