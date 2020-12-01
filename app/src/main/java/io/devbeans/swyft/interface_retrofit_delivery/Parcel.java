
package io.devbeans.swyft.interface_retrofit_delivery;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Parcel {

    @SerializedName("parcelId")
    @Expose
    private String parcelId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("scannedOn")
    @Expose
    private String scannedOn;

    @SerializedName("amount")
    @Expose
    private Float amount;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("vendorId")
    @Expose
    private String vendorId;

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public Boolean getParcel_to_mark_complete() {
        return parcel_to_mark_complete;
    }

    public void setParcel_to_mark_complete(Boolean parcel_to_mark_complete) {
        this.parcel_to_mark_complete = parcel_to_mark_complete;
    }

    public Boolean parcel_to_mark_complete = false;

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @SerializedName("orderType")
    @Expose
    private String orderType;

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
