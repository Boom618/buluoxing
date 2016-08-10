package com.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.buluoxing.famous.R;
import com.buluoxing.famous.user.AddMissionBeansActivity;
import com.buluoxing.famous.user.AddMissionMoneyActivity;
import com.detail.Mission;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.Common;
import com.util.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


public class MyMissionListAdapter extends BaseAdapter {
	public JSONArray dataList;
	private LayoutInflater mInflater;
	public Context context;

	public void setDataList(JSONArray list) {
		dataList = list;
	}
	public MyMissionListAdapter(Context ctx){
		context = ctx;
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
			return object.getInt("id");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public class Holder { public TextView username; }
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		mInflater = LayoutInflater.from(context);
		//观察convertView随ListView滚动情况             
		Log.v("MyListViewBase", "getView " + position + " " + convertView);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.my_mission_item,null);
		}


		try {
			final JSONObject data = dataList.getJSONObject(position);

			((TextView)convertView.findViewById(R.id.mission_title)).setText(data.getString("task_title"));
			((TextView)convertView.findViewById(R.id.mission_type)).setText(data.getString("task_pattern_str"));
			((TextView)convertView.findViewById(R.id.mission_href)).setText(data.getString("link_url"));
			Mission mission = new Mission(data,(Activity)context);

			if(data.getString("task_type").equals(Config.TaskMoney)) {
				Double per = data.getDouble("price")/data.getDouble("number");

				DecimalFormat df=new   java.text.DecimalFormat("#.##");

				String remain  = String.format("剩余：%s元          剩余：%s个名额",mission.remainMoney,mission.surplus);
				((TextView) convertView.findViewById(R.id.misson_remain)).setText(remain);
			} else {
				String remain  = String.format("剩余：%d红豆      剩余：%s个名额",data.getInt("integral")*data.getInt("surplus"),data.getString("surplus"));
				((TextView)convertView.findViewById(R.id.misson_remain)).setText(remain);
			}


			((TextView)convertView.findViewById(R.id.mission_href)).setText(data.getString("link_url"));

			String dateString = Common.getDateStrFromPhpTime(data.getString("starttime"), "MM月dd日")+"-"
				+Common.getDateStrFromPhpTime(data.getString("endtime"), "MM月dd日");

			((TextView)convertView.findViewById(R.id.date_block)).setText(dateString);


//			if(data.getInt("isfinish")==0) {
//				((TextView) convertView.findViewById(R.id.status)).setText("进行中");
//			} else {
//				((TextView) convertView.findViewById(R.id.status)).setText("已结束");
//			}

			if(data.getString("task_type").equals(Config.TaskBeans)) {
				((ImageView)convertView.findViewById(R.id.task_type_icon)).setImageResource(R.drawable.bean);

				convertView.findViewById(R.id.add_more).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, AddMissionBeansActivity.class);
						intent.putExtra("task",data.toString());
						((Activity) context).startActivityForResult(intent, 100);
					}
				});

			} else {
				((ImageView)convertView.findViewById(R.id.task_type_icon)).setImageResource(R.drawable.cash);
				convertView.findViewById(R.id.add_more).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, AddMissionMoneyActivity.class);
						intent.putExtra("task", data.toString());
						((Activity) context).startActivityForResult(intent,101);
					}
				});
			}






		} catch (JSONException e) {
			e.printStackTrace();
		}

		return convertView;
	}
}
