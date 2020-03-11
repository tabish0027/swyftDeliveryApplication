package io.devbeans.swyft;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.devbeans.swyft.adapters.adapter_status_packages_scanning;
import io.devbeans.swyft.data_models.model_order_item;
import io.devbeans.swyft.interface_retrofit.Parcel;
import io.devbeans.swyft.interface_retrofit.PickupParcel;
import io.devbeans.swyft.interface_retrofit_delivery.Datum;
import io.devbeans.swyft.interface_retrofit_delivery.RiderActivityDelivery;

public class activity_order_status_scanning extends Activity {

    public androidx.constraintlayout.widget.ConstraintLayout con_orders_scanned,con_orders_remaining;
    public RecyclerView order_list_remaining,order_list_scanned;
    public adapter_status_packages_scanning ad_orders_scanned,ad_orders_remaining;
    public TextView tx_count_scanned,tx_count_remaining;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_order_status);

        order_list_remaining= findViewById(R.id.order_list_remaining);
        order_list_scanned= findViewById(R.id.order_list_scanned);


        con_orders_scanned= findViewById(R.id.con_orders_scanned);
        con_orders_remaining= findViewById(R.id.con_orders_remaining);



        tx_count_scanned= findViewById(R.id.tx_count_scanned);
        tx_count_remaining= findViewById(R.id.tx_count_remaining);


        Databackbone.getinstance().ar_orders_scanned = new ArrayList<>();
        Databackbone.getinstance().ar_orders_remaining= new ArrayList<>();


        ad_orders_scanned = new adapter_status_packages_scanning(Databackbone.getinstance().ar_orders_scanned, this);
        ad_orders_remaining = new adapter_status_packages_scanning(Databackbone.getinstance().ar_orders_remaining, this);


        order_list_remaining.setAdapter(ad_orders_remaining);
        order_list_scanned.setAdapter(ad_orders_scanned);



        con_orders_scanned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(order_list_scanned.getVisibility() == View.VISIBLE)
                {
                    order_list_scanned.setVisibility(View.GONE);
                }
                else
                {
                    order_list_scanned.setVisibility(View.VISIBLE);
                }
            }
        });
        con_orders_remaining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(order_list_remaining.getVisibility() == View.VISIBLE)
                {
                    order_list_remaining.setVisibility(View.GONE);
                }
                else
                {
                    order_list_remaining.setVisibility(View.VISIBLE);
                }
            }
        });

        final ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_order_status_scanning.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_parcels();

    }

    public void load_parcels(){

        Databackbone.getinstance().ar_orders_scanned.clear();
        Databackbone.getinstance().ar_orders_remaining.clear();

        ArrayList<model_order_item>  temp_ar_orders_scanned = new ArrayList<>();
        ArrayList<model_order_item>  temp_ar_orders_remaining= new ArrayList<>();

       if(!Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery")) {
           PickupParcel parcelPickups = Databackbone.getinstance().getParcelsForPickup() ;
           if(parcelPickups == null)
               activity_order_status_scanning.this.finish();
           List<Parcel> parcels = parcelPickups.getParcels() ;
           for (int i = 0; i < parcels.size(); i++) {
               if (!parcels.get(i).getScanned()) {

                   temp_ar_orders_remaining.add(new model_order_item(parcels.get(i).getParcelId(), "", "", "remain"));

               } else {

                   temp_ar_orders_scanned.add(new model_order_item(parcels.get(i).getParcelId(), parcels.get(i).getScannedOn(), "", "scan"));
               }
           }
       }else{
           RiderActivityDelivery BatchToDeliver = Databackbone.getinstance().getDeliveryTask();
           if(BatchToDeliver == null   )
               activity_order_status_scanning.this.finish();
           List<Datum> Locations= BatchToDeliver.getData();

           for (int i = 0; i < Locations.size(); i++) {
               Datum data = Locations.get(i);

               for (int j = 0; j < data.getParcels().size(); j++) {
                   if(!(data.getParcels().get(j).getStatus().equals("scanned")||data.getParcels().get(j).getStatus().equals("started"))){
                       temp_ar_orders_remaining.add(new model_order_item(data.getParcels().get(j).getParcelId(), "", "", "remain"));

                   }
                   else{
                       temp_ar_orders_scanned.add(new model_order_item(data.getParcels().get(j).getParcelId(),data.getParcels().get(j).getScannedOn(), "", "scan"));

                   }
               }
           }

       }
        Databackbone.getinstance().ar_orders_scanned.addAll(temp_ar_orders_scanned);
        Databackbone.getinstance().ar_orders_remaining.addAll(temp_ar_orders_remaining);
        update_view();
    }


    public void update_view(){
        tx_count_scanned.setText(Integer.toString(Databackbone.getinstance().ar_orders_scanned.size()));
        tx_count_remaining.setText(Integer.toString(Databackbone.getinstance().ar_orders_remaining.size()));


        ad_orders_scanned.update_list();
        ad_orders_remaining.update_list();


    }


}
