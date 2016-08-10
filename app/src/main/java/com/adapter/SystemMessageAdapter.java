package com.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.buluoxing.famous.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.Common;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class SystemMessageAdapter extends BaseAdapter {
	public JSONArray dataList;
	private LayoutInflater mInflater;
	public Context context;

	public void setDataList(JSONArray list) {
		dataList = list;
	}

	public SystemMessageAdapter(Context ctx) {
		context = ctx;
	}

	@Override
	public int getCount() {
		return dataList.length();
	}

	@Override
	public Object getItem(int position) {
		try {
			return (Object) dataList.getJSONObject(position);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		try {
			JSONObject object = dataList.getJSONObject(position);
			return object.getInt("id");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		mInflater = LayoutInflater.from(context);
		//观察convertView随ListView滚动情况             
		Log.v("MyListViewBase", "getView " + position + " " + convertView);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sys_message_item, null);
		}

		try {
			JSONObject data = dataList.getJSONObject(position);
			((TextView) convertView.findViewById(R.id.message)).setText(data.getString("message"));
			((TextView) convertView.findViewById(R.id.date_time)).setText(Common.getDateStrFromPhpTime(data.getString("starttime"),"yyyy年MM月dd日 HH:MM"));
			if(data.getInt("is_show")==1) {
				((TextView) convertView.findViewById(R.id.date_time)).setBackgroundResource(R.drawable.radius_rect_disable);
			} else {
				((TextView) convertView.findViewById(R.id.date_time)).setBackgroundResource(R.drawable.radius_login);
			}


		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
}
