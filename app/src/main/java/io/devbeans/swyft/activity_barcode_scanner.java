package io.devbeans.swyft;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import io.devbeans.swyft.interface_retrofit.PickupParcel;
import io.devbeans.swyft.interface_retrofit.parcel_scan;

import java.util.List;

import io.devbeans.swyft.interface_retrofit.Parcel;
import io.devbeans.swyft.interface_retrofit.swift_api;
import io.devbeans.swyft.interface_retrofit_delivery.Datum;
import io.devbeans.swyft.interface_retrofit_delivery.RiderActivityDelivery;
import io.devbeans.swyft.interface_retrofit_delivery.parcel_scan_delivery;
import io.devbeans.swyft.interface_retrofit_delivery.swift_api_delivery;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class activity_barcode_scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    LinearLayout barcodescannerview;
    TextView tx_barcode ;
    ConstraintLayout layout_scanned_id,layout_add_parcel;
    ImageView btn_refreash,btn_add_parcel;
    ConstraintLayout barcode_remaining_parcels ;
    TextView tx_parcels_to_scan;
    ProgressBar progressBar = null;
    int pending_parcels_to_scan = 0;
    EditText edt_parcel_id;
    Button btn_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        barcodescannerview =  findViewById(R.id.barcodescannerview);
        tx_barcode = findViewById(R.id.tx_barcode);
        mScannerView = new ZXingScannerView(this);
        btn_refreash = findViewById(R.id.btn_refreash);
        tx_parcels_to_scan = findViewById(R.id.tx_parcels_to_scan);
        btn_add_parcel = findViewById(R.id.btn_add_parcel);
        layout_add_parcel = findViewById(R.id.layout_add_parcel);
        progressBar = (ProgressBar)findViewById(R.id.url_loading_animation);
        progressBar.setVisibility(View.INVISIBLE);
        edt_parcel_id = findViewById(R.id.edt_parcel_id);
        btn_add = findViewById(R.id.btn_add);

        final ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_barcode_scanner.this.finish();
            }
        });
        barcode_remaining_parcels = findViewById(R.id.barcode_remaining_parcels);
        barcode_remaining_parcels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showParcelList = new Intent(activity_barcode_scanner.this,activity_order_status_scanning.class);
                activity_barcode_scanner.this.startActivity(showParcelList);
                overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_parcel_id.getText().length()!=0){
                    String Scannedbarcode = edt_parcel_id.getText().toString();
                    mScannerView.stopCameraPreview();
                    EnableLoading();
                    check_parcel_to_scan(Scannedbarcode);
                }
            }
        });
        btn_refreash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreashScanner();
            }
        });
        //View child1 = LayoutInflater.from(this).inflate(mScannerView, null);
        layout_scanned_id = findViewById(R.id.constraintLayout5);
        layout_scanned_id.setVisibility(View.INVISIBLE);
        barcodescannerview.addView(mScannerView);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 2);
        load_parcels_to_scan();
        btn_add_parcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_add_parcel.setVisibility(View.VISIBLE);
                layout_scanned_id.setVisibility(View.INVISIBLE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 2){
            mScannerView = new ZXingScannerView(this);
            barcodescannerview.addView(mScannerView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(com.google.zxing.Result rawResult) {

        String Scannedbarcode = rawResult.getText();
        mScannerView.stopCameraPreview();
        EnableLoading();
        check_parcel_to_scan(Scannedbarcode);
       // mScannerView.resumeCameraPreview(this);


    }
    public void refreashScanner(){
        layout_add_parcel.setVisibility(View.GONE);
        layout_add_parcel.setVisibility(View.VISIBLE);

        mScannerView.resumeCameraPreview(activity_barcode_scanner.this);
        layout_scanned_id.setVisibility(View.INVISIBLE);
        layout_add_parcel.setVisibility(View.GONE);
    }
    public void scanparceldone(String id){
        tx_barcode.setText(id);
        layout_scanned_id.setVisibility(View.VISIBLE);
        layout_add_parcel.setVisibility(View.GONE);
        this.mScannerView.playSoundEffect(SoundEffectConstants.CLICK);

        Vibrator v = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        int resID=getResources().getIdentifier("tick", "raw", getPackageName());

        MediaPlayer mediaPlayer=MediaPlayer.create(this,resID);
        mediaPlayer.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {

            v.vibrate(500);
        }
       refreahScanner();
    }
    public void load_parcels_to_scan(){


        if(Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery")){

            //if(Databackbone.getinstance().task_to_show >= Databackbone.getinstance().parcelsdelivery.size()   )
             //   activity_barcode_scanner.this.finish();
            RiderActivityDelivery riderActivityDelivery = Databackbone.getinstance().getDeliveryTask();
            if(riderActivityDelivery == null)
                activity_barcode_scanner.this.finish();
            List<Datum> Locations = riderActivityDelivery.getData();
            //List<Datum> Locations= Databackbone.getinstance().parcelsdelivery.get(Databackbone.getinstance().task_to_show).getData();
            pending_parcels_to_scan = 0 ;
            for (int i = 0; i < Locations.size(); i++) {
                Datum data = Locations.get(i);

                for (int j = 0; j < data.getParcels().size(); j++) {
                    if((data.getParcels().get(j).getStatus().equals("pending"))){
                        pending_parcels_to_scan = pending_parcels_to_scan + 1;
                    }
                }
            }
            if(pending_parcels_to_scan <= 1)
                tx_parcels_to_scan.setText(Integer.toString(pending_parcels_to_scan)+" Parcel left to Scan");
            else tx_parcels_to_scan.setText(Integer.toString(pending_parcels_to_scan)+" Parcel left to Scan");

        }else{
            PickupParcel pickupparcels=   Databackbone.getinstance().getParcelsForPickup();
            if(pickupparcels == null)
                activity_barcode_scanner.this.finish();
            List<Parcel> parcels=   pickupparcels.getParcels();

            pending_parcels_to_scan = 0 ;
            for(int i =0 ; i < parcels.size();i++){
                if(!parcels.get(i).getScanned()){
                    pending_parcels_to_scan++;
                }
            }

            if(pending_parcels_to_scan <= 1)
                tx_parcels_to_scan.setText(Integer.toString(pending_parcels_to_scan)+" Parcel left to Scan");
            else tx_parcels_to_scan.setText(Integer.toString(pending_parcels_to_scan)+" Parcel left to Scan");

        }



    }
    public void check_parcel_to_scan(String id){
       if(Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery"))
       {
           Boolean check = false;
           //if(Databackbone.getinstance().task_to_show >= Databackbone.getinstance().parcelsdelivery.size()   )
           //    activity_barcode_scanner.this.finish();
           RiderActivityDelivery riderActivityDelivery = Databackbone.getinstance().getDeliveryTask();
           if(riderActivityDelivery == null)
               activity_barcode_scanner.this.finish();


           List<Datum> Locations= riderActivityDelivery.getData();

           for (int i = 0; i < Locations.size(); i++) {
               Datum data = Locations.get(i);

               for (int j = 0; j < data.getParcels().size(); j++) {
                   if(data.getParcels().get(j).getParcelId().equals(id) ){
                       if(!data.getParcels().get(j).getStatus().equals("pending")){
                           Databackbone.getinstance().showAlsertBox(this, "Error", "Parcel Already Scanned");
                           DisableLoading();
                           refreahScanner();
                           return;
                       }
                       check = true;
                       break;

                   }
               }
               if(check)break;
           }
           if(!check){
               Databackbone.getinstance().showAlsertBox(this, "Error", "Parcel not found");
               DisableLoading();
               refreahScanner();
           }else{
               send_request_to_server_for_delivery(id);
           }
       }
       else {
           PickupParcel parcelspickup = Databackbone.getinstance().getParcelsForPickup();
           if(parcelspickup == null)
               activity_barcode_scanner.this.finish();
           if(!parcelspickup.getTaskStatus().equals("started")){
               Databackbone.getinstance().showAlsertBox(this, "Error", "Task Not Active");

               DisableLoading();
               refreahScanner();
               return;
           }

           Boolean check = false;
           List<Parcel> parcels = parcelspickup.getParcels();
           for (int i = 0; i < parcels.size(); i++) {
               if (parcels.get(i).getParcelId().equals(id)) {
                   check = true;
                   break;
               }
           }
           if (!check) {
               Databackbone.getinstance().showAlsertBox(this, "Error", "Parcel not found");

               DisableLoading();
               refreahScanner();
           } else {
               send_request_to_server(id);
           }
       }

    }
    public void send_request_to_server(final String id){

        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api riderapi = retrofit.create(swift_api.class);

        Call<List<PickupParcel>> call = riderapi.scanParcels(Databackbone.getinstance().rider.getId(),(id),new parcel_scan(Databackbone.getinstance().getParcelsForPickup().getTaskId(),Databackbone.getinstance().rider.getUserId()));
        call.enqueue(new Callback<List<PickupParcel>>() {
            @Override
            public void onResponse(Call<List<PickupParcel>> call, Response<List<PickupParcel>> response) {
                if(response.isSuccessful()){
                    List<PickupParcel> parcels = response.body();

                   // Databackbone.getinstance().parcels = parcels;
                    Databackbone.getinstance().parcels = Databackbone.getinstance().resortParcelsPickup(parcels);

                    Scan_successfull(id);


                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")){
                            Intent intent = new Intent(activity_barcode_scanner.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_barcode_scanner.this,jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        DisableLoading();
                    }

                }

            }

            @Override
            public void onFailure(Call<List<PickupParcel>> call, Throwable t) {
                System.out.println(t.getCause());
                DisableLoading();
                Databackbone.getinstance().showAlsertBox(activity_barcode_scanner.this,"Error","Error Connecting To Server (Parcels/{parcelid}/scan-parcel) "+t.getMessage());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(3000);
                            runOnUiThread (new Thread(new Runnable() {
                                public void run() {
                                    refreashScanner();
                                }
                            }));


                        }catch (Exception i){

                        }
                    }
                }).start();

            }
        });

    }
    public void send_request_to_server_for_delivery(final String id){

        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api_delivery riderapi = retrofit.create(swift_api_delivery.class);

        Call<List<RiderActivityDelivery>> call = riderapi.scan_parcels_delivery(Databackbone.getinstance().rider.getId(),new parcel_scan_delivery(Databackbone.getinstance().getDeliveryTask().getTaskId(),id));
        call.enqueue(new Callback<List<RiderActivityDelivery>>() {
            @Override
            public void onResponse(Call<List<RiderActivityDelivery>> call, Response<List<RiderActivityDelivery>> response) {
                if(response.isSuccessful()){
                    List<RiderActivityDelivery> parcels = response.body();
                    parcels = Databackbone.getinstance().resortDelivery(parcels);
                    Databackbone.getinstance().parcelsdelivery = parcels;
                    Databackbone.getinstance().remove_location_complete();
                    Scan_successfull_delivery(id);


                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")){
                            Intent intent = new Intent(activity_barcode_scanner.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_barcode_scanner.this,jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
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
                DisableLoading();
                Databackbone.getinstance().showAlsertBox(activity_barcode_scanner.this,"Error","Error Connecting To Server (Parcels/scan-delivery-parcel) "+t.getMessage());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(3000);
                            runOnUiThread (new Thread(new Runnable() {
                                public void run() {
                                    refreashScanner();
                                }
                            }));


                        }catch (Exception i){

                        }
                    }
                }).start();

            }
        });

    }
    public void Scan_successfull_delivery(String id){
        scanparceldone(id);
        LoadParcelsForDelivery();

    }
    public void Scan_successfull(String id){
        scanparceldone(id);
        load_parcels_to_scan();
        DisableLoading();
    }
    public void DisableLoading(){
        btn_add_parcel.setEnabled(true);
        btn_refreash.setEnabled(true);
        barcode_remaining_parcels.setClickable(true);

        progressBar.setVisibility(View.GONE);
    }
    public void EnableLoading(){

        btn_add_parcel.setEnabled(false);
        btn_refreash.setEnabled(false);
        barcode_remaining_parcels.setClickable(false);
        progressBar.setVisibility(View.VISIBLE);
    }
    public void LoadParcelsForDelivery(){
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api_delivery riderapidata = retrofit.create(swift_api_delivery.class);
        EnableLoading();
        Call<List<RiderActivityDelivery>> call = riderapidata.manageTaskfordelivery(Databackbone.getinstance().rider.getId(),(Databackbone.getinstance().rider.getUserId()));
        call.enqueue(new Callback<List<RiderActivityDelivery>>() {
            @Override
            public void onResponse(Call<List<RiderActivityDelivery>> call, Response<List<RiderActivityDelivery>> response) {
                if(response.isSuccessful()){

                    List<RiderActivityDelivery> parcels = response.body();
                    // System.out.println(parcels.size());
                    parcels = Databackbone.getinstance().resortDelivery(parcels);
                    Databackbone.getinstance().parcelsdelivery = parcels;
                    Databackbone.getinstance().remove_location_complete();
                    load_parcels_to_scan();

                    DisableLoading();

                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")){
                            Intent intent = new Intent(activity_barcode_scanner.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_barcode_scanner.this,jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
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
                Databackbone.getinstance().showAlsertBox(activity_barcode_scanner.this,"Error","Error Connecting To Server (Parcels/scan-delivery-parcel) "+t.getMessage());
                DisableLoading();

            }
        });


    }
    public void AllParcelScanned(){
        if(pending_parcels_to_scan == 0)
            //return ;
        new AlertDialog.Builder(activity_barcode_scanner.this)
                .setTitle("confirmation")
                .setMessage("All Parcels Scanned ")

                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activity_barcode_scanner.this.finish();
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void refreahScanner(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(3000);
                    runOnUiThread (new Thread(new Runnable() {
                        public void run() {
                            refreashScanner();
                            AllParcelScanned();
                        }
                    }));


                }catch (Exception i){

                }
            }
        }).start();
    }
}