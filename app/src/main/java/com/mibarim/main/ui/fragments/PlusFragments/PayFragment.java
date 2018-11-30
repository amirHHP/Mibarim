package com.mibarim.main.ui.fragments.PlusFragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.mibarim.main.BootstrapApplication;
import com.mibarim.main.R;
import com.mibarim.main.models.Plus.PassRouteModel;
import com.mibarim.main.models.ScoreModel;
import com.mibarim.main.ui.activities.PayActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hamed on 3/5/2016.
 */
public class PayFragment extends Fragment {

    private ScrollView layout;
    private PassRouteModel passRouteModel;
    @Bind(R.id.switch_discount)
    protected android.support.v7.widget.SwitchCompat switch_discount;
    @Bind(R.id.discount_layout)
    protected RelativeLayout discount_layout;
    @Bind(R.id.price)
    protected TextView price;
    @Bind(R.id.credit)
    protected TextView credit;
    @Bind(R.id.discount_code)
    protected EditText discount_code;
    @Bind(R.id.discount_btn)
    protected AppCompatButton discount_btn;
    @Bind(R.id.reserve_btn)
    protected AppCompatButton reserve_btn;
    @Bind(R.id.charge_amount)
    protected EditText charge_amount;
long creditMoney;


    public PayFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (ScrollView) inflater.inflate(R.layout.fragment_pay, container, false);




        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());
        passRouteModel=((PayActivity)getActivity()).getPassRouteModel();
        //charge_amount.setText(passRouteModel.PricingString);
        price.setText(passRouteModel.PricingString);

/*
        discount_code.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    discount_code.clearFocus();
                }
                return false;
            }
        });
*/





        discount_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    String discountCode = discount_code.getText().toString();
                    long chargrAmount = Long.valueOf(charge_amount.getText().toString());
                    long seatPrice = passRouteModel.Price;

                    ((PayActivity) getActivity()).submitDiscountCode(discountCode,chargrAmount,seatPrice);
                    return true;
                }
                return false;
            }
        });
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/IRANSans(FaNum)_Light.ttf");
        reserve_btn.setTypeface(font);
        reserve_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    String discountCode = discount_code.getText().toString();
                    long chargeAmount = Long.valueOf(charge_amount.getText().toString());
                    long seatPrice = passRouteModel.Price;
                    ((PayActivity) getActivity()).BookSeat(passRouteModel.TripId,discountCode,chargeAmount,seatPrice,creditMoney);
                    return true;
                }
                return false;
            }
        });

        discount_layout.setVisibility(View.GONE);
        switch_discount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    discount_layout.setVisibility(View.VISIBLE);
                    //discount_layout.animate().translationYBy(120).translationY(0).setDuration(1000);
                }else {
                    discount_layout.setVisibility(View.GONE);
                    //discount_layout.animate().translationYBy(0).translationY(120).setDuration(1000);
                }
            }
        });
    }

    public void setCredit(ScoreModel scoreModel) {
        long toPay=passRouteModel.Price-scoreModel.CreditMoney;
        if(toPay<0){
            toPay=0;
        }
        charge_amount.setText(String.valueOf(toPay));
        credit.setText(scoreModel.CreditMoneyString);
        creditMoney=scoreModel.CreditMoney;
    }

    public void ClearCode() {
        discount_code.setText("");
    }
}
