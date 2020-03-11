package io.devbeans.swyft.interface_retrofit_delivery;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class delivery_earnings {

    @SerializedName("daily")
    @Expose
    private delivery_earnings_interval daily;
    @SerializedName("weekly")
    @Expose
    private delivery_earnings_interval weekly;
    @SerializedName("monthly")
    @Expose
    private delivery_earnings_interval monthly;

    public delivery_earnings_interval getDaily() {
        return daily;
    }

    public void setDaily(delivery_earnings_interval daily) {
        this.daily = daily;
    }

    public delivery_earnings_interval getWeekly() {
        return weekly;
    }

    public void setWeekly(delivery_earnings_interval weekly) {
        this.weekly = weekly;
    }

    public delivery_earnings_interval getMonthly() {
        return monthly;
    }

    public void setMonthly(delivery_earnings_interval monthly) {
        this.monthly = monthly;
    }

}