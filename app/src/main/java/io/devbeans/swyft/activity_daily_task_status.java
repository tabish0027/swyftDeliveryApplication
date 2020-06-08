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

import java.util.ArrayList;
import java.util.List;

import io.devbeans.swyft.adapters.*;
import io.devbeans.swyft.data_models.model_daily_package_item;
import io.devbeans.swyft.interface_retrofit.PickupParcel;
import io.devbeans.swyft.interface_retrofit.manage_task;
import io.devbeans.swyft.interface_retrofit.swift_api;
import io.devbeans.swyft.interface_retrofit_delivery.RiderActivityDelivery;
import io.devbeans.swyft.interface_retrofit_delivery.swift_api_delivery;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class activity_daily_task_status extends Activity {


    public RecyclerView task_list;
    public adapter_status_daily_task ad_task;
    public SwipeRefreshLayout swipeToRefresh;
    ProgressBar progressBar = null;
    ConstraintLayout pendingTask;
    private Call<List<PickupParcel>> call;
    TextView tx_empty_view;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_task_status);
        final ImageView btn_back = findViewById(R.id.btn_back);
        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        progressBar = (ProgressBar)findViewById(R.id.url_loading_animation);
        progressBar.setVisibility(View.GONE);
        pendingTask = findViewById(R.id.pendingTask);
        pendingTask.setVisibility(View.GONE);
        tx_empty_view= findViewById(R.id.tx_empty_view);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_daily_task_status.this.finish();
            }
        });
        task_list = findViewById(R.id.task_list_daily);




        //generate_test_Data();
        if(Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery"))
        {
            ad_task = new adapter_status_daily_task(Databackbone.getinstance().ar_task_daily_delivery, this);
        }
        else{
            ad_task = new adapter_status_daily_task(Databackbone.getinstance().ar_task_daily_pickup, this);

        }


        task_list.setAdapter(ad_task);

        ad_task.setOnItemClickListener(new adapter_status_daily_task.ClickListener() {
            @Override
            public void onItemClick(int position, View v,Boolean check) {
                if(check) {
                    if (Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery")) {
                        Databackbone.getinstance().task_to_show = Databackbone.getinstance().parcelsdelivery.get(position).getTaskId();
                        StartDeliveryTask(position);
                    }
                    else {
                        Databackbone.getinstance().task_to_show = Databackbone.getinstance().parcels.get(position).getTaskId();

                        //Databackbone.getinstance().task_to_show = Databackbone.getinstance()position;

                        startPicupTask(position);
                    }
                }else{
                    if (Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery"))
                    {
                        Databackbone.getinstance().task_to_show = Databackbone.getinstance().parcelsdelivery.get(position).getTaskId();

                        //Databackbone.getinstance().task_to_show = position;
                        Intent orders = new Intent(activity_daily_task_status.this,activity_daily_order_status.class);
                        orders.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        orders.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        activity_daily_task_status.this.startActivity(orders);
                    }
                    else
                    {
                        Databackbone.getinstance().task_to_show = Databackbone.getinstance().parcels.get(position).getTaskId();

                        Intent orders = new Intent(activity_daily_task_status.this,activity_barcode_scanner.class);
                        orders.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        orders.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        activity_daily_task_status.this.startActivity(orders);
                    }
                }

            }

            @Override
            public void onItemLongClick(int position, View v,Boolean check) {

                Toast.makeText(activity_daily_task_status.this,"onItemLongClick position: " + position,Toast.LENGTH_LONG).show();
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

        // check for null values exception generted ////

        /*
        pendingTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pendingtask = new Intent(activity_daily_task_status.this, activity_barcode_scanner.class);
                pendingtask.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingtask.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity_daily_task_status.this.startActivity(pendingtask);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_Data();
        update_view();

    }

    public void startPicupTask(int position){
//Toast.makeText(activity_daily_order_status.this,"onItemClick position: " + position,Toast.LENGTH_LONG).show();
        if(Databackbone.getinstance().ar_task_daily_pickup.get(position).m_remaining_parcels_to_scan == 0){
            completeTaskConfirmation(Databackbone.getinstance().parcels.get(position).getTaskId());

        }
        /*
        else if(Databackbone.getinstance().ar_task_daily_delivery.get(position).status)
        {
            Databackbone.getinstance().pickup_to_process = position;
            Intent barcode_scanner = new Intent(activity_daily_task_status.this,activity_barcode_scanner.class);
            barcode_scanner.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            barcode_scanner.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity_daily_task_status.this.startActivity(barcode_scanner);

        }
        */
        else{
            if(check_is_any_pickup_task_active()){
                Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,"Error","The is already an active task");
                return ;

            }
            startTaskConfirmation(Databackbone.getinstance().parcels.get(position).getName(),Databackbone.getinstance().parcels.get(position).getTaskId());

        }
    }
    public void StartDeliveryTask(int position){

            if(Databackbone.getinstance().parcelsdelivery.get(position).getData().size() == 0) {
                completeTaskConfirmation(Databackbone.getinstance().parcelsdelivery.get(position).getTaskId());
            }
//            else if(check_is_any_delivery_task_active()){
//                Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,"Error","Only one task can be activated at a time");
//                return;
//            }
          /*if( Databackbone.getinstance().ar_task_daily_delivery.get(position).m_remaining_parcels_to_scan ==0 &&!Databackbone.getinstance().ar_task_daily_delivery.get(position).status){

                Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,"Error","The is already an active task");
                return ;
            } */
          else {
              startTaskConfirmation(Databackbone.getinstance().parcelsdelivery.get(position).getData().get(0).getName(), Databackbone.getinstance().parcelsdelivery.get(position).getTaskId());
          }
        /*
        else{
           // completeTaskConfirmation(Databackbone.getinstance().parcelsdelivery.get(position).getData().get(0).getName(),Databackbone.getinstance().parcelsdelivery.get(position).getTaskId());
            //Databackbone.getinstance().showAlsertBox(activity_daily_order_status.this,"Error","Task Not Active");
        }*/
    }

    public void load_Data() {
        tx_empty_view.setVisibility(View.INVISIBLE);
        if(!Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery")) {
            Databackbone.getinstance().ar_task_daily_pickup.clear();

            ArrayList<model_daily_package_item> ar_task_daily_pickup = new ArrayList<>();
            if (Databackbone.getinstance().parcels == null)
                return;
            Boolean activated_task = false;
            for (int i = 0; i < Databackbone.getinstance().parcels.size(); i++) {
                LatLng m_location = new LatLng(Databackbone.getinstance().parcels.get(i).getLocation().getGeoPoints().getLat(), Databackbone.getinstance().parcels.get(i).getLocation().getGeoPoints().getLng());

                if (!Databackbone.getinstance().parcels.get(i).getTaskStatus().equalsIgnoreCase("pending"))
                    activated_task = true;
                int size = 0;
                int total_parcel = Databackbone.getinstance().parcels.get(i).getParcels().size();
                for (int parcelscancount = 0; parcelscancount < total_parcel; parcelscancount++) {
                    if (!Databackbone.getinstance().parcels.get(i).getParcels().get(parcelscancount).getScanned())
                        size++;
                }


                double distance = Databackbone.getinstance().CalculationByDistance(Databackbone.getinstance().parcels.get(i).getLocation().getGeoPoints().getLat(), Databackbone.getinstance().parcels.get(i).getLocation().getGeoPoints().getLng());
                model_daily_package_item dataModelValue = new model_daily_package_item(Databackbone.getinstance().parcels.get(i).getTaskId(), Databackbone.getinstance().parcels.get(i).getName(), Databackbone.getinstance().parcels.get(i).getLocation().getAddress(), Double.toString(Databackbone.getinstance().parcels.get(i).getDistance()) + "KM", Databackbone.getinstance().parcels.get(i).getLocation().getAddress(), activated_task, m_location, size);
                ar_task_daily_pickup.add(dataModelValue);
                activated_task = false;
            }
            if(ar_task_daily_pickup.size() == 0)
                tx_empty_view.setVisibility(View.VISIBLE);
            Databackbone.getinstance().ar_task_daily_pickup.addAll(ar_task_daily_pickup);
        }else{
            Databackbone.getinstance().ar_task_daily_delivery.clear();

            ArrayList<model_daily_package_item> temp_ar_task_daily_delivery = new ArrayList<>();
            if (Databackbone.getinstance().parcelsdelivery == null)
                return;
            Boolean activated_task = false;
            for (int i = 0; i < Databackbone.getinstance().parcelsdelivery.size(); i++) {
                //LatLng m_location = new LatLng(Databackbone.getinstance().parcelsdelivery.get(i).getData().get(0).getLocation().getGeoPoints().getLat(), Databackbone.getinstance().parcelsdelivery.get(i).getData().get(0).getLocation().getGeoPoints().getLng());

                if (!Databackbone.getinstance().parcelsdelivery.get(i).getTaskStatus().equalsIgnoreCase("pending"))
                    activated_task = true;
                int Total_parcels = 0;
                int Total_parcels_remaining_to_scan = 0;
                for (int parcelscancount = 0; parcelscancount < Databackbone.getinstance().parcelsdelivery.get(i).getData().size(); parcelscancount++) {
                    Total_parcels += Databackbone.getinstance().parcelsdelivery.get(i).getData().get(parcelscancount).getParcels().size();

                }
                //int total_parcel = Databackbone.getinstance().parcelsdelivery.get(i).getData().size();

                //double distance = CalculationByDistance(Databackbone.getinstance().parcelsdelivery.get(i).getData().get(0).getLocation().getGeoPoints().getLat(), Databackbone.getinstance().parcelsdelivery.get(i).getData().get(0).getLocation().getGeoPoints().getLng());
                //model_daily_package_item dataModelValue = new model_daily_package_item(Databackbone.getinstance().parcelsdelivery.get(i).getTaskId(), Databackbone.getinstance().parcelsdelivery.get(i).getData().get(0).getName(), Databackbone.getinstance().parcelsdelivery.get(0).getData().get(0).getLocation().getAddress(), Double.toString(distance) + "KM", Databackbone.getinstance().parcelsdelivery.get(0).getData().get(0).getLocation().getAddress(), activated_task, m_location, size);

                String mb_task_id = Databackbone.getinstance().parcelsdelivery.get(i).getTaskId();
                String mb_name =Databackbone.getinstance().parcelsdelivery.get(i).getTaskId();
                String mb_address ="Status : " + Databackbone.getinstance().parcelsdelivery.get(i).getTaskStatus();
                String mb_distance = Integer.toString(Total_parcels);
                String mb_zone = "Parcel";
                int location_left = Databackbone.getinstance().parcelsdelivery.get(i).getData().size();
                if(Total_parcels > 1)
                    mb_zone += "s";
                Boolean status = activated_task;
                LatLng location = null;

                int remaining_parcels_to_scan = Total_parcels_remaining_to_scan;
                model_daily_package_item dataModelValue = new model_daily_package_item(mb_task_id,mb_name,mb_address,mb_distance,mb_zone,status,location,remaining_parcels_to_scan,location_left);

                temp_ar_task_daily_delivery.add(dataModelValue);
                activated_task = false;
            }

            if(temp_ar_task_daily_delivery.size() == 0)
                tx_empty_view.setVisibility(View.VISIBLE);
            Databackbone.getinstance().ar_task_daily_delivery.addAll(temp_ar_task_daily_delivery);
        }
        check_is_task_active_and_complete();

    }
    public void check_is_task_active_and_complete(){
        if(!Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery")) {
            for (int i = 0; i < Databackbone.getinstance().ar_task_daily_pickup.size(); i++) {
                if(Databackbone.getinstance().ar_task_daily_pickup.get(i).m_remaining_parcels_to_scan  == 0 && Databackbone.getinstance().ar_task_daily_pickup.get(i).status){
                    activate_Task_activater(Databackbone.getinstance().ar_task_daily_pickup.get(i).mb_task_id,"completed");
                    return;
                }
            }
        }else
        {
            for (int i = 0; i < Databackbone.getinstance().ar_task_daily_delivery.size(); i++) {
                if(Databackbone.getinstance().ar_task_daily_delivery.get(i).m_remaining_parcels_Addresses  == 0 && Databackbone.getinstance().ar_task_daily_delivery.get(i).status){
                    activate_Task_delivery_activater(Databackbone.getinstance().ar_task_daily_delivery.get(i).mb_task_id,"completed");
                    return;
                }
            }

        }
    }

    public void LoadParcels(){
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api riderapi = retrofit.create(swift_api.class);
        EnableLoading();
        Call<List<PickupParcel>> call = riderapi.getParcelsByRiders(Databackbone.getinstance().rider.getId(),Databackbone.getinstance().rider.getUserId());
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
                    DisableLoading();

                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")){
                            Intent intent = new Intent(activity_daily_task_status.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
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
                Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,"Error","Error Connecting To Server (Riders/get-tasks) "+t.getMessage());

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

        ad_task.update_list();

    }

    public void startTaskConfirmation(final String name , final String taskid){
        new AlertDialog.Builder(activity_daily_task_status.this)
                .setTitle("Notice")
                .setMessage("You are about to start Task for " + name )

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery"))
                            activate_Task_delivery_activater(taskid,"started");
                        else
                            activate_Task_activater(taskid,"started");



                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void completeTaskConfirmation(final String taskid){
        new AlertDialog.Builder(activity_daily_task_status.this)
                .setTitle("Notice")
                .setMessage("You are about to mark this task complete" )

                .setPositiveButton("Completed", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery"))
                            activate_Task_delivery_activater(taskid,"completed");
                        else
                            activate_Task_activater(taskid,"completed");

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void activate_Task_activater(final String taskId,final String action){
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api riderapi = retrofit.create(swift_api.class);
        EnableLoading();
        double distance = Databackbone.getinstance().getfinalcouvereddistance (taskId,activity_daily_task_status.this);

        Call<List<PickupParcel>> call = riderapi.manageTask(Databackbone.getinstance().rider.getId(),taskId,new manage_task(action,(int)distance,BuildConfig.VERSION_NAME));
        call.enqueue(new Callback<List<PickupParcel>>() {
            @Override
            public void onResponse(Call<List<PickupParcel>> call, Response<List<PickupParcel>> response) {
                if (response.code() != 200) {
                    Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,"Error","This app version is obsolete, Please Download the newer version");
                    DisableLoading();
                    return;
                }
                if(response.isSuccessful()){

                    List<PickupParcel> parcels = response.body();
                    // System.out.println(parcels.size());
                    Databackbone.getinstance().taskStart (taskId,Databackbone.getinstance().current_location.latitude,Databackbone.getinstance().current_location.longitude,0.0,activity_daily_task_status.this);
                    Databackbone.getinstance().parcels = Databackbone.getinstance().resortParcelsPickup(parcels);

                    //Databackbone.getinstance().parcels = parcels;
                    load_Data();
                    update_view();
                   DisableLoading();
                    if(action.equals("started")){
                        new AlertDialog.Builder(activity_daily_task_status.this)
                                .setTitle("confirmation")
                                .setMessage("Task "+action)

                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                      if(Databackbone.getinstance().RiderTypeDelivery) {
                                          Intent orders = new Intent(activity_daily_task_status.this, activity_daily_order_status.class);
                                          orders.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                          orders.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                          activity_daily_task_status.this.startActivity(orders);
                                      }

                                    }
                                })

                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }
                    else{
                        Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,"confirmation","Task "+action);
                        Databackbone.getinstance().SaveData("swift_work_progress","none",getApplicationContext());

                    }

                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")){
                            Intent intent = new Intent(activity_daily_task_status.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        DisableLoading();
                    }
                }

            }

            @Override
            public void onFailure(Call<List<PickupParcel>> call, Throwable t) {
                Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,"Error","Error Connecting To Server (RiderTasks/{taskid}/manage-task) "+t.getMessage());

                DisableLoading();
            }
        });


    }


    public void activate_Task_delivery_activater(final String taskId, final String action){
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api_delivery riderapi = retrofit.create(swift_api_delivery.class);
        EnableLoading();
        double distance = Databackbone.getinstance().getfinalcouvereddistance (taskId,activity_daily_task_status.this);
        if(distance == 0)distance = 1.0; // as the server dont accept distance that is 0
        Call<List<RiderActivityDelivery>> call = riderapi.manageTask(Databackbone.getinstance().rider.getId(),taskId,new manage_task(action,(int)distance,BuildConfig.VERSION_NAME));
        call.enqueue(new Callback<List<RiderActivityDelivery>>() {
            @Override
            public void onResponse(Call<List<RiderActivityDelivery>> call, Response<List<RiderActivityDelivery>> response) {
                if (response.code() != 200 && response.code() != 400) {
                    Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,"Error","This app version is obsolete, Please Download the newer version");
                    DisableLoading();
                    return;
                }
//                else if (response.code() == 400) {
//                    Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,"Error","Please submit the collected amount to swyft office before you mark this task complete");
//                    DisableLoading();
//                    return;
//                }
                if(response.isSuccessful()){

                    List<RiderActivityDelivery> parcels = response.body();
                    // System.out.println(parcels.size());
                    parcels = Databackbone.getinstance().resortDelivery(parcels);
                    Databackbone.getinstance().parcelsdelivery = parcels;

                    Databackbone.getinstance().remove_location_complete();
                    Databackbone.getinstance().taskStart (taskId,Databackbone.getinstance().current_location.latitude,Databackbone.getinstance().current_location.longitude,0.0,activity_daily_task_status.this);
                    load_Data();
                    update_view();
                    DisableLoading();
                    if(action.equals("started"))
                    {
                        new AlertDialog.Builder(activity_daily_task_status.this)
                                .setTitle("confirmation")
                                .setMessage("Task "+action)

                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent orders = new Intent(activity_daily_task_status.this,activity_daily_order_status.class);
                                        orders.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        orders.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        activity_daily_task_status.this.startActivity(orders);
                                    }
                                })

                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }else{
                        Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,"confirmation","Task "+action);
                        Databackbone.getinstance().SaveData("swift_work_progress","none",getApplicationContext());

                    }

                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")){
                            Intent intent = new Intent(activity_daily_task_status.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
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
                Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,"Error","Error Connecting To Server (RiderTasks/{taskid}/manage-task) "+t.getMessage());

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
                            Intent intent = new Intent(activity_daily_task_status.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            DisableLoading();
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
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
                Databackbone.getinstance().showAlsertBox(activity_daily_task_status.this,"Error","Error Connecting To Server (Riders/get-tasks) "+t.getMessage());

                DisableLoading();
                load_Data();
            }
        });


    }
    public boolean check_is_any_delivery_task_active()
    {
        for(int i =0;i<Databackbone.getinstance().ar_task_daily_delivery.size();i++)
        {
            if(Databackbone.getinstance().ar_task_daily_delivery.get(i).status)
                return true;
        }
        return false;
    }
    public boolean check_is_any_pickup_task_active()
    {
        for(int i =0;i<Databackbone.getinstance().parcels.size();i++)
        {
            if(!Databackbone.getinstance().parcels.get(i).getTaskStatus().equalsIgnoreCase("pending"))
                return true;
        }
        return false;
    }
}
