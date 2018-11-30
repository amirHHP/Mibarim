package com.mibarim.main.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mibarim.main.R;
import com.mibarim.main.models.FilterModel;
import com.mibarim.main.models.FilterTimeModel;
import com.mibarim.main.ui.activities.MainActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alireza on 11/22/2017.
 */

public class SuggestedTimesAdapter extends ArrayAdapter<FilterTimeModel> {

    Context myContext;
    List<FilterTimeModel> items;

    public SuggestedTimesAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<FilterTimeModel> objects) {
        super(context, resource, objects);
        myContext = context;

        items = new ArrayList<>();
        items = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.suggested_times_card_item, null);
        }

        final FilterTimeModel selectedItem =  getItem(position);


        final TextView priceStringTextview = (TextView) view.findViewById(R.id.price_string_textview);
        final TextView priceTitleTextview = (TextView) view.findViewById(R.id.price_title_textview);
        final TextView timeHourTextview = (TextView) view.findViewById(R.id.time_hour_textview);
        final TextView timeMinuteTextview = (TextView) view.findViewById(R.id.time_minute_textview);
        final TextView pairPassengersTextview = (TextView) view.findViewById(R.id.pair_passengers);

        LinearLayout timeLayout = (LinearLayout) view.findViewById(R.id.time_linear_layout);

        TextView suggestOrChooseDescriptionText = (TextView) view.findViewById(R.id.suggest_or_choose_description_text);
        TextView suggestOrChooseDescriptionTextSecond = (TextView) view.findViewById(R.id.suggest_or_choose_description_text_second);





        String hour = Integer.toString(items.get(position).TimeHour);
        String min = Integer.toString(items.get(position).TimeMinute);
        String pairPass = Integer.toString(items.get(position).PairPassengers);


        NumberFormat numberFormat = new DecimalFormat("00");

        if (!selectedItem.IsManual) {

            timeHourTextview.setText(numberFormat.format(items.get(position).TimeHour));
            timeMinuteTextview.setText(numberFormat.format(items.get(position).TimeMinute));

            priceStringTextview.setText(items.get(position).PriceString);
            pairPassengersTextview.setText(pairPass);
        } else {
//            timeHourTextview.setText(numberFormat.format(items.get(position).TimeHour));
//            timeMinuteTextview.setText(numberFormat.format(items.get(position).TimeMinute));
//            pairPassengersTextview.setText(pairPass);

//            timeHourTextview.setVisibility(View.GONE);
//            timeMinuteTextview.setVisibility(View.GONE);

            timeLayout.setVisibility(View.GONE);

            suggestOrChooseDescriptionTextSecond.setVisibility(View.GONE);
            priceTitleTextview.setText("حداکثر");
            priceStringTextview.setText(items.get(position).PriceString);
            suggestOrChooseDescriptionText.setText("زمان سفر خود را انتخاب کنید");
            pairPassengersTextview.setVisibility(View.GONE);
        }


//        final View finalView = view;
        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                priceStringTextview.setTextColor(myContext.getResources().getColor(R.color.main_black));
                timeHourTextview.setTextColor(myContext.getResources().getColor(R.color.main_black));
                timeMinuteTextview.setTextColor(myContext.getResources().getColor(R.color.main_black));
                pairPassengersTextview.setTextColor(myContext.getResources().getColor(R.color.main_black));




            }
        });*/



        return view;

    }


}
