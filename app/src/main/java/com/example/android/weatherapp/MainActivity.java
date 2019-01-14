package com.example.android.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ArrayList<WeatherData> weatherList;
    WeatherData mWeatherData;
    private RecyclerView mRecyclerView;
    private ForecastAdapter mAdapter;
    Boolean Flag_First = true;
    Intent iService;
    BroadcastReceiver receiver;
    String serviceData;

    String cityname ;
    String countryname;
    String description= null, icon = null;

    ImageView cardview_image;
    TextView cardview_description, cardview_date, cardview_city, cardview_temperature,
            cardview_humidity, cardview_pressure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 5);
        mRecyclerView.setLayoutManager(mLayoutManager);


        cardview_image = findViewById(R.id.cardview_image);
        cardview_date = findViewById(R.id.cardview_date);
        cardview_city = findViewById(R.id.cardview_city);
        cardview_description = findViewById(R.id.cardview_description);
        cardview_temperature = findViewById(R.id.cardview_temperature);
        cardview_humidity = findViewById(R.id.cardview_humidityvalue);
        cardview_pressure = findViewById(R.id.cardview_pressurevalue);



    }


    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        cityname = intent.getStringExtra("weather_city");
        countryname = intent.getStringExtra("weather_country");

        if(cityname == null) {
            cityname = "toronto";
            countryname = "canada";
            PreferenceDB sharedPreference = new PreferenceDB();
            LocationData location = new LocationData();
            location.setCity(cityname);
            location.setCountry(countryname);
            ArrayList<LocationData> savedLocation = sharedPreference.getLocations(this);
            if(savedLocation != null) {
                for (int i = 0; i < savedLocation.size(); i++) {
                    if ((savedLocation.get(i).getCity()).equalsIgnoreCase(cityname)) {
                      //  Toast.makeText(getApplicationContext(), "Already exist " + location.getCity() + ", " + location.getCountry(), Toast.LENGTH_LONG).show();
                    }

                }
            }
            else {
                sharedPreference.addLocation(this, location);
            }
        }
        FetchWeatherTask mFetchWeatherTask = new FetchWeatherTask();
        mFetchWeatherTask.execute(cityname, countryname);

        FetchForeCastTask mFetchForeCastTask = new FetchForeCastTask();
        mFetchForeCastTask.execute(cityname, countryname);


    }

    protected void onReStart() {
        super.onResume();
        Intent intent = getIntent();
        cityname = intent.getStringExtra("weather_city");
        countryname =intent.getStringExtra("weather_country");
        FetchWeatherTask mFetchWeatherTask = new FetchWeatherTask();
        mFetchWeatherTask.execute(cityname, countryname);

        FetchForeCastTask mFetchForeCastTask = new FetchForeCastTask();
        mFetchForeCastTask.execute(cityname, countryname);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, WeatherData> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected WeatherData doInBackground(String... params) {

            String location = params[0];
            String country = params[1];
            URL weatherRequestUrl = NetworkHandler.buildUrl(location, country, true);

            try {
                String jsonWeatherResponse = NetworkHandler
                        .getResponseFromHttpUrl(weatherRequestUrl);

                WeatherData wData = JsonUtil.getWeatherValueFromJson(location, jsonWeatherResponse);


                return wData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(WeatherData wData) {
            cardview_date.setText(wData.getWeather_date());
            cardview_city.setText(wData.getCity());
            cardview_description.setText(wData.getDescription());
            String temString = WeatherUtil.getNumberFormat(wData.getTemp()) + "°C";
            cardview_temperature.setText(temString);
            cardview_humidity.setText(wData.getHumidity() + "%");
            String pString = WeatherUtil.getNumberFormat(wData.getPressure()) + "mph";
            cardview_pressure.setText(pString);
            int sId = WeatherUtil.getTheIconID(wData.getIcon());
            cardview_image.setImageResource(sId);

        }
    }


    public class FetchForeCastTask extends AsyncTask<String, Void, ArrayList<WeatherData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<WeatherData> doInBackground(String... params) {

            String location = params[0];
            String country = params[1];
            URL weatherRequestUrl = NetworkHandler.buildUrl(location, country, false);

            try {
                String jsonWeatherResponse = NetworkHandler
                        .getResponseFromHttpUrl(weatherRequestUrl);

                ArrayList<WeatherData> wList = JsonUtil.getForecastValueFromJson(location, jsonWeatherResponse);
                return wList;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<WeatherData> wList) {
            weatherList = wList;
            mAdapter = new ForecastAdapter(weatherList);
            mRecyclerView.setAdapter(mAdapter);

        }
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
            Intent i = new Intent(MainActivity.this, FavoriteActivity.class);
            startActivity(i);
            //finish();
            return true;
        }
        else if (id == R.id.action_add) {
            /*Intent i = new Intent(MainActivity.this, EditActivity.class);
            Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + id);
            i.putExtra(MyContentProvider.CONTENT_ITEM_TYPE, uri);
            startActivityForResult(i, ACTIVITY_EDIT);*/
            Intent i = new Intent(MainActivity.this, AddLocationActivity.class);
            startActivity(i);
            //finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}