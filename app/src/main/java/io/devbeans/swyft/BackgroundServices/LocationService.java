//package io.devbeans.swyft.BackgroundServices;
//
//import android.app.Service;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.core.widget.NestedScrollView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import io.devbeans.swyft.BuildConfig;
//import io.devbeans.swyft.Databackbone;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.lang.reflect.Type;
//import java.text.DateFormat;
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.TimeZone;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import io.devbeans.swyft.Tracker.GPSTracker;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class LocationService extends Service implements LocationListener {
//
//    boolean isGPSEnable = false;
//    boolean isNetworkEnable = false;
//    double latitude, longitude;
//    LocationManager locationManager;
//    Location location;
//    private Handler mHandler = new Handler();
//    private Timer mTimer = null;
//    public static String str_receiver = "io.devbeans.swyft.BackgroundServices.receiver";
//    Intent intent;
//
//    GPSTracker gpsTracker;
//
//    private Handler ApiHandler = new Handler();
//    private Timer ApiTimer = null;
//    long notify_interval = 3000;
//    long Api_interval = 30000;
//
//    LocationListener mlocListener;
//    LocationManager mlocManager;
//    int count = 0;
//
//    RecyclerView location_history_recycler;
//    NestedScrollView nestedScrollView;
//
//    SharedPreferences sharedpreferences;
//    SharedPreferences.Editor mEditor;
//    public static final String MyPREFERENCES = "MyPrefs";
//    Gson gson = new Gson();
//    DecimalFormat dFormat;
//    String CompressedLatitude;
//    String CompressedLongitude;
//
//    List<com.devbeans.covidtracker.Models.Location> savedLocationList = new ArrayList<>();
//    com.devbeans.covidtracker.Models.Location locationModel = new com.devbeans.covidtracker.Models.Location();
//
//    public LocationService() {
//
//    }
//
////    @Override
////    public int onStartCommand(Intent intent, int flags, int startId) {
////        Toast.makeText(this, " MyService Started", Toast.LENGTH_LONG).show();
////        return START_STICKY;
////    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Databackbone.getinstance().contextapp = getApplicationContext();
//        if (BuildConfig.BUILD_TYPE.equals("debug")) {
//            Databackbone.getinstance().Base_URL = BuildConfig.API_BASE_URL;
//            Databackbone.getinstance().central_retrofit = null;
//            Databackbone.getinstance().getRetrofitbuilder();
//        }
//        dFormat = new DecimalFormat("#.#####");
//        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
//        mEditor = sharedpreferences.edit();
//        mTimer = new Timer();
//        mTimer.schedule(new TimerTaskToGetLocation(), 5, notify_interval);
//        ApiTimer = new Timer();
//        ApiTimer.schedule(new TimerTaskToHitApi(),10000,Api_interval);
//        intent = new Intent(str_receiver);
////        fn_getlocation();
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }
//
//    private void UpdateLocation(){
//
//        PersonModel personModel = new PersonModel();
//        String json = sharedpreferences.getString("PersonData", "");
//        if (json != null){
//            if (!json.equals("")){
//                Type type = new TypeToken<PersonModel>() {}.getType();
//                personModel = gson.fromJson(json, type);
//            }
//        }
//
//        PatchPersonModel patchPersonModel = new PatchPersonModel();
//
//        patchPersonModel.location = savedLocationList;
//
//        String abc = gson.toJson(patchPersonModel);
//
//        covid_api covidapi = Databackbone.getinstance().getRetrofitbuilder().create(covid_api.class);
//
//        Call<String> call = covidapi.patchPeople("application/json", personModel.getId(), patchPersonModel, sharedpreferences.getString("AccessToken", ""));
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                if (response.isSuccessful()) {
//
//                    savedLocationList.clear();
//
//                    String json = gson.toJson(savedLocationList);
//                    mEditor.putString("LocationList", json).apply();
//
//                    Toast.makeText(LocationService.this, "Location Updated To Server", Toast.LENGTH_SHORT).show();
//
//                } else {
//                    try {
//                        JSONObject jObjError = new JSONObject(response.errorBody().toString());
//                        if (jObjError.getJSONObject("error").getString("statusCode").equals("401") || jObjError.getJSONObject("error").getString("statusCode").equals("404")) {
//                            //sharedpreferences must be removed
//                            mEditor.clear().apply();
//                            stopService(new Intent(LocationService.this, LocationService.class));
//                        }else {
////                        Toast.makeText(LocationService.this, response.message(), Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                System.out.println(t.getCause());
////                Toast.makeText(LocationService.this, "Api Hitting Failure", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
//
//    private void fn_getlocation(){
//
//        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
//        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//        if (!isGPSEnable && !isNetworkEnable){
//            Toast.makeText(LocationService.this, "Network or GPS is disabled", Toast.LENGTH_SHORT).show();
//        }else {
//            if (isGPSEnable){
//                gpsTracker = new GPSTracker(getApplicationContext());
//
//                if (String.valueOf(gpsTracker.getLatitude()).length() > 4){
//                    CompressedLatitude = String.valueOf(Double.valueOf(dFormat.format(gpsTracker.getLatitude())));
//                }else {
//                    CompressedLatitude = String.valueOf(gpsTracker.getLatitude());
//                }
//                if (String.valueOf(gpsTracker.getLongitude()).length() > 4){
//                    CompressedLongitude = String.valueOf(Double.valueOf(dFormat.format(gpsTracker.getLongitude())));
//                }else {
//                    CompressedLongitude = String.valueOf(gpsTracker.getLongitude());
//                }
//
//                TimeZone tz = TimeZone.getTimeZone("UTC");
//                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
//                df.setTimeZone(tz);
//                String currentTime = df.format(new Date());
//
//                if (savedLocationList != null && !savedLocationList.isEmpty()){
//
//                    boolean match = false;
//                    int index = 0;
//
//                    for (int i = 0; i < savedLocationList.size(); i++){
//                        if (savedLocationList.get(i).getLong().equals(CompressedLongitude) && savedLocationList.get(i).getLat().equals(CompressedLatitude)) {
//                            match = true;
//                            count = savedLocationList.get(i).getSync();
//                            index = i;
//                            break;
//                        }
//                    }
//
//                    if (match) {
//                        locationModel.setLat(CompressedLatitude);
//                        locationModel.setAltitude(String.valueOf(gpsTracker.getLocation().getAltitude()));
//                        locationModel.setLong(CompressedLongitude);
//                        locationModel.setStarttime(savedLocationList.get(index).getStarttime());
//                        locationModel.setSync(count++);
//                        locationModel.setSyncToLedger("false");
//                        locationModel.setDate(savedLocationList.get(index).getStarttime());
//
//                        savedLocationList.set(index, locationModel);
//                        String list = gson.toJson(savedLocationList);
//                        mEditor.putString("LocationList", list).apply();
//
//                    } else {
//
//                        locationModel.setLat(CompressedLatitude);
//                        locationModel.setAltitude(String.valueOf(gpsTracker.getLocation().getAltitude()));
//                        locationModel.setLong(CompressedLongitude);
//                        locationModel.setStarttime(savedLocationList.get(index).getStarttime());
//                        locationModel.setEndtime(currentTime);
//                        locationModel.setSync(count++);
//                        locationModel.setSyncToLedger("false");
//                        locationModel.setDate(savedLocationList.get(index).getStarttime());
//
//                        savedLocationList.set(index, locationModel);
//
//                        locationModel.setLat(CompressedLatitude);
//                        locationModel.setAltitude(String.valueOf(gpsTracker.getLocation().getAltitude()));
//                        locationModel.setLong(CompressedLongitude);
//                        locationModel.setStarttime(currentTime);
//                        locationModel.setSync(0);
//                        locationModel.setSyncToLedger("false");
//                        locationModel.setDate(currentTime);
//                        savedLocationList.add(locationModel);
//
//                        String locationList = gson.toJson(savedLocationList);
//                        mEditor.putString("LocationList", locationList).apply();
//                    }
//
//                }else {
//
//                    locationModel.setLat(CompressedLatitude);
//                    locationModel.setAltitude(String.valueOf(gpsTracker.getLocation().getAltitude()));
//                    locationModel.setLong(CompressedLongitude);
//                    locationModel.setStarttime(currentTime);
//                    locationModel.setSync(0);
//                    locationModel.setSyncToLedger("false");
//                    locationModel.setDate(currentTime);
//                    savedLocationList.add(locationModel);
//
//                    String locationList = gson.toJson(savedLocationList);
//                    mEditor.putString("LocationList", locationList).apply();
//
//                }
//
//                fn_update(gpsTracker.getLocation());
//            }else if (isNetworkEnable){
//                gpsTracker = new GPSTracker(getApplicationContext());
//
//                if (String.valueOf(gpsTracker.getLatitude()).length() > 4){
//                    CompressedLatitude = String.valueOf(Double.valueOf(dFormat.format(gpsTracker.getLatitude())));
//                }else {
//                    CompressedLatitude = String.valueOf(gpsTracker.getLatitude());
//                }
//                if (String.valueOf(gpsTracker.getLongitude()).length() > 4){
//                    CompressedLongitude = String.valueOf(Double.valueOf(dFormat.format(gpsTracker.getLongitude())));
//                }else {
//                    CompressedLongitude = String.valueOf(gpsTracker.getLongitude());
//                }
//
//                TimeZone tz = TimeZone.getTimeZone("UTC");
//                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
//                df.setTimeZone(tz);
//                String currentTime = df.format(new Date());
//
//                if (savedLocationList != null && !savedLocationList.isEmpty()){
//
//                    boolean match = false;
//                    int index = 0;
//
//                    for (int i = 0; i < savedLocationList.size(); i++){
//                        if (savedLocationList.get(i).getLong().equals(CompressedLongitude) && savedLocationList.get(i).getLat().equals(CompressedLatitude)) {
//                            match = true;
//                            count = savedLocationList.get(i).getSync();
//                            index = i;
//                            break;
//                        }
//                    }
//
//                    if (match) {
//                        locationModel.setLat(CompressedLatitude);
//                        locationModel.setAltitude(String.valueOf(gpsTracker.getLocation().getAltitude()));
//                        locationModel.setLong(CompressedLongitude);
//                        locationModel.setStarttime(savedLocationList.get(index).getStarttime());
//                        locationModel.setSync(count++);
//                        locationModel.setSyncToLedger("false");
//                        locationModel.setDate(savedLocationList.get(index).getStarttime());
//
//                        savedLocationList.set(index, locationModel);
//                        String list = gson.toJson(savedLocationList);
//                        mEditor.putString("LocationList", list).apply();
//
//                    } else {
//
//                        locationModel.setLat(CompressedLatitude);
//                        locationModel.setAltitude(String.valueOf(gpsTracker.getLocation().getAltitude()));
//                        locationModel.setLong(CompressedLongitude);
//                        locationModel.setStarttime(savedLocationList.get(index).getStarttime());
//                        locationModel.setEndtime(currentTime);
//                        locationModel.setSync(count++);
//                        locationModel.setSyncToLedger("false");
//                        locationModel.setDate(savedLocationList.get(index).getStarttime());
//
//                        savedLocationList.set(index, locationModel);
//
//                        locationModel.setLat(CompressedLatitude);
//                        locationModel.setAltitude(String.valueOf(gpsTracker.getLocation().getAltitude()));
//                        locationModel.setLong(CompressedLongitude);
//                        locationModel.setStarttime(currentTime);
//                        locationModel.setSync(0);
//                        locationModel.setSyncToLedger("false");
//                        locationModel.setDate(currentTime);
//                        savedLocationList.add(locationModel);
//
//                        String locationList = gson.toJson(savedLocationList);
//                        mEditor.putString("LocationList", locationList).apply();
//                    }
//
//                }else {
//
//                    locationModel.setLat(CompressedLatitude);
//                    locationModel.setAltitude(String.valueOf(gpsTracker.getLocation().getAltitude()));
//                    locationModel.setLong(CompressedLongitude);
//                    locationModel.setStarttime(currentTime);
//                    locationModel.setSync(0);
//                    locationModel.setSyncToLedger("false");
//                    locationModel.setDate(currentTime);
//                    savedLocationList.add(locationModel);
//
//                    String locationList = gson.toJson(savedLocationList);
//                    mEditor.putString("LocationList", locationList).apply();
//
//                }
//
//                fn_update(gpsTracker.getLocation());
//            }
//        }
//
//    }
//
//    private class TimerTaskToGetLocation extends TimerTask {
//        @Override
//        public void run() {
//
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    fn_getlocation();
//                }
//            });
//
//        }
//    }
//
//    private class TimerTaskToHitApi extends TimerTask {
//        @Override
//        public void run() {
//
//            ApiHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (savedLocationList != null){
//                        if (!savedLocationList.isEmpty()){
//                            for (int i = 0; i < savedLocationList.size(); i++){
//                                if (savedLocationList.get(i).getEndtime() == null || savedLocationList.get(i).getEndtime().isEmpty()){
//                                    savedLocationList.remove(i);
//                                }
//                            }
//                            if (!savedLocationList.isEmpty()){
//                                UpdateLocation();
//                            }
//                        }
//                    }
//                }
//            });
//
//        }
//    }
//
//    private void fn_update(Location location){
//
//        intent.putExtra("latutide",location.getLatitude()+"");
//        intent.putExtra("longitude",location.getLongitude()+"");
//        sendBroadcast(intent);
//    }
//
//
//}