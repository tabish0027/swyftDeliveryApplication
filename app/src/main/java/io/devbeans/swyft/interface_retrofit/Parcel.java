
package io.devbeans.swyft.interface_retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Parcel {

    @SerializedName("parcelId")
    @Expose
    private String parcelId;


    @SerializedName("scannedOn")
    @Expose
    private String scannedOn;

    public String getParcelBatchId() {
        return parcelBatchId;
    }

    public void setParcelBatchId(String parcelBatchId) {
        this.parcelBatchId = parcelBatchId;
    }

    @SerializedName("scanned")
    @Expose
    private Boolean scanned;

    @SerializedName("parcelBatchId")
    @Expose
    private String parcelBatchId;


    public String getParcelId() {
        return parcelId;
    }

    public String getScannedOn() {
        return scannedOn;
    }

    public void setScannedOn(String scannedOn) {
        this.scannedOn = scannedOn;
    }

    public void setParcelId(String parcelId) {
        this.parcelId = parcelId;
    }

    public Boolean getScanned() {
        return scanned;
    }

    public void setScanned(Boolean scanned) {
        this.scanned = scanned;
    }

}
