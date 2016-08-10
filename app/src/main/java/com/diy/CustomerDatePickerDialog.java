package com.diy;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

/**
 * Created by Administrator on 2016/7/7 0007.
 * 自定义 日历 显示 只显示 年 月 （不显示 日 ）
 */
public class CustomerDatePickerDialog extends DatePickerDialog {

    public CustomerDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
        this.setTitle(year + "年" + (monthOfYear + 1) + "月");
        ((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
    }

    // 更改 标题 设置
    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
        this.setTitle(year + "年" + (month + 1) + "月");

    }
}
