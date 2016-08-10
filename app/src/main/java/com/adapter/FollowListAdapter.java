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
import com.buluoxing.famous.user.FollowActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
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


public class FollowListAdapter extends BaseAdapter {
	public JSONArray dataList;
	private LayoutInflater mInflater;
	public Context context;

	public void setDataList(JSONArray list) {
		dataList = list;
	}

	public FollowListAdapter(Context ctx) {
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
			convertView = mInflater.inflate(R.layout.follow_item, null);
		}

		try {
			JSONObject data = dataList.getJSONObject(position);
			((TextView) convertView.findViewById(R.id.username)).setText(data.getString("nickname"));


			Long t = data.getLong("regdate");
			t = t*1000;

			SimpleDateFormat sdr  = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			String followTime  = sdr.format(new Date(t));

			((TextView) convertView.findViewById(R.id.date)).setText(followTime);

			ImageLoader imageLoader = ImageLoader.getInstance();
			((ImageView) convertView.findViewById(R.id.user_icon)).setImageResource(R.drawable.default_avatar);
			imageLoader.displayImage(data.getString("photo"), (ImageView) convertView.findViewById(R.id.user_icon));



			if(Common.getUserId(context).equals(data.getString("id"))) {
				((ImageView)convertView.findViewById(R.id.follow_btn)).setVisibility(View.INVISIBLE);
			} else {
				((ImageView)convertView.findViewById(R.id.follow_btn)).setVisibility(View.VISIBLE);
			}

			((ImageView)convertView.findViewById(R.id.follow_btn)).setImageResource(data.getString("is_follow").equals("1") ? R.drawable.unfollow : R.drawable.follow);


			((ImageView)convertView.findViewById(R.id.follow_btn)).setTag(data);

			Common.controlUserType(convertView,data.getString("type"));
			convertView.findViewById(R.id.follow_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					JSONObject data = (JSONObject)v.getTag();
					try {
						HashMap<String,String>params = new HashMap<String, String>();
						params.put("action","handleFollowUser");
						params.put("user_id", Common.getUserId(context));
						params.put("follow_id", data.getString("id"));
						if(data.getString("is_follow").equals("1")) {
							params.put("status","3");
							((ImageView)v).setImageResource(R.drawable.follow);
							data.put("is_follow", "0");



						} else {
							params.put("status","1");
							((ImageView)v).setImageResource(R.drawable.unfollow);
							data.put("is_follow", "1");
						}
						v.setTag(data);
						Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpSuccessInterface() {
							@Override
							public void run(String result) {
								Log.i("follow",result);
							}
						});
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
}