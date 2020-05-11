package io.devbeans.swyft;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.devbeans.swyft.adapters.*;
import io.devbeans.swyft.data_models.*;
import io.devbeans.swyft.interface_retrofit.PickupParcel;
import io.devbeans.swyft.interface_retrofit.manage_task;
import io.devbeans.swyft.interface_retrofit.swift_api;
import io.devbeans.swyft.interface_retrofit_delivery.Datum;
import io.devbeans.swyft.interface_retrofit_delivery.RiderActivityDelivery;
import io.devbeans.swyft.interface_retrofit_delivery.mark_parcel_complete;
import io.devbeans.swyft.interface_retrofit_delivery.swift_api_delivery;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class activity_daily_order_status  extends Activity {


    public RecyclerView order_list_daily;
    public adapter_status_daily_packages ad_orders_daily;
    public SwipeRefreshLayout swipeToRefresh;
    ProgressBar progressBar = null;
    ConstraintLayout pendingorder;
    TextView tx_pending_title,tx_parcels_status_count,tx_empty_view;
    int pending_parcels_to_scan = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_order_status);
        final ImageView btn_back = findViewById(R.id.btn_back);
        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        progressBar = (ProgressBar)findViewById(R.id.url_loading_animation);
        progressBar.setVisibility(View.GONE);
        pendingorder = findViewById(R.id.pendingTask);
        tx_pending_title = findViewById(R.id.tx_pending_title);
        tx_parcels_status_count = findViewById(R.id.tx_parcels_status_count);
        tx_empty_view = findViewById(R.id.tx_empty_view);
        tx_pending_title.setText("Pending Parcels");


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad_orders_daily = null;
                activity_daily_order_status.this.finish();
            }
        });
        order_list_daily = findViewById(R.id.order_list_daily);




        //generate_test_Data();
        ad_orders_daily = new adapter_status_daily_packages(Databackbone.getinstance().ar_orders_daily, this);


        order_list_daily.setAdapter(ad_orders_daily);

        ad_orders_daily.setOnItemClickListener(new adapter_status_daily_packages.ClickListener() {
            @Override
            public void onItemClick(int position, View v,Boolean check) {
                RiderActivityDelivery delivery= Databackbone.getinstance().getDeliveryTask();
                if(delivery == null) {
                    activity_daily_order_status.this.finish();
                    return;
                }
                if(check) {
                    if (Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery"))
                    {


                        Databackbone.getinstance().delivery_to_show = delivery.getData().get(position).getParcels().get(0).getParcelId();

                        StartDeliveryorder(position);}
                    else
                        {   //startPicuporder(position);
                        }
                }else{
                    if (Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery"))
                    {
                        Databackbone.getinstance().delivery_to_show = delivery.getData().get(position).getParcels().get(0).getParcelId();



                        Intent orders = new Intent(activity_daily_order_status.this,activity_form.class);
                        orders.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        orders.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        activity_daily_order_status.this.startActivity(orders);
                    }

                }


            }

            @Override
            public void onItemLongClick(int position, View v,Boolean check) {

                Toast.makeText(activity_daily_order_status.this,"onItemLongClick position: " + position,Toast.LENGTH_LONG).show();
            }
        });

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery"))
                    LoadParcelsForDelivery();
                else


                LoadParcels();
            }
        });
        if(!Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery"))
        {
            pendingorder.setVisibility(View.GONE);
        }
        pendingorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Databackbone.getinstance().getDeliveryTask().getTaskStatus().equals("started"))
                {
                    Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"Error","Please Start the task before you scan parcels");

                    return ;
                }
                Intent pendingorder = new Intent(activity_daily_order_status.this, activity_barcode_scanner.class);
                pendingorder.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingorder.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity_daily_order_status.this.startActivity(pendingorder);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_Data();
        update_view();

    }

    public void startPicuporder(int position){
         if(Databackbone.getinstance().ar_orders_daily.get(position).m_remaining_parcels_to_scan ==0){
            completeorderConfirmation(Databackbone.getinstance().parcels.get(position).getName(),Databackbone.getinstance().parcels.get(position).getTaskId());

        }
        else if(Databackbone.getinstance().ar_orders_daily.get(position).status)
        {
            Databackbone.getinstance().pickup_to_process = position;
            Intent barcode_scanner = new Intent(activity_daily_order_status.this,activity_barcode_scanner.class);
            barcode_scanner.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            barcode_scanner.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity_daily_order_status.this.startActivity(barcode_scanner);

        }

        else{
            if(check_is_any_pickup_order_active()){
                Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"Error","The is already an active order");
                return ;

            }

            startorderConfirmation(Databackbone.getinstance().parcels.get(position).getName(),Databackbone.getinstance().parcels.get(position).getTaskId());
            //Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"Error","Task Not Active");

        }
    }
    public void StartDeliveryorder(int position){
        if(!Databackbone.getinstance().getDeliveryTask().getTaskStatus().equals("started"))
        {
            Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"Error","Please Start the task before you scan parcels");

            return ;
        }
        if(pending_parcels_to_scan != 0){
            Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"Error","Please Scan all parcels before you start");

            return;
        }
        //if(Databackbone.getinstance().ar_orders_daily.get(position).status)
//        if(check_is_any_delivery_order_active()){
//            Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"Error","Their is already an active order");
//            return ;
//
//        }
//        else {
            //startorderConfirmation(Databackbone.getinstance().parcelsdelivery.get(position).getData().get(0).getName(), Databackbone.getinstance().parcelsdelivery.get(position).getTaskId());
            markParcelsToComplete(position);

//        }
        /*


        else{
           // completeTaskConfirmation(Databackbone.getinstance().parcelsdelivery.get(position).getData().get(0).getName(),Databackbone.getinstance().parcelsdelivery.get(position).getTaskId());

            //Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"Error","Task Not Active");
        }*/
    }

    public void load_Data() {
        tx_empty_view.setVisibility(View.INVISIBLE);
        if(!Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery")) {
            Databackbone.getinstance().ar_orders_daily.clear();

            ArrayList<model_daily_package_item> temp_ar_orders_daily = new ArrayList<>();
            if (Databackbone.getinstance().parcels == null)
                return;
            Boolean activated_order = false;
            for (int i = 0; i < Databackbone.getinstance().parcels.size(); i++) {
                LatLng m_location = new LatLng(Databackbone.getinstance().parcels.get(i).getLocation().getGeoPoints().getLat(), Databackbone.getinstance().parcels.get(i).getLocation().getGeoPoints().getLng());

                if (!Databackbone.getinstance().parcels.get(i).getTaskStatus().equalsIgnoreCase("pending"))
                    activated_order = true;
                int size = 0;
                int total_parcel = Databackbone.getinstance().parcels.get(i).getParcels().size();
                for (int parcelscancount = 0; parcelscancount < total_parcel; parcelscancount++) {
                    if (!Databackbone.getinstance().parcels.get(i).getParcels().get(parcelscancount).getScanned())
                        size++;
                }
                double distance = CalculationByDistance(Databackbone.getinstance().parcels.get(i).getLocation().getGeoPoints().getLat(), Databackbone.getinstance().parcels.get(i).getLocation().getGeoPoints().getLng());
                model_daily_package_item dataModelValue = new model_daily_package_item(Databackbone.getinstance().parcels.get(i).getTaskId(), Databackbone.getinstance().parcels.get(i).getName(), Databackbone.getinstance().parcels.get(i).getLocation().getAddress(), Double.toString(Databackbone.getinstance().parcels.get(i).getDistance()) + "KM", Databackbone.getinstance().parcels.get(i).getLocation().getAddress(), activated_order, m_location, size);
                temp_ar_orders_daily.add(dataModelValue);
                activated_order = false;
            }
            if(temp_ar_orders_daily.size() == 0)
                tx_empty_view.setVisibility(View.VISIBLE);
            Databackbone.getinstance().ar_orders_daily.addAll(temp_ar_orders_daily);
        }else{
            Databackbone.getinstance().ar_orders_daily.clear();

            ArrayList<model_daily_package_item> temp_ar_orders_daily = new ArrayList<>();
            if (Databackbone.getinstance().parcelsdelivery == null)
                return;
            Boolean activated_order = false;
            if(Databackbone.getinstance().getDeliveryTask()== null) {
                activity_daily_order_status.this.finish();
                return;
            }
            //if(Databackbone.getinstance().task_to_show >= Databackbone.getinstance().parcelsdelivery.size()   )
            //    activity_daily_order_status.this.finish();
            List<Datum> Locations= Databackbone.getinstance().getDeliveryTask().getData();
            String orderid = Databackbone.getinstance().getDeliveryTask().getTaskId();
            pending_parcels_to_scan = 0 ;
            if(Locations.size() == 0)
                this.finish();
            for (int i = 0; i < Locations.size(); i++) {
                Datum data = Locations.get(i);
                LatLng m_location = new LatLng(data.getLocation().getGeoPoints().getLat(), data.getLocation().getGeoPoints().getLng());

                for (int j = 0; j < data.getParcels().size(); j++) {
                    if((data.getParcels().get(j).getStatus().equals("pending"))){
                        pending_parcels_to_scan = pending_parcels_to_scan + 1;
                    }
                    if (data.getParcels().get(j).getStatus().equalsIgnoreCase("started"))
                        activated_order = true;
                }

                int total_parcel = Locations.get(i).getParcels().size();

                double distance = CalculationByDistance( Locations.get(i).getLocation().getGeoPoints().getLat(),  Locations.get(i).getLocation().getGeoPoints().getLng());
                model_daily_package_item dataModelValue = new model_daily_package_item(orderid, data.getName(), data.getLocation().getAddress(), Double.toString(data.getDistance()) + "KM", data.getLocation().getAddress(), activated_order, m_location, total_parcel,data.getParcels());
                temp_ar_orders_daily.add(dataModelValue);
                activated_order = false;
            }
            Databackbone.getinstance().ar_orders_daily.addAll(temp_ar_orders_daily);
            if(temp_ar_orders_daily.size() == 0)
                tx_empty_view.setVisibility(View.VISIBLE);
            if(pending_parcels_to_scan <= 1)
            tx_parcels_status_count.setText(Integer.toString(pending_parcels_to_scan)+" Parcel left to Scan");
            else tx_parcels_status_count.setText(Integer.toString(pending_parcels_to_scan)+" Parcel left to Scan");


        }

        check_is_order_active_and_complete();

    }
    public void check_is_order_active_and_complete(){
        if(!Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery")) {
            for (int i = 0; i < Databackbone.getinstance().ar_orders_daily.size(); i++) {
                if(Databackbone.getinstance().ar_orders_daily.get(i).m_remaining_parcels_to_scan == 0 && Databackbone.getinstance().ar_orders_daily.get(i).status){
                    activate_order_activater(Databackbone.getinstance().ar_orders_daily.get(i).mb_task_id,"completed");
                    return;
                }
            }
        }else
        {


        }
    }
    public double CalculationByDistance(double Lat,double Lng) {

        if(Databackbone.getinstance().current_location == null)
            return -1;
        LatLng EndP = new LatLng(Lat,Lng);
        LatLng StartP = Databackbone.getinstance().current_location;
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        double finalDistance = 0.0;
        finalDistance = round(km, 1);
        if(finalDistance < 0)
            finalDistance = finalDistance * -1;
        return finalDistance;
    }
    public void LoadParcels(){
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api riderapi = retrofit.create(swift_api.class);
        EnableLoading();
        retrofit2.Call<List<PickupParcel>> call = riderapi.getParcelsByRiders(Databackbone.getinstance().rider.getId(),Databackbone.getinstance().rider.getUserId());
        call.enqueue(new Callback<List<PickupParcel>>() {
            @Override
            public void onResponse(retrofit2.Call<List<PickupParcel>> call, Response<List<PickupParcel>> response) {
                if(response.isSuccessful()){

                    List<PickupParcel> parcels = response.body();
                    // System.out.println(parcels.size());
                    Databackbone.getinstance().parcels = Databackbone.getinstance().resortParcelsPickup(parcels);

                    //Databackbone.getinstance().parcels = parcels;
                    load_Data();
                    update_view();
                    DisableLoading();

                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")){
                            Intent intent = new Intent(activity_daily_order_status.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
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
                Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"Error","Error Connecting To Server (Riders/get-tasks) "+t.getMessage());
                DisableLoading();
            }
        });


    }
    public void DisableLoading(){

        progressBar.setVisibility(View.GONE);
    }
    public void EnableLoading(){
        swipeToRefresh.setRefreshing(false);
        progressBar.setVisibility(View.VISIBLE);
    }
    public void update_view() {

        ad_orders_daily.update_list();

    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    public void startorderConfirmation(final String name , final String orderid){
        new AlertDialog.Builder(activity_daily_order_status.this)
                .setTitle("Notice")
                .setMessage("You are about to start order for " + name )

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery"))
                            activate_order_delivery_activater(orderid,"started");
                        else
                            activate_order_activater(orderid,"started");



                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void completeorderConfirmation(final String name , final String orderid){
        new AlertDialog.Builder(activity_daily_order_status.this)
                .setTitle("Notice")
                .setMessage("You have already scanned all the parcels" )

                .setPositiveButton("Completed", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery"))
                            activate_order_delivery_activater(orderid,"completed");
                        else
                            activate_order_activater(orderid,"completed");

                    }
                })
                .setNegativeButton("Issue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void activate_order_activater(String orderId,final String action){
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api riderapi = retrofit.create(swift_api.class);
        EnableLoading();
        Call<List<PickupParcel>> call = riderapi.manageTask(Databackbone.getinstance().rider.getId(),orderId,new manage_task(action,(float)0.0,BuildConfig.VERSION_NAME));
        call.enqueue(new Callback<List<PickupParcel>>() {
            @Override
            public void onResponse(Call<List<PickupParcel>> call, Response<List<PickupParcel>> response) {
                if (response.code() != 200) {
                    Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"Error","This app version is obsolete, Please Download the newer version");
                    DisableLoading();
                    return;
                }
                if(response.isSuccessful()){

                    List<PickupParcel> parcels = response.body();
                    // System.out.println(parcels.size());
                    Databackbone.getinstance().parcels = Databackbone.getinstance().resortParcelsPickup(parcels);

                    //Databackbone.getinstance().parcels = parcels;
                    load_Data();
                    update_view();
                    Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"confirmation","order "+action);
                    DisableLoading();

                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")){
                            Intent intent = new Intent(activity_daily_order_status.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        DisableLoading();
                    }
                }

            }

            @Override
            public void onFailure(Call<List<PickupParcel>> call, Throwable t) {
                Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"Error","Error Connecting To Server (RiderTasks/{taskid}/manage-task) "+t.getMessage());
                DisableLoading();
            }
        });


    }
/*
    public void activate_order_delivery_activater(String orderId,String Action){
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api riderapi = retrofit.create(swift_api.class);
        EnableLoading();
        Call<List<PickupParcel>> call = riderapi.manageTask(Databackbone.getinstance().rider.getId(),orderId,new manage_order(Action));
        call.enqueue(new Callback<List<PickupParcel>>() {
            @Override
            public void onResponse(Call<List<PickupParcel>> call, Response<List<PickupParcel>> response) {
                if(response.isSuccessful()){

                    List<PickupParcel> parcels = response.body();
                    // System.out.println(parcels.size());
                    Databackbone.getinstance().parcels = Databackbone.getinstance().resortParcelsPickup(parcels);

                    //Databackbone.getinstance().parcels = parcels;
                    load_Data();
                    update_view();
                    Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"confirmation","Task Completed");
                    DisableLoading();

                }
                else{
                    DisableLoading();
                }

            }

            @Override
            public void onFailure(Call<List<PickupParcel>> call, Throwable t) {
                DisableLoading();
            }
        });


    }
    */

    public void activate_order_delivery_activater(String orderId,final String action){
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api_delivery riderapi = retrofit.create(swift_api_delivery.class);
        EnableLoading();
        Call<List<RiderActivityDelivery>> call = riderapi.manageTask(Databackbone.getinstance().rider.getId(),orderId,new manage_task(action,(float)0.0,BuildConfig.VERSION_NAME));
        call.enqueue(new Callback<List<RiderActivityDelivery>>() {
            @Override
            public void onResponse(Call<List<RiderActivityDelivery>> call, Response<List<RiderActivityDelivery>> response) {
                if (response.code() != 200) {
                    Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"Error","This app version is obsolete, Please Download the newer version");
                    DisableLoading();
                    return;
                }
                if(response.isSuccessful()){

                    List<RiderActivityDelivery> parcels = response.body();
                    // System.out.println(parcels.size());
                    parcels = Databackbone.getinstance().resortDelivery(parcels);
                    Databackbone.getinstance().parcelsdelivery = parcels;
                    Databackbone.getinstance().remove_location_complete();
                    load_Data();
                    update_view();
                    DisableLoading();
                    Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"confirmation","order "+action);


                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")){
                            Intent intent = new Intent(activity_daily_order_status.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
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
                Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"Error","Error Connecting To Server (RiderTasks/{taskid}/manage-task) "+t.getMessage());

                DisableLoading();
            }
        });


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

                    DisableLoading();
                    load_Data();
                    update_view();
                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")){
                            Intent intent = new Intent(activity_daily_order_status.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
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
                Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"Error","Error Connecting To Server (Riders/get-tasks) "+t.getMessage());
                DisableLoading();
                load_Data();
            }
        });


    }
    public boolean check_is_any_delivery_order_active()
    {
        for(int i =0;i<Databackbone.getinstance().ar_orders_daily.size();i++)
        {
            if(Databackbone.getinstance().ar_orders_daily.get(i).status)
                return true;
        }
        return false;
    }
    public boolean check_is_any_pickup_order_active()
    {
        for(int i =0;i<Databackbone.getinstance().parcels.size();i++)
        {
            if(!Databackbone.getinstance().parcels.get(i).getTaskStatus().equalsIgnoreCase("pending"))
                return true;
        }
        return false;
    }
    public void markParcelsToComplete( int order_to_start){
        double lat = 0.0;
        double lng = 0.0;
        final List<String> parcelIds = getParcelsToComplete(order_to_start);
        String reason = "";
        String action = "started";
        String taskId = Databackbone.getinstance().getDeliveryTask().getTaskId();
        if(parcelIds.size() == 0)
        {
            Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this, "Error", "Server code error 102");
            DisableLoading();
            return;
        }

        if(Databackbone.getinstance().current_location != null){
            lat = Databackbone.getinstance().current_location.latitude;
            lng = Databackbone.getinstance().current_location.longitude;
        }
        mark_parcel_complete com_parcels = new mark_parcel_complete(parcelIds,action,taskId,lat,  lng, reason);

        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api_delivery riderapi = retrofit.create(swift_api_delivery.class);
        EnableLoading();
        Call<List<RiderActivityDelivery>> call = riderapi.markParcelComplete(Databackbone.getinstance().rider.getId(),com_parcels);
        call.enqueue(new Callback<List<RiderActivityDelivery>>() {
            @Override
            public void onResponse(Call<List<RiderActivityDelivery>> call, Response<List<RiderActivityDelivery>> response) {
                if(response.isSuccessful()){

                    List<RiderActivityDelivery> parcels = response.body();
                    parcels = Databackbone.getinstance().resortDelivery(parcels);
                    Databackbone.getinstance().parcelsdelivery = parcels;
                    Databackbone.getinstance().remove_location_complete();

                    DisableLoading();

                    //Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this, "Confirmation", "Order Started");
                    new AlertDialog.Builder(activity_daily_order_status.this)
                            .setTitle("confirmation")
                            .setMessage("Order Start")

                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent orders = new Intent(activity_daily_order_status.this,activity_form.class);
                                    orders.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    orders.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    activity_daily_order_status.this.startActivity(orders);
                                }
                            })

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    load_Data();
                    update_view();



                }
                else{
                    Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this, "Error", "Server code error 98");
                    DisableLoading();
                }

            }

            @Override
            public void onFailure(Call<List<RiderActivityDelivery>> call, Throwable t) {
                System.out.println(t.getCause());
                Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this, "Error", "Server code error 99");
                DisableLoading();
            }
        });

    }
    public List<String> getParcelsToComplete(int order){
        Datum data = Databackbone.getinstance().getDeliveryTask().getData().get(order);
        List<String> parcels_id = new ArrayList<String>();

        for (int j = 0; j < data.getParcels().size(); j++) {
                parcels_id.add(data.getParcels().get(j).getParcelId());
        }
        return parcels_id;
    }

}
