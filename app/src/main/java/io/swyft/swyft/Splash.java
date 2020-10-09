package io.swyft.swyft;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.devbeans.swyft.BuildConfig;
import io.devbeans.swyft.Databackbone;
import io.devbeans.swyft.R;
import io.devbeans.swyft.activity_login;
import io.devbeans.swyft.activity_mapview;
import io.devbeans.swyft.interface_retrofit.Rider;
import io.devbeans.swyft.interface_retrofit.RiderDetails;
import io.devbeans.swyft.interface_retrofit.swift_api;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Splash extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    List<Rider> riderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        mEditor = sharedpreferences.edit();
        setContentView(R.layout.activity_splash);

        Gson gson = new Gson();
        String json = sharedpreferences.getString("Rider", "");
        Type type = new TypeToken<List<Rider>>() {
        }.getType();
        riderList = gson.fromJson(json, type);

        Thread thr = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    if (riderList != null && !riderList.isEmpty()) {
                        Databackbone.getinstance().rider = riderList.get(0);
                        checkVersionControl();
                    } else {
                        Intent i = new Intent(Splash.this, activity_login.class);
                        Splash.this.startActivity(i);
                        finish();
                    }
                } catch (Exception i) {

                }
            }
        });
        thr.start();


    }

    public void checkVersionControl() {
        swift_api riderapi = Databackbone.getinstance().getRetrofitbuilder().create(swift_api.class);

        Call<Void> call = riderapi.getversioncontrol(Databackbone.getinstance().rider.getId(), BuildConfig.VERSION_NAME);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() != 200) {

                    if (response.code() == 404 || response.code() == 401) {
                        mEditor.clear().commit();
                        Intent intent = new Intent(Splash.this, activity_login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Databackbone.getinstance().showAlsertBox(Splash.this, "Error", "This app version is obsolete, Please Download the newer version");
                        return;
                    }
                }
                getRiderDetail();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println(t.getCause());
                Databackbone.getinstance().showAlsertBox(Splash.this, "Error", "Error Connecting To Server (app-version-check) " + t.getMessage());
            }
        });
    }

    public void getRiderDetail() {
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api riderapi = retrofit.create(swift_api.class);

        Call<RiderDetails> call = riderapi.getRider(Databackbone.getinstance().rider.getId(), Databackbone.getinstance().rider.getUserId());
        call.enqueue(new Callback<RiderDetails>() {
            @Override
            public void onResponse(Call<RiderDetails> call, Response<RiderDetails> response) {
                if (response.isSuccessful()) {

                    RiderDetails riderActivity = response.body();
                    Databackbone.getinstance().riderdetails = riderActivity;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(Splash.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED &&
                                ContextCompat.checkSelfPermission(Splash.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
                                ContextCompat.checkSelfPermission(Splash.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                            ActivityCompat.requestPermissions(Splash.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        else {
                            Intent i = new Intent(Splash.this, activity_mapview.class);
                            Splash.this.startActivity(i);
                            finish();
                        }
                    } else {
                        Intent i = new Intent(Splash.this, activity_mapview.class);
                        Splash.this.startActivity(i);
                        finish();
                    }

                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        if (jsonObject.getJSONObject("error").getString("statusCode").equals("401") || jsonObject.getJSONObject("error").getString("statusCode").equals("404")) {
                            Intent intent = new Intent(Splash.this, activity_login.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            //DeactivateRider();
                            Databackbone.getinstance().showAlsertBox(Splash.this, jsonObject.getJSONObject("error").getString("statusCode"), jsonObject.getJSONObject("error").getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<RiderDetails> call, Throwable t) {
                System.out.println(t.getCause());
                Databackbone.getinstance().showAlsertBox(Splash.this, "Error", "Error Connecting To Server Error " + t.getMessage());

                //DeactivateRider();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == 0) {


            Intent i = new Intent(Splash.this, activity_mapview.class);
            Splash.this.startActivity(i);
            finish();

        } else {
            Databackbone.getinstance().showAlsertBox(Splash.this, "Error", "Please give permission for location");
        }
    }

}