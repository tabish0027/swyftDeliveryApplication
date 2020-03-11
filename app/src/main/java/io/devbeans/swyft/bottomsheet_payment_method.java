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

public class bottomsheet_payment_method extends BottomSheetDialogFragment {

    Button btn_submit;
    ConstraintLayout btn_payment_method_jazzcash,btn_payment_method_card,btn_payment_method_cod,btn_payment_method_easy_paisa;;
    Boolean check_btn_payment_method_jazzcash = false;
    Boolean check_btn_payment_method_card = false;
    Boolean check_btn_payment_method_cod = false;
    Boolean check_btn_payment_method_easy_paisa = false;


    ImageView btn_close;
    public bottomsheet_payment_method() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottomsheet_payment_method, container, false);
        btn_submit = v.findViewById(R.id.btn_submit);
        btn_close = v.findViewById(R.id.btn_close);

        btn_payment_method_jazzcash = v.findViewById(R.id.btn_payment_method_jazzcash);
        btn_payment_method_card = v.findViewById(R.id.btn_payment_method_card);
        btn_payment_method_cod = v.findViewById(R.id.btn_payment_method_cod);
        btn_payment_method_easy_paisa = v.findViewById(R.id.btn_payment_method_easy_paisa);


        btn_payment_method_cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_btn_payment_method_cod){
                    check_btn_payment_method_cod = false;
                    btn_payment_method_cod.setBackgroundResource(R.drawable.round_bottom_sheet_button_back_unselected);
                }else
                {
                    check_btn_payment_method_cod = true;
                    btn_payment_method_cod.setBackgroundResource(R.drawable.round_bottom_sheet_button_back);

                }
            }
        });
        btn_payment_method_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_btn_payment_method_card){
                    check_btn_payment_method_card= false;
                    btn_payment_method_card.setBackgroundResource(R.drawable.round_bottom_sheet_button_back_unselected);
                }else
                {
                    check_btn_payment_method_card = true;
                    btn_payment_method_card.setBackgroundResource(R.drawable.round_bottom_sheet_button_back);

                }
            }
        });

        btn_payment_method_jazzcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_btn_payment_method_jazzcash){
                    check_btn_payment_method_jazzcash = false;
                    btn_payment_method_jazzcash.setBackgroundResource(R.drawable.round_bottom_sheet_button_back_unselected);
                }else
                {
                    check_btn_payment_method_jazzcash = true;
                    btn_payment_method_jazzcash.setBackgroundResource(R.drawable.round_bottom_sheet_button_back);

                }
            }
        });

        btn_payment_method_easy_paisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_btn_payment_method_easy_paisa){
                    check_btn_payment_method_easy_paisa = false;
                    btn_payment_method_easy_paisa.setBackgroundResource(R.drawable.round_bottom_sheet_button_back_unselected);
                }else
                {
                    check_btn_payment_method_easy_paisa = true;
                    btn_payment_method_easy_paisa.setBackgroundResource(R.drawable.round_bottom_sheet_button_back);

                }
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomsheet_payment_method.this.dismiss();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomsheet_payment_method.this.dismiss();
            }
        });
        return v;
    }
}
