package com.example.mini_.pathless;

import com.google.android.gms.maps.model.LatLng;

public class MarkerInformation {
    private LatLng latLong;

    public MarkerInformation(){

    }

    public LatLng getLatLong() {
        return latLong;
    }

    public void setLatLong(LatLng latLong) {
        this.latLong = latLong;
    }
}
