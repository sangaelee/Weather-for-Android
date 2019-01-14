package com.example.android.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreferenceDB {
    public static String SHARED_PREFERENCES_FILE_NAME = "WeatherLocation";
    public static String SHARED_PREFERENCES_KEY_LOCATION = "location";

    public PreferenceDB() {
        super();
    }

    public void saveLocations(Context context, List<LocationData> locations) {
        SharedPreferences sharedPreferences;
        Editor editor;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonLocation = gson.toJson(locations);
        editor.putString(SHARED_PREFERENCES_KEY_LOCATION, jsonLocation);
        editor.commit();
    }

    public void addLocation(Context context, LocationData location) {
        List<LocationData> locations = getLocations(context);
        if (locations == null)
            locations = new ArrayList<LocationData>();
        locations.add(location);
        saveLocations(context, locations);
    }

    public void removeLocation(Context context, LocationData location) {
        ArrayList<LocationData> locations = getLocations(context);
        if (locations != null) {
            locations.remove(location);
            saveLocations(context, locations);
        }
    }

    public ArrayList<LocationData> getLocations(Context context) {
        SharedPreferences sharedPreferences;
        List<LocationData> locations;

        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);

        if (sharedPreferences.contains(SHARED_PREFERENCES_KEY_LOCATION)) {
            String jsonFavorites = sharedPreferences.getString(SHARED_PREFERENCES_KEY_LOCATION, null);
            Gson gson = new Gson();
            LocationData[] favoriteItems = gson.fromJson(jsonFavorites,
                    LocationData[].class);

            locations = Arrays.asList(favoriteItems);
            locations = new ArrayList<LocationData>(locations);
        } else
            return null;

        return (ArrayList<LocationData>) locations;
    }
}
