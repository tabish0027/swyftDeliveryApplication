package io.devbeans.swyft.interface_retrofit_delivery;

import android.graphics.Bitmap;

public class patch_upload {
    Bitmap signature = null;
    String patchModel = "Parcel";
    String modelInstanceId ="";
    String uploadContainer ="parcels";
    String updateOn = "deliverySignature";

    public patch_upload(String modelInstanceId, Bitmap signature ) {
        this.signature = signature;
        this.modelInstanceId = modelInstanceId;
    }
}
