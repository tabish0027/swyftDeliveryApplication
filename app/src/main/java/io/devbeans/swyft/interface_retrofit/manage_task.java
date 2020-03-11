package io.devbeans.swyft.interface_retrofit;

public class manage_task {
    public String status;

    public String parcelId;
    public float distance;
    public String appVersion="";

    public manage_task(String status, String parcelId) {
        this.status = status;
        this.parcelId = parcelId;
    }
    public manage_task(String status,float distance) {
        this.status = status;
        this.distance = distance;
    }
    public manage_task(String status,float distance,String appVersion) {
        this.status = status;
        this.distance = distance;
        this.appVersion = appVersion;
    }
}
