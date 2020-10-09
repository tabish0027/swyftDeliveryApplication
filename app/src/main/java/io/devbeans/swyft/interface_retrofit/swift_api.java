package io.devbeans.swyft.interface_retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface swift_api {

    @POST("Riders/login?include=user")
    Call<Rider> getRiderFromLogin(@Body login credentials);


    //@PATCH("Riders/{riderId}")
    //Call<RiderActivity> getRiderFromLogin(@Header("Authorization") String Authorization,@Path("riderId") String riderId);

    @GET("Riders/{riderId}")
    Call<RiderDetails> getRider(@Header("Authorization") String Authorization,@Path("riderId") String riderId);

    @POST("Riders/logout")
    Call<String> logout(@Query("access_token") String accesstoken);

    @GET("app-version-check")
    Call<Void> getversioncontrol(@Header("Authorization") String Authorization, @Query("version") String version);

    @POST("Riders/mark-attendance")
    Call<RiderDetails> markattendance(@Header("Authorization") String Authorization,@Body markattendance status);



    @GET("Riders/get-tasks")
    Call<List<PickupParcel>> getParcelsByRiders(@Header("Authorization") String Authorization, @Query("riderId") String riderId);

    @POST("Parcels/{parcelid}/scan-parcel")
    Call<List<PickupParcel>> scanParcels(@Header("Authorization") String Authorization, @Path("parcelid") String parcelid,@Body parcel_scan user_task);

    @POST("RiderTasks/{tasklid}")
    Call<List<PickupParcel>> RiderTasksupdate(@Header("Authorization") String Authorization, @Path("tasklid") String tasklid);

    @POST("RiderTasks/{taskid}/manage-task")
    Call<List<PickupParcel>> manageTask(@Header("Authorization") String Authorization, @Path("taskid") String parcelid,@Body manage_task user_task);

    @POST("Riders/forgot-password")
    Call<PasswordResetRequest> requestotp(@Body username name);

    @POST("Riders/reset-password-via-otp")
    Call<PasswordResetRequest> reset_password_forget(@Body reset_password name);

}
