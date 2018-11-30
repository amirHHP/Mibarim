package com.mibarim.main.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.mibarim.main.R;
import com.mibarim.main.models.SubStationModel;
import com.mibarim.main.ui.fragments.SubstationListFragment;

import java.util.List;

/**
 * Created by Alireza on 10/9/2017.
 */

public class SubstationListAdapter extends RecyclerView.Adapter<SubstationListAdapter.ViewHolder> {


    // Create new views (invoked by the layout manager)
    @Override
    public SubstationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.station_card_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    private List<SubStationModel> items;
    private Activity _activity;
    private SubstationListFragment.ItemTouchListener onItemTouchListener;


    //private RelativeLayout lastLayout;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        //public RelativeLayout row_layout;
        //public TextView username;
        public TextView station_add;


        public ViewHolder(View v) {
            super(v);
            station_add = (TextView) v.findViewById(R.id.station_add);


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardViewTap(v, getPosition());
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SubstationListAdapter(Activity activity, List<SubStationModel> list, SubstationListFragment.ItemTouchListener onItemTouchListener) {
        _activity = activity;
        items = list;
        this.onItemTouchListener = onItemTouchListener;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.station_add.setText(items.get(position).StAdd);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }




}
