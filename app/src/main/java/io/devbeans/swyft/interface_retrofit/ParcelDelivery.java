
package io.devbeans.swyft.interface_retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParcelDelivery {

    @SerializedName("parcelId")
    @Expose
    private String parcelId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("scannedOn")
    @Expose
    private String scannedOn;

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

    public String getScannedOn() {
        return scannedOn;
    }

    public void setScannedOn(String scannedOn) {
        this.scannedOn = scannedOn;
    }

}
