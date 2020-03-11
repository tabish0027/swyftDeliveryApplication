package io.devbeans.swyft.data_models;

public class model_parcel {
   public String mb_task_id = "";
   public String Parcelid = "";
   public String mb_name = "";
   public String mb_address = "";
   public String status = "";
   public float ammount = 0;
   public String Description = "";
   public boolean selected = false;

   public model_parcel(String mb_task_id, String parcelid, String mb_name, String mb_address, String status, Float ammount, String description, boolean selected) {
      this.mb_task_id = mb_task_id;
      Parcelid = parcelid;
      this.mb_name = mb_name;
      this.mb_address = mb_address;
      this.status = status;
      this.ammount = ammount;
      Description = description;
      this.selected = selected;
   }
}
