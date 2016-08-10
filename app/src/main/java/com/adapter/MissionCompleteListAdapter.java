package com.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.buluoxing.famous.R;
import com.buluoxing.famous.mission.DoMissionActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.Common;
import com.util.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MissionCompleteListAdapter extends BaseAdapter {
	public JSONArray dataList;
	private LayoutInflater mInflater;
	public Context context;

	public void setDataList(JSONArray list) {
		dataList = list;
	}
	public MissionCompleteListAdapter(Context ctx){
		context = ctx;
		startTimer();
	}
	@Override
	public int getCount() {
		return dataList.length();
	}

	@Override
	public Object getItem(int position) {
		try {
			return (Object)dataList.getJSONObject(position);
		} catch (JSONException e) {
			e.printStackTrace();
			return  null;
		}
	}

	@Override
	public long getItemId(int position) {
		try {
			JSONObject  object= dataList.getJSONObject(position);
			return object.getInt("task_id");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}
	public void startTimer() {
		final android.os.Handler handler = new android.os.Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				for (int i=0;i<dataList.length();i++) {
					try {

						JSONObject mission = dataList.getJSONObject(i);
						View item = (View) mission.get("view");

						if(mission.getString("stype").equals("1")) {
							long wait_seconds = Config.sysConfig.getJSONObject("prompt_conf").getLong("wait_seconds");
							long remain = wait_seconds-(new Date().getTime() - mission.getLong("wait_start")*1000)/1000;

							if(remain<=0){
								item.findViewById(R.id.get_money_now).setVisibility(View.VISIBLE);
								item.findViewById(R.id.complete_status).setVisibility(View.INVISIBLE);
								((TextView)item.findViewById(R.id.get_money_now)).setText("领取"+mission.getString("price")+"元");
								continue;
							} else {

							}

							String text = Common.getMissionRemainGetMoneyText(remain);

							((TextView)item.findViewById(R.id.complete_status)).setText(text);
							((TextView)item.findViewById(R.id.complete_status)).setTextColor(Color.parseColor("#ff0000"));
						}


					} catch (JSONException e) {
						//e.printStackTrace();
					}
				}

			};
		};
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// 需要做的事:发送消息
				Message message = new Message();
				handler.sendMessage(message);
			}
		};
		timer.schedule(task, 0, 1000); // 1s后执行task,经过1s再次执行
	}
	// public class Holder { public TextView username; }
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Holder holder;
		mInflater = LayoutInflater.from(context);
		//观察convertView随ListView滚动情况             
		Log.v("MyListViewBase", "getView " + position + " " + convertView);
		if (convertView == null){
			convertView = mInflater.inflate(R.layout.mission_complete_item,null);
		}

	            /*得到各个控件的对象*/
		try {
			dataList.getJSONObject(position).put("view",convertView);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			final JSONObject data = dataList.getJSONObject(position);

			((TextView)convertView.findViewById(R.id.username)).setText(data.getString("user_name"));
			((TextView)convertView.findViewById(R.id.area)).setText(data.getString("user_domain"));
			((TextView)convertView.findViewById(R.id.city)).setText(data.getString("user_city"));
			((TextView)convertView.findViewById(R.id.complete_time)).setText(Common.getDateStrFromPhpTime(data.getString("finishdate"), "yyyy-MM-dd HH:mm"));


			((TextView)convertView.findViewById(R.id.mission_title)).setText(data.getString("task_title"));
			((TextView)convertView.findViewById(R.id.mission_request)).setText(data.getString("task_require"));
			((TextView)convertView.findViewById(R.id.mission_type)).setText(data.getString("task_pattern_str"));

			if(data.getString("task_type").equals(Config.TaskBeans)) {
				((TextView)convertView.findViewById(R.id.get_what)).setText("奖励红豆：");
				((TextView)convertView.findViewById(R.id.avg_money)).setText(data.getString("integral")+"红豆");
				((TextView)convertView.findViewById(R.id.complete_status)).setText("已领取"+data.getString("integral")+"红豆");
			} else {
				((TextView)convertView.findViewById(R.id.get_what)).setText("任务奖励：");
				((TextView)convertView.findViewById(R.id.avg_money)).setText(data.getString("price") + "元");
				if (data.getString("stype").equals("2")) {
					((TextView) convertView.findViewById(R.id.complete_status)).setText("已领取" + data.getString("price") + "元");
					((TextView) convertView.findViewById(R.id.complete_status)).setVisibility(View.VISIBLE);
				}
				convertView.findViewById(R.id.get_money_now).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, DoMissionActivity.class);
						intent.putExtra("task_type",Config.TaskMoney);
						intent.putExtra("task",data.toString());
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
					}
				});
			}


			//((TextView)convertView.findViewById(R.id.mission_href)).setText(data.getString("link_url"));




			ImageView imageView  = (ImageView) convertView.findViewById(R.id.user_icon);
			imageView.setImageResource(R.drawable.default_avatar);

			ImageLoader imageLoader = ImageLoader.getInstance();

			imageLoader.displayImage(data.getString("user_photo"),imageView,Config.options);


		} catch (JSONException e) {
			e.printStackTrace();
		}


		return convertView;
	}
}
