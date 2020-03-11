package io.devbeans.swyft.interface_retrofit_delivery;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class delivery_wallet {

    @SerializedName("amount")
    @Expose
    private Float amount;


    public Float getamount() {
        return amount;
    }

    public void setDaily(Float amount) {
        this.amount = amount;
    }



}