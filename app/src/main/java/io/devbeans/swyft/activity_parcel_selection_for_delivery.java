package io.devbeans.swyft;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.devbeans.swyft.adapters.*;
import io.devbeans.swyft.data_models.model_parcel;
import io.devbeans.swyft.interface_retrofit_delivery.Datum;
import io.devbeans.swyft.interface_retrofit_delivery.RiderActivityDelivery;

public class activity_parcel_selection_for_delivery extends AppCompatActivity {
    ImageView btn_back;
    TextView tx_amount_to_collect;
    Button btn_delivered,btn_diclined;
    RecyclerView rv_list_parcels;
    ConstraintLayout btn_diselect_all,btn_select_all;
    TextView tx_count_delivered;
    String amount_str;
    adapter_status_daily_packages_delivery_selection ad_orders_selections;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel_selection_for_delivery);
        btn_back = findViewById(R.id.btn_back);
        rv_list_parcels = findViewById(R.id.rv_list_parcels);
        tx_count_delivered = findViewById(R.id.tx_count_delivered);

        ad_orders_selections = new adapter_status_daily_packages_delivery_selection(Databackbone.getinstance().ar_orders_parcels_selections, this);


        rv_list_parcels.setAdapter(ad_orders_selections);
        btn_delivered = findViewById(R.id.btn_delivered);
        btn_diclined = findViewById(R.id.btn_diclined);

        btn_diselect_all= findViewById(R.id.btn_diselect_all);
        btn_select_all= findViewById(R.id.btn_select_all);;


        tx_amount_to_collect = findViewById(R.id.tx_amount_to_collect);

        btn_diselect_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreashData(false);
            }
        });

        btn_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreashData(true);
            }
        });


        btn_diclined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // using BottomSheetDialogFragment
                mark_parcels_to_process();
                bottomsheet_orderdeclined bottomSheetFragment = new bottomsheet_orderdeclined();

                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }

        });
        btn_delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mark_parcels_to_process();
                Intent declined = new Intent(activity_parcel_selection_for_delivery.this, activity_signature_pad.class);
                declined.putExtra("amount", amount_str);
                declined.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                declined.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity_parcel_selection_for_delivery.this.startActivity(declined);


            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity_parcel_selection_for_delivery.this.finish();
            }
        });


        mark_park_parcel_to_complete();

        if(checkIforderActive()){
                disable_process_button();
        }
        ad_orders_selections.setOnItemClickListener(new adapter_status_daily_packages_delivery_selection.ClickListener() {
            @Override
            public void onItemClick(int position, View v,Boolean check) {
               // if(check) {
                   Databackbone.getinstance().ar_orders_parcels_selections.get(position).selected = !Databackbone.getinstance().ar_orders_parcels_selections.get(position).selected;
                    RefreashData();
                //}


            }

            @Override
            public void onItemLongClick(int position, View v,Boolean check) {

              }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        //checkIfAnyParcelLeft();
        LoadData();
    }
    public void LoadData(){
        RiderActivityDelivery RiderDelivery = Databackbone.getinstance().getDeliveryTask();
        if(RiderDelivery == null) {
            activity_parcel_selection_for_delivery.this.finish();

            return;
        }
        if(RiderDelivery.getData().size() ==0){
            activity_parcel_selection_for_delivery.this.finish();
            return;
        }
        Datum data=   Databackbone.getinstance().getDeliveryParcelsTask() ;
        if(data  == null){
            activity_parcel_selection_for_delivery.this.finish();
            return;
        }
        Databackbone.getinstance().ar_orders_parcels_selections.clear();
        ArrayList<model_parcel> parcels= new ArrayList<>();
        int TotalAmount=0,Totalparcels=0;
        for(int i=0;i<data.getParcels().size();i++){
            if(data.getParcels().get(i).getStatus().equals("started")||data.getParcels().get(i).getStatus().equals("pending")){
                String mb_task_id =RiderDelivery.getTaskId();
                String parcelid = data.getParcels().get(i).getParcelId();
                String mb_name = data.getName();
                String mb_address =  data.getLocation().getAddress();
                String status = data.getParcels().get(i).getStatus();
                Float amount = data.getParcels().get(i).getAmount();
                TotalAmount += amount;
                Totalparcels++;
                String description = data.getParcels().get(i).getDescription();
                boolean selected = true;
                parcels.add(new model_parcel(mb_task_id, parcelid, mb_name, mb_address, status, amount, description, selected));
            }
        }
        Databackbone.getinstance().ar_orders_parcels_selections.addAll(parcels);

        tx_count_delivered.setText(Integer.toString(Totalparcels));
        tx_amount_to_collect.setText(Integer.toString(TotalAmount));
        amount_str = Integer.toString(TotalAmount);
        ad_orders_selections.update_list();

    }
    public void RefreashData(){

        ArrayList<model_parcel> parcels= Databackbone.getinstance().ar_orders_parcels_selections;
        int TotalAmount=0,Totalparcels=0;
        for(int i=0;i<parcels.size();i++){
            if(parcels.get(i).selected){

                float amount = parcels.get(i).ammount;
                TotalAmount += amount;
                Totalparcels++;

            }
        }


        tx_count_delivered.setText(Integer.toString(Totalparcels));
        tx_amount_to_collect.setText(Integer.toString(TotalAmount));
        amount_str = Integer.toString(TotalAmount);
        ad_orders_selections.update_list();
        if(checkIforderActive()){
            disable_process_button();
        }else
        {
            if(Totalparcels!=0){
                able_process_button();
            }else{
                disable_process_button();
            }
        }
    }
    public void RefreashData(Boolean check){

        ArrayList<model_parcel> parcels= Databackbone.getinstance().ar_orders_parcels_selections;
        int TotalAmount=0,Totalparcels=0;
        for(int i=0;i<parcels.size();i++){
            parcels.get(i).selected = check;
            if(parcels.get(i).selected){

                float amount = parcels.get(i).ammount;
                TotalAmount += amount;
                Totalparcels++;

            }
        }
        if(Totalparcels!=0){
            able_process_button();
        }else{
            disable_process_button();
        }

        tx_count_delivered.setText(Integer.toString(Totalparcels));
        tx_amount_to_collect.setText(Integer.toString(TotalAmount));
        amount_str = Integer.toString(TotalAmount);
        ad_orders_selections.update_list();

    }
    public void mark_park_parcel_to_complete(){
         Databackbone.getinstance().getDeliveryParcelsTask().markAllParcelToBeComplete();

    }
    /*
    public void checkIfAnyParcelLeft(){
        Boolean check_any_parcel_left = true;

        if(Databackbone.getinstance().parcelsdelivery.get(Databackbone.getinstance().delivery_to_show).getData().size() == 0)
        {
            activity_parcel_selection_for_delivery.this.finish();
            return;
        }
        Datum data = Databackbone.getinstance().parcelsdelivery.get(Databackbone.getinstance().delivery_to_show).getData().get(Databackbone.getinstance().task_to_show);
        for(int i=0;i<data.getParcels().size();i++)
            if(data.getParcels().get(i).getStatus().equals("started")||data.getParcels().get(i).getStatus().equals("pending"))
            {
                check_any_parcel_left = false;
                break;
            }
        if(check_any_parcel_left){

            activity_parcel_selection_for_delivery.this.finish();
        }

    }
    */

    public Boolean checkIforderActive(){
        Boolean check_any_parcel_left = true;
        Datum data = Databackbone.getinstance().getDeliveryParcelsTask();
        if(data == null)
            activity_parcel_selection_for_delivery.this.finish();
        for(int i=0;i<data.getParcels().size();i++)
            if(data.getParcels().get(i).getStatus().equals("pending")||data.getParcels().get(i).getStatus().equals("scanned"))
                return true;
            else
                return false;

        return false;

    }
    /*
    public int totalamounttocollect(){
        int amount = 0;
        Datum data = Databackbone.getinstance().parcelsdelivery.get(Databackbone.getinstance().task_to_show).getData().get(Databackbone.getinstance().delivery_to_show);
        for(int i=0;i<data.getParcels().size();i++)
            //if(data.getParcels().get(i).getStatus().equals("started")||data.getParcels().get(i).getStatus().equals("pending"))
                amount += data.getParcels().get(i).getAmount();

        return amount;

    }*/
    public void mark_parcels_to_process(){
         List<String> parcels_id = new ArrayList<String>();

        ArrayList<model_parcel> parcels= Databackbone.getinstance().ar_orders_parcels_selections;
        int TotalAmount=0,Totalparcels=0;
        for(int i=0;i<parcels.size();i++){
            if(parcels.get(i).selected){
                parcels_id.add(parcels.get(i).Parcelid);

            }
        }


        Databackbone.getinstance().parcel_to_process =  parcels_id;
    }
    public void disable_process_button(){
        btn_diclined.setEnabled(false);
        btn_delivered.setEnabled(false);
        btn_delivered.setVisibility(View.INVISIBLE);
        btn_diclined.setVisibility(View.INVISIBLE);
    }
    public void able_process_button(){
        btn_diclined.setEnabled(true);
        btn_delivered.setEnabled(true);
        btn_delivered.setVisibility(View.VISIBLE);
        btn_diclined.setVisibility(View.VISIBLE);
    }
}
