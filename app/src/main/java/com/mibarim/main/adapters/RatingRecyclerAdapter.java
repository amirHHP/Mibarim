/*
package com.mibarim.main.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.mibarim.main.R;
import com.mibarim.main.models.Plus.PassRouteModel;
import com.mibarim.main.models.RatingModel;
import com.mibarim.main.ui.fragments.PlusFragments.PassengerCardFragment;
import com.mibarim.main.ui.fragments.PlusFragments.RatingCardFragment;

import java.util.Calendar;
import java.util.List;

import cn.nekocode.badge.BadgeDrawable;

*/
/**
 * Created by Hamed on 10/16/2016.
 *//*

public class RatingRecyclerAdapter extends RecyclerView.Adapter<RatingRecyclerAdapter.ViewHolder> {
    private List<RatingModel> items;
    private Activity _activity;
    private RatingCardFragment.ItemTouchListener onItemTouchListener;
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
    //private RelativeLayout lastLayout;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public RatingBar rating;
        public BootstrapCircleThumbnail userimage;
        public TextView username;


        public ViewHolder(View v) {
            super(v);
            rating = (RatingBar) v.findViewById(R.id.rating);
            userimage = (BootstrapCircleThumbnail) v.findViewById(R.id.userimage);
            username = (TextView) v.findViewById(R.id.username);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RatingRecyclerAdapter(Activity activity, List<RatingModel> list, RatingCardFragment.ItemTouchListener onItemTouchListener) {
        _activity = activity;
        items = list;
        this.onItemTouchListener = onItemTouchListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RatingRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.passenger_card_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.username.setText(items.get(position).UserFamily);
        holder.rating.setRating(items.get(position).LastRate);
       */
/* holder.carString.setText(items.get(position).CarString);
        holder.seats.setText("ظرفیت: " + items.get(position).EmptySeats + " از " + items.get(position).CarSeats);
        *//*

        */
/*holder.src_distance.setText(items.get(position).SrcDistance);
        holder.dst_distance.setText(items.get(position).DstDistance);*//*


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }
}
*/
