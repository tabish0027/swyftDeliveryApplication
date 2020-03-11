package io.devbeans.swyft.data_models;

public class model_wallets_order {
   public String mb_order_id = "";
   public String mb_date = "";
   public String mb_time = "";
   public String mb_price = "";

   public model_wallets_order(String mb_order_id, String mb_date, String mb_time, String mb_price) {
      this.mb_order_id = mb_order_id;
      this.mb_date = mb_date;
      this.mb_time = mb_time;
      this.mb_price = mb_price;
   }
}
