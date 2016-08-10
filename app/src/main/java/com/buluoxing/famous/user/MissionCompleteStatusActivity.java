package com.buluoxing.famous.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adapter.CompletedUserListAdapter;
import com.buluoxing.famous.ImageListActivity;
import com.buluoxing.famous.R;
import com.diy.MyListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
// 做任务完成后的  任务详情
public class MissionCompleteStatusActivity extends Activity {
	private MyListView listView;
	private CompletedUserListAdapter mAdapter;
	private JSONArray dataList = new JSONArray();
	private int pageNum = 1;
	private JSONArray sampleList;
	private JSONObject task;
	private String taskType;
	private JSONObject taskDetail;

	private TextView missTitle; // 任务标题
	private TextView missRequest; // 任务要求
	private TextView missType; 	  // 任务类型
	private TextView missNum; 	  // 任务剩余数量
	private TextView myReward;   	// 我的奖励
	private TextView countReward;   // 总奖励
	private TextView typeReward;   // 奖励类型

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_mission_complete_status);
		setContentView(R.layout.activity_mission_complete_status1);

		initView();  //  初始化控件

		View backView = findViewById(R.id.back);
		if(backView!=null ) {
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		try {
			task = new JSONObject(getIntent().getStringExtra("task"));
			taskType = task.getString("task_type");
			if (taskType.equals("红豆任务") || taskType.equals("1")) {
				taskType = "1";
			} else {
				taskType = "2";
			}
			Log.e("task",task.toString());


			HashMap<String,String> params = new HashMap<>();
			params.put("action","taskDetail");
			try {
				params.put("task_id",task.getString("id"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			params.put("user_id", Common.getUserId(this));

			Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
				@Override
				public void run(String result) {

				}

				@Override
				public void run(JSONObject result) {
					try {
						taskDetail = result.getJSONObject("result");
						Log.e("任务详情", "run: taskDetail = " + taskDetail);
						init();
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

				@Override
				public void run(JSONArray result) {

				}
			});



		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void initView(){
		missTitle = (TextView) findViewById(R.id.id_mission_title);
		missRequest = (TextView) findViewById(R.id.mission_request);
		missType = (TextView) findViewById(R.id.mission_type);
		missNum = (TextView) findViewById(R.id.num_surplus);
		myReward = (TextView) findViewById(R.id.id_my_reward);
		typeReward = (TextView) findViewById(R.id.id_reward_type);
		countReward = (TextView) findViewById(R.id.get_what);
	}
	public void init() throws JSONException {
		String getWhat = "";
		String getAllWhat = "";

		Log.i("fufufufufufufuck",taskType);
		if(taskType.equals(Config.TaskBeans)) {
			getWhat = task.getString("integral"); //  红豆数量
			typeReward.setText("红豆奖励：");
			getWhat = getWhat += "红豆";
			countReward.setText(getWhat);
		} else {
			getWhat = task.getString("price");
			getAllWhat = task.getString("surplus_price");
			getWhat = getWhat += "元";
			typeReward.setText("现金奖励：");
			countReward.setText((Integer.parseInt(getAllWhat) / 100) + "元");
		}

		//((TextView)findViewById(R.id.get_what)).setText(getWhat);

		myReward.setText(getWhat);  //  我的奖励
		missTitle.setText(task.getString("task_title"));
		missRequest.setText(task.getString("task_require"));
		missType.setText(task.getString("task_pattern_str"));
		missNum.setText(task.getString("surplus_num"));


		JSONArray uploadedSampleList = taskDetail.getJSONArray("photo_array");

		HashMap<String,JSONObject> uploadedHashMap = new HashMap<>();

		for (int i=0;i<uploadedSampleList.length();i++) {
			JSONObject sample = uploadedSampleList.getJSONObject(i);
			uploadedHashMap.put(sample.getString("pattern_id"),sample);
		}

		sampleList = taskDetail.getJSONArray("mode_array");

		for (int i=0;i<sampleList.length();i++) {
			JSONObject sample = sampleList.getJSONObject(i);

			if(!sample.getString("img_url").equals("")) {
				sample.put("mode_url", sample.getString("img_url"));
			}
			if(uploadedHashMap.containsKey(sample.getString("pattern_id"))) {
				//sample.put("mode_url",uploadedHashMap.get(sample.getString("pattern_id")).getString("mode_url"));
				sample.put("screen_short",uploadedHashMap.get(sample.getString("pattern_id")).getString("img_url"));
				sample.put("yc_url",uploadedHashMap.get(sample.getString("pattern_id")).getString("yc_url"));
			}
		}

		Log.i("ff",sampleList.toString());


		initSampleList();

		listView = (MyListView)findViewById(R.id.completed_user_list);
		mAdapter = new CompletedUserListAdapter(this);
		mAdapter.setDataList(dataList);
		listView.setAdapter(mAdapter);

		listView.setMyPullUpListViewCallBack(new MyListView.MyPullUpListViewCallBack() {
			@Override
			public void scrollBottomState() {
				getDataList();
			}
		});
		listView.setFocusable(false);
		listView.setFocusableInTouchMode(false);
		getDataList();
	}
	private void getDataList() {
		HashMap<String,String> params = new HashMap<>();
		params.put("action", "finishTaskList");
		try {
			params.put("task_id", task.getString("id"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.put("page_num", pageNum+"");
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				Log.i("user_list", "MissionComplete :" + result);
				try {
					JSONObject resultObject = new JSONObject(result);

					if (!resultObject.getString("status").equals(Config.HttpSuccessCode)) {
						if(pageNum==1) {
							LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) listView.getLayoutParams();
							params1.height = (int) getResources().getDimension(R.dimen.px100);
							listView.setLayoutParams(params1);
						}
						if( pageNum==1) {
							listView.noMoreData("暂无数据");
						} else {
							listView.noMoreData();
						}
					} else {


						JSONArray newData = resultObject.getJSONArray("result");
						if(pageNum==1) {
							LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) listView.getLayoutParams();
							if(newData.length()*(int)getResources().getDimension(R.dimen.px179)<getResources().getDimension(R.dimen.px1920)) {
								params1.height = newData.length()*(int)getResources().getDimension(R.dimen.px179);
							} else {
								//  一次性加载全部数据 （没有分页）
//								params1.height = (int) getResources().getDimension(R.dimen.px1920);
								params1.height = newData.length()*(int)getResources().getDimension(R.dimen.px179);

							}
							listView.setLayoutParams(params1);
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

	public void initSampleList(){

		((LinearLayout) findViewById(R.id.sample_list)).removeAllViews();
		for(int i=0;i<sampleList.length();i++) {

			try {
				JSONObject taskMode = sampleList.getJSONObject(i);
				LayoutInflater mInflater = LayoutInflater.from(this);
				LinearLayout itemView = (LinearLayout) mInflater.inflate(R.layout.sample_do_item, null);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params.topMargin = (int) getResources().getDimension(R.dimen.px55);
				itemView.setLayoutParams(params);
				((LinearLayout) findViewById(R.id.sample_list)).addView(itemView);
				((TextView) itemView.findViewById(R.id.sample_title)).setText(taskMode.getString("title"));
				ImageLoader imageLoader = ImageLoader.getInstance();
				((ImageView) itemView.findViewById(R.id.add_sample_image)).setImageResource(R.drawable.screen_loading);
				((ImageView) itemView.findViewById(R.id.add_image_confirm)).setBackgroundResource(R.drawable.loading_image);

				if(taskType.equals(Config.TaskMoney)) {
					Log.i("fuck,fuck", "fuck");
					itemView.findViewById(R.id.do_mission_confirm).setVisibility(View.VISIBLE);

					if( taskMode.getInt("is_yc")==1) {
						itemView.findViewById(R.id.do_mission_confirm).setVisibility(View.INVISIBLE);
					}
				}



				imageLoader.displayImage(taskMode.getString("mode_url"), (ImageView) itemView.findViewById(R.id.sample), Config.options);


				ImageListActivity.setImageViewCanShowBig((ImageView) itemView.findViewById(R.id.sample), taskMode.getString("mode_url"));

				imageLoader.displayImage(taskMode.getString("screen_short"), (ImageView) itemView.findViewById(R.id.add_sample_image), Config.options);

				ImageListActivity.setImageViewCanShowBig((ImageView) itemView.findViewById(R.id.add_sample_image), taskMode.getString("screen_short"));


				imageLoader.displayImage(taskMode.getString("yc_url"), (ImageView) itemView.findViewById(R.id.add_image_confirm), Config.options);

				ImageListActivity.setImageViewCanShowBig((ImageView) itemView.findViewById(R.id.add_image_confirm), taskMode.getString("yc_url"));

				//((ImageView) itemView.findViewById(R.id.add_image_confirm)).setImageResource(R.drawable.loading_image);
			} catch (JSONException e) {
				e.printStackTrace();
			}
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
