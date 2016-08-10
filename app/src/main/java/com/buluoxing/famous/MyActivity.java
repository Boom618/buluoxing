package com.buluoxing.famous;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.umeng.analytics.MobclickAgent;
import com.util.AppManager;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by admin on 2016/6/2.
 */
public abstract class MyActivity extends Activity {

    protected Context mainContext;

    protected abstract int getLayout();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());

        // 注册EventBus
//        EventBus.getDefault().register(this);

        AppManager.getAppManager().addActivity(this);

        mainContext = this;
        initSomeDefaultEvent();
    }

    public void goActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    public void initSomeDefaultEvent() {
        View backView = findViewById(R.id.back);
        if (backView != null) {
            backView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    public TextView findTextViewById(int rsd) {
        return (TextView) findViewById(rsd);
    }

    public ImageView findImageViewById(int rsd) {
        return (ImageView) findViewById(rsd);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        EventBus.getDefault().unregister(this);

        AppManager.getAppManager().finishActivity(this);
    }

    // ------------------------------------ APP  修改版-----------


    // 友盟 session的统计
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 显示 时间控件
     *
     * @param view    显示的控件
     * @param maxYear 显示的年数
     * @param type    控件的类型
     */
    public void showControlTime(final TextView view, boolean loop, int maxYear, final String timeType, TimePickerView.Type type) {
        TimePickerView pickerView = new TimePickerView(this, type);

        //控制时间范围
        final Calendar calendar = Calendar.getInstance();
        pickerView.setRange(calendar.get(Calendar.YEAR) - maxYear, calendar.get(Calendar.YEAR));//要在setTime 之前才有效果哦

        pickerView.setTime(new Date());
        // 循环滚动
        pickerView.setCyclic(loop);
        pickerView.setCancelable(true);

        pickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                Date newTime = calendar.getTime();

                // 当前时间 与 选择的时间 对比
                int i = newTime.compareTo(date);
                if (i >= 0) {
                    if (timeType.equals("ALL")) {
                        view.setText(getAllTime(date));

                    } else if (timeType.equals("YMD")) {
                        view.setText(get3Time(date));

                    }else if (timeType.equals("YM")){
                        view.setText(get2Time(date));
                    }else{
                        // 默认显示
                        view.setText(get3Time(date));
                    }
                }else{
                    // 选择的是未来的时间 设定为当前时间
                    if (timeType.equals("ALL")) {
                        view.setText(getAllTime(newTime));

                    } else if (timeType.equals("YMD")) {
                        view.setText(get3Time(newTime));

                    }else if (timeType.equals("YM")){
                        view.setText(get2Time(newTime));
                    }else {
                        view.setText(get3Time(newTime));
                    }
                }
            }


        }
    );

    pickerView.show();


}


    public static String getAllTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    public static String get3Time(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String get2Time(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }

    /**
     *  请求网络 Dialog
     * @param title  提示语
     * @param isCancel  点击屏幕是否 可以取消
     * @return
     */
    protected SweetAlertDialog showHttpDialog(String title,boolean isCancel){
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        pDialog.setCancelable(isCancel);

        return pDialog;
    }
}
