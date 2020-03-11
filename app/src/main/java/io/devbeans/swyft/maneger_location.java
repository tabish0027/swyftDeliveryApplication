package io.devbeans.swyft;



import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;


import android.util.Log;
import android.widget.Toast;


import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.Timer;
import java.util.TimerTask;


public class maneger_location extends Service implements LocationListener {
    /**
     * indicates how to behave if the service is killed
     */
    int mStartMode;

    /**
     * interface for clients that bind
     */
    IBinder mBinder;

    /**
     * indicates whether onRebind should be used
     */
    boolean mAllowRebind;

    /**
     * Called when the service is being created.
     */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATE = 10;

    private static final long MIN_TIME_BW_UPDATE = 1000 * 60 * 1;

    protected LocationManager locationManager;
    Handler handler = new Handler(Looper.getMainLooper());


    private static final String TAG = "BOOMBOOMTESTGPS";
    public static LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    String location_sip = "";
    String location_sip_name = "";


    Location mLastLocation;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            // Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            //showToast("Location changes");
            Databackbone.getinstance().current_location = new LatLng(location.getLatitude(),location.getLongitude()) ;
            OnLocationChangeCheckProximity(location);
       }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }


    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


    Thread thr;

    @Override
    public void onCreate() {
        initializeLocationManager();

        //activateGeoFence(getApplicationContext());

    }

    public void OnLocationChangeCheckProximity(final Location location) {
        // Log.e(TAG, "onLocationChanged: " + location);
        String protocol = Databackbone.getinstance().GetData("swift_work_progress",getApplicationContext());
        if(protocol.equals("none"))
            return;
        else
            Databackbone.getinstance().updateDistance(getApplicationContext());
        /*
        handler.post(new Runnable() {

            @Override
            public void run() {
                String message = "Location=" + location.getLatitude() + "," + location.getLongitude();

                //Toast.makeText(server_location_update.this.getApplicationContext(),"Location Received " + message,Toast.LENGTH_SHORT).show();
            }
        });
        mLastLocation = location;
        Location loc2 = new Location("");

        loc2.setLatitude(BackBoneDataFlow.getInstance().Geofence_lat);
        loc2.setLongitude(BackBoneDataFlow.getInstance().Geofence_lon);


        final float distanceInMeters = location.distanceTo(loc2);
        getGeoFenceCordinatesAndLastState();
        if (BackBoneDataFlow.getInstance().radius != 0)


            if (distanceInMeters < BackBoneDataFlow.getInstance().radius - 10 && !BackBoneDataFlow.getInstance().Geofence_last_state.equals("ENTER")) {
                String message = "Location=" + location.getLatitude() + "," + location.getLongitude() + ":State=Enter:Distance(m)=" + distanceInMeters;
                setGeoFenceCurrentState("ENTER");
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(server_location_update.this.getApplicationContext(), "Entered Geo Location", Toast.LENGTH_SHORT).show();

                        String detailes = BackBoneDataFlow.getInstance().Geofence_lat + "," + BackBoneDataFlow.getInstance().Geofence_lon + "," + BackBoneDataFlow.getInstance().radius + "," + distanceInMeters;
                        //Toast.makeText(server_location_update.this.getApplicationContext(),detailes,Toast.LENGTH_SHORT).show();
                    }
                });
                UpdateLocationWithCordinateAndTime(message);
            } else if (distanceInMeters > BackBoneDataFlow.getInstance().radius + 10 && !BackBoneDataFlow.getInstance().Geofence_last_state.equals("EXIT")) {
                setGeoFenceCurrentState("EXIT");
                String message = "Location=" + location.getLatitude() + "," + location.getLongitude() + ":State=Exit:Distance(m)=" + distanceInMeters;

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(server_location_update.this.getApplicationContext(), "Exit Geo Location", Toast.LENGTH_SHORT).show();

                        //String detailes = BackBoneDataFlow.getInstance().Geofence_lat + "," + BackBoneDataFlow.getInstance().Geofence_lon + "," + BackBoneDataFlow.getInstance().radius+","+distanceInMeters;
                        // Toast.makeText(server_location_update.this.getApplicationContext(),detailes,Toast.LENGTH_SHORT).show();

                    }
                });

            }

        handler.post(new Runnable() {

            @Override
            public void run() {
                // Toast.makeText(server_location_update.this.getApplicationContext(),"Updating Location On Server",Toast.LENGTH_SHORT).show();
            }
        });

        */
    }

    private void initializeLocationManager() {
        //Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);


            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 50, mLocationListeners[0]);

                }
                if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 50, mLocationListeners[0]);

                }


            }

        }
    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //showToast("service Started");
        try {
            thr = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {

                        try {

                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {

                                }
                            }, 10000);
                            Thread.sleep(30000);


                        } catch (Exception i) {
                            showToast("Exception Generated inner");
                            //thr.stop();

                        }
                    }
                }
            });
            thr.start();
        } catch (Exception i) {
            //showToast("Exception Generated");
            return mStartMode;
        }
        return mStartMode;

    }

    void activateGeoFence(Context context) {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //showToast("Location Maneger Set");
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 50, mLocationListeners[0]);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 50, mLocationListeners[1]);
        } else {
        }

    }
    void showToast(String Message){
        Toast.makeText(getApplicationContext(),Message,Toast.LENGTH_LONG).show();

    }
    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {

    }

    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {

    }


    @Override
    public void onLocationChanged(Location location) {

        System.out.println("working");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }





}
