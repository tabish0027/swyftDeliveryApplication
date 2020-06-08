package io.devbeans.swyft;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.devbeans.swyft.interface_retrofit_delivery.RiderActivityDelivery;
import io.devbeans.swyft.interface_retrofit_delivery.mark_parcel_complete;
import io.devbeans.swyft.interface_retrofit_delivery.swift_api_delivery;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class activity_delivery_status extends AppCompatActivity {

    CheckBox cb_incomplete,cb_consignee,cb_refure,cb_funds;
    TextView tx_note ;
    ProgressBar progressBar = null;
    Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_note);
        cb_incomplete = findViewById(R.id.cb_incomplete);
        cb_consignee = findViewById(R.id.cb_consignee);
        cb_refure = findViewById(R.id.cb_refure);
        cb_funds = findViewById(R.id.cb_funds);
        tx_note = findViewById(R.id.tx_note);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markParcelsTonotcomplete();
            }
        });
        progressBar = (ProgressBar)findViewById(R.id.url_loading_animation);

        final ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_delivery_status.this.finish();
            }
        });
    }
    public void markParcelsTonotcomplete( ){
        btn_submit.setEnabled(false);
        double lat = 0.0;
        double lng = 0.0;
        final List<String> parcelIds = Databackbone.getinstance().parcel_to_process;
        String reason = "";

        if(cb_incomplete.isChecked())
            reason += "incomplete,";
        if(cb_consignee.isChecked())
            reason += "consignee,";
        if(cb_refure.isChecked())
            reason += "refuse to receive,";
        if(cb_funds.isChecked())
            reason += ",";

        reason += " REASON : ";
        reason += tx_note.getText().toString();


        String action = Databackbone.getinstance().not_delivered_reason;
        //String taskId = Databackbone.getinstance().parcelsdelivery.get(Databackbone.getinstance().task_to_show).getTaskId();
        String taskId = Databackbone.getinstance().getDeliveryTask().getTaskId();

        if(parcelIds.size() == 0)
        {
            Databackbone.getinstance().showAlsertBox(activity_delivery_status.this, "Error", "Server code error 102");
            DisableLoading();
            return;
        }

        if(Databackbone.getinstance().current_location != null){
            lat = Databackbone.getinstance().current_location.latitude;
            lng = Databackbone.getinstance().current_location.longitude;
        }
        String date = "19-11-2019";
        String phase = "Morning";
        List<String> checkbox = new ArrayList<>();
        checkbox.add("Not Enought Funds");
        mark_parcel_complete com_parcels = new mark_parcel_complete(parcelIds,action,taskId,lat,  lng, reason,date,phase,checkbox);

        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api_delivery riderapi = retrofit.create(swift_api_delivery.class);
        EnableLoading();
        Call<List<RiderActivityDelivery>> call = riderapi.markParcelComplete(Databackbone.getinstance().rider.getId(),com_parcels);
        call.enqueue(new Callback<List<RiderActivityDelivery>>() {
            @Override
            public void onResponse(Call<List<RiderActivityDelivery>> call, Response<List<RiderActivityDelivery>> response) {
                if(response.isSuccessful()){
                    btn_submit.setEnabled(true);

                    List<RiderActivityDelivery> parcels = response.body();
                    Databackbone.getinstance().parcelsdelivery = parcels;
                    parcels = Databackbone.getinstance().resortDelivery(parcels);
                    Databackbone.getinstance().remove_location_complete();

                    DisableLoading();
                    new AlertDialog.Builder(activity_delivery_status.this)
                            .setTitle("Not Delivered")
                            .setMessage("Confirmed")

                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    activity_delivery_status.this.finish();
                                }
                            })

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                }
                else{
                    btn_submit.setEnabled(true);
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")){
                            Intent intent = new Intent(activity_delivery_status.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_delivery_status.this,jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        DisableLoading();
                    }
                }

            }

            @Override
            public void onFailure(Call<List<RiderActivityDelivery>> call, Throwable t) {
                System.out.println(t.getCause());
                btn_submit.setEnabled(true);
                Databackbone.getinstance().showAlsertBox(activity_delivery_status.this,"Error","Error Connecting To Server (Parcels/manage-parcel) "+t.getMessage());
                DisableLoading();
            }
        });

    }

    public void DisableLoading(){

        progressBar.setVisibility(View.GONE);
    }
    public void EnableLoading(){

        progressBar.setVisibility(View.VISIBLE);
    }
}
