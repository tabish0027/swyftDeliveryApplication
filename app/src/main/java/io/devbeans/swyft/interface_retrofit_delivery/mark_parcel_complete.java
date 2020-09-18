package io.devbeans.swyft.interface_retrofit_delivery;

import java.util.List;

public class mark_parcel_complete {
    public List<String> parcelIds = null;
    public String status = "";
    public String taskId = "";

    public double lat = 0.0;
    public double lng = 0.0;
    public String reason = "";

    public String image = "";


    public String date = "";
    public String phase = "";
    public String receivedBy = "";
    public List<String> checkboxDataArray = null;
    public mark_parcel_complete(String image, List<String> parcelIds, String status, String taskId, double lat, double lng, String reason, String receivedBy) {
        this.parcelIds = parcelIds;
        this.status = status;
        this.taskId = taskId;
        this.lat = lat;
        this.lng = lng;
        this.reason = reason;
        this.receivedBy = receivedBy;
        this.image = image;

    }

    public mark_parcel_complete(List<String> parcelIds, String status, String taskId, double lat, double lng, String reason) {
        this.parcelIds = parcelIds;
        this.status = status;
        this.taskId = taskId;
        this.lat = lat;
        this.lng = lng;
        this.reason = reason;
    }

    public mark_parcel_complete(String image, List<String> parcelIds, String status, String taskId, double lat, double lng, String reason, String date, String phase, List<String> checkboxDataArray) {
        this.parcelIds = parcelIds;
        this.status = status;
        this.taskId = taskId;
        this.lat = lat;
        this.lng = lng;
        this.reason = reason;
        this.date = date;
        this.phase = phase;
        this.checkboxDataArray = checkboxDataArray;
        this.image = image;
    }
}
