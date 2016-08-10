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
import com.util.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CompletedUserListAdapter extends BaseAdapter {
	public JSONArray dataList;
	private LayoutInflater mInflater;
	public Context context;

	public void setDataList(JSONArray list) {
		dataList = list;
	}
	public CompletedUserListAdapter(Context ctx){
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

	public class Holder {
		public TextView day;
		public TextView time;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		mInflater = LayoutInflater.from(context);
		//观察convertView随ListView滚动情况             
		Log.v("MyListViewBase", "getView " + position + " " + convertView);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.comleted_user_item,null);
		}



		try {
			JSONObject data = dataList.getJSONObject(position);
			((TextView)convertView.findViewById(R.id.username)).setText(data.getString("nickname"));

			Long completeTime = data.getLong("regdate")*1000;

			SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd",
				Locale.CHINA);
			String dateString = sdr.format(new Date(completeTime));
			((TextView)convertView.findViewById(R.id.complete_time)).setText(dateString);

			String danwei = "个红豆";
			if(data.getString("type").equals("2")) {
				danwei = "元";
			}

			String getWhat = String.format("+ %s%s",data.getString("number"),danwei);

			((TextView)convertView.findViewById(R.id.get_money)).setText(getWhat);

			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(data.getString("photo"),(ImageView)convertView.findViewById(R.id.user_icon), Config.options);

		} catch (JSONException e) {
			e.printStackTrace();
		}


		return convertView;
	}
}
