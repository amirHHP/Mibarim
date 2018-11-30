package com.mibarim.main.ui.activities.worker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mibarim.main.R;

import java.util.List;

/**
 * Created by mohammad hossein on 22/01/2018.
 */

public class workerRecyclerAdapter extends RecyclerView.Adapter<workerRecyclerAdapter.ViewHolder> {

    private Activity _activity;
    private workerServiceActivity.ItemClickListiner onItemClickListiner;
    private List<workerModel> items;
    @Override
    public workerRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.worker_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.worker_time.setText(items.get(position).worker_time);
        holder.worker_price.setText(items.get(position).worker_price);
        holder.worker_car_type.setText(items.get(position).worker_car_type);
        holder.worker_destination.setText(items.get(position).worker_destination);
        holder.worker_origin.setText(items.get(position).worker_origin);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView worker_destination,worker_origin,worker_time,worker_price,worker_car_type;
        public Button worker_button;
        public ViewHolder(View itemView) {
            super(itemView);

            worker_time = (TextView)itemView.findViewById(R.id.worker_time);
            worker_price = (TextView)itemView.findViewById(R.id.worker_price);
            worker_button = (Button) itemView.findViewById(R.id.worker_button_list);
            worker_car_type = (TextView) itemView.findViewById(R.id.worker_car_type);
            worker_destination = (TextView)itemView.findViewById(R.id.worker_destination);
            worker_origin = (TextView)itemView.findViewById(R.id.worker_origin);

            worker_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListiner.onButtonClick(v,getPosition());
                }
            });
        }
    }

    public workerRecyclerAdapter(Activity _activity, workerServiceActivity.ItemClickListiner onItemClickListiner, List<workerModel> items) {
        this._activity = _activity;
        this.onItemClickListiner = onItemClickListiner;
        this.items = items;
    }
}
