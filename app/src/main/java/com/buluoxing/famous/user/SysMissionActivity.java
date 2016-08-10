package com.buluoxing.famous.user;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.adapter.BeansMissionListAdapter;
import com.adapter.SysMissionListAdapter;
import com.buluoxing.famous.R;
import com.diy.MyListView;
import com.umeng.analytics.MobclickAgent;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SysMissionActivity extends Activity {

	private MyListView listView;
	private SysMissionListAdapter mAdapter;
	private JSONArray dataList = new JSONArray();
	private int pageNum = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sys_mission);

		listView = (MyListView)findViewById(R.id.mission_list);
		mAdapter = new SysMissionListAdapter(this);
		mAdapter.setDataList(dataList);
		listView.setAdapter(mAdapter);
		listView.setMyPullUpListViewCallBack(new MyListView.MyPullUpListViewCallBack() {
			@Override
			public void scrollBottomState() {
				getDataList();
			}
		});
		getDataList();
	}
	private void getDataList() {
		HashMap<String,String> params = new HashMap<>();
		params.put("action", "packegTaskList");
		params.put("user_id", Common.getUserId(this));
		params.put("page_num", pageNum + "");

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/System/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				Log.i("io_list", result);
				try {
					JSONObject resultObject = new JSONObject(result);
					if (!resultObject.getString("status").equals(Config.HttpSuccessCode)) {
						if( pageNum==1) {
							listView.noMoreData("暂无数据");
						} else {
							listView.noMoreData();
						}
					} else {
						JSONArray newData = resultObject.getJSONArray("result");

						if (newData.length() == 0) {
							if( pageNum==1) {
								listView.noMoreData("暂无数据");
							} else {
								listView.noMoreData();
							}
							return;
						}
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
