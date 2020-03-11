
package io.devbeans.swyft.interface_retrofit_delivery;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.devbeans.swyft.interface_retrofit.GeoPoints;

public class Location {

    @SerializedName("geoPoints")
    @Expose
    private io.devbeans.swyft.interface_retrofit.GeoPoints geoPoints;
    @SerializedName("address")
    @Expose
    private String address;

    public io.devbeans.swyft.interface_retrofit.GeoPoints getGeoPoints() {
        return geoPoints;
    }

    public void setGeoPoints(GeoPoints geoPoints) {
        this.geoPoints = geoPoints;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
