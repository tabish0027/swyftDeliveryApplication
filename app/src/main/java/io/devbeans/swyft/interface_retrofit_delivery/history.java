package io.devbeans.swyft.interface_retrofit_delivery;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class history {

    @SerializedName("parcelId")
    @Expose
    private String parcelId;
    @SerializedName("status")
    @Expose
    private String status;

    public String getParcelId() {
        return parcelId;
    }

    public void setParcelId(String parcelId) {
        this.parcelId = parcelId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}