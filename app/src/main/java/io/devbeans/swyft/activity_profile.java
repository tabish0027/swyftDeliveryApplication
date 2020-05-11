package io.devbeans.swyft;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

public class activity_profile  extends Activity {
    Button btn_today;
    Button btn_month;
    Button btn_week;
    ImageView profile_image;

    TextView tx_pro_name,tx_pro_rating,tx_pro_location,tx_pro_phone,tx_pro_distance,tx_pro_duration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        btn_month = findViewById(R.id.btn_month);
        btn_today = findViewById(R.id.btn_today);
        btn_week = findViewById(R.id.btn_week);
        profile_image = findViewById(R.id.profile_image);
        tx_pro_name = findViewById(R.id.tx_pro_name);
        tx_pro_rating = findViewById(R.id.tx_pro_rating);
        tx_pro_location = findViewById(R.id.tx_pro_location);
        tx_pro_phone = findViewById(R.id.tx_pro_phone);
        tx_pro_distance = findViewById(R.id.tx_pro_distance);
        tx_pro_duration = findViewById(R.id.tx_pro_duration);


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

        final ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_profile.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadData();
    }

    public void SelectDay(){
        btn_today.setBackgroundResource(R.drawable.button_earn_select);
        btn_week.setBackgroundResource(R.drawable.button_earn_unselect);
        btn_month.setBackgroundResource(R.drawable.button_earn_unselect);
        btn_today.setTextColor(Color.parseColor("#221e1f"));
        btn_week.setTextColor(Color.parseColor("#ffffff"));
        btn_month.setTextColor(Color.parseColor("#ffffff"));


        tx_pro_distance.setText(Double.toString(Databackbone.getinstance().riderdetails.getTodayDistance())+" KM");
        tx_pro_duration.setText(Double.toString(Databackbone.getinstance().riderdetails.getTodayHours()) + " Hour");

    }
    public void SelectMonth(){
        btn_month.setBackgroundResource(R.drawable.button_earn_select);
        btn_week.setBackgroundResource(R.drawable.button_earn_unselect);
        btn_today.setBackgroundResource(R.drawable.button_earn_unselect);
        btn_month.setTextColor(Color.parseColor("#221e1f"));
        btn_week.setTextColor(Color.parseColor("#ffffff"));
        btn_today.setTextColor(Color.parseColor("#ffffff"));
        tx_pro_distance.setText(Double.toString(Databackbone.getinstance().riderdetails.getPreviousMonthDistance())+" KM");
        tx_pro_duration.setText(Double.toString(Databackbone.getinstance().riderdetails.getPreviousMonthHours()) + " Hour");
    }
    public void SelectWeek(){
        btn_week.setBackgroundResource(R.drawable.button_earn_select);
        btn_today.setBackgroundResource(R.drawable.button_earn_unselect);
        btn_month.setBackgroundResource(R.drawable.button_earn_unselect);
        btn_week.setTextColor(Color.parseColor("#221e1f"));
        btn_today.setTextColor(Color.parseColor("#ffffff"));
        btn_month.setTextColor(Color.parseColor("#ffffff"));
        tx_pro_distance.setText(Double.toString(Databackbone.getinstance().riderdetails.getPreviousWeekDistance())+" KM");
        tx_pro_duration.setText(Double.toString(Databackbone.getinstance().riderdetails.getPreviousWeekHours())+ " Hour");

    }
    public void LoadData(){
        try {
            if (Databackbone.getinstance().riderdetails == null)
                activity_profile.this.finish();
            tx_pro_name.setText(Databackbone.getinstance().riderdetails.getFirstName() + " " + Databackbone.getinstance().riderdetails.getLastName());
            tx_pro_rating.setText(Databackbone.getinstance().riderdetails.getType());
            tx_pro_location.setText(Databackbone.getinstance().riderdetails.getAddress());
            tx_pro_phone.setText(Databackbone.getinstance().riderdetails.getPhone());
            String imageUri = Databackbone.getinstance().riderdetails.getProfilePicture();
            Picasso.with(this).setLoggingEnabled(true);
            Picasso.with(this).load(imageUri).into(profile_image);

            SelectDay();
        }catch (Exception i){
            activity_profile.this.finish();
        }
    }

}