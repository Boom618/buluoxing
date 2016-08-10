package com.buluoxing.famous.user;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.adapter.MissionCompleteUserListAdapter;
import com.adapter.SystemMessageAdapter;
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

public class SystemMessageActivity extends Activity {

	private MyListView listView;
	private SystemMessageAdapter mAdapter;
	private JSONArray dataList = new JSONArray();
	private int pageNum = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_message);

		View backView = findViewById(R.id.back);
		if(backView!=null ) {
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		listView = (MyListView)findViewById(R.id.message_list);
		mAdapter = new SystemMessageAdapter(this);
		mAdapter.setDataList(dataList);
		listView.setAdapter(mAdapter);
		listView.setMyPullUpListViewCallBack(new MyListView.MyPullUpListViewCallBack() {
			@Override
			public void scrollBottomState() {
				//getDataList();
			}
		});
		getDataList();
		readNews();
	}
	private void readNews(){
		HashMap<String,String> params = new HashMap<>();
		params.put("action", "modifyMessage");
		params.put("user_id", Common.getUserId(this));

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/System/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {

			}

			@Override
			public void run(JSONArray result) {

			}
		});

	}
	private void getDataList() {
		HashMap<String,String> params = new HashMap<>();
		params.put("action", "sysMessage");
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
