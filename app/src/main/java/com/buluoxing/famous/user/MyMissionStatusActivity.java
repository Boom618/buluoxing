package com.buluoxing.famous.user;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.adapter.MissionCompleteUserListAdapter;
import com.adapter.MyMoneyIoListAdapter;
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
// 已发布任务 - 任务进度 - 任务进度状态
public class MyMissionStatusActivity extends Activity {

	private MyListView listView;
	private MissionCompleteUserListAdapter mAdapter;
	private JSONArray dataList = new JSONArray();
	private int pageNum = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_mission_status);

		View backView = findViewById(R.id.back);
		if(backView!=null ) {
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
		listView = (MyListView)findViewById(R.id.completed_user_list);
		mAdapter = new MissionCompleteUserListAdapter(this);
		mAdapter.setDanwei("元");
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
	public void reloadDatalist() {
		dataList = new JSONArray();
		pageNum = 1;
		mAdapter.setDataList(dataList);
		mAdapter.notifyDataSetChanged();
		listView.haveData();
		getDataList();
	}

	private void getDataList() {
		HashMap<String,String> params = new HashMap<>();
		params.put("action", "userReleaseTaskInfo");
		params.put("task_id", getIntent().getStringExtra("task_id"));
		params.put("user_id", Common.getUserId(this));
		params.put("page_num", pageNum + "");

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpSuccessInterface() {
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
						JSONArray newData = resultObject.getJSONObject("result").getJSONArray("data_array");


						int total = resultObject.getJSONObject("result").getInt("count_all");
						int remain = resultObject.getJSONObject("result").getInt("count_surplus");

						((TextView) findViewById(R.id.remain_number)).setText(remain + "");
						((TextView) findViewById(R.id.number_complete)).setText((total - remain) + "");
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
	public void onActivityResult( int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("fucuufufufuf", "fklasjkfasdfasdf");
		if(requestCode==101 && resultCode==101) {
			reloadDatalist();
		}
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
