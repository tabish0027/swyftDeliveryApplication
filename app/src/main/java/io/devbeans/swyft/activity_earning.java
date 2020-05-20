package io.devbeans.swyft;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONObject;

import io.devbeans.swyft.interface_retrofit_delivery.delivery_earnings;
import io.devbeans.swyft.interface_retrofit_delivery.swift_api_delivery;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class activity_earning extends Activity {
    Button btn_today;
    Button btn_month;
    Button btn_week;
    TextView tx_earning_salary,tx_earning_commission,tx_earning_fule,tx_earning_maintance,tx_earning_total;
    public SwipeRefreshLayout swipeToRefresh;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earning);
        btn_month = findViewById(R.id.btn_month);
        btn_today = findViewById(R.id.btn_today);
        btn_week = findViewById(R.id.btn_week);

        tx_earning_salary= findViewById(R.id.tx_earning_salary);
        tx_earning_commission= findViewById(R.id.tx_earning_commission);
        tx_earning_fule= findViewById(R.id.tx_earning_fule);
        tx_earning_maintance= findViewById(R.id.tx_earning_maintance);
        tx_earning_total= findViewById(R.id.tx_earning_total);

        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        final ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_earning.this.finish();
            }
        });
        btn_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectDay();

            }
        });
        btn_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectWeek();


            }
        });

        btn_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectMonth();

            }
        });
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getEarnings();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        SelectDay();
    }

    public void getEarnings() {
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api_delivery riderapidata = retrofit.create(swift_api_delivery.class);

        Call<delivery_earnings> call = riderapidata.deliveryEarning(Databackbone.getinstance().rider.getId(),(Databackbone.getinstance().rider.getUserId()));
        call.enqueue(new Callback<delivery_earnings>() {
            @Override
            public void onResponse(Call<delivery_earnings> call, Response<delivery_earnings> response) {
                if(response.isSuccessful()){

                    delivery_earnings dailyearning = response.body();
                    Databackbone.getinstance().delivery_driver_earning = dailyearning;

                    //  DisableLoading();
                    // load_Data();
                    // update_view();
                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")){
                            Intent intent = new Intent(activity_earning.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(activity_earning.this,jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                swipeToRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<delivery_earnings> call, Throwable t) {
                System.out.println(t.getCause());
                Databackbone.getinstance().showAlsertBox(activity_earning.this,"Error","Error Connecting To Server (Riders/get-earnings) "+t.getMessage());

                swipeToRefresh.setRefreshing(false);
                //DisableLoading();
                // load_Data();
            }
        });
    }
    public void SelectDay(){
        btn_today.setBackgroundResource(R.drawable.button_earn_select);
        btn_week.setBackgroundResource(R.drawable.button_earn_unselect);
        btn_month.setBackgroundResource(R.drawable.button_earn_unselect);
        btn_today.setTextColor(Color.parseColor("#221e1f"));
        btn_week.setTextColor(Color.parseColor("#ffffff"));
        btn_month.setTextColor(Color.parseColor("#ffffff"));
        tx_earning_salary.setText("PKR. "+Integer.toString(0));
        tx_earning_commission.setText("PKR. "+Integer.toString(0));
        if (Databackbone.getinstance().delivery_driver_earning != null){
            tx_earning_fule.setText("PKR. "+Float.toString(Databackbone.getinstance().delivery_driver_earning.getDaily().getFuel()));
            tx_earning_maintance.setText("PKR. "+Float.toString(Databackbone.getinstance().delivery_driver_earning.getDaily().getMaintenance()));
            tx_earning_total.setText("PKR. "+Float.toString(Databackbone.getinstance().delivery_driver_earning.getDaily().getEarnings()));
        }

    }
    public void SelectMonth(){
        btn_month.setBackgroundResource(R.drawable.button_earn_select);
        btn_week.setBackgroundResource(R.drawable.button_earn_unselect);
        btn_today.setBackgroundResource(R.drawable.button_earn_unselect);
        btn_month.setTextColor(Color.parseColor("#221e1f"));
        btn_week.setTextColor(Color.parseColor("#ffffff"));
        btn_today.setTextColor(Color.parseColor("#ffffff"));
        tx_earning_salary.setText("PKR. "+Integer.toString(0));
        tx_earning_commission.setText("PKR. "+Integer.toString(0));
        if (Databackbone.getinstance().delivery_driver_earning != null){
            tx_earning_fule.setText("PKR. "+Float.toString(Databackbone.getinstance().delivery_driver_earning.getMonthly().getFuel()));
            tx_earning_maintance.setText("PKR. "+Float.toString(Databackbone.getinstance().delivery_driver_earning.getMonthly().getMaintenance()));
            tx_earning_total.setText("PKR. "+Float.toString(Databackbone.getinstance().delivery_driver_earning.getMonthly().getEarnings()));
        }
    }
    public void SelectWeek(){
        btn_week.setBackgroundResource(R.drawable.button_earn_select);
        btn_today.setBackgroundResource(R.drawable.button_earn_unselect);
        btn_month.setBackgroundResource(R.drawable.button_earn_unselect);
        btn_week.setTextColor(Color.parseColor("#221e1f"));
        btn_today.setTextColor(Color.parseColor("#ffffff"));
        btn_month.setTextColor(Color.parseColor("#ffffff"));
        tx_earning_salary.setText("PKR. "+Integer.toString(0));
        tx_earning_commission.setText("PKR. "+Integer.toString(0));
        if (Databackbone.getinstance().delivery_driver_earning != null){
            tx_earning_fule.setText("PKR. "+Float.toString(Databackbone.getinstance().delivery_driver_earning.getWeekly().getFuel()));
            tx_earning_maintance.setText("PKR. "+Float.toString(Databackbone.getinstance().delivery_driver_earning.getWeekly().getMaintenance()));
            tx_earning_total.setText("PKR. "+Float.toString(Databackbone.getinstance().delivery_driver_earning.getWeekly().getEarnings()));
        }
    }
}
