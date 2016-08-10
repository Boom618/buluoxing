package com.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buluoxing.famous.R;
import com.detail.Mission;
import com.util.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SysMissionListAdapter extends BaseAdapter {
	public JSONArray dataList;
	private LayoutInflater mInflater;
	public Context context;

	public void setDataList(JSONArray list) {
		dataList = list;
	}
	public SysMissionListAdapter(Context ctx){
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
			return 0;
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
		Mission mission = null;
		JSONObject data = null;
		try {
			data = dataList.getJSONObject(position);
			mission = new Mission(data,(Activity)context);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (convertView == null) {
			if(mission.taskType.equals(Config.TaskBeans)) {
				convertView = mInflater.inflate(R.layout.mission_beans_item, null);
			} else {
				convertView = mInflater.inflate(R.layout.mission_money_item, null);
			}
		}
		if(mission!=null) {
			String is_task = null;
			try {
				is_task = data.getString("is_task"); //0 不可做 1 可做
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mission.displayInItemView((RelativeLayout) convertView,is_task);
		}
		return convertView;
	}
}
