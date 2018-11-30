package com.mibarim.main.adapters;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.mibarim.main.R;
import com.mibarim.main.models.RatingModel;


import java.util.ArrayList;

/**
 * Created by Alireza on 9/16/2017.
 */

public class RatingAdapter extends ArrayAdapter<RatingModel> implements View.OnClickListener {
    private ArrayList<RatingModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView userNameTv;
        TextView txtVersion;
//        ImageView userImage;
        BootstrapCircleThumbnail userImage;
        LinearLayout ratingDescriptionLayout;
        RatingBar ratingBar;
        CheckBox presenceCheckbox;
        EditText ratingDescriptionEt;
        Button addDescriptionButton;
        android.support.v7.widget.SwitchCompat presenceSwitchButton;
        TextView presenceTextview;
    }


    public RatingAdapter(ArrayList<RatingModel> data, Context context) {
        super(context, R.layout.rating_row_item, data);
        this.dataSet = data;
        this.mContext = context;

    }


    @Override
    public void onClick(View v) {


        int position = (Integer) v.getTag();
        Object object = getItem(position);
        RatingModel dataModel = (RatingModel) object;


        /*switch (v.getId()) {

            case R.id.item_info:

//                Snackbar.make(v, "Release date " + dataModel.getRatingBarInt(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();

                break;


        }*/


    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final RatingModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.rating_row_item, parent, false);
            viewHolder.ratingDescriptionLayout = (LinearLayout) convertView.findViewById(R.id.rating_description_layout);
            viewHolder.userNameTv = (TextView) convertView.findViewById(R.id.user_name_textview);
            viewHolder.presenceCheckbox = (CheckBox) convertView.findViewById(R.id.presence_checkbox);
            viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.rating_bar);
            viewHolder.userImage = (BootstrapCircleThumbnail) convertView.findViewById(R.id.user_image);
            viewHolder.presenceCheckbox = (CheckBox) convertView.findViewById(R.id.presence_checkbox);
            viewHolder.ratingDescriptionEt = (EditText) convertView.findViewById(R.id.rating_description_edittext);
            viewHolder.addDescriptionButton = (Button) convertView.findViewById(R.id.add_description_button);
            viewHolder.presenceSwitchButton = (SwitchCompat) convertView.findViewById(R.id.presence_switch_button);
            viewHolder.presenceTextview = (TextView) convertView.findViewById(R.id.presence_textview);



//            viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.version_number);
//            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);

            result = convertView;
/*

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
*/

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;


//        viewHolder.ratingDescriptionLayout.setVisibility(View.GONE);

//        viewHolder.ratingDescriptionLayout.setVisibility(View.INVISIBLE);

        viewHolder.userNameTv.setText(dataModel.getName() + " " + dataModel.getFamily());
        viewHolder.ratingBar.setProgress(dataModel.getRate());
        viewHolder.userImage.setImageBitmap(dataModel.getImageBitmap());



        viewHolder.presenceSwitchButton.setChecked(true);
        viewHolder.presenceCheckbox.setChecked(true);
        dataModel.setPresence(1);


        /*viewHolder.presenceSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewHolder.presenceSwitchButton.isChecked()) {
                    viewHolder.ratingBar.setEnabled(true);
                    viewHolder.addDescriptionButton.setEnabled(true);
                    viewHolder.ratingDescriptionEt.setEnabled(true);
                    dataModel.setPresence(1);
                    viewHolder.presenceTextview.setText("همسفر حضور داشت");
                } else {
                    viewHolder.ratingBar.setProgress(0);
                    viewHolder.ratingBar.setEnabled(false);
                    viewHolder.addDescriptionButton.setEnabled(false);
                    viewHolder.ratingDescriptionEt.setEnabled(false);
                    viewHolder.ratingDescriptionEt.setText("");
                    dataModel.setPresence(0);
                    viewHolder.presenceTextview.setText("همسفر حضور نداشت");
                }

            }
        });*/


        viewHolder.presenceSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    viewHolder.ratingBar.setEnabled(true);
                    viewHolder.addDescriptionButton.setEnabled(true);
                    viewHolder.ratingDescriptionEt.setEnabled(true);
                    dataModel.setPresence(1);
                    viewHolder.presenceTextview.setText(R.string.joined_the_trip);
                } else{
                    viewHolder.ratingBar.setProgress(0);
                    viewHolder.ratingBar.setEnabled(false);
                    viewHolder.addDescriptionButton.setEnabled(false);
                    viewHolder.ratingDescriptionEt.setEnabled(false);
                    viewHolder.ratingDescriptionEt.setText("");
                    dataModel.setPresence(0);
                    viewHolder.presenceTextview.setText("حضور نداشت");
                }
            }
        });



        /*viewHolder.presenceCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.presenceCheckbox.isChecked()) {
                    viewHolder.ratingBar.setEnabled(true);
                    viewHolder.addDescriptionButton.setEnabled(true);
                    viewHolder.ratingDescriptionEt.setEnabled(true);
                    dataModel.setPresence(1);
                } else {
                    viewHolder.ratingBar.setProgress(0);
                    viewHolder.ratingBar.setEnabled(false);
                    viewHolder.addDescriptionButton.setEnabled(false);
                    viewHolder.ratingDescriptionEt.setEnabled(false);
                    viewHolder.ratingDescriptionEt.setText("");
                    dataModel.setPresence(0);
                }

            }
        });*/


        /*viewHolder.addDescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.ratingDescriptionLayout.setVisibility(View.VISIBLE);
                viewHolder.addDescriptionButton.setVisibility(View.GONE);
            }
        });*/


        /*viewHolder.ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int stars = viewHolder.ratingBar.getNumStars();
                dataModel.setRate(stars);

            }
        });*/


        /*viewHolder.ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    viewHolder.ratingBar.setIsIndicator(false);
                    return true;
                }

                return false;

            }
        });*/


        viewHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                int stars = viewHolder.ratingBar.getNumStars();
                int stars = (int) viewHolder.ratingBar.getRating();
                dataModel.setRate(stars);
//                viewHolder.ratingBar.setIsIndicator(true);


            }
        });


//        viewHolder.ratingDescriptionEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                String rateDescription = viewHolder.ratingDescriptionEt.getText().toString();
//                dataModel.setRateDescription(rateDescription);
//            }
//        });

        viewHolder.ratingDescriptionEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String rateDescription = viewHolder.ratingDescriptionEt.getText().toString();
                dataModel.setRateDescription(rateDescription);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


//        viewHolder.userImage.setImageResource();
//        viewHolder.txtVersion.setText(dataModel.getVersion_number());
//        viewHolder.info.setOnClickListener(this);
//        viewHolder.presenceCheckbox.setTag(position);
//        viewHolder.ratingBar.setTag(position);
        // Return the completed view to render on screen

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        return convertView;
    }


}
