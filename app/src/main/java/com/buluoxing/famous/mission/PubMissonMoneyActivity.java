package com.buluoxing.famous.mission;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buluoxing.famous.AreaSelectorActivity;
import com.buluoxing.famous.ConfirmActivity;
import com.buluoxing.famous.ImageListActivity;
import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.buluoxing.famous.SelectTimeActivity;
import com.buluoxing.famous.user.AdnNameLengthFilter;
import com.buluoxing.famous.user.KolSetupActivity;
import com.detail.UserInfo;
import com.diy.AutoNewLineLayout;
import com.diy.ContainsEmojiEditText;
import com.diy.MyCheckBox;
import com.diy.MyRadioGroup;
import com.diy.WheelView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.Common;
import com.util.Config;
import com.util.FileUpload;
import com.util.Http;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PubMissonMoneyActivity extends MyActivity {
    public JSONArray taskModelList = new JSONArray();
    public long startTime;
    public long endTime;
    private ImageView currentUploadImageView = null;
    private Boolean uploading = false;
    private String[] cityIdList = {"203170"};       // 城市ID
    private String[] cityNameList = {"全国"};     // 城市名称（默认全国）
    public String currentTaskMode = "";
    private String sex = "1";
    private ArrayList<LinearLayout> sampleViewList = new ArrayList<>();
    private String[] photoIds;
    private String shareImageId;
    private static final String[] PLANETS = new String[]{"Mercury", "Venus", "Earth", "Mars", "Jupiter", "Uranus", "Neptune", "Pluto"};
    private int minAge = 0;
    private int maxAge = 99;
    private ArrayList<MyCheckBox> checkBoxList = new ArrayList<>();
    private String currentSection = "wx";
    private double lowSpend = 0;
    private double lowToalMoney;
    private double changePercent;
    // 任务标题
    private ContainsEmojiEditText mEditTextMoneyTitle;
    // 任务要求
    private ContainsEmojiEditText mEditTextMoneyTask;

//    private TextView mMoneyAverage;     // 平分红包
//    private TextView mMoneyRandom;     // 随机红包

    private MyRadioGroup moneyRadio;    // 红包类型控件
    private String moneyType = "1";          //红包类型


    private MyCheckBox initOneCheckbox(JSONObject taskMode, int index) throws JSONException {
        MyCheckBox checkBox = new MyCheckBox(this);
        checkBox.setText(taskMode.getString("title"));
        int width = (int) getResources().getDimension(R.dimen.px423);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = (int) getResources().getDimension(R.dimen.px43);
        checkBox.setLayoutParams(params);
        checkBox.setTag(index);
        LinearLayout sampleView = createSample(index);
        sampleViewList.add(sampleView);
        ((LinearLayout) findViewById(R.id.sample_list)).addView(sampleView);
        sampleView.setVisibility(View.GONE);


        checkBox.setOnCheckStatusChangedListener(new MyCheckBox.OnCheckStatusChangedListener() {
            @Override
            public void check(MyCheckBox cb, Boolean checked) {
                int index = (int) cb.getTag();

                if (!checked && checkIsDependentBySomeBody(index)) {
                    cb.check(true);
                    return;
                }

                try {
                    taskModelList.getJSONObject(index).put("checked", checked);

                    if (!taskModelList.getJSONObject(index).getString("cat").equals(currentSection)) {
                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (checked) {
                    checkIsDependSomeBody(index);
                    sampleViewList.get(index).setVisibility(View.VISIBLE);
                } else {
                    sampleViewList.get(index).setVisibility(View.GONE);
                }
                calLowSpend();
            }
        });
        return checkBox;
    }

    private void calLowSpend() {
        lowSpend = 0;
        for (int i = 0; i < taskModelList.length(); i++) {
            try {
                Boolean checked = taskModelList.getJSONObject(i).getBoolean("checked");
                if (checked) {
                    if (taskModelList.getJSONObject(i).getString("cat").equals(currentSection)) {
                        lowSpend += taskModelList.getJSONObject(i).getDouble("molow") / 100.0;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        int missionNumber = 0;
        try {
            missionNumber = Integer.parseInt(findTextViewById(R.id.mission_number).getText().toString());
        } catch (Exception e) {

        }
        lowToalMoney = missionNumber * lowSpend;
        findTextViewById(R.id.low_total_money).setText(String.format("红包总额不低于%.2f元", lowToalMoney));
        findTextViewById(R.id.low_spend).setText(String.format("平均每任务最低%.2f", lowSpend));
    }

    private void freshSampleList() {
        for (int i = 0; i < sampleViewList.size(); i++) {
            try {
                if (!taskModelList.getJSONObject(i).getString("cat").equals(currentSection)) {
                    sampleViewList.get(i).setVisibility(View.GONE);
                } else {
                    if (taskModelList.getJSONObject(i).getBoolean("checked")) {
                        sampleViewList.get(i).setVisibility(View.VISIBLE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        calLowSpend();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_pub_misson_money;
    }

    public boolean checkIsDependentBySomeBody(int index) {

        try {
            JSONObject taskMode = taskModelList.getJSONObject(index);

            for (int i = 0; i < taskModelList.length(); i++) {
                JSONObject t = taskModelList.getJSONObject(i);
                if ((t.getInt("is_zf") == taskMode.getInt("id")) && t.getBoolean("checked")) {
                    Toast.makeText(PubMissonMoneyActivity.this, "选择" + t.getString("title") + "的时候，必须选择" + taskMode.getString("title"), Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void checkIsDependSomeBody(int index) {

        try {
            JSONObject taskMode = taskModelList.getJSONObject(index);
            int d = taskMode.getInt("is_zf");
            if (d > 0) {
                for (int i = 0; i < taskModelList.length(); i++) {
                    JSONObject t = taskModelList.getJSONObject(i);
                    if (t.getInt("id") == d) {
                        if (!checkBoxList.get(i).checked) {
                            checkBoxList.get(i).check(true);
                            Toast.makeText(PubMissonMoneyActivity.this, taskMode.getString("title") + "模式必须选中" + taskModelList.getJSONObject(i).getString("title"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doUnormalCheckbox(final JSONObject taskMode, MyCheckBox checkbox) throws JSONException {
        if (taskMode.getString("ischeck").equals("1")) {
            checkbox.check(true);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //限制输入字数50
        mEditTextMoneyTask = (ContainsEmojiEditText) findViewById(R.id.mission_require);
        mEditTextMoneyTask.setFilters(new InputFilter[]{new AdnNameLengthFilter(this, 45)});

        mEditTextMoneyTitle = (ContainsEmojiEditText) findViewById(R.id.mission_title);
        String url = findTextViewById(R.id.mission_href).getText().toString();
//        if ( !url.isEmpty() ){
//            mEditTextMoneyTitle.setFilters(new InputFilter[]{new AdnNameLengthFilter(this, 48)});
//        }
        // id_money_average
//        mMoneyAverage = (TextView) findViewById(R.id.id_money_average);
//        mMoneyRandom = (TextView) findViewById(R.id.id_money_random);
        moneyRadio = (MyRadioGroup) findViewById(R.id.money_radio);
        JSONObject sysConfig = Config.sysConfig;
        if (sysConfig == null) {
            Toast.makeText(this, "网络异常，请检查你的网络", Toast.LENGTH_SHORT).show();
        }


        try {
            JSONObject taskModelListMap = sysConfig.getJSONObject("task_mode_conf");
            Log.i("TAG", "onCreate: taskModelListMap = " + taskModelListMap);
            JSONArray weChatList = taskModelListMap.getJSONArray("wechat");
            JSONArray weiBoList = taskModelListMap.getJSONArray("weibo");
            JSONArray otherList = taskModelListMap.getJSONArray("other");
            int change = sysConfig.getJSONObject("prompt_conf").getInt("hb_charge_percent");
            changePercent = change / 100.0;

            int index = 0;
            AutoNewLineLayout autoNewLineLayout = (AutoNewLineLayout) findViewById(R.id.mission_type_list_wx);
            autoNewLineLayout.setOnelineNumber(2);
            autoNewLineLayout.setRowDistance(0);
            for (int i = 0; i < weChatList.length(); i++) {
                JSONObject taskMode = weChatList.getJSONObject(i);
                taskMode.put("cat", "wx");
                taskModelList.put(taskMode);
                taskMode.put("checked", false);
                MyCheckBox checkbox = initOneCheckbox(taskMode, index);
                checkBoxList.add(checkbox);
                autoNewLineLayout.addNewView(checkbox);
                index++;

                doUnormalCheckbox(taskMode, checkbox);


            }


            AutoNewLineLayout autoNewLineLayoutWeibo = (AutoNewLineLayout) findViewById(R.id.mission_type_list_weibo);
            autoNewLineLayoutWeibo.setOnelineNumber(2);
            autoNewLineLayoutWeibo.setRowDistance(0);
            for (int i = 0; i < weiBoList.length(); i++) {
                JSONObject taskMode = weiBoList.getJSONObject(i);
                taskMode.put("cat", "weibo");
                taskModelList.put(taskMode);
                taskMode.put("checked", false);
                MyCheckBox checkbox = initOneCheckbox(taskMode, index);
                checkBoxList.add(checkbox);
                autoNewLineLayoutWeibo.addNewView(checkbox);
                index++;
                doUnormalCheckbox(taskMode, checkbox);
            }


            AutoNewLineLayout autoNewLineLayoutOther = (AutoNewLineLayout) findViewById(R.id.mission_type_list_other);
            autoNewLineLayoutOther.setOnelineNumber(2);
            autoNewLineLayoutOther.setRowDistance(0);
            for (int i = 0; i < otherList.length(); i++) {
                JSONObject taskMode = otherList.getJSONObject(i);
                taskMode.put("cat", "other");
                taskModelList.put(taskMode);
                taskMode.put("checked", false);
                MyCheckBox checkbox = initOneCheckbox(taskMode, index);
                checkBoxList.add(checkbox);
                autoNewLineLayoutOther.addNewView(checkbox);
                index++;
                doUnormalCheckbox(taskMode, checkbox);
            }
            calLowSpend();
            photoIds = new String[taskModelList.length()];
        } catch (JSONException e) {
            e.printStackTrace();
        }

        findViewById(R.id.start_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PubMissonMoneyActivity.this, SelectTimeActivity.class);
                intent.putExtra("code", 100);
                intent.putExtra("default_time", startTime);
                startActivityForResult(intent, 100);
            }
        });
        findViewById(R.id.end_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PubMissonMoneyActivity.this, SelectTimeActivity.class);
                intent.putExtra("code", 101);
                intent.putExtra("default_time", endTime);
                startActivityForResult(intent, 101);
            }
        });
        findViewById(R.id.pub_mission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> tids = new ArrayList<String>();
                Log.i("taskList", taskModelList.toString());
                for (int i = 0; i < taskModelList.length(); i++) {
                    try {

                        Boolean checked = taskModelList.getJSONObject(i).getBoolean("checked");

                        if (checked) {
                            if (taskModelList.getJSONObject(i).getString("cat").equals(currentSection)) {
                                tids.add(taskModelList.getJSONObject(i).getString("id"));
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                String idsString = "";
                if (tids.size() > 0) {
                    idsString = StringUtils.join(tids.toArray(), ",");
                }

                HashMap<String, String> params = new HashMap<String, String>();

                params.put("action", "userRelease");
                params.put("user_id", Common.getUserId(PubMissonMoneyActivity.this));
                params.put("link_url", ((EditText) findViewById(R.id.mission_href)).getText().toString());
                params.put("title", ((EditText) findViewById(R.id.mission_title)).getText().toString());
                params.put("task_require", ((EditText) findViewById(R.id.mission_require)).getText().toString());
                params.put("task_type", "2");
                params.put("start_time", Common.getDateStrFromTime(startTime, " yyyy-MM-dd HH:mm:ss"));
                params.put("end_time", Common.getDateStrFromTime(endTime, " yyyy-MM-dd HH:mm:ss"));
                params.put("task_pattern", idsString);
                params.put("hb_type",moneyType);

                ArrayList<String> uploadIds = new ArrayList<String>();
                for (int i = 0; i < photoIds.length; i++) {
                    if (photoIds[i] != null) {


                        try {
                            if (taskModelList.getJSONObject(i).getString("cat").equals(currentSection)) {
                                uploadIds.add(photoIds[i]);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


                if (shareImageId != null) {
                    params.put("share_img", shareImageId);
                }

                params.put("photo_id", StringUtils.join(uploadIds.toArray(), ","));
                params.put("sex", sex);
                params.put("start_old", (minAge + 1) + "");
                params.put("end_old", (maxAge + 1) + "");
                params.put("number", ((EditText) findViewById(R.id.mission_number)).getText().toString().trim());

                String price = ((EditText) findViewById(R.id.price_total)).getText().toString().trim();
                if (price.isEmpty()) {
                    Toast.makeText(PubMissonMoneyActivity.this, "任务总金额不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("price", price);
//                int priceTotal = Integer.parseInt(price) * 100;
                Double priceTotal = Double.parseDouble(price) * 100;

                if (priceTotal / 100 < lowToalMoney) {
                    Toast.makeText(PubMissonMoneyActivity.this, String.format("任务总额不低于%.2f元", lowToalMoney), Toast.LENGTH_SHORT).show();
                    return;
                }

//                price = priceTotal + "";
                price = String.valueOf(priceTotal);

                params.put("price", price);
                params.put("provincial_city", StringUtils.join(cityNameList, ","));


                if (params.get("link_url").equals("")) {
                    Toast.makeText(PubMissonMoneyActivity.this, "链接不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (params.get("title").equals("")) {
                    Toast.makeText(PubMissonMoneyActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //if(params.get("task_require").equals("")) {
                //	Toast.makeText(PubMissonMoneyActivity.this,"任务要求不能为空",Toast.LENGTH_SHORT).show();
                //return;
                //}
                if (params.get("number").equals("")) {
                    Toast.makeText(PubMissonMoneyActivity.this, "任务数量不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (params.get("price").equals("")) {
                    Toast.makeText(PubMissonMoneyActivity.this, "任务总金额不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }


                final Common.LoadingHandler handler = Common.loading(PubMissonMoneyActivity.this, "发布任务中...");


                Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
                    @Override
                    public void run(String result) {
                        Log.i("result", result.toString());
                    }

                    @Override
                    public void run(JSONObject result) {
                        try {
                            handler.close();

                            String resultCode = result.getString("status");

                            Log.i("result", result.toString());

                            if (resultCode.equals(Config.NeedMoreMoney)) {
                                Intent intent = new Intent(PubMissonMoneyActivity.this, NeedMoreMoneyActivity.class);
                                intent.putExtra("type", "2");
                                startActivity(intent);
                            } else {
                                if (resultCode.equals(Config.HttpSuccessCode)) {
                                    Toast.makeText(PubMissonMoneyActivity.this, "任务时间结束后两天内未追加任务系统自动返还现金或红豆", Toast.LENGTH_SHORT).show();
                                    Intent intentNoti = new Intent();
                                    intentNoti.setAction("update.task.list");
                                    sendBroadcast(intentNoti);

                                    Common.getUserInfo(PubMissonMoneyActivity.this, new Common.OnGetUserInfoListener() {
                                        @Override
                                        public void geted(UserInfo userInfo) {
                                            if (userInfo.is_kol == 0) {
                                                Intent intent = new Intent(PubMissonMoneyActivity.this, ConfirmActivity.class);
                                                intent.putExtra("message", "立即申请网红?");
                                                startActivityForResult(intent, 801);
                                            } else {
                                                finish();
                                            }

                                        }
                                    });


                                } else {
                                    // result.getString("message")
                                    Toast.makeText(PubMissonMoneyActivity.this, "请选择发布任务类型", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void run(JSONArray result) {

                    }
                });
            }
        });

        findViewById(R.id.select_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PubMissonMoneyActivity.this, AreaSelectorActivity.class);
                intent.putExtra("city_id_list", cityIdList);
                startActivityForResult(intent, 102);
            }
        });
        // 红包类型
        moneyRadio.setOnCheckStatusChangedListener(new MyRadioGroup.OnCheckStatusChangedListener() {
            @Override
            public void check(MyRadioGroup cb, int position) {
                moneyType = (position + 1) + "";
            }
        });
        moneyRadio.addRadio("随机红包");
        moneyRadio.addRadio("平分红包");
        moneyRadio.setRadioCheckByIndex(0);

        ((MyRadioGroup) findViewById(R.id.sex_radio)).setOnCheckStatusChangedListener(new MyRadioGroup.OnCheckStatusChangedListener() {
            @Override
            public void check(MyRadioGroup cb, int position) {
                sex = (position + 1) + "";
            }
        });

        ((MyRadioGroup) findViewById(R.id.sex_radio)).addRadio("男");
        ((MyRadioGroup) findViewById(R.id.sex_radio)).addRadio("女");
        ((MyRadioGroup) findViewById(R.id.sex_radio)).addRadio("不限");
        ((MyRadioGroup) findViewById(R.id.sex_radio)).setRadioCheckByIndex(2);


        Common.getProvinceData(this, new Http.HttpSuccessInterface() {
            @Override
            public void run(String result) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    JSONArray provinceList = resultJson.getJSONArray("result");

                    JSONObject allCity = provinceList.getJSONObject(provinceList.length() - 1).getJSONArray("city_array").getJSONObject(0);

                    Log.i("all_city", allCity.toString());

                    cityIdList[0] = allCity.getString("id");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.wx_section).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.mission_type_list_wx).setVisibility(View.VISIBLE);
                findViewById(R.id.mission_type_list_weibo).setVisibility(View.GONE);
                findViewById(R.id.mission_type_list_other).setVisibility(View.GONE);

                findViewById(R.id.weibo_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
                findViewById(R.id.other_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
                findViewById(R.id.wx_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
                v.setBackgroundResource(R.drawable.section_bg);
                currentSection = "wx";

                freshSampleList();
            }
        });
        findViewById(R.id.weibo_section).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.mission_type_list_wx).setVisibility(View.GONE);
                findViewById(R.id.mission_type_list_weibo).setVisibility(View.VISIBLE);
                findViewById(R.id.mission_type_list_other).setVisibility(View.GONE);

                findViewById(R.id.weibo_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
                findViewById(R.id.other_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
                findViewById(R.id.wx_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
                v.setBackgroundResource(R.drawable.section_bg);

                currentSection = "weibo";

                freshSampleList();
            }
        });
        findViewById(R.id.other_section).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.mission_type_list_wx).setVisibility(View.GONE);
                findViewById(R.id.mission_type_list_weibo).setVisibility(View.GONE);
                findViewById(R.id.mission_type_list_other).setVisibility(View.VISIBLE);

                findViewById(R.id.weibo_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
                findViewById(R.id.other_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
                findViewById(R.id.wx_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
                v.setBackgroundResource(R.drawable.section_bg);

                currentSection = "other";

                freshSampleList();
            }
        });

        try {
            String title = Config.sysConfig.getJSONObject("prompt_conf").getString("hb_charge");
            findTextViewById(R.id.rate_info).setText(title);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //开始修改版
        findViewById(R.id.mission_title).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getUrlTitle();
                }
            }
        });


        findViewById(R.id.upload_mission_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                                 /* 开启Pictures画面Type设定为image */
                intent.setType("image/*");
                                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                intent.setAction(Intent.ACTION_GET_CONTENT);
                                /* 取得相片后返回本画面 */
                startActivityForResult(intent, 103);
            }
        });


        //  选择 最小年龄
        findViewById(R.id.start_old).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                ArrayList<String> ageList = new ArrayList<String>();
                for (int i = 1; i <= 100; i++) {
                    ageList.add(i + "岁");
                }

                View outerView = LayoutInflater.from(PubMissonMoneyActivity.this).inflate(R.layout.wheel_view, null);
                final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                wv.setOffset(2);
                wv.setItems(ageList);
                wv.setSeletion(minAge);

                new AlertDialog.Builder(PubMissonMoneyActivity.this)
                        .setTitle("请选择最小年龄")
                        .setView(outerView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //showDialog("你选择了确定");
                                ((TextView) v).setText(wv.getSeletedItem());
                                minAge = wv.getSeletedIndex();
                            }
                        })
                        .show();
            }
        });

        // 选择最大你年龄
        findViewById(R.id.end_old).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                ArrayList<String> ageList = new ArrayList<String>();
                for (int i = 1; i <= 100; i++) {
                    ageList.add(i + "岁");
                }

                View outerView = LayoutInflater.from(PubMissonMoneyActivity.this).inflate(R.layout.wheel_view, null);
                final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                wv.setOffset(2);
                wv.setItems(ageList);
                wv.setSeletion(maxAge);

                new AlertDialog.Builder(PubMissonMoneyActivity.this)
                        .setTitle("请选择最大年龄")
                        .setView(outerView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //showDialog("你选择了确定");
                                ((TextView) v).setText(wv.getSeletedItem());
                                maxAge = wv.getSeletedIndex();
                            }
                        })
                        .show();

            }
        });


        Long time = new Date().getTime();
        startTime = time;
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分",
                Locale.CHINA);
        String dateString = sdr.format(new Date(startTime));
        ((TextView) findViewById(R.id.start_time)).setText(dateString);
        endTime = time + 10 * 86400 * 1000;
        String dateString2 = sdr.format(new Date(endTime));

        ((TextView) findViewById(R.id.end_time)).setText(dateString2);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int length = mEditTextMoneyTask.getText().length();
        if ( length > 0 && length < 5) {
            Toast.makeText(this, "如写评须5字", Toast.LENGTH_SHORT).show();
        }

        // 可领取人数 和 任务金额 EditText
        numberPrice();
    }

    //   可领取人数 和 任务金额 EditText
    private void numberPrice(){

        // 发布任务 可领取人数
        ((EditText) findViewById(R.id.mission_number)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int missionNumber = 0;
                    try {
                        missionNumber = Integer.parseInt(findTextViewById(R.id.mission_number).getText().toString().trim());
                    } catch (Exception e) {

                    }
                    lowToalMoney = missionNumber * lowSpend;

                    lowToalMoney = lowToalMoney / (1 - changePercent);

                    findTextViewById(R.id.low_total_money).setText(String.format("红包总额不低于%.2f元", lowToalMoney));

                    double priceToal = Double.parseDouble(findTextViewById(R.id.price_total).getText().toString().trim());
                    if (missionNumber > 0) {
                        double per_price = priceToal * (1 - changePercent) / missionNumber;
                        findTextViewById(R.id.per_price).setText(String.format("%.2f", per_price));
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //   发布任务总金额
        ((EditText) findViewById(R.id.price_total)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double priceToal = 0;
                    try {
                        priceToal = Double.parseDouble(findTextViewById(R.id.price_total).getText().toString().trim());
                        findTextViewById(R.id.real_total).setText(String.format("%.2f", priceToal * (1 - changePercent)));
                        int missionNumber = Integer.parseInt(findTextViewById(R.id.mission_number).getText().toString().trim());
                        if (missionNumber > 0) {
                            double per_price = priceToal * (1 - changePercent) / missionNumber;
                            findTextViewById(R.id.per_price).setText(String.format("%.2f", per_price));
                        }
                        findTextViewById(R.id.price_change).setText(String.format("%.2f", priceToal * changePercent));
                    } catch (Exception e) {
                    }


                } catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void getUrlTitle() {

        final Common.LoadingHandler handler = Common.loading(this, "获取标题...");
        final Handler titleHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 2) {
                    findTextViewById(R.id.mission_href).setText(msg.obj.toString());
                } else {
                    String result = msg.obj.toString();
                    if (!result.isEmpty()) {
                        findTextViewById(R.id.mission_title).setText(result);
                    }
                    handler.close();
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = findTextViewById(R.id.mission_href).getText().toString();
                // 此处判断 链接为空时  导致 加载Dialog 不能消失
//                if (url.equals("")) {
//                    return;
//                }

                if (!url.contains("http")) {
                    url = "http://" + url;
                    Message msg = titleHandler.obtainMessage();
                    msg.obj = url;
                    msg.what = 2;
                    msg.sendToTarget();

                }
                Document doc = null;
                try {

                    doc = Jsoup.connect(url).get();
                    String title = doc.title();

                    Message msg = titleHandler.obtainMessage();
                    msg.obj = title;
                    msg.sendToTarget();
                } catch (IOException e) {
                    Message msg = titleHandler.obtainMessage();
                    msg.obj = "";
                    msg.sendToTarget();
                    e.printStackTrace();
                } catch (Exception e) {
                    Message msg = titleHandler.obtainMessage();
                    msg.obj = "";
                    msg.sendToTarget();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public LinearLayout createSample(final int taskIndex) {
        LinearLayout itemView = null;
        try {
            JSONObject taskMode = taskModelList.getJSONObject(taskIndex);
            LayoutInflater mInflater = LayoutInflater.from(this);
            itemView = (LinearLayout) mInflater.inflate(R.layout.sample_item, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = (int) getResources().getDimension(R.dimen.px55);
            params.rightMargin = (int) getResources().getDimension(R.dimen.px55);
            itemView.setLayoutParams(params);
            ((TextView) itemView.findViewById(R.id.sample_title)).setText("默认" + taskMode.getString("title") + "示例图");
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(taskMode.getString("img"), (ImageView) itemView.findViewById(R.id.sample), Config.options);
            ImageListActivity.setImageViewCanShowBig((ImageView) itemView.findViewById(R.id.sample), taskMode.getString("img"));
            itemView.findViewById(R.id.add_sample_image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                                        /* 开启Pictures画面Type设定为image */
                    intent.setType("image/*");
                                        /* 使用Intent.ACTION_GET_CONTENT这个Action */
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                         /* 取得相片后返回本画面 */
                    startActivityForResult(intent, taskIndex);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemView;
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {


        Log.i("fuck", requestCode + ":" + resultCode);

        if (requestCode == 801) {
            if (resultCode == 1) {
                Intent intent = new Intent(PubMissonMoneyActivity.this, KolSetupActivity.class);
                intent.putExtra("request_kol", "1");
                startActivity(intent);
                finish();
            } else {
                finish();
            }
            return;
        }
        if (resultCode == 100) {
            Long time = data.getLongExtra("time", 0);
            startTime = time;
            SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分",
                    Locale.CHINA);
            String dateString = sdr.format(new Date(startTime));
            ((TextView) findViewById(R.id.start_time)).setText(dateString);
        } else if (resultCode == 101) {
            Long time = data.getLongExtra("time", 0);
            endTime = time;
            SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分",
                    Locale.CHINA);
            String dateString = sdr.format(new Date(endTime));

            ((TextView) findViewById(R.id.end_time)).setText(dateString);
        } else if (resultCode == 102) {
            // 接口城市id 改城市名称
            cityIdList = data.getStringArrayExtra("city_id_list");
            cityNameList = data.getStringArrayExtra("city_name_list");
            String citySelect = StringUtils.join(cityNameList, ",");
            ((TextView) findViewById(R.id.area)).setText(citySelect);
        } else if (requestCode == 103) {
            if (data != null) {
                FileUpload fileUpload = new FileUpload(this);
                fileUpload.setMaxWidth(100);
                fileUpload.setImageShowHolder(findImageViewById(R.id.upload_mission_avatar));
                HashMap<String, String> params = new HashMap<>();
                params.put("type", "5");

                fileUpload.uploadByResult(data, params);

                fileUpload.setOnUploadStatusChangedListener(new FileUpload.OnUploadStatusChangedListener() {
                    @Override
                    public void success(JSONObject result) {
                        try {
                            //  原来传图片id   数据库保存的是图片路径 img_short_url
                            //shareImageId = result.getJSONObject("result").getString("id");
                            shareImageId = result.getJSONObject("result").getString("img_short_url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        } else {
            if (data != null) {
                FileUpload fileUpload = new FileUpload(this);
                fileUpload.setImageShowHolder((ImageView) sampleViewList.get(requestCode).findViewById(R.id.add_sample_image));
                HashMap<String, String> params = new HashMap<>();
                params.put("task_mode", Config.TaskMoney);
                params.put("type", "3");
                try {
                    params.put("task_mode", taskModelList.getJSONObject(requestCode).getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                fileUpload.uploadByResult(data, params);

                fileUpload.setOnUploadStatusChangedListener(new FileUpload.OnUploadStatusChangedListener() {
                    @Override
                    public void success(JSONObject result) {
                        try {
                            photoIds[requestCode] = result.getJSONObject("result").getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
