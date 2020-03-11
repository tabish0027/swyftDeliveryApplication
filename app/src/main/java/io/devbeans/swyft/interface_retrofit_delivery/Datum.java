
package io.devbeans.swyft.interface_retrofit_delivery;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.devbeans.swyft.interface_retrofit.Location;

public class Datum implements Comparable<Datum>{


    private double distance=0.0;

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("parcels")
    @Expose
    private List<Parcel> parcels = null;

    @Override
    public int compareTo(Datum o) {
        if (o == null) {
            return -1;
        }
        int c = Double.valueOf(distance).compareTo(o.distance);
        if (c != 0) {
            return c;
        }
        return name.compareTo(o.name);
    }
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
    public List<Parcel> getParcels() {
        return parcels;
    }

    public void setParcels(List<Parcel> parcels) {
        this.parcels = parcels;
    }

    public void markAllParcelToBeComplete(){
        for(int i =0 ; i<parcels.size();i++){
            parcels.get(i).parcel_to_mark_complete = true;
        }
    }
}
