package io.devbeans.swyft;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vivekkaushik.datepicker.DatePickerTimeline;
import com.vivekkaushik.datepicker.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.devbeans.swyft.adapters.adapter_status_daily_wallet;
import io.devbeans.swyft.data_models.model_wallets_order;
import io.devbeans.swyft.interface_retrofit_delivery.history;
import io.devbeans.swyft.interface_retrofit_delivery.swift_api_delivery;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class activity_wallet_orders extends Activity {


    public RecyclerView order_list_wallet;
    public adapter_status_daily_wallet ad_orders_wallet;
    public DatePickerTimeline datePickerTimeline;
    public SwipeRefreshLayout swipeToRefresh;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        final ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_wallet_orders.this.finish();
            }
        });
         datePickerTimeline = findViewById(R.id.datePickerTimeline);
        datePickerTimeline.setInitialDate(2019, 3, 21);
        datePickerTimeline.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int dayOfWeek) {
                // Do Something
            }

            @Override
            public void onDisabledDateSelected(int year, int month, int day, int dayOfWeek, boolean isDisabled) {
                // Do Something
            }
        });
        // Disable date
        Date[] dates = {Calendar.getInstance().getTime()};
        datePickerTimeline.deactivateDates(dates);
        datePickerTimeline.setVisibility(View.VISIBLE);

        datePickerTimeline.setDateTextColor(Color.GRAY);
        datePickerTimeline.setDayTextColor(Color.BLACK);
        datePickerTimeline.setMonthTextColor(Color.GRAY);
        order_list_wallet = findViewById(R.id.waleet_items);




        //generate_test_Data();
        ad_orders_wallet = new adapter_status_daily_wallet(Databackbone.getinstance().ar_orders_wallet, this);


        order_list_wallet.setAdapter(ad_orders_wallet);


        generate_test_Data();



    }

    @Override
    protected void onResume() {
        super.onResume();


    }


    public void generate_test_Data() {
        Databackbone.getinstance().ar_orders_wallet.clear();

        ArrayList<model_wallets_order> temp_ar_orders_wallet = new ArrayList<>();
        temp_ar_orders_wallet.add(new model_wallets_order("4384745", "Amir " + Integer.toString(0), "G 47 DHA lahore", "17."+Integer.toString(0)+" Rs"));

        for (int i = 1; i < 10; i++) {
            temp_ar_orders_wallet.add(new model_wallets_order("4384745", "Amir " + Integer.toString(i), "G 47 DHA lahore", "17."+Integer.toString(i)+" Rs"));
        }
        Databackbone.getinstance().ar_orders_wallet.addAll(temp_ar_orders_wallet);
        update_view();


    }
    public void gethistory() {
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api_delivery riderapidata = retrofit.create(swift_api_delivery.class);

        Call<List<history>> call = riderapidata.deliveryhistory(Databackbone.getinstance().rider.getId(),(Databackbone.getinstance().rider.getUserId()));
        call.enqueue(new Callback<List<history>>() {
            @Override
            public void onResponse(Call<List<history>> call, Response<List<history>> response) {
                if(response.isSuccessful()){

                    List<history> history = response.body();
                    Databackbone.getinstance().history = history;

                    //  DisableLoading();
                    // load_Data();
                    // update_view();
                }
                else{
                    //DisableLoading();
                }

            }

            @Override
            public void onFailure(Call<List<history>> call, Throwable t) {
                System.out.println(t.getCause());

                //DisableLoading();
                // load_Data();
            }
        });
    }
    public void update_view() {

        ad_orders_wallet.update_list();

    }
}
