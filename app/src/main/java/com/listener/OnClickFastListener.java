package com.listener;

import android.view.View;

import static android.view.View.*;

/**
 * Created by Administrator on 2016/7/27 0027.
 *
 *  重复点击  的监听类
 */
public abstract class OnClickFastListener implements OnClickListener {

    // 防止快速点击默认等待时长为1000ms
    private long DELAY_TIME = 1000;
    private static long lastClickTime;

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < DELAY_TIME) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 快速点击事件回调方法
     * @param v
     */
    public abstract void onFastClick(View v);

    @Override
    public void onClick(View v) {
        // 判断当前点击事件与前一次点击事件时间间隔是否小于阙值
        if (isFastDoubleClick()) {
            return;
        }

        onFastClick(v);
    }



    /**
     * 设置默认快速点击事件时间间隔
     * @param delay_time
     * @return
     */
    public OnClickFastListener setLastClickTime(long delay_time) {

        this.DELAY_TIME = delay_time;

        return this;
    }
}
