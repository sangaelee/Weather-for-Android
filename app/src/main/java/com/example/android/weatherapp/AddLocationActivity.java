package com.example.android.weatherapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Toast;


import java.net.URL;
import java.util.ArrayList;
import android.text.Editable;
import android.text.TextWatcher;

public class AddLocationActivity extends AppCompatActivity implements android.widget.SearchView.OnQueryTextListener,LocationAdapter.LocationAdapterListener{
    private RecyclerView recyclerView;
    private ArrayList<LocationData> locationList;
    private PreferenceDB sharedPreference;
    private LocationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        android.widget.SearchView sv = (android.widget.SearchView) findViewById(R.id.search_view);
        sv.setOnQueryTextListener(this);


        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        sharedPreference = new PreferenceDB();

        FetchLocationTask mFetchLocationTask = new FetchLocationTask();
        mFetchLocationTask.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Intent i = new Intent(AddLocationActivity.this, FavoriteActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        super.onBackPressed();
    }




    public void onLocationSelected(LocationData location) {
        //saved preference(duplication check)
        ArrayList<LocationData> savedLocation = sharedPreference.getLocations(this);
        for(int i=0; i<savedLocation.size();i++) {
            if((savedLocation.get(i).getCity()).equalsIgnoreCase(location.getCity())) {
                Toast.makeText(getApplicationContext(), "Already exist " + location.getCity() + ", " + location.getCountry(), Toast.LENGTH_LONG).show();
                return;
            }

        }

        sharedPreference.addLocation(this, location);
       Toast.makeText(getApplicationContext(), "Selected: " + location.getCity() + ", " + location.getCountry(), Toast.LENGTH_LONG).show();
        //go to Favorite
        Intent i = new Intent(AddLocationActivity.this, FavoriteActivity.class);
        startActivity(i);
    }


    //Async
    public class FetchLocationTask extends AsyncTask<Void, Void, ArrayList<LocationData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<LocationData> doInBackground(Void... params) {

            URL requestUrl = NetworkHandler.buildCityUrl();

            try {
                String jsonResponse = NetworkHandler.getResponseFromHttpUrl(requestUrl);
                //String jsonResponse = readJSONFromAsset();

                ArrayList<LocationData> wList = JsonUtil.getLocationValueFromJson(jsonResponse);
                return wList;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(ArrayList<LocationData> wList) {
            locationList = wList;
            mAdapter = new LocationAdapter(AddLocationActivity.this, locationList, AddLocationActivity.this);
            recyclerView.setAdapter(mAdapter);


        }
    }



    @Override
    public boolean onQueryTextSubmit(String s) {
        mAdapter.getFilter().filter(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        mAdapter.getFilter().filter(s);
        return false;
    }
}
