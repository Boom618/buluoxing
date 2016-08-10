package com.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buluoxing.famous.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MyBeansIoListAdapter extends BaseAdapter {
	public JSONArray dataList;
	private LayoutInflater mInflater;
	public Context context;

	public void setDataList(JSONArray list) {
		dataList = list;
	}
	public MyBeansIoListAdapter(Context ctx){
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
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.beans_io_item,null);
	                /*得到各个控件的对象*/
			//holder.username = (TextView) convertView.findViewById(R.id.username);
			convertView.setTag(holder);//绑定ViewHolder对象                   }
		} else {
			holder = (Holder) convertView.getTag();//取出ViewHolder对象
		}


//
//		try {
//			JSONObject data = dataList.getJSONObject(position);
//			holder.username.setText(data.getString("username"));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}


		return convertView;
	}
}
