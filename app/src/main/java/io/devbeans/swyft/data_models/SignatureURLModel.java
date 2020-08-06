package io.devbeans.swyft.data_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignatureURLModel {

    @SerializedName("message")
    @Expose
    private String message = "";
    @SerializedName("url")
    @Expose
    private String url = "";

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
