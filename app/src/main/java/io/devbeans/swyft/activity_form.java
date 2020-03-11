package io.devbeans.swyft;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import io.devbeans.swyft.interface_retrofit.GeoPoints;
import io.devbeans.swyft.interface_retrofit_delivery.Datum;

public class activity_form extends AppCompatActivity {
    ImageView btn_back;
    TextView tx_name,tx_address,tx_parcel_id,tx_payment_method,tx_amount_to_collect;
    Button btn_delivered;
    ImageView btn_payment_method,btn_navigation,btn_parcel_selection,btn_sms,btn_phone;
    Button btn_diclined;
    TextView tx_description_title,tx_description_detail;
    String PhoneNumber = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Databackbone.getinstance().contextapp = getApplicationContext();
        setContentView(R.layout.activity_form);
        btn_back = findViewById(R.id.btn_back);
        btn_delivered = findViewById(R.id.btn_delivered);
        btn_payment_method = findViewById(R.id.btn_payment_method);
        btn_navigation = findViewById(R.id.btn_navigation);
        tx_amount_to_collect = findViewById(R.id.tx_amount_to_collect);
        btn_parcel_selection= findViewById(R.id.btn_parcel_selection);
        tx_description_detail = findViewById(R.id.tx_description_detail);
        tx_description_title = findViewById(R.id.tx_description_title);
        btn_sms = findViewById(R.id.btn_sms);
        btn_phone = findViewById(R.id.btn_phone);
        btn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenMessege();
            }
        });
        btn_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDiler();
            }
        });
        btn_diclined = findViewById(R.id.btn_diclined);
        btn_diclined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // using BottomSheetDialogFragment
                mark_all_parcels_to_process();
                bottomsheet_orderdeclined bottomSheetFragment = new bottomsheet_orderdeclined();

                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }

        });
        btn_delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mark_all_parcels_to_process();
                Intent declined = new Intent(activity_form.this, activity_signature_pad.class);
                declined.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                declined.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity_form.this.startActivity(declined);


            }
        });
        btn_parcel_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent declined = new Intent(activity_form.this, activity_parcel_selection_for_delivery.class);
                declined.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                declined.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity_form.this.startActivity(declined);
            }
        });
        btn_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoPoints points = Databackbone.getinstance().getDeliveryTask().getData().get(0).getLocation().getGeoPoints();
                Offlice_Activity(new LatLng(points.getLat(),points.getLng()));
            }
        });
        btn_payment_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomsheet_payment_method bottomSheetFragment = new bottomsheet_payment_method();

                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

    }
    public void Offlice_Activity(LatLng location){
        String location_to_string = Double.toString(location.latitude) + ","+Double.toString(location.longitude);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr="+location_to_string));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        this.getApplicationContext().startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_data();
        checkIfAnyParcelLeft();
    }

    public void mark_park_parcel_to_complete(){
        Databackbone.getinstance().getDeliveryParcelsTask().markAllParcelToBeComplete();

    }
    public void checkIfAnyParcelLeft(){
        Boolean check_any_parcel_left = true;
        Datum DeliveryLocation = Databackbone.getinstance().getDeliveryParcelsTask();
        if(DeliveryLocation == null)
            activity_form.this.finish();
        Databackbone.getinstance().getDeliveryParcelsTask();
        try {
            if (Databackbone.getinstance().getDeliveryParcelsTask().getParcels().size() == 0) {
                activity_form.this.finish();
                return;
            }
            Datum data = Databackbone.getinstance().getDeliveryParcelsTask();
            for (int i = 0; i < data.getParcels().size(); i++)
                if (data.getParcels().get(i).getStatus().equals("scanned") ||data.getParcels().get(i).getStatus().equals("started") || data.getParcels().get(i).getStatus().equals("pending")) {
                    check_any_parcel_left = false;
                    break;
                }
            if (check_any_parcel_left) {

                activity_form.this.finish();
            }
        }catch (Exception index_out_of_boud){
            activity_form.this.finish();
        }

    }
    public Boolean checkIforderActive(){
         Datum data = Databackbone.getinstance().getDeliveryParcelsTask();
         if(data == null)
             activity_form.this.finish();
        for(int i=0;i<data.getParcels().size();i++)
            if(data.getParcels().get(i).getStatus().equals("pending")||data.getParcels().get(i).getStatus().equals("scanned"))
                return true;
            else
                return false;

        return false;

    }
    public float totalamounttocollect(){
        float amount = 0;
        int parcel_count = 0;
        Datum data = Databackbone.getinstance().getDeliveryParcelsTask();
        if(data == null)
            activity_form.this.finish();
        for(int i=0;i<data.getParcels().size();i++)
            if(data.getParcels().get(i).getStatus().equals("started")||data.getParcels().get(i).getStatus().equals("pending")||data.getParcels().get(i).getStatus().equals("scanned")) {
                amount += data.getParcels().get(i).getAmount();
                parcel_count++;
            }

        if(parcel_count == 1 ){
            btn_parcel_selection.setVisibility(View.INVISIBLE);
            btn_delivered.setText("DELIVERED");
            btn_diclined.setText("DECLINED");
            tx_parcel_id.setText("Parcels Id : " + data.getParcels().get(0).getParcelId());
            tx_description_detail.setText(data.getParcels().get(0).getDescription());
            tx_description_title.setText("Parcel Description");
        }
        else{
            tx_parcel_id.setText("Parcels count : " + Integer.toString(parcel_count));

            btn_delivered.setText("DELIVERED ALL");
            btn_diclined.setText("DECLINED ALL");
            tx_description_detail.setText("Please process all Parcels to mark this task complete");
            tx_description_title.setText("Disclaimer");
        }
        return amount;

    }
    public void mark_all_parcels_to_process(){
        Datum data = Databackbone.getinstance().getDeliveryParcelsTask();
        if(data == null)
            activity_form.this.finish();
        List<String> parcels_id = new ArrayList<String>();

        for (int j = 0; j < data.getParcels().size(); j++) {
            if(data.getParcels().get(j).parcel_to_mark_complete)
                parcels_id.add(data.getParcels().get(j).getParcelId());
        }
        Databackbone.getinstance().parcel_to_process =  parcels_id;
    }
    public void load_data(){

        try {
            Datum data = Databackbone.getinstance().getDeliveryParcelsTask();
            if(data == null)
                activity_form.this.finish();

            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity_form.this.finish();
                }
            });
            tx_name = findViewById(R.id.tx_name);
            tx_address = findViewById(R.id.tx_address);
            tx_parcel_id = findViewById(R.id.tx_parcel_id);
            tx_payment_method = findViewById(R.id.tx_payment_method);
            tx_name.setText(data.getName());
            PhoneNumber = data.getPhone();
            tx_address.setText(data.getLocation().getAddress());
            tx_payment_method.setText("Cash");
            mark_park_parcel_to_complete();

            if (checkIforderActive()) {
                btn_diclined.setEnabled(false);
                btn_delivered.setEnabled(false);
                btn_delivered.setVisibility(View.INVISIBLE);
                btn_diclined.setVisibility(View.INVISIBLE);
            }
            tx_amount_to_collect.setText(Float.toString(totalamounttocollect()));
        }catch (Exception i){
            activity_form.this.finish();
        }
    }
    public void OpenDiler(){
        Intent phoneintent = new Intent(Intent.ACTION_DIAL);
        phoneintent.setData(Uri.parse("tel:0"+PhoneNumber));
        startActivity(phoneintent);
    }

    public void OpenMessege(){
        try {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", "0" + PhoneNumber);
            smsIntent.putExtra("sms_body", "");
            startActivity(smsIntent);
        }catch (Exception i){
            Databackbone.getinstance().showAlsertBox(this,"error","This phone dont support sms sync app Please Contact support for this ");
        }
    }

}
