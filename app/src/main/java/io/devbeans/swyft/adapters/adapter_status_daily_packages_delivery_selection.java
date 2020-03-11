package io.devbeans.swyft.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.devbeans.swyft.R;
import io.devbeans.swyft.data_models.model_parcel;

public class adapter_status_daily_packages_delivery_selection extends RecyclerView.Adapter<adapter_status_daily_packages_delivery_selection.status_daily_packages_delivery_selection_holder>  {


    public ArrayList<model_parcel> m_orderList =null;
    public Context mContext;

    private static ClickListener clickListener;
    public adapter_status_daily_packages_delivery_selection(ArrayList<model_parcel> orderList, Context context) {
        this.m_orderList = orderList;
        this.mContext = context;
    }


    @Override
    public status_daily_packages_delivery_selection_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_item_package_parcel_selection, parent, false);

        return new status_daily_packages_delivery_selection_holder(view);
    }

    @Override
    public int getItemCount() {
        int size= m_orderList.size();
        return  size;
    }





    @Override
    public void onBindViewHolder(@NonNull status_daily_packages_delivery_selection_holder holder, final int position) {
        final model_parcel order = m_orderList.get(position);

        

        holder.tx_parcel_Price.setText(Float.toString(order.ammount));
        holder.tx_parcel_description.setText(order.Description);
        holder.tx_parcel_id.setText(order.Parcelid);
        holder.cb_parcel_selected.setChecked(order.selected);

    }
    public void setOnItemClickListener(ClickListener clickListener) {
        adapter_status_daily_packages_delivery_selection.clickListener = clickListener;
    }

    public class status_daily_packages_delivery_selection_holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{



        public CheckBox cb_parcel_selected ;
        public TextView tx_parcel_id ;
        public TextView tx_parcel_description ;
        public TextView tx_parcel_Price ;
          public status_daily_packages_delivery_selection_holder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

              cb_parcel_selected =itemView.findViewById(R.id.cb_parcel_selected); ;
              tx_parcel_id =itemView.findViewById(R.id.tx_parcel_id);
              tx_parcel_description =itemView.findViewById(R.id.tx_parcel_description);
              tx_parcel_Price =itemView.findViewById(R.id.tx_parcel_Price);
              cb_parcel_selected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(getAdapterPosition(), v,true);

                }
            });


        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v,false);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v,false);
            return false;
        }
    }

    public interface ClickListener {
        void onItemClick(int position, View v, Boolean check);
        void onItemLongClick(int position, View v, Boolean check);
    }
    public void update_list(){
        notifyDataSetChanged();
    }
}
