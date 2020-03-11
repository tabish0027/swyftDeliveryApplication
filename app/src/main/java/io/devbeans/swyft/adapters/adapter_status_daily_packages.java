package io.devbeans.swyft.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import io.devbeans.swyft.Databackbone;
import io.devbeans.swyft.R;
import io.devbeans.swyft.data_models.model_daily_package_item;

public class adapter_status_daily_packages extends RecyclerView.Adapter<adapter_status_daily_packages.model_order_daily_item_holder> {


    public ArrayList<model_daily_package_item> m_orderList =null;
    public Context mContext;

    private static ClickListener clickListener;
    public adapter_status_daily_packages(ArrayList<model_daily_package_item> orderList, Context context) {
        this.m_orderList = orderList;
        this.mContext = context;
    }



    @Override
    public model_order_daily_item_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_item_today_package_order, parent, false);

        return new model_order_daily_item_holder(view);
    }

    @Override
    public int getItemCount() {
        int size= m_orderList.size();
        return  size;
    }





    @Override
    public void onBindViewHolder(@NonNull model_order_daily_item_holder holder, final int position) {
        final model_daily_package_item order = m_orderList.get(position);


        holder.mb_name.setText(order.mb_name);
        holder.mb_address.setText(order.mb_address);
        holder.mb_distance.setText(order.mb_distance);
        holder.mb_zone.setText(order.mb_zone);
        if(order.status)
        {
            holder.mb_parcel_type.setImageResource(R.drawable.icon_circle_deliverd);
            holder.parcel_type_bottom_bar.setBackgroundColor(Color.parseColor("#90703090"));
            holder.mb_parcel_type_background.setBackgroundResource(R.drawable.round_daily_package_active);
            if(!Databackbone.getinstance().rider.getUser().getType().equalsIgnoreCase("delivery")) {

                holder.btn_navigation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(mContext)
                                .setTitle("Navigation Request")
                                .setMessage("Activate Navigation for " + m_orderList.get(position).mb_name)

                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        Offlice_Activity(m_orderList.get(position).m_location);

                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })

                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });
            }else{
                holder.btn_navigation.setVisibility(View.INVISIBLE);
            }





        }
        else
        {
            holder.mb_parcel_type.setImageResource(R.drawable.icon_circle_reattempt);
            holder.parcel_type_bottom_bar.setBackgroundColor(Color.parseColor("#90f15b22"));
            holder.mb_parcel_type_background.setBackgroundResource(R.drawable.round_daily_package_deactive);
            holder.btn_navigation.setVisibility(View.INVISIBLE);
        }
    }
    public void setOnItemClickListener(ClickListener clickListener) {
        adapter_status_daily_packages.clickListener = clickListener;
    }

    public class model_order_daily_item_holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{



        public TextView mb_name ;
        public TextView mb_address ;
        public TextView mb_distance ;
        public TextView mb_zone ;
        public ImageView mb_parcel_type;
        public LinearLayout parcel_type_bottom_bar;
        public ConstraintLayout mb_parcel_type_background;
        public ImageView btn_navigation,btn_activate;
        public model_order_daily_item_holder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mb_name =itemView.findViewById(R.id.mb_name); ;
            mb_address =itemView.findViewById(R.id.mb_address);
            mb_distance =itemView.findViewById(R.id.mb_distance);
            mb_zone =itemView.findViewById(R.id.mb_zone);
            mb_parcel_type =itemView.findViewById(R.id.parcel_type);
            parcel_type_bottom_bar =itemView.findViewById(R.id.parcel_type_bottom_bar);
            mb_parcel_type_background =itemView.findViewById(R.id.parcel_type_background);
            btn_navigation =itemView.findViewById(R.id.btn_navigation);
            btn_activate =itemView.findViewById(R.id.btn_activate);
            btn_activate.setOnClickListener(new View.OnClickListener() {
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
    public void Offlice_Activity(LatLng location){
        String location_to_string = Double.toString(location.latitude) + ","+Double.toString(location.longitude);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr="+location_to_string));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.getApplicationContext().startActivity(intent);
    }
    public interface ClickListener {
        void onItemClick(int position, View v,Boolean check);
        void onItemLongClick(int position, View v,Boolean check);
    }
    public void update_list(){
        notifyDataSetChanged();
    }
}
