package io.devbeans.swyft.adapters;

import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.devbeans.swyft.R;
import io.devbeans.swyft.data_models.model_order_item;

public class adapter_status_packages_list extends RecyclerView.Adapter<adapter_status_packages_list.model_order_item_holder> {

    // List to store all the contact details
    public ArrayList<model_order_item> m_orderList =null;
    public Context mContext;

    // Counstructor for the Class
    public adapter_status_packages_list(ArrayList<model_order_item> orderList, Context context) {
        this.m_orderList = orderList;
        this.mContext = context;
    }


    @Override
    public model_order_item_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_item_package_order_status, parent, false);

        return new model_order_item_holder(view);
    }

    @Override
    public int getItemCount() {
        int size= m_orderList.size();
        return  size;
    }



    // This method is called when binding the data to the views being created in RecyclerView

    @Override
    public void onBindViewHolder(@NonNull model_order_item_holder holder, final int position) {
        final model_order_item order = m_orderList.get(position);

        // Set the data to the views here
        holder.setmb_order_id(order.mb_order_id);
        holder.setmb_date(order.mb_date);
        holder.setmb_time(order.mb_time);
        holder.setmb_type(order.mb_type);

        // You can set click listners to indvidual items in the viewholder here
        // make sure you pass down the listner or make the Data members of the viewHolder public

    }

    // This is your ViewHolder class that helps to populate data to the view
    public class model_order_item_holder extends RecyclerView.ViewHolder {


        public TextView mb_order_id;
        public TextView mb_date ;
        public TextView mb_time ;
        public ImageView mb_type ;
        public model_order_item_holder(View itemView) {
            super(itemView);

            mb_order_id = itemView.findViewById(R.id.parcel_id);
            mb_date = itemView.findViewById(R.id.parcel_date);
            mb_time = itemView.findViewById(R.id.parcel_time);
            mb_type = itemView.findViewById(R.id.parcel_type);
        }

        public void setmb_order_id(String data) {
            mb_order_id.setText(data);
        }

        public void setmb_date(String data) {
            mb_date.setText(data);
        }
        public void setmb_time(String data) {
            mb_time.setText(data);
        }

        public void setmb_type(String data) {
            if(data.equals("delivered")) mb_type.setImageResource(R.drawable.icon_circle_deliverd);
            else if(data.equals("declined")) mb_type.setImageResource(R.drawable.icon_circle_declined);
            else if(data.equals("reattempt")) mb_type.setImageResource(R.drawable.icon_circle_reattempt);

        }
    }
    public void update_list(){
        notifyDataSetChanged();
    }
}
