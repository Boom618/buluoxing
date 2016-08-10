package com.buluoxing.famous.user;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.BeansMissionListAdapter;
import com.adapter.MyBeansIoListAdapter;
import com.adapter.MyMoneyIoListAdapter;
import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.diy.CustomerDatePickerDialog;
import com.diy.MyListView;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

//  个人中心 - 现金  我的现金
public class MyMoneyActivity extends MyActivity {

    private MyListView listView;
    private MyMoneyIoListAdapter mAdapter;
    public JSONArray dataList = new JSONArray();
    private int pageNum = 1;
    private int y;
    private int m;
    private String dateString;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected int getLayout() {
        return R.layout.activity_my_money;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        listView = (MyListView) findViewById(R.id.my_money_io);
        mAdapter = new MyMoneyIoListAdapter(this);
        mAdapter.setDanwei("元");
        mAdapter.setDataList(dataList);
        listView.setAdapter(mAdapter);
        listView.setMyPullUpListViewCallBack(new MyListView.MyPullUpListViewCallBack() {
            @Override
            public void scrollBottomState() {
                getDataList();
            }
        });


        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        y = ca.get(Calendar.YEAR);
        m = ca.get(Calendar.MONTH);

        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM", Locale.CHINA);
        sdr.format(new Date());
        dateString = sdr.format(new Date());
        TextView dataView = (TextView) findViewById(R.id.date_view);
        dataView.setText(dateString);
        dataView.setOnTouchListener(new onDoubleClick());

//        dataView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 系统自带日历控件
//                /*new DatePickerDialog(MyMoneyActivity.this,
//					new DatePickerDialog.OnDateSetListener() {
//						@Override
//						public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//							y = year;
//							m = monthOfYear;
//							monthOfYear++;
//							String mm = monthOfYear >= 10 ? monthOfYear + "" : "0" + monthOfYear;
//							dateString = year + "-" + mm;
//							((TextView) findViewById(R.id.date_view)).setText(dateString);
//							reloadDataList();
//						}
//					},
//					y, m, 1).show();*/
//                // ------------------------------------------------------------------
//
//                new CustomerDatePickerDialog(MyMoneyActivity.this,
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                                y = year;
//                                m = monthOfYear;
//                                monthOfYear++;
//                                String mm = monthOfYear >= 10 ? monthOfYear + "" : "0" + monthOfYear;
//                                dateString = year + "-" + mm;
//                                ((TextView) findViewById(R.id.date_view)).setText(dateString);
//                                reloadDataList();
//                            }
//                        },
//                        y, m, 1).show();
//            }
//        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadDataList();
            }
        });
        //getDataList();

        findViewById(R.id.add_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goActivity(AddMoneyActivity.class);
            }
        });
        findViewById(R.id.get_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goActivity(GetMoneyActivity.class);
            }
        });
    }


    private void reloadDataList() {

        dataList = new JSONArray();
        pageNum = 1;
        mAdapter.setDataList(dataList);
        mAdapter.notifyDataSetChanged();
        listView.haveData();
        getDataList();

    }

    protected void onResume() {
        super.onResume();

        reloadDataList();
    }

    private void getDataList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "userIncomeList");
        params.put("type", Config.TaskMoney);
        params.put("user_id", Common.getUserId(this));
        params.put("page_num", pageNum + "");
        params.put("time", dateString);

        Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpSuccessInterface() {
            @Override
            public void run(String result) {
                Log.i("io_list", result);
                swipeRefreshLayout.setRefreshing(false);
                try {
                    JSONObject resultObject = new JSONObject(result);
                    if (!resultObject.getString("status").equals(Config.HttpSuccessCode)) {
                        if (pageNum == 1) {
                            listView.noMoreData("暂无数据");
                        } else {
                            listView.noMoreData();
                        }
                    } else {
                        JSONArray newData = resultObject.getJSONObject("result").getJSONArray("data_array");

                        JSONObject ioTotal = resultObject.getJSONObject("result").getJSONObject("count_array");

                        ((TextView) findViewById(R.id.my_recieve)).setText(ioTotal.getString("num_get"));
                        ((TextView) findViewById(R.id.my_out)).setText(ioTotal.getString("num_use"));

                        for (int i = 0; i < newData.length(); i++) {
                            dataList.put(newData.get(i));
                        }
                        mAdapter.notifyDataSetChanged();
                        pageNum++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // ------------------ 双击不 不处理
	class onDoubleClick implements View.OnTouchListener{
		int count = 0;
		int firClick = 0;
		int secClick = 0;
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(MotionEvent.ACTION_DOWN == event.getAction()){
				count++;
				if(count == 1){
					firClick = (int) System.currentTimeMillis();
                    new CustomerDatePickerDialog(MyMoneyActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    y = year;
                                    m = monthOfYear;
                                    monthOfYear++;
                                    String mm = monthOfYear >= 10 ? monthOfYear + "" : "0" + monthOfYear;
                                    dateString = year + "-" + mm;
                                    ((TextView) findViewById(R.id.date_view)).setText(dateString);
                                    //  导致item 数据重复
                                    reloadDataList();
                                }
                            },
                            y, m, 1).show();

				} else if (count == 2){
					secClick = (int) System.currentTimeMillis();
					if(secClick - firClick < 1000){
						//双击事件   不处理
					}
					count = 0;
					firClick = 0;
					secClick = 0;
				}
			}
			return true;
		}
	}
}
