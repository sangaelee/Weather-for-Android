package com.example.android.weatherapp;

import android.content.Context;
import android.preference.Preference;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {
    private static final String TAG = FavoriteAdapter.class.getSimpleName();
    ArrayList<LocationData> locations;
    PreferenceDB sharedPreference;
    private Context context;
    private FavoriteAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView mCity, mCountry;


        public MyViewHolder(View view) {
            super(view);
            mCity = (TextView)view.findViewById(R.id.favorite_city);
            mCountry = (TextView) view.findViewById(R.id.favorite_country);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onLocationSelected(locations.get(getAdapterPosition()));
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onLocationLongClicked(locations.get(getAdapterPosition()));
                    return true;
                }
            });


        }


    }

    public interface FavoriteAdapterListener {
        void onLocationSelected(LocationData location);
        void onLocationLongClicked(LocationData location);
    }

    public FavoriteAdapter(Context context, ArrayList<LocationData> list, FavoriteAdapterListener listener) {
        if(list == null) {
            this.locations = new ArrayList<LocationData>();
        }
        else {
            this.locations = list;
        }
        this.context = context;
        this.listener = listener;
        sharedPreference = new PreferenceDB();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_favorite, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        if(locations == null) return;
        else {
            LocationData location = (LocationData) locations.get(position);
            Log.i(TAG, "SangaeLee city="+location.getCity());
            myViewHolder.mCity.setText(location.getCity());
            myViewHolder.mCountry.setText(location.getCountry());
        }
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }
}
