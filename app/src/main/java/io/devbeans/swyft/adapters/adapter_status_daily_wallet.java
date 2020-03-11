package io.devbeans.swyft.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.devbeans.swyft.R;
import io.devbeans.swyft.data_models.model_wallets_order;

public class adapter_status_daily_wallet extends RecyclerView.Adapter<adapter_status_daily_wallet.model_order_wallet_item_holder> {


    public ArrayList<model_wallets_order> m_orderList =null;
    public Context mContext;


    public adapter_status_daily_wallet(ArrayList<model_wallets_order> orderList, Context context) {
        this.m_orderList = orderList;
        this.mContext = context;
    }


    @Override
    public model_order_wallet_item_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_item_wallet, parent, false);

        return new model_order_wallet_item_holder(view);
    }

    @Override
    public int getItemCount() {
        int size= m_orderList.size();
        return  size;
    }





    @Override
    public void onBindViewHolder(@NonNull model_order_wallet_item_holder holder, final int position) {
        final model_wallets_order order = m_orderList.get(position);


        holder.mb_parcel_id.setText(order.mb_order_id);
        holder.mb_time.setText(order.mb_time);
        holder.mb_date.setText(order.mb_date);
        holder.mb_price.setText(order.mb_price);

    }


    public class model_order_wallet_item_holder extends RecyclerView.ViewHolder {



        public TextView mb_parcel_id ;
        public TextView mb_time ;
        public TextView mb_date ;
        public TextView mb_price ;


        public model_order_wallet_item_holder(View itemView) {
            super(itemView);


            mb_parcel_id =itemView.findViewById(R.id.wallet_parcel_id); ;
            mb_time =itemView.findViewById(R.id.parcel_date);
            mb_date =itemView.findViewById(R.id.parcel_time);
            mb_price =itemView.findViewById(R.id.wallet_amount);


        }


    }
    public void update_list(){
        notifyDataSetChanged();
    }
}
