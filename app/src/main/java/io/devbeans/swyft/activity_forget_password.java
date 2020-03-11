package io.devbeans.swyft;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import io.devbeans.swyft.interface_retrofit.PasswordResetRequest;
import io.devbeans.swyft.interface_retrofit.reset_password;
import io.devbeans.swyft.interface_retrofit.swift_api;
import io.devbeans.swyft.interface_retrofit.username;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class activity_forget_password extends AppCompatActivity {


    ConstraintLayout panel_reset_password,panel_request_otp;
    ProgressBar progressBar = null;
    EditText tx_otp,tx_password,tx_confirm_password,tx_username;
    Button btn_reset_password,btn_requestotp;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        panel_reset_password = findViewById(R.id.panel_reset_password);
        panel_request_otp = findViewById(R.id.panel_request_otp);
        btn_back = findViewById(R.id.btn_back);

        tx_otp = findViewById(R.id.tx_otp);
        tx_password = findViewById(R.id.tx_password);
        tx_confirm_password = findViewById(R.id.tx_confirm_password);
        tx_username = findViewById(R.id.tx_username);
        tx_username.setText("923004820761");
        btn_reset_password = findViewById(R.id.btn_reset_password);
        btn_requestotp= findViewById(R.id.btn_requestotp);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_forget_password.this.finish();
            }
        });
        progressBar = (ProgressBar)findViewById(R.id.url_loading_animation);
        btn_requestotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tx_username.getText().toString().length() > 10){
                    request_Otp(tx_username.getText().toString());
                }
                else {
                    Databackbone.getinstance().showAlsertBox(activity_forget_password.this,"error","Please enter a valid Phone Number");
                }
            }
        });
        btn_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tx_password.getText().toString().equals(tx_confirm_password.getText().toString()))
                {
                    if(tx_password.getText().toString().length() > 4) {
                        if (!tx_otp.getText().toString().isEmpty()  )
                        {reset_password(tx_otp.getText().toString(),tx_password.getText().toString());
                        }
                        else{
                            Databackbone.getinstance().showAlsertBox(activity_forget_password.this,"error","Please enter a valid opt");

                        }
                    }
                    else{
                        Databackbone.getinstance().showAlsertBox(activity_forget_password.this,"error","Password length Should Be greater then 6");

                    }
                }else{
                    Databackbone.getinstance().showAlsertBox(activity_forget_password.this,"error","Password and confirmpassword not shame");

                }
            }
        });
        panel_reset_password.setVisibility(View.GONE);



    }
    public void request_Otp(String phonenumber){

        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api riderapi = retrofit.create(swift_api.class);

        Call<PasswordResetRequest> call = riderapi.requestotp(new username(phonenumber));
        EnableLoading();
        call.enqueue(new Callback<PasswordResetRequest>() {
            @Override
            public void onResponse(Call<PasswordResetRequest> call, Response<PasswordResetRequest> response) {
                if(response.isSuccessful()){

                    PasswordResetRequest rider = response.body();
                    if(!rider.getMessage().equalsIgnoreCase("")){
                        panel_reset_password.setVisibility(View.VISIBLE);
                        panel_request_otp.setVisibility(View.GONE);
                    }
                    else{
                        panel_reset_password.setVisibility(View.GONE);
                        panel_request_otp.setVisibility(View.VISIBLE);
                    }

                }
                DisableLoading();
            }

            @Override
            public void onFailure(Call<PasswordResetRequest> call, Throwable t) {
                System.out.println(t.getCause());
                panel_reset_password.setVisibility(View.GONE);
                panel_request_otp.setVisibility(View.VISIBLE);DisableLoading();
            }
        });
    }
    public void reset_password(String otp , String password){
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api riderapi = retrofit.create(swift_api.class);

        Call<PasswordResetRequest> call = riderapi.reset_password_forget(new reset_password(password,otp));
        EnableLoading();
        call.enqueue(new Callback<PasswordResetRequest>() {
            @Override
            public void onResponse(Call<PasswordResetRequest> call, Response<PasswordResetRequest> response) {
                if(response.isSuccessful()){

                    PasswordResetRequest rider = response.body();
                    if(!rider.getMessage().equalsIgnoreCase("")){

                        new AlertDialog.Builder(activity_forget_password.this)
                                .setTitle("Password reset")
                                .setMessage("Done")

                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        activity_forget_password.this.finish();
                                    }
                                })

                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                    }
                    else{
                        panel_reset_password.setVisibility(View.GONE);
                        panel_request_otp.setVisibility(View.VISIBLE);
                        Databackbone.getinstance().showAlsertBox(activity_forget_password.this,"Password reset","Error");

                    }

                }
                DisableLoading();
            }

            @Override
            public void onFailure(Call<PasswordResetRequest> call, Throwable t) {
                System.out.println(t.getCause());DisableLoading();
                panel_reset_password.setVisibility(View.GONE);
                panel_request_otp.setVisibility(View.VISIBLE);
                Databackbone.getinstance().showAlsertBox(activity_forget_password.this,"Password reset","Error");
            }
        });
    }
    public void DisableLoading(){

        progressBar.setVisibility(View.GONE);
    }
    public void EnableLoading(){

        progressBar.setVisibility(View.VISIBLE);
    }
}
