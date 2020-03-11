package io.devbeans.swyft;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;


import io.devbeans.swyft.network.ApiController;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import io.devbeans.swyft.interface_retrofit.*;
public class activity_login extends AppCompatActivity {

    Button btn_login;
    ProgressBar progressBar = null;
    EditText username , password,ip_con;
    Button btn_forget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Databackbone.getinstance().contextapp = getApplicationContext();
        btn_login = findViewById(R.id.btn_login);
        progressBar = (ProgressBar)findViewById(R.id.url_loading_animation);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btn_forget = findViewById(R.id.btn_forget);
        ip_con = findViewById(R.id.ip_con);
        if (!BuildConfig.BUILD_TYPE.equals("debug")) {
            ip_con.setVisibility(View.INVISIBLE);
        }
        else{
            ip_con.setText(BuildConfig.API_BASE_URL);
        }
        btn_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent forget = new Intent(activity_login.this, activity_forget_password.class);
                forget.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                forget.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity_login.this.startActivity(forget);
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
           // window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_app_theam));
        }

        // user with bugs
        //username.setText("03465175407");password.setText("12345"); // delivery stage

        username.setText("03450000002");password.setText("12345"); // delivery stage


        Sprite doubleBounce = new DoubleBounce();
        progressBar.setVisibility(View.GONE);
        progressBar.setIndeterminateDrawable(doubleBounce);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(activity_login.this,activity_mapview.class);
                //activity_login.this.startActivity(i);

                if(Databackbone.getinstance().checkInternet(activity_login.this)){
                    return;
                }
                Start_login();

    }
    public void changeStatusBarColor(){



    }
    public void Start_login(){
        username.setEnabled(false);
        password.setEnabled(false);
        btn_login.setEnabled(false);
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            String ip = ip_con.getText().toString();
            Databackbone.getinstance().Base_URL = ip;
            Databackbone.getinstance().central_retrofit = null;
            Databackbone.getinstance().getRetrofitbuilder();
        }
        progressBar.setVisibility(View.VISIBLE);


        String username_data = username.getText().toString();
        String password_data = password.getText().toString();

        if(username_data.length() < 4 || password_data.length() < 4)
        {
            Databackbone.getinstance().showAlsertBox(activity_login.this,"Error","user or password too short");
            EnableLogin();
            return;
        }
        login loginCredentials = new login();
        loginCredentials.username = username.getText().toString();
        loginCredentials.password = password.getText().toString();



        swift_api riderapi = Databackbone.getinstance().getRetrofitbuilder().create(swift_api.class);

        Call<Rider> call = riderapi.getRiderFromLogin(loginCredentials);
        call.enqueue(new Callback<Rider>() {
            @Override
            public void onResponse(Call<Rider> call, Response<Rider> response) {
                if(response.isSuccessful()){

                    Rider rider = response.body();
                    Databackbone.getinstance().rider = rider;
                   checkVersionControl();
                   // Toast.makeText(activity_login.this,rider.getId(),Toast.LENGTH_LONG).show();


                }else{
                    Databackbone.getinstance().showAlsertBox(activity_login.this,"error","wrong username or password");
                            EnableLogin();
                }

            }

            @Override
            public void onFailure(Call<Rider> call, Throwable t) {
                System.out.println(t.getCause());
                Databackbone.getinstance().showAlsertBox(activity_login.this,"Error","Error Connecting To Server Error Code 22 Riders/login "+t.getMessage());

                EnableLogin();
            }
        });

    }
        });

    }
    public void checkVersionControl(){
        swift_api riderapi = Databackbone.getinstance().getRetrofitbuilder().create(swift_api.class);

        Call<Void> call = riderapi.getversioncontrol(Databackbone.getinstance().rider.getId(),BuildConfig.VERSION_NAME);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() != 200) {
                    Databackbone.getinstance().showAlsertBox(activity_login.this,"Error","This app version is obsolete, Please Download the newer version");
                    EnableLogin();
                    return;
                }
                    getRiderDetail();



            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println(t.getCause());
                EnableLogin();
            }
        });
    }
    public void getRiderDetail(){
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api riderapi = retrofit.create(swift_api.class);

        Call<RiderDetails> call = riderapi.getRider(Databackbone.getinstance().rider.getId(),Databackbone.getinstance().rider.getUserId());
        call.enqueue(new Callback<RiderDetails>() {
            @Override
            public void onResponse(Call<RiderDetails> call, Response<RiderDetails> response) {
                if(response.isSuccessful()){

                    RiderDetails riderActivity = response.body();
                    Databackbone.getinstance().riderdetails = riderActivity;
                    ApiController.getInstance().getEarnings();
                    ApiController.getInstance().getwallet();
                    ApiController.getInstance().gethistory();
                    if (ContextCompat.checkSelfPermission(activity_login.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
                        ActivityCompat.requestPermissions(activity_login.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    else
                    {
                        Intent i = new Intent(activity_login.this,activity_mapview.class);
                        activity_login.this.startActivity(i);

                    }

                }
                else{
                    EnableLogin();
                    //DeactivateRider();
                    Databackbone.getinstance().showAlsertBox(activity_login.this,"Error","Error Connecting To Server Error Code 33");
                }

            }

            @Override
            public void onFailure(Call<RiderDetails> call, Throwable t) {
                System.out.println(t.getCause());
                EnableLogin();
                Databackbone.getinstance().showAlsertBox(activity_login.this,"Error","Error Connecting To Server Error Code 34");

                //DeactivateRider();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        EnableLogin();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults[0] == 0 ){


                Intent i = new Intent(activity_login.this, activity_mapview.class);
                activity_login.this.startActivity(i);

        }
        else{
            Databackbone.getinstance().showAlsertBox(activity_login.this,"Error","Please give permission for location");
        }
    }

    public void EnableLogin(){
        progressBar.setVisibility(View.GONE);
        username.setEnabled(true);
        password.setEnabled(true);
        btn_login.setEnabled(true);
    }
}
