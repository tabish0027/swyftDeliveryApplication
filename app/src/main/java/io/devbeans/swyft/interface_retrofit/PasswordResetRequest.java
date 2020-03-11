package io.devbeans.swyft.interface_retrofit;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PasswordResetRequest {

    @SerializedName("message")
    @Expose
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}