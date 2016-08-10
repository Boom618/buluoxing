package com.buluoxing.famous.user;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.buluoxing.famous.bean.KolAreaBean;
import com.buluoxing.famous.view.FlowLayout;
import com.detail.UserInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.internal.Streams;
import com.umeng.analytics.MobclickAgent;
import com.util.ACache;
import com.util.Common;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2016/8/4 0004.
 * <p/>
 * 网红档案管理 领域 - 1.0.1 版
 */
public class KolAreaActivity extends MyActivity {

    private ImageView mBack;
    private TextView mSave;

    private FlowLayout mIsArea; // 选中领域
    private FlowLayout mAllArea; // 全部领域
    private LayoutInflater mInflater;
//    private boolean isClick = true; // 已选领域


    private KolAreaBean mArea;  // 领域
    private ArrayList<String> areaList; // 存name
    private ArrayList<String> areaIdList; // 存id

//    private ArrayList<HashMap<String,String>> areaList; //

    private ACache mCache;


    @Override
    protected int getLayout() {
        return R.layout.activity_kol_area;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        mCache = ACache.get(this);

        initArea();

    }


    /**
     * 初始化 网红 选中的领域
     */
    private void initArea() {

        String userId = Common.getUserId(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "userRefreshInfo");
        params.put("user_id", userId);
        Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
            @Override
            public void run(String result) {
            }

            @Override
            public void run(JSONObject result) {

                try {
                    Log.e("result", result.toString());
                    // 结果值
                    JSONObject outCome = result.getJSONObject("result");
                    if ( outCome == null){
                        return;
                    }
                    // 领域名称
                    String name = outCome.getString("domian_name");
                    if (name.length()>0){

                        String id = outCome.getString("domian_id");
                        String[] splitName = name.split(",");
                        String[] splitId = id.split(",");
                        List<String> firstName = new ArrayList<String>();
                        List<String> firstId = new ArrayList<String>();

                        firstName = Arrays.asList(splitName);
                        firstId = Arrays.asList(splitId);

                        for (int i = 0; i < firstName.size(); i++) {
                            final TextView tv = (TextView) mInflater.inflate(R.layout.activity_user_kol_is_text,
                                    mIsArea, false);
                            tv.setText(firstName.get(i));
                            tv.setTag(firstId.get(i));  // 设置 ID
                            tv.setTextColor(Color.GREEN);
                            mIsArea.addView(tv);
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mIsArea.removeView(tv);
                                }
                            });


                        }
                    }else {
                        // 用户没有选中领域
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

    private void initView() {
        mBack = (ImageView) findViewById(R.id.back);
        mSave = (TextView) findViewById(R.id.save);
        mIsArea = (FlowLayout) findViewById(R.id.is_area);
        mAllArea = (FlowLayout) findViewById(R.id.all_area);
        mInflater = LayoutInflater.from(this);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveArea();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        initData();
        // 缓存 暂时 没做
//        String allArea = mCache.getAsString("allArea");
//        Log.e("TAG", "onStart: allArea = " + allArea );
//
//        if (allArea.isEmpty()){
//        }else {
//            ArrayList<String> strList = new ArrayList<String>();
//
//            int size = strList.size();
//            showAreaList(strList);
//        }


    }

    private void initData() {

        //String userId = Common.getUserId(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "sysDomainList");

        Http.postApiWithToken(Http.buildBaseApiUrl("/Home/System/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
            @Override
            public void run(String result) {
                Log.e("TAG", "run: result = " + result);
            }

            @Override
            public void run(JSONObject result) {
                try {

                    Gson gson = new Gson();
                    Log.e("TAG", "run: result = " + result.toString());
                    mArea = gson.fromJson(result.toString(), KolAreaBean.class);
                    List<KolAreaBean.ResultBean> beanList = mArea.getResult();
                    int size = beanList.size();
                    areaList = new ArrayList<String>();
                    areaIdList = new ArrayList<String>();
                    for (int i = 0; i < size; i++) {
                        KolAreaBean.ResultBean bean = beanList.get(i);

                        String name = bean.getName();
                        String id = bean.getId();


                        //areaMap.put(bean.getId(),bean.getName());

                        areaList.add(name);
                        areaIdList.add(id);

                    }
                    // 缓存 领域列表
                    //mCache.put("allArea",areaList.toString());


                    //  显示 所有领域
                    showAreaList(areaList);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void run(JSONArray result) {

            }
        });
    }

    /**
     * 显示 所有领域
     *
     * @param areaList
     */
    private void showAreaList(ArrayList areaList) {
        Log.e("TAG", "showALLAreaList: areaList = " + areaList);
        if (areaList.size() == 0) {
            return;
        }
        for (int i = 0; i < areaList.size(); i++) {

            final TextView tv = (TextView) mInflater.inflate(R.layout.activity_user_kol_text,
                    mAllArea, false);
            Log.e("TAG", "list.get: " + areaList.get(i).toString());
            tv.setText(areaList.get(i) + "");
            tv.setTag(areaIdList.get(i));
            mAllArea.addView(tv);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //tv.setTextColor(Color.GREEN);

//                    if (isClick){
//                        //tv.setTextColor(Color.GREEN);
//                        isClick = false;
//                    }else {
//                        isClick = true;
//                        //tv.setTextColor(Color.parseColor("#5BC4ED"));
//                    }
                    String num = (String) tv.getTag();
                    String string = tv.getText().toString();
                    ArrayList<String> list = new ArrayList<>();
                    list.add(string);
                    // 显示 选中 领域
                    showText(num, string, list);
                }
            });
        }
    }

    /**
     * 显示选中 领域
     *
     * @param num
     * @param text
     * @param list
     */
    private void showText(String num, String text, ArrayList<String> list) {

        Log.e("TAG", "showText: num = " + num);
        int childCount = mIsArea.getChildCount();

        if (childCount > 1) {
            new SweetAlertDialog(KolAreaActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("亲,只能选中两个领域")
                    //.setContentText("只能选中两个领域")
                    .setConfirmText("确定")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    }).show();
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            final TextView tv = (TextView) mInflater.inflate(R.layout.activity_user_kol_is_text,
                    mIsArea, false);
            tv.setText(text);
            tv.setTag(num);
            tv.setTextColor(Color.GREEN);

            mIsArea.addView(tv);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIsArea.removeView(tv);
                }
            });


        }

    }

    private String getAreaId() {


        int childCount = mIsArea.getChildCount();
        if (childCount == 0) {
            return "";
        }

        String str = "";
        for (int i = 0; i < childCount; i++) {
            TextView view = (TextView) mIsArea.getChildAt(i);
            String text = (String) view.getTag();
            Log.e("getAreaId", "getAreaId: text = " + text);
            str += text + ",";
        }
        Log.e("getAreaId", "str = " + str);

        String s = str.substring(0, str.length() - 1);
        return s;
    }

    /**
     * 保存
     */
    private void onSaveArea() {
        final SweetAlertDialog dialog = showHttpDialog("保存中", false);
        dialog.show();

        String areaId = getAreaId();
        String userId = Common.getUserId(KolAreaActivity.this);
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "modifyUserInfo");
        params.put("user_id", userId);
        params.put("domian_str", areaId);

        Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
            @Override
            public void run(String result) {

            }

            @Override
            public void run(JSONObject result) {
                Log.e("save", result.toString());
                Toast.makeText(KolAreaActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                dialog.cancel();
                finish();
            }

            @Override
            public void run(JSONArray result) {

            }
        });
    }

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


}
