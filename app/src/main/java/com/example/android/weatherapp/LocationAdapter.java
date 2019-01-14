package com.example.android.weatherapp;

import android.content.Context;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyViewHolder>implements Filterable{
    private Context context;
    private ArrayList<LocationData> wList;
    private ArrayList<LocationData> locationFilterList;
    ValueFilter valueFilter;
    private LocationAdapterListener listener;




    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView mCity, mCountry;


        public MyViewHolder(View view) {
            super(view);
            mCity = (TextView)view.findViewById(R.id.city);
            mCountry = (TextView) view.findViewById(R.id.country);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onLocationSelected(wList.get(getAdapterPosition()));

                    //Toast.makeText(context, wList.get(getAdapterPosition()).getCity(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

//interface
    public interface LocationAdapterListener {
        void onLocationSelected(LocationData location);
    }

    public LocationAdapter(Context context, ArrayList<LocationData> mList, LocationAdapterListener listener) {
        this.context =  context;
        this.listener = listener;
        this.wList = mList;
        this.locationFilterList = mList;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_location, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        if(wList == null) return;
        else {
            LocationData location = wList.get(position);
            myViewHolder.mCity.setText(location.getCity());
            myViewHolder.mCountry.setText(location.getCountry());
        }
    }

    @Override
    public int getItemCount() {
        return wList.size();
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<LocationData> filterList = new ArrayList<LocationData>();
                for (int i = 0; i < locationFilterList.size(); i++) {
                    if ((locationFilterList.get(i).getCity().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {

                        LocationData location = new LocationData();
                        location.setCity(locationFilterList.get(i).getCity());
                        location.setCountry(locationFilterList.get(i).getCountry());
                        filterList.add(location);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = locationFilterList.size();
                results.values = locationFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            wList = (ArrayList<LocationData>) results.values;
            notifyDataSetChanged();
        }

    }


}
