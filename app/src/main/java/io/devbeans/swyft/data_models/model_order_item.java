package io.devbeans.swyft.data_models;

public class model_order_item {
   public String mb_order_id = "";
   public String mb_date = "";
   public String mb_time = "";
   public String mb_type = "";
   public String mb_batch_id = "";

   public model_order_item(String mb_order_id, String mb_date, String mb_time, String mb_type) {
      this.mb_order_id = mb_order_id;
      this.mb_date = mb_date;
      this.mb_time = mb_time;
      this.mb_type = mb_type;
   }
}
