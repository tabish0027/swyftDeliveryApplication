package io.devbeans.swyft.interface_retrofit_delivery;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class delivery_earnings_interval {

    @SerializedName("fuel")
    @Expose
    private Float fuel;
    @SerializedName("maintenance")
    @Expose
    private Float maintenance;
    @SerializedName("earnings")
    @Expose
    private Float earnings;

    public Float getFuel() {
        return fuel;
    }

    public void setFuel(Float fuel) {
        this.fuel = fuel;
    }

    public Float getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(Float maintenance) {
        this.maintenance = maintenance;
    }

    public Float getEarnings() {
        return earnings;
    }

    public void setEarnings(Float earnings) {
        this.earnings = earnings;
    }

}