package com.buluoxing.famous.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.buluoxing.famous.SelectTimeActivity;
import com.diy.AutoNewLineLayout;
import com.diy.MyCheckBox;
import com.util.Common;
import com.util.Config;
import com.util.FileUpload;
import com.util.Http;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
// 追加红豆任务
public class AddMissionBeansActivity extends MyActivity {

	private HashMap<String, JSONObject> modeAssoc = new HashMap<>();
	private JSONObject task;
	private Long endTime;

	@Override
	protected int getLayout() {
		return R.layout.activity_add_mission_beans;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			task = new JSONObject(getIntent().getStringExtra("task"));
			JSONObject taskModelListMap = null;
			taskModelListMap = Config.sysConfig.getJSONObject("task_mode_conf");
			JSONArray weChatList = taskModelListMap.getJSONArray("wechat");
			JSONArray weiBoList = taskModelListMap.getJSONArray("weibo");
			JSONArray otherList = taskModelListMap.getJSONArray("other");

			for (int i=0;i<weChatList.length();i++) {
				JSONObject taskMode = weChatList.getJSONObject(i);
				Log.i("fuck",taskMode.toString());
				modeAssoc.put(taskMode.getString("id"),taskMode);
			}
			for (int i=0;i<weiBoList.length();i++) {
				JSONObject taskMode = weiBoList.getJSONObject(i);
				modeAssoc.put(taskMode.getString("id"),taskMode);
			}
			for (int i=0;i<otherList.length();i++) {
				JSONObject taskMode = otherList.getJSONObject(i);
				modeAssoc.put(taskMode.getString("id"),taskMode);
			}

			JSONArray modeList = task.getJSONArray("mode_array");

			int lowSpend = 0;
			for (int i=0;i<modeList.length();i++) {
				String mid = modeList.getJSONObject(i).getString("pattern_id");
				lowSpend += modeAssoc.get(mid).getInt("inlow");
			}

			findTextViewById(R.id.tips).setText(String.format("每次任务最低消费%d红豆",lowSpend));

			findViewById(R.id.end_time).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(AddMissionBeansActivity.this, SelectTimeActivity.class);
					intent.putExtra("code", 101);
					startActivityForResult(intent, 101);
				}
			});

			final int finalLowSpend = lowSpend;
			((EditText)findViewById(R.id.mission_number)).addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					try {
						int missionNumber = Integer.parseInt(findTextViewById(R.id.mission_number).getText().toString());

						findTextViewById(R.id.total_spend).setText(String.format("任务奖励，本次任务共消费%d红豆", missionNumber * finalLowSpend));
					} catch (Exception e) {

					}
				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});

			findViewById(R.id.addadd).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					HashMap<String, String> params = new HashMap<String, String>();
					params.put("action", "userAddTask");
					params.put("user_id", Common.getUserId(AddMissionBeansActivity.this));
					try {
						params.put("task_id", task.getString("id"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					params.put("number", findTextViewById(R.id.mission_number).getText().toString());
					params.put("type", "1");
					params.put("end_time", Common.getDateStrFromTime(endTime, "yyyy-MM-dd HH:mm:ss"));

					final Common.LoadingHandler handler = Common.loading(AddMissionBeansActivity.this);
					Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
						@Override
						public void run(String result) {
							handler.close();
						}

						@Override
						public void run(JSONObject result) {
							try {
								if (result.getString("status").equals(Config.HttpSuccessCode)) {
									setResult(100);
									finish();
									Toast.makeText(AddMissionBeansActivity.this, "追加成功", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(AddMissionBeansActivity.this, result.getString("message"), Toast.LENGTH_SHORT).show();
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
			findTextViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});

			endTime = task.getLong("endtime")*1000;

			SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分",
				Locale.CHINA);
			String dateString = sdr.format(new Date(task.getLong("endtime")*1000));
			((TextView)findViewById(R.id.end_time)).setText(dateString);


		} catch (JSONException e) {
			e.printStackTrace();
		}


	}


	@Override
	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		 if(resultCode==101) {
			Long time = data.getLongExtra("time",0);
			endTime = time;
			SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分",
				Locale.CHINA);
			String dateString = sdr.format(new Date(endTime));

			((TextView)findViewById(R.id.end_time)).setText(dateString);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
