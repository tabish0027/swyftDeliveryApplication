
package io.devbeans.swyft.interface_retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("geoPoints")
    @Expose
    private GeoPoints geoPoints;
    @SerializedName("address")
    @Expose
    private String address;

    public GeoPoints getGeoPoints() {
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
