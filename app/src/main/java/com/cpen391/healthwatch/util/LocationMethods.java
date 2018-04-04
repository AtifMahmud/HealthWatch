package com.cpen391.healthwatch.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationMethods {
    private Context mContext;

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
}
