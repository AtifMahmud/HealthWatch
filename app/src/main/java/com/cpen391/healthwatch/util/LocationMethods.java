package com.cpen391.healthwatch.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationMethods {
    private static final String TAG = LocationMethods.class.getSimpleName();

    private Context mContext;
    private final static long minute = 60000;
    private final static long hour = minute * 60;
    private final static long day = hour * 24;
    private final static long month = day * 30;
    private final static long year = day * 365;

    private String mLocationData;

    public LocationMethods(Context context) {
        mContext = context;
    }

    /**
     *
     * @param locationData a json string of the form: {lat: double, lng: double, time: long}
     */
    public void setLocationData(String locationData) {
        mLocationData = locationData;
    }

    public String getCity() {
        if (mLocationData != null) {
            try {
                JSONObject locationJSON = new JSONObject(mLocationData);
                return getCity(locationJSON.getDouble("lat"), locationJSON.getDouble("lng"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getCity(double lat, double lng) {
        Log.d(TAG, "lat: " + lat);
        Log.d(TAG, "lng: " + lng);
        String cityName = "";
        try {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Log.d(TAG, "address: " + addresses);
            String addressLine = addresses.get(0).getAddressLine(0);
            Log.d(TAG, "addressLine: " + addressLine);
            cityName = addressLine.split(",")[1].trim();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        return cityName;
    }

    public String getTimeLastUpdated() {
        if (mLocationData != null) {
            try {
                JSONObject locationJSON = new JSONObject(mLocationData);
                return getTimeLastUpdated(locationJSON.getLong("time"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getTimeLastUpdated(long lastUpdated) {
        long currentTime = System.currentTimeMillis();
        Log.d(TAG, "current time: " + currentTime);
        Log.d(TAG, "last updated time: " + lastUpdated);
        long difference  = currentTime - lastUpdated;
        String message;

        if (difference < minute) {
            message = "updated " + difference/1000 + " seconds ago";
        } else if (difference < hour) {
            message = "updated " + difference/minute + " minutes ago";
        } else if (difference < day) {
            message = "updated " + difference/hour + " hours ago";
        } else if (difference < month) {
            message = "updated " + difference/day + " days ago";
        } else if (difference < year) {
            message = "updated " + difference/month + " months ago";
        } else {
            Date fullDate = new Date(lastUpdated);
            message = "last updated: " + fullDate;
        }

        return message;
    }
}
