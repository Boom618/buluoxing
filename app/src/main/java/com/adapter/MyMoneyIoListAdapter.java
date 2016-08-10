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
import com.util.Config;
import com.util.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MyMoneyIoListAdapter extends BaseAdapter {
	public JSONArray dataList;
	private LayoutInflater mInflater;
	public Context context;
	public String danwei = "元";
	public void setDanwei(String danwei) {
		this.danwei = danwei;
	}

	public void setDataList(JSONArray list) {
		dataList = list;
	}
	public MyMoneyIoListAdapter(Context ctx){
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
		return 0;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		mInflater = LayoutInflater.from(context);
		//观察convertView随ListView滚动情况             
		Log.v("MyListViewBase", "getView " + position + " " + convertView);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.money_io_item,null);
	                /*得到各个控件的对象*/
			//holder.username = (TextView) convertView.findViewById(R.id.username);
		}
		try {
			JSONObject data = dataList.getJSONObject(position);

			((TextView) convertView.findViewById(R.id.detail)).setText(data.getString("type")+"  "+data.getString("number")+danwei);
			((TextView) convertView.findViewById(R.id.status)).setText(data.getString("status"));
			((TextView) convertView.findViewById(R.id.status)).setText(data.getString("status"));
			((TextView) convertView.findViewById(R.id.io_type)).setText(data.getString("type_name"));

			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(Common.getUserInfo(context).photo,(ImageView)convertView.findViewById(R.id.user_icon), Config.options);

			Common.controlUserType(convertView,Common.getUserInfo(context).type);


			Long regdate = data.getLong("regdate");
			regdate = regdate*1000;

			SimpleDateFormat sdr = new SimpleDateFormat("MM-dd HH:mm",
				Locale.CHINA);
			String dateString = sdr.format(new Date(regdate));
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(regdate));

			String[] dateDetail = dateString.split(" ");
			String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
			int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
			if (w < 0){
				w = 0;
			}
			((TextView) convertView.findViewById(R.id.day)).setText(weekDays[w]);

			TextView time = (TextView) convertView.findViewById(R.id.time);

			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd",Locale.CHINA);
			String day = sdf.format(new Date());
			String service = dateDetail[0];
			if (service.equals(day)){
				// 显示 当天时间  14:25
				time.setText(dateDetail[1]);
			}else {
				// 不是今天 显示 日期  7-25
				time.setText(dateDetail[0]);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
}
