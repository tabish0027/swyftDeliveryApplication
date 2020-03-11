
package io.devbeans.swyft.interface_retrofit;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PickupParcel implements Comparable<PickupParcel> {

    private double distance_from_current_location = 0.0;
    @SerializedName("name")
    @Expose
    private String name;
    private double distance=0.0;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("poc")
    @Expose
    private Poc poc;
    @SerializedName("parcels")
    @Expose
    private List<Parcel> parcels = null;
    @SerializedName("taskId")
    @Expose
    private String taskId;
    @SerializedName("taskStatus")
    @Expose
    private String taskStatus="";

    @Override
    public int compareTo(PickupParcel o) {
        if (o == null) {
            return -1;
        }
        int c = Double.valueOf(distance).compareTo(o.distance);
        if (c != 0) {
            return c;
        }
        return name.compareTo(o.name);
    }

    public double getDistance_from_current_location() {
        return distance_from_current_location;
    }

    public void setDistance_from_current_location(double distance_from_current_location) {
        this.distance_from_current_location = distance_from_current_location;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Poc getPoc() {
        return poc;
    }

    public void setPoc(Poc poc) {
        this.poc = poc;
    }

    public List<Parcel> getParcels() {
        return parcels;
    }

    public void setParcels(List<Parcel> parcels) {
        this.parcels = parcels;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

}
