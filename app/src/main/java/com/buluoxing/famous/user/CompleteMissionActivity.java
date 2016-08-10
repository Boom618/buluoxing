package com.buluoxing.famous.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.MissionCompleteListAdapter;
import com.adapter.MyMoneyIoListAdapter;
import com.buluoxing.famous.R;
import com.buluoxing.famous.mission.DoMissionActivity;
import com.diy.MyListView;
import com.umeng.analytics.MobclickAgent;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
// 个人中心 - 已结任务
public class CompleteMissionActivity extends Activity {

	private MyListView listView;
	private MissionCompleteListAdapter mAdapter;
	private JSONArray dataList = new JSONArray();
	private int pageNum = 1;
	private SwipeRefreshLayout swipeRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complete_mission);
		View backView = findViewById(R.id.back);
		if(backView!=null ) {
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		listView = (MyListView)findViewById(R.id.mission_list);
		//mAdapter = new MissionCompleteListAdapter(this);
		mAdapter = new MissionCompleteListAdapter(getApplicationContext());
		mAdapter.setDataList(dataList);
		listView.setAdapter(mAdapter);
		listView.setMyPullUpListViewCallBack(new MyListView.MyPullUpListViewCallBack() {
			@Override
			public void scrollBottomState() {
				getDataList();
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				try {
					JSONObject mission = dataList.getJSONObject(position);
					if(mission.getString("stype").equals("1")) {
						Intent intent = new Intent(CompleteMissionActivity.this,DoMissionActivity.class);
						intent.putExtra("task_id",mission.getString("id"));
						intent.putExtra("task_type",mission.getString("task_type"));
						intent.putExtra("task",mission.toString());
						startActivity(intent);
					} else {
						Intent intent = new Intent(CompleteMissionActivity.this, MissionCompleteStatusActivity.class);
						try {
							String task = dataList.getJSONObject(position).toString();
							intent.putExtra("task", task);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						startActivity(intent);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}



			}
		});
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		//设置刷新时动画的颜色，可以设置4个
		swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				reloadDataBeansList();
			}
		});
		reloadDataBeansList();
	}
	private void reloadDataBeansList(){
		dataList = new JSONArray();
		pageNum = 1;
		mAdapter.setDataList(dataList);
		mAdapter.notifyDataSetChanged();
		listView.haveData();
		getDataList();
	}
	private void getDataList() {
		HashMap<String,String> params = new HashMap<>();
		params.put("action", "userTaskList");
		params.put("type", "2");
		params.put("user_id", Common.getUserId(this));
		params.put("page_num", pageNum + "");
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				Log.i("io_list", result);
				try {
					swipeRefreshLayout.setRefreshing(false);
					JSONObject resultObject = new JSONObject(result);
					if (!resultObject.getString("status").equals(Config.HttpSuccessCode)) {
						if( pageNum==1) {
							listView.noMoreData("暂无数据");
						} else {
							listView.noMoreData();
						}
					} else {
						JSONArray newData = resultObject.getJSONObject("result").getJSONArray("task_list");

						String  taskCount = resultObject.getJSONObject("result").getString("task_count");

						((TextView) findViewById(R.id.task_count)).setText(taskCount);

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
