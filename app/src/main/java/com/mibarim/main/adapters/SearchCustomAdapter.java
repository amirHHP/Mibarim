package com.mibarim.main.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import com.mibarim.main.R;
import com.mibarim.main.models.StationModel;

import java.util.ArrayList;
import java.util.List;

// The standard text view adapter only seems to search from the beginning of whole words
// so we've had to write this whole class to make it possible to search
// for parts of the arbitrary string we want
public class SearchCustomAdapter extends BaseAdapter implements Filterable {

    private List<StationModel>originalData = null;
    private List<StationModel>filteredData = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public SearchCustomAdapter(Context context, List<StationModel> data) {
        this.filteredData = data ;
        this.originalData = data ;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return filteredData.size();
    }

    public Object getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.search_row_item, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.station_name);

            // Bind the data efficiently with the holder.

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.text.setText(filteredData.get(position).Name);

        return convertView;
    }

    static class ViewHolder {
        TextView text;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();//.replaceAll(" ", "").replace('\u200C', ' ');

            FilterResults results = new FilterResults();

            final List<StationModel> list = originalData;

            int count = list.size();
            final ArrayList<StationModel> nlist = new ArrayList<>(count);

            String filterableString ;
            String temp;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).Name;
                temp = list.get(i).Name.replaceAll(" ", "");

                StationModel model = new StationModel();

                model.setMainStationId(list.get(i).MainStationId);
                model.setName(list.get(i).Name);
                model.setStLat(list.get(i).StLat);
                model.setStLng(list.get(i).StLng);

//                filterableString = list.get(i).replaceAll(" ", "");
                if (temp.toLowerCase().contains(filterString)) {
//                    nlist.add();
                    nlist.add(model);


                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<StationModel>) results.values;
            notifyDataSetChanged();
        }

    }


    public List<StationModel> getItems() {
        return filteredData;
    }


}