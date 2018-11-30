package com.mibarim.main.adapters;

/**
 * Created by Alireza on 11/21/2017.
 */

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mibarim.main.R;
import com.mibarim.main.models.Plus.PassRouteModel;
import com.mibarim.main.ui.fragments.PlusFragments.PassengerCardFragment;
import com.mibarim.main.ui.fragments.RouteDetailsFragment;

import java.util.Calendar;
import java.util.List;

import cn.nekocode.badge.BadgeDrawable;

/**
 * Created by Hamed on 10/16/2016.
 */
public class RouteDetailsAdapter extends RecyclerView.Adapter<RouteDetailsAdapter.ViewHolder> {
    private List<PassRouteModel> items;
    private Activity _activity;
    private RouteDetailsFragment.ItemTouchListener onItemTouchListener;
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;


    //private RelativeLayout lastLayout;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        //public RelativeLayout row_layout;
        //public TextView username;
        public TextView timing;
        public TextView seats;
        public TextView src_address;
        //public ImageView src_img;
        public TextView dst_address;
        public Button station_dest;
        public Button src_station;
        //public ImageButton dst_img;
        public TextView carString;
        /*public TextView src_distance;
        public TextView dst_distance;*/
//        public BootstrapCircleThumbnail userimage;
        public AppCompatButton book_trip;


        public ViewHolder(View v) {
            super(v);
            //row_layout = (RelativeLayout) v.findViewById(R.id.row_layout);
            //username = (TextView) v.findViewById(R.id.username);
            timing = (TextView) v.findViewById(R.id.timing);
            seats = (TextView) v.findViewById(R.id.seats);
            src_address = (TextView) v.findViewById(R.id.src_address);
            //src_img = (ImageView) v.findViewById(R.id.src_img);
            dst_address = (TextView) v.findViewById(R.id.dst_address);
            station_dest = (Button) v.findViewById(R.id.station_dest);
            src_station = (Button) v.findViewById(R.id.src_station);
            //dst_img = (ImageButton) v.findViewById(R.id.dst_img);
            carString = (TextView) v.findViewById(R.id.carString);
            /*src_distance = (TextView) v.findViewById(R.id.src_distance);
            dst_distance = (TextView) v.findViewById(R.id.dst_distance);*/
            //userimage = (BootstrapCircleThumbnail) v.findViewById(R.id.userimage);
            book_trip = (AppCompatButton) v.findViewById(R.id.book_trip);
            Typeface font = Typeface.createFromAsset(_activity.getAssets(), "fonts/IRANSans(FaNum)_Light.ttf");
            book_trip.setTypeface(font);
            src_station.setTypeface(font);
            station_dest.setTypeface(font);
            book_trip.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if(clickDuration < MAX_CLICK_DURATION) {
                                onItemTouchListener.onBookBtnClick(v, getPosition());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;
                }
            });


            /*src_address.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if(clickDuration < MAX_CLICK_DURATION) {
                                onItemTouchListener.onSrcLinkClick(v, getPosition());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;
                }
            });*/
            src_station.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if(clickDuration < MAX_CLICK_DURATION) {
                                onItemTouchListener.onSrcLinkClick(v, getPosition());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;
                }
                /*@Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        onItemTouchListener.onSrcLinkClick(v, getPosition());
                        return true;
                    }
                    return false;
                }*/
            });
            station_dest.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if(clickDuration < MAX_CLICK_DURATION) {
                                onItemTouchListener.onDstLinkClick(v, getPosition());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;
                }
                /*@Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        onItemTouchListener.onDstLinkClick(v, getPosition());
                        return true;
                    }
                    return false;
                }*/
            });
            /*dst_address.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if(clickDuration < MAX_CLICK_DURATION) {
                                onItemTouchListener.onDstLinkClick(v, getPosition());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;
                }
            });*/
            /*v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardViewTap(v, getPosition());
                    *//*if (lastLayout != null) {
                        lastLayout.setSelected(false);
                    }
                    row_layout.setSelected(true);
                    lastLayout = row_layout;*//*
                }
            });
            userimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onUserImageClick(v, getPosition());
                }
            });*/
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RouteDetailsAdapter(Activity activity, List<PassRouteModel> list, RouteDetailsFragment.ItemTouchListener onItemTouchListener) {
        _activity = activity;
        items = list;
        this.onItemTouchListener = onItemTouchListener;
    }



    // Create new views (invoked by the layout manager)
    @Override
    public RouteDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.passenger_card_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        //changeFont(parent);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (items.get(position).IsBooked) {
            BadgeDrawable drawableBadge =
                    new BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                            .badgeColor(0xFF77FF00)
                            .text1("رزرو شد")
                            .build();
            SpannableString spannableString =
                    new SpannableString(TextUtils.concat(
                            items.get(position).TimingString,
                            " ",
                            drawableBadge.toSpannable()
                    ));
            holder.timing.setText(spannableString);
            holder.book_trip.setText("مشاهده سفر");
            /*if (items.get(position).TripState == TripStates.InPreTripTime.toInt() ||
                    items.get(position).TripState == TripStates.InRiding.toInt() ||
                    items.get(position).TripState == TripStates.InTripTime.toInt()) {

            } else {
                holder.book_trip.setVisibility(View.GONE);
            }*/
        } else {
            if (items.get(position).EmptySeats == 0) {
                BadgeDrawable drawableBadge =
                        new BadgeDrawable.Builder()
                                .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                                .badgeColor(0xFFBF360C)
                                .text1("پرشد")
                                .build();
                SpannableString spannableString =
                        new SpannableString(TextUtils.concat(
                                items.get(position).TimingString,
                                " ",
                                drawableBadge.toSpannable()
                        ));
                holder.timing.setText(spannableString);
                holder.book_trip.setVisibility(View.GONE);
            } else {
                holder.timing.setText(items.get(position).TimingString);
                holder.book_trip.setVisibility(View.VISIBLE);
                holder.book_trip.setText("رزرو صندلی (" + items.get(position).PricingString + " تومان) ");
            }
        }
        if (items.get(position).IsVerified) {
            BadgeDrawable drawableBadge =
                    new BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                            .badgeColor(0xFF77FF00)
                            .text1("✔")
                            .build();
            SpannableString spannableString =
                    new SpannableString(TextUtils.concat(
                            items.get(position).Name,
                            " ",
                            items.get(position).Family,
                            " ",
                            drawableBadge.toSpannable()
                    ));

            //holder.username.setText(spannableString);
        } else {
            //holder.username.setText(items.get(position).Name + " " + items.get(position).Family);
        }
        //holder.userimage.setImageBitmap(((MainCardActivity) _activity).getImageById(items.get(position).UserImageId, R.mipmap.ic_user_black));

        holder.src_address.setText(items.get(position).SrcAddress);
        holder.dst_address.setText(items.get(position).DstAddress);
        holder.carString.setText(items.get(position).CarString);
        holder.seats.setText("ظرفیت: " + items.get(position).EmptySeats + " از " + items.get(position).CarSeats);
        /*holder.src_distance.setText(items.get(position).SrcDistance);
        holder.dst_distance.setText(items.get(position).DstDistance);*/

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }
    /*private void changeFont(ViewGroup layout) {//Where 'layout' is the parent
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                changeFont((ViewGroup) child);
            } else if (child instanceof Button) {
                ((Button) child).setTypeface(Typeface.DEFAULT);
            }
        }
    }*/
}

