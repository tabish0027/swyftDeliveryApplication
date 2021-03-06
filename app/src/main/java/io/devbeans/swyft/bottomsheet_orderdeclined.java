package io.devbeans.swyft;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class bottomsheet_orderdeclined extends BottomSheetDialogFragment {

    ConstraintLayout btn_order_canceled,btn_order_diclined;
    ImageView reattempt_image, cancel_image;
    TextView reattept_text, cancel_text;
    Button btn_submit;
    boolean check_btn_order_canceled=false,check_btn_order_diclined=false;

    ImageView btn_close;
    public bottomsheet_orderdeclined() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottomsheet_orderdeclined, container, false);
        btn_order_canceled = v.findViewById(R.id.btn_order_canceled);
        btn_order_diclined = v.findViewById(R.id.btn_order_declined);
        reattempt_image = v.findViewById(R.id.imageView142);
        cancel_image = v.findViewById(R.id.imageView14);
        reattept_text = v.findViewById(R.id.textView352);
        cancel_text = v.findViewById(R.id.textView35);
        btn_close = v.findViewById(R.id.btn_close);
        btn_submit = v.findViewById(R.id.btn_submit);
        btn_submit.setEnabled(false);
        btn_submit.setBackgroundResource(R.drawable.drawer_round_disable);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomsheet_orderdeclined.this.dismiss();
            }
        });
        btn_order_canceled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_order_canceled.setBackgroundResource(R.drawable.round_bottom_sheet_button_back_filled);
                btn_order_diclined.setBackgroundResource(R.drawable.round_bottom_sheet_button_back_unselected);
                reattempt_image.setImageResource(R.drawable.icon_refreash_dark);
                reattept_text.setTextColor(getResources().getColor(R.color.black));
                cancel_image.setImageResource(R.drawable.icon_cross_white);
                cancel_text.setTextColor(getResources().getColor(R.color.white));
                check_btn_order_canceled = true;
                check_btn_order_diclined = false;
                btn_submit.setEnabled(true);
                btn_submit.setBackgroundResource(R.drawable.drawer_round);
            }
        });
        btn_order_diclined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_order_canceled.setBackgroundResource(R.drawable.round_bottom_sheet_button_back_unselected);
                btn_order_diclined.setBackgroundResource(R.drawable.round_bottom_sheet_button_back_filled);
                reattempt_image.setImageResource(R.drawable.icon_refreash);
                reattept_text.setTextColor(getResources().getColor(R.color.white));
                cancel_image.setImageResource(R.drawable.icon_cross);
                cancel_text.setTextColor(getResources().getColor(R.color.black));
                check_btn_order_canceled = false;
                check_btn_order_diclined = true;
                btn_submit.setEnabled(true);
                btn_submit.setBackgroundResource(R.drawable.drawer_round);
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_btn_order_canceled){
                    Databackbone.getinstance().not_delivered_reason = "returned";
                    Intent  declined = new Intent(bottomsheet_orderdeclined.this.getActivity(), activity_delivery_status.class);
                    declined.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    declined.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    getActivity().startActivity(declined);
                    bottomsheet_orderdeclined.this.dismiss();
                }else if(check_btn_order_diclined)
                {
                    Databackbone.getinstance().not_delivered_reason = "reattempt_delivered";
                    Intent  declined = new Intent(bottomsheet_orderdeclined.this.getActivity(), activity_delivery_status.class);
                    declined.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    declined.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    getActivity().startActivity(declined);
                    bottomsheet_orderdeclined.this.dismiss();
                }else{
                     bottomsheet_orderdeclined.this.dismiss();
                }

            }
        });
        return v;
    }
}
