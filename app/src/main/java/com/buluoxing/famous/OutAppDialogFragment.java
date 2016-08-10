package com.buluoxing.famous;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

// 退出 APP Activity -  改写 DialogFragment （解除绑定微信 待改写）
public class OutAppDialogFragment extends DialogFragment {

    private View     view;
    private TextView message;
    private TextView yes;
    private TextView cancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.dialogfragment_out_app, container);

        message = (TextView) view.findViewById(R.id.message);
        yes = (TextView) view.findViewById(R.id.yes);
        cancel = (TextView) view.findViewById(R.id.cancel);

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();


        message.setText("退出部落星吗？");

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if ( getActivity()!= null ){
                    getActivity().finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


}
