package com.diy;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.buluoxing.famous.R;
import com.buluoxing.famous.api.APIOkHttp;
import com.buluoxing.famous.bean.UserLoginBean;
import com.buluoxing.famous.eventmodle.LoginEvent;
import com.buluoxing.famous.fileload.FileApi;
import com.buluoxing.famous.fileload.FileCallback;
import com.buluoxing.famous.view.FlowLayout;
import com.util.Config;
import com.util.DataManager;
import com.util.T;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by Administrator on 2016/7/15 0015.
 * 测试 类
 */
public class MyDemo extends Activity implements View.OnClickListener {


    TextView time;
    AlertDialog dialog;

    TextView txtProgress;
    HDialogBuilder hDialogBuilder;
    RadioGroup group;

    FlowLayout mFlowLayout ;
    FlowLayout mIsFlow ;

    LayoutInflater mInflater;
    LayoutInflater mInflater1;

    // SpotsDialog  加载 Dialog
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册EventBus
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        setContentView(R.layout.activity_a_demo);


        time = (TextView) findViewById(R.id.id_time);

        findViewById(R.id.id_dialog1).setOnClickListener(this);
        findViewById(R.id.id_dialog2).setOnClickListener(this);
        findViewById(R.id.id_dialog3).setOnClickListener(this);
        findViewById(R.id.id_dialog4).setOnClickListener(this);
        findViewById(R.id.id_dialog5).setOnClickListener(this);
        group = (RadioGroup) findViewById(R.id.id_group);
        final RadioButton ave = (RadioButton) findViewById(R.id.id_ave);
        RadioButton random = (RadioButton) findViewById(R.id.id_random);

        String deviceInfo = getDeviceInfo(this);
        Log.i("TAG", "onCreate: deviceInfo = " + deviceInfo);

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (ave.getId() == checkedId) {

                }
            }
        });

        final String[] mVals = new String[]
                { "Hello", "Android", "Weclome Hi ", "Android", "Weclome", "Button ImageView",
                        "Android", "Weclome Hello", "Button Text", "TextView" };




        mFlowLayout = (FlowLayout) findViewById(R.id.id_flow); //  列表
        mIsFlow = (FlowLayout) findViewById(R.id.id_isFlow); // 选中


        mInflater = LayoutInflater.from(this);
        mInflater1 = LayoutInflater.from(this);

        for (int i = 0; i < mVals.length; i++)
        {

            final TextView tv = (TextView) mInflater.inflate(R.layout.activity_user_kol_text,
                    mFlowLayout, false);
            tv.setText(mVals[i]);
            tv.setTag(i);
            mFlowLayout.addView(tv);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //tv.setTextColor(Color.GREEN);
                    if (isClick){
                        //tv.setTextColor(Color.GREEN);
                        isClick = false;
                    }else {
                        isClick = true;
                        //tv.setTextColor(Color.parseColor("#5BC4ED"));
                    }
                    int num  = (int) tv.getTag();
                    String string = tv.getText().toString();
                    ArrayList<String> list = new ArrayList<>();
                    list.add(string);
                    // 显示 选中 领域
                    showText(num,string ,list);
                }
            });
        }





    }

    boolean isClick = true;


    private void showText(int id ,String text , ArrayList list){
        int childCount = mIsFlow.getChildCount();
        if (childCount >1 ){
            Toast.makeText(MyDemo.this,"只能选中两个领域" + childCount,Toast.LENGTH_SHORT).show();
            return;
        }
        for ( int i = 0; i < list.size(); i++ ){
            final TextView tv = (TextView) mInflater1.inflate(R.layout.activity_user_kol_is_text,
                    mIsFlow, false);
            tv.setText(text);
            //tv.setTag(i);
            tv.setTextColor(Color.GREEN);
            mIsFlow.addView(tv);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIsFlow.removeView(tv);
                }
            });
        }


    }

//    private onTextClick mClick;
//
//    public interface onTextClick{
//        void isClick();
//    }
//
//    class demo implements MyDemo.onTextClick{
//
//        @Override
//        public void isClick() {
//
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_dialog1:
                SpotsDialog dialog = new SpotsDialog(MyDemo.this);
                dialog.show();
                dialog.dismiss();
//                showBar(v); //  主题
                break;
            case R.id.id_dialog2:
                //new SpotsDialog(this, "Custom message").show();
                checkUpDate(this);
                break;
            case R.id.id_dialog3:
                T.show(MyDemo.this, "SpotsDialog 3 ", 0);
                break;
            case R.id.id_dialog4:
                T.show(MyDemo.this, "postHttp 4 ", 0);
                postHttp();
                break;
            case R.id.id_dialog5:
                T.show(MyDemo.this, "postHttp 5 ", 0);
                postLogin();
//                showControlTime(TimePickerView.Type.YEAR_MONTH);
                break;
        }
    }

    private void showBar(View view) {
        Snackbar snackbar = Snackbar.make(view, " data deleted ", Snackbar.LENGTH_LONG);
        snackbar.setAction("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T.show(MyDemo.this, "删除数据", 0);
            }
        });
        snackbar.show();
    }

    private void showControlTime(TimePickerView.Type type) {
        TimePickerView pickerView = new TimePickerView(this, type);

        //控制时间范围
        final Calendar calendar = Calendar.getInstance();
        pickerView.setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR));//要在setTime 之前才有效果哦

        pickerView.setTime(new Date());
        pickerView.setCyclic(false);
        pickerView.setCancelable(true);

        pickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {

                Date newTime = calendar.getTime();

                // 当前时间 与 选择的时间 对比
                int i = newTime.compareTo(date);
                if (i >= 0) {
                    time.setText("当前选择时间 i =" + i + " " + getTime(date));
                } else {
                    // 选择的是未来的时间 设定为当前时间
                    time.setText("当前选择时间 i =" + i + " " + getTime(newTime));
                }

            }
        });


        pickerView.show();


    }


    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    private void postHttp() {
        //APIOkHttp.userLogin();

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("")
                .setContentText("恭喜您注册成功,可前往个人中心修改昵称和头像")
                .setConfirmText("确认")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event) {
        Toast.makeText(this, "onEvent :" + event.getMessage(), Toast.LENGTH_SHORT).show();
        time.setText("EventBus = ");
        if (event != null) {
            UserLoginBean result = event.getResult();
            UserLoginBean.ResultBean result1 = result.getResult();
            String nickname = result1.getNickname();
            System.out.println("nickname = " + nickname);

        }

    }

    private void postLogin() {
        Toast.makeText(this, "打开相机和相册===", Toast.LENGTH_SHORT).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        View viewDialog = View.inflate(this, R.layout.dialog_bottom_layout, null);
        dialog.setView(viewDialog, 0, 0, 0, 0);

        LinearLayout linearLayout = (LinearLayout) viewDialog.findViewById(R.id.layout_dialog);
        int childCount = linearLayout.getChildCount();
        dialogListener listener = new dialogListener();
        for (int i = 0; i < childCount; i++) {
            View view = linearLayout.getChildAt(i);
            if (view instanceof Button) {
                view.setOnClickListener(listener);
            }
        }

        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.personal_info_bottom_dialog);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.8f;
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        lp.width = (int) (display.getWidth()); //设置宽度
        window.setAttributes(lp);

    }


    class dialogListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.id_openCamera:
                    Toast.makeText(MyDemo.this, "打开相机", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    openCamera();
                    break;
                case R.id.id_openPhtots:
                    dialog.dismiss();
                    openPhotos();
                    break;
                case R.id.id_hide:
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    // 调用系统相册
    private void openPhotos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        startActivityForResult(intent, 0);
    }

    private Uri cameraUri;

    // 调用系统相机
    private void openCamera() {


        File file = createImageFile();
        cameraUri = Uri.fromFile(file);
        Log.d("TAG", " cameraUri: " + cameraUri + ", path: " + cameraUri.getPath());

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, 1);
    }

    // 创建 文件路径
    private File createImageFile() {

        File myDir =
                new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "buluoxing");

        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fineName = "headPic" + ".jpg";
        File file = new File(myDir + File.separator + fineName);
        if (file.exists()) {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Event 注册过
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }


    // ------- 版本更新
    private void checkUpDate(Context context) {
        Toast.makeText(this, "检测更新", Toast.LENGTH_SHORT).show();
        if (!isUpDate(context, 1)) {
            //更新
            String baseUrl = Config.Version_URL;
            String fileName = "buluoxing.apk";
            String fileStoreDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File
                    .separator + "buluoxing";
            String fileStoreName = fileName;
            showLoadingDialog();

            FileApi.getInstance(baseUrl)
                    .loadFileByName(fileName, new FileCallback(fileStoreDir, fileStoreName) {
                        @Override
                        public void onSuccess(File file) {
                            super.onSuccess(file);
                            hDialogBuilder.dismiss();
                        }

                        @Override
                        public void progress(long progress, long total) {
                            updateProgress(progress, total);
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            hDialogBuilder.dismiss();
                            call.cancel();
                        }
                    });

        } else {
            final SpotsDialog dialog = new SpotsDialog(context, "当前版本为最新版本,不需要更新");
            dialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 2000);


        }

    }

    /**
     * 更新下载进度
     *
     * @param progress
     * @param total
     */
    private void updateProgress(long progress, long total) {
        txtProgress.setText(String.format("正在下载：(%s/%s)",
                DataManager.getFormatSize(progress),
                DataManager.getFormatSize(total)));
    }

    /**
     * 显示下载对话框
     */
    private void showLoadingDialog() {
        txtProgress = (TextView) View.inflate(MyDemo.this, R.layout
                .layout_hd_dialog_custom_tv, null);
        hDialogBuilder = new HDialogBuilder(MyDemo.this);
        hDialogBuilder.setCustomView(txtProgress)
                .title("下载")
                .nBtnText("取消")
                .nBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hDialogBuilder.dismiss();
                        FileApi.cancelLoading();
                    }
                })
                .show();
    }

    private boolean isUpDate(Context context, int code) {
        String versionName = "";
        int versioncode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versioncode = pi.versionCode;
            Log.e("VersionInfo", "versionName = " + versionName);
            Log.e("VersionInfo", "versioncode = " + versioncode);
            if (versioncode < code) {
                // 更新
                return true;
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
            return false;
        }

        // 其他情况 一律 不更新
        return false;
    }

    //----------------- 获取 设备 信息-------

    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
//                Class<!--?--> clazz = Class.forName("android.content.Context");
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = null;
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }
            String mac = null;
            FileReader fstream = null;
            try {
                fstream = new FileReader("/sys/class/net/wlan0/address");
            } catch (FileNotFoundException e) {
                fstream = new FileReader("/sys/class/net/eth0/address");
            }
            BufferedReader in = null;
            if (fstream != null) {
                try {
                    in = new BufferedReader(fstream, 1024);
                    mac = in.readLine();
                } catch (IOException e) {
                } finally {
                    if (fstream != null) {
                        try {
                            fstream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            json.put("mac", mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
