package com.buluoxing.famous.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.adapter.BeansMissionListAdapter;
import com.adapter.FollowListAdapter;
import com.adapter.KolListAdapter;
import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.buluoxing.famous.kol.KolActivity;
import com.diy.MyListView;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class FollowActivity extends MyActivity {


	private MyListView listViewFollow;
	private FollowListAdapter mAdapterFollow;
	private JSONArray dataListFollow =new JSONArray();
	private int pageNumFollow = 1;
	private String type;
	private String fUserId = "";

	@Override
	protected int getLayout() {
		return R.layout.activity_follow;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getIntent().getStringExtra("type").equals("follow")) {
			findTextViewById(R.id.title).setText(getResources().getString(R.string.followers));
			type = "1";
		} else {
			findTextViewById(R.id.title).setText(getResources().getString(R.string.fans));
			type = "2";
		}
		listViewFollow = (MyListView)findViewById(R.id.follow_list);
		mAdapterFollow = new FollowListAdapter(this);
		mAdapterFollow.setDataList(dataListFollow);
		listViewFollow.setAdapter(mAdapterFollow);
		listViewFollow.setMyPullUpListViewCallBack(new MyListView.MyPullUpListViewCallBack() {
			@Override
			public void scrollBottomState() {
				getDataFollowList();
			}
		});


		if(getIntent().getStringExtra("fuser_id")!=null) {
			fUserId = getIntent().getStringExtra("fuser_id");
		} else {
			fUserId = Common.getUserId(this);
		}

		getDataFollowList();
		listViewFollow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					JSONObject user = dataListFollow.getJSONObject(position);
					if(user.getInt("type")!=0) {
						Intent intent = new Intent(FollowActivity.this, KolActivity.class);
						intent.putExtra("kol_id", user.getString("id"));
						startActivity(intent);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	public void reloadDataList(){

		dataListFollow = new JSONArray();
		pageNumFollow = 1;
		mAdapterFollow.setDataList(dataListFollow);
		mAdapterFollow.notifyDataSetChanged();
		listViewFollow.haveData();
		getDataFollowList();

	}
	public void getDataFollowList(){
		HashMap<String,String> params = new HashMap<>();
		params.put("action","followList");
		params.put("user_id", Common.getUserId(this));
		params.put("type", type);
		params.put("fuser_id", fUserId);
		params.put("page_num", pageNumFollow + "");
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				try {
					JSONObject resultObject = new JSONObject(result);
					Log.i("follow_list", result);
					if (!resultObject.getString("status").equals(Config.HttpSuccessCode)) {
						if( pageNumFollow==1) {
							listViewFollow.noMoreData("暂无数据");
						} else {
							listViewFollow.noMoreData();
						}
					} else {
						JSONArray newData = resultObject.getJSONArray("result");
						for (int i = 0; i < newData.length(); i++) {
							dataListFollow.put(newData.get(i));
						}
						mAdapterFollow.notifyDataSetChanged();
						pageNumFollow++;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
