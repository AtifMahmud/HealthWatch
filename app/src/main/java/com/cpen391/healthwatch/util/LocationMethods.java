package com.cpen391.healthwatch.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationMethods {
    private Context mContext;
    private final static long minute = 60 * 1000;
    private final static long hour = minute * 60;
    private final static long day = hour * 24;
    private final static long month = day * 30;
    private final static long year = day * 365;

    public LocationMethods(Context context) {
        mContext = context;
    }

    public String getCity(double lat, double lng) {
        String cityName = "";
        try {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            String addressLine = addresses.get(0).getAddressLine(1);
            cityName = addressLine.split(",")[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    public String getTimeLastUpdated(long lastUpdated) {
        long currentTime = System.currentTimeMillis();
        long difference  = currentTime - lastUpdated;
        String message;

        if (difference < minute) {
            message = "updated " + difference + "seconds ago";
        } else if (difference < hour) {
            message = "updated " + difference/minute + "minutes ago";
        } else if (difference < day) {
            message = "updated " + difference/hour + "hours ago";
        } else if (difference < month) {
            message = "updated " + difference/day + "days ago";
        } else if (difference < year) {
            message = "updated " + difference/month + "months ago";
        } else {
            Date fullDate = new Date(currentTime);
            message = "last updated: " + fullDate;
        }

        return message;
    }
}
