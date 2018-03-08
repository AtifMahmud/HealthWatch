package com.cpen391.healthwatch.map.marker;

import com.cpen391.healthwatch.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by william on 2018/3/4.
 * A class that is used to obtain icon images for markers.
 */
class CustomIcons {
    static BitmapDescriptor getIcon(String type) {
        switch(type) {
            case "ambulance": return BitmapDescriptorFactory.fromResource(R.drawable.ambulance_icon);
            case "hospital": return BitmapDescriptorFactory.fromResource(R.drawable.hospital_icon);
            default:
                return BitmapDescriptorFactory.fromResource(R.drawable.hospital_icon);
        }
    }
}
