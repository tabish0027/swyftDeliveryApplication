package io.devbeans.swyft.data_models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.devbeans.swyft.interface_retrofit_delivery.Parcel;

public class model_daily_package_item {
   public String mb_task_id = "";
   public String mb_name = "";
   public String mb_address = "";
   public String mb_distance = "";
   public String mb_zone = "";
   public Boolean status = false;
   public LatLng m_location = null;
   public int m_remaining_parcels_to_scan = 0;
   public int m_remaining_parcels_Addresses = 0;
   List<Parcel> delivery_parcels;
   public model_daily_package_item(String mb_task_id, String mb_name, String mb_address, String mb_distance, String mb_zone, Boolean status, LatLng location, int remaining_parcels_to_scan, List<Parcel> mparcels) {
      this.mb_task_id = mb_task_id;
      this.mb_name = mb_name;
      this.mb_address = mb_address;
      this.mb_distance = mb_distance;
      this.mb_zone = mb_zone;
      this.status = status;
      this.m_location = location;
      this.m_remaining_parcels_to_scan = remaining_parcels_to_scan;
      this.delivery_parcels = mparcels;
   }

   public model_daily_package_item(String mb_task_id, String mb_name, String mb_address, String mb_distance, String mb_zone,Boolean status,LatLng location,int remaining_parcels_to_scan) {
      this.mb_task_id = mb_task_id;
      this.mb_name = mb_name;
      this.mb_address = mb_address;
      this.mb_distance = mb_distance;
      this.mb_zone = mb_zone;
      this.status = status;
      this.m_location = location;
      this.m_remaining_parcels_to_scan = remaining_parcels_to_scan;
   }
   public model_daily_package_item(String mb_task_id, String mb_name, String mb_address, String mb_distance, String mb_zone,Boolean status,LatLng location,int remaining_parcels_to_scan,int m_remaining_parcels_Addresses) {
      this.mb_task_id = mb_task_id;
      this.mb_name = mb_name;
      this.mb_address = mb_address;
      this.mb_distance = mb_distance;
      this.mb_zone = mb_zone;
      this.status = status;
      this.m_location = location;
      this.m_remaining_parcels_to_scan = remaining_parcels_to_scan;
      this.m_remaining_parcels_Addresses = m_remaining_parcels_Addresses;
   }

}
