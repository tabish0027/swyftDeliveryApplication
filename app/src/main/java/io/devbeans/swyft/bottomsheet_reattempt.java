package io.devbeans.swyft;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class bottomsheet_reattempt extends BottomSheetDialogFragment {
    ImageView btn_close;
    ConstraintLayout btn_order_morning,btn_order_evening;
    Button btn_submit;
    boolean check_btn_order_morning=false,check_btn_order_evening=false;
    public bottomsheet_reattempt() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottomsheet_reattempt, container, false);
        btn_close = v.findViewById(R.id.btn_close);
        btn_order_morning = v.findViewById(R.id.btn_morning);
        btn_order_evening = v.findViewById(R.id.btn_evening);

        btn_close = v.findViewById(R.id.btn_close);
        btn_order_morning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_order_morning.setBackgroundResource(R.drawable.round_bottom_sheet_button_back);
                btn_order_evening.setBackgroundResource(R.drawable.round_bottom_sheet_button_back_unselected);
                check_btn_order_morning = true;
                check_btn_order_evening = false;
            }
        });
        btn_order_evening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_order_morning.setBackgroundResource(R.drawable.round_bottom_sheet_button_back_unselected);
                btn_order_evening.setBackgroundResource(R.drawable.round_bottom_sheet_button_back);
                check_btn_order_morning = false;
                check_btn_order_evening = true;
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomsheet_reattempt.this.dismiss();
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomsheet_reattempt.this.dismiss();
            }
        });
        return v;
    }
}
