package com.example.android.weatherapp;

import android.content.Intent;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity implements FavoriteAdapter.FavoriteAdapterListener{
    private RecyclerView recyclerView;
    private ArrayList<LocationData> locationList;
    private PreferenceDB sharedPreference;
    private FavoriteAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        recyclerView = findViewById(R.id.recyclerview_favorite);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        sharedPreference = new PreferenceDB();
        locationList = sharedPreference.getLocations(this);
        mAdapter = new FavoriteAdapter(FavoriteActivity.this, locationList, FavoriteActivity.this);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_back) {
            Intent i = new Intent(FavoriteActivity.this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        if (id == R.id.action_add) {
            Intent i = new Intent(FavoriteActivity.this, AddLocationActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onLocationSelected(LocationData location) {
        Toast.makeText(getApplicationContext(), "Selected: " + location.getCity() + ", " + location.getCountry(), Toast.LENGTH_LONG).show();
        Intent i = new Intent(FavoriteActivity.this, MainActivity.class);
        i.putExtra("weather_city", location.getCity());
        i.putExtra("weather_country", location.getCountry());
        startActivity(i);
    }

    public void onLocationLongClicked(LocationData location){
        Toast.makeText(getApplicationContext(), "Long pressed: " + location.getCity() + ", " + location.getCountry(), Toast.LENGTH_LONG).show();
        sharedPreference.removeLocation(this, location);
        locationList = sharedPreference.getLocations(this);
        mAdapter.notifyDataSetChanged();
    }
}
