package com.buluoxing.famous.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.MissionCompleteListAdapter;
import com.adapter.MyMissionListAdapter;
import com.buluoxing.famous.mission.DoMissionActivity;
import com.buluoxing.famous.mission.MissionStatusActivity;
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
// 个人中心 - 已发布任务  任务进度
public class MyMissionActivity extends Activity {
	private MyListView listView;
	private MyMissionListAdapter mAdapter;
	private JSONArray dataList = new JSONArray();
	private int pageNum = 1;


	private MyListView listView2;
	private MyMissionListAdapter mAdapter2;
	private JSONArray dataList2 = new JSONArray();
	private int pageNum2 = 1;
	private String currentTaskType = "1";
	private SwipeRefreshLayout swipeRefreshLayout;
	private SwipeRefreshLayout swipeRefreshLayout2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_mission);

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
		mAdapter = new MyMissionListAdapter(this);
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
				Intent intent = new Intent(MyMissionActivity.this, MyMissionStatusActivity.class);
				try {
					intent.putExtra("task_id", dataList.getJSONObject(position).getString("id"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				startActivity(intent);
			}
		});



		listView2 = (MyListView)findViewById(R.id.mission_list2);
		mAdapter2 = new MyMissionListAdapter(this);
		mAdapter2.setDataList(dataList2);
		listView2.setAdapter(mAdapter2);
		listView2.setMyPullUpListViewCallBack(new MyListView.MyPullUpListViewCallBack() {
			@Override
			public void scrollBottomState() {
				getDataList2();
			}
		});




		listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(MyMissionActivity.this, MyMissionStatusActivity.class);
				try {
					intent.putExtra("task_id", dataList2.getJSONObject(position).getString("id"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				startActivity(intent);
			}
		});

		findViewById(R.id.show_beans).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				v.setBackgroundResource(R.drawable.tab_select);
				((TextView) v).setTextColor(Color.parseColor("#47dfe4"));
				findViewById(R.id.swipe_container).setVisibility(View.VISIBLE);

				((TextView) findViewById(R.id.show_money)).setBackgroundResource(0);
				((TextView) findViewById(R.id.show_money)).setTextColor(Color.parseColor("#919296"));
				findViewById(R.id.swipe_container2).setVisibility(View.GONE);

				 currentTaskType = Config.TaskBeans;
			}
		});

		findViewById(R.id.show_money).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				v.setBackgroundResource(R.drawable.tab_select);
				((TextView) v).setTextColor(Color.parseColor("#47dfe4"));
				findViewById(R.id.swipe_container2).setVisibility(View.VISIBLE);

				((TextView) findViewById(R.id.show_beans)).setBackgroundResource(0);
				((TextView) findViewById(R.id.show_beans)).setTextColor(Color.parseColor("#919296"));
				findViewById(R.id.swipe_container).setVisibility(View.GONE);
				currentTaskType = Config.TaskMoney;
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
		swipeRefreshLayout2 = (SwipeRefreshLayout) findViewById(R.id.swipe_container2);
		//设置刷新时动画的颜色，可以设置4个
		swipeRefreshLayout2.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

		swipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				reloadDataMoneyList();
			}
		});

		reloadDataMoneyList();
		reloadDataBeansList();
	}
	private void reloadDataBeansList(){

		dataList = new JSONArray();
		pageNum = 1;
		mAdapter.setDataList(dataList);
		mAdapter.notifyDataSetChanged();
		listView.haveData();
		Log.i("fuck", "fuck");
		getDataList();

	}
	private void reloadDataMoneyList(){
		dataList2 = new JSONArray();
		pageNum2 = 1;
		mAdapter2.setDataList(dataList2);
		mAdapter2.notifyDataSetChanged();
		listView2.haveData();
		getDataList2();
	}

	private void getDataList() {
		HashMap<String,String> params = new HashMap<>();
		params.put("action", "userTaskList");
		params.put("type", "1");
		params.put("task_type", "1");
		params.put("user_id", Common.getUserId(this));
		params.put("page_num", pageNum + "");

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				try {
					swipeRefreshLayout.setRefreshing(false);
					Log.i("result", result);
					JSONObject resultObject = new JSONObject(result);
					if (!resultObject.getString("status").equals(Config.HttpSuccessCode)) {
						if( pageNum==1) {
							listView.noMoreData("暂无数据");
						} else {
							listView.noMoreData();
						}
					} else {
						JSONArray newData = resultObject.getJSONObject("result").getJSONArray("task_list");

						String taskCount = resultObject.getJSONObject("result").getString("task_count");

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
	private void getDataList2() {
		HashMap<String,String> params = new HashMap<>();
		params.put("action", "userTaskList");
		params.put("type", "1");
		params.put("task_type", "2");
		params.put("user_id", Common.getUserId(this));
		params.put("page_num", pageNum2 + "");

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				try {
					swipeRefreshLayout2.setRefreshing(false);
					Log.i("result",result);
					JSONObject resultObject = new JSONObject(result);
					if (!resultObject.getString("status").equals(Config.HttpSuccessCode)) {
						if( pageNum2==1) {
							listView2.noMoreData("暂无数据");
						} else {
							listView2.noMoreData();
						}
					} else {
						JSONArray newData = resultObject.getJSONObject("result").getJSONArray("task_list");

						String taskCount = resultObject.getJSONObject("result").getString("task_count");

						((TextView) findViewById(R.id.task_count)).setText(taskCount);

						for (int i = 0; i < newData.length(); i++) {
							dataList2.put(newData.get(i));
						}
						mAdapter2.notifyDataSetChanged();
						pageNum2++;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		//reloadDataBeansList();
		//reloadDataMoneyList();
	}
	public void onActivityResult( int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.i("fucuufufufuf","fklasjkfasdfasdf");
		if(requestCode==101 && resultCode==101) {
			reloadDataMoneyList();
		}
		if(requestCode==100&& resultCode==100){
			reloadDataBeansList();
		}
	}


	@Override
	protected void onPause() {
		super.onPause();

		MobclickAgent.onPause(this);
	}
}
