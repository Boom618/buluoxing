package com.buluoxing.famous.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.buluoxing.famous.SelectTimeActivity;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
// MyMissionListAdapter - 追加现金任务
public class AddMissionMoneyActivity extends MyActivity {

	private Long endTime;
	private JSONObject task;
	private HashMap<String,JSONObject> modeAssoc = new HashMap<>();
	private double changePercent;
	public double totalLowSpend = 0;
	@Override
	protected int getLayout() {
		return R.layout.activity_add_mission_money;


	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			String string = Config.sysConfig.getJSONObject("prompt_conf").getString("hb_charge");
			Log.d("Sys", "onCreate: string :" + string);
			findTextViewById(R.id.rate_info).setText(Config.sysConfig.getJSONObject("prompt_conf").getString("hb_charge"));

			int change =Config.sysConfig.getJSONObject("prompt_conf").getInt("hb_charge_percent");
			changePercent = change/100.0;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			task = new JSONObject(getIntent().getStringExtra("task"));
			JSONObject taskModelListMap = null;
			taskModelListMap = Config.sysConfig.getJSONObject("task_mode_conf");
			JSONArray weChatList = taskModelListMap.getJSONArray("wechat");
			JSONArray weiBoList = taskModelListMap.getJSONArray("weibo");
			JSONArray otherList = taskModelListMap.getJSONArray("other");

			for (int i=0;i<weChatList.length();i++) {
				JSONObject taskMode = weChatList.getJSONObject(i);
				Log.i("fuck", taskMode.toString());
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
				lowSpend += modeAssoc.get(mid).getInt("molow")/100;
			}

//			findTextViewById(R.id.tips).setText(String.format("平均每任务最低%d元",lowSpend));

			findViewById(R.id.end_time).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(AddMissionMoneyActivity.this, SelectTimeActivity.class);
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
						totalLowSpend =  finalLowSpend * missionNumber/(1-changePercent);
						findTextViewById(R.id.tips_2).setText(String.format("任务总额不低于%.2f元", totalLowSpend));
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

					HashMap<String,String> params = new HashMap<String, String>();
					params.put("action","userAddTask");
					params.put("user_id", Common.getUserId(AddMissionMoneyActivity.this));
					try {
						params.put("task_id",task.getString("id"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					params.put("number",findTextViewById(R.id.mission_number).getText().toString());

					String priceTotal = findTextViewById(R.id.price_total).getText().toString();
					if(priceTotal.equals("")) {

						Toast.makeText(AddMissionMoneyActivity.this,"追加总金额不能为空!",Toast.LENGTH_SHORT).show();
						return;
					}

					double priceToal = Double.parseDouble(priceTotal);
					if(priceToal<totalLowSpend) {
						Toast.makeText(AddMissionMoneyActivity.this,String.format("追加总金额不能小于%.2f元!",totalLowSpend),Toast.LENGTH_SHORT).show();
						return;
					}

					params.put("price",Double.parseDouble(findTextViewById(R.id.price_total).getText().toString())*100+"");
					params.put("type","2");
					params.put("end_time", Common.getDateStrFromTime(endTime, "yyyy-MM-dd HH:mm:ss"));

					final Common.LoadingHandler handler = Common.loading(AddMissionMoneyActivity.this);
					Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
						@Override
						public void run(String result) {
							handler.close();
						}

						@Override
						public void run(JSONObject result) {
							try {
								if (result.getString("status").equals(Config.HttpSuccessCode)) {
									setResult(101);
									finish();
									Toast.makeText(AddMissionMoneyActivity.this, "追加成功", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(AddMissionMoneyActivity.this, result.getString("message"), Toast.LENGTH_SHORT).show();
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
