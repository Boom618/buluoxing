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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buluoxing.famous.ImageListActivity;
import com.buluoxing.famous.R;
import com.buluoxing.famous.user.ReportConfirmActivity;
import com.bumptech.glide.Glide;
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


public class MissionCompleteUserListAdapter extends BaseAdapter {
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
	public MissionCompleteUserListAdapter(Context ctx){
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
			convertView = mInflater.inflate(R.layout.my_mission_user_item,null);
		}
		try {
			final JSONObject data = dataList.getJSONObject(position);
			final JSONObject dataRow = data;

			String getWhat = data.getString("type").equals("红豆任务")?"+  "+data.getString("number")+"红豆":"+  "+data.getString("number")+"元";
			((TextView) convertView.findViewById(R.id.detail)).setText(getWhat);
			((TextView) convertView.findViewById(R.id.user_name)).setText(data.getString("nickname"));
			((ImageView) convertView.findViewById(R.id.report)).setImageResource(data.getInt("is_report") == 0 ? R.drawable.report : R.drawable.reported);

			if(data.getInt("stype")==2) {

				((TextView) convertView.findViewById(R.id.complete_time)).setText("完成时间：" + Common.getDateStrFromPhpTime(data.getString("regdate"), "yyyy-MM-dd"));
			} else {
				((TextView) convertView.findViewById(R.id.complete_time)).setText("已接受任务");
			}

			ImageLoader imageLoader  = ImageLoader.getInstance();
			imageLoader.displayImage(data.getString("photo"), (ImageView) convertView.findViewById(R.id.user_icon), Config.options);

			final View selfView = convertView;
			convertView.findViewById(R.id.see_detail).setTag(false);
			convertView.findViewById(R.id.see_detail).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					if ((boolean) v.getTag()) {
						((LinearLayout) selfView.findViewById(R.id.user_upload_list)).removeAllViews();
						((ImageView) selfView.findViewById(R.id.see_detail)).setImageResource(R.drawable.see_detail_down);
						v.setTag(false);
						return;
					}
					((ImageView) selfView.findViewById(R.id.see_detail)).setImageResource(R.drawable.see_detail_up);
					v.setTag(true);


					HashMap<String, String> params = new HashMap<String, String>();
					params.put("action", "userFinishTaskInfo");
					try {
						params.put("finish_id", data.getString("finish_id"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					params.put("user_id", Common.getUserId(context));

					Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
						@Override
						public void run(String result) {

						}

						@Override
						public void run(JSONObject result) {


							try {
								JSONArray uploadList = result.getJSONArray("result");

								for (int i = 0; i < uploadList.length(); i++) {
									JSONObject data = uploadList.getJSONObject(i);
									LinearLayout itemView = (LinearLayout) mInflater.inflate(R.layout.user_upload_item, null);
									LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
									itemView.setLayoutParams(params1);
									ImageView left = (ImageView) itemView.findViewById(R.id.mode_image);
									ImageView right = (ImageView) itemView.findViewById(R.id.task_screenshot);
									ImageView rrrrright = (ImageView) itemView.findViewById(R.id.image_confirm);
									ImageLoader imageLoader = ImageLoader.getInstance();

									try {
										((TextView) itemView.findViewById(R.id.left_task_mode_info)).setText(data.getString("title") + "示例图");
										imageLoader.displayImage(data.getString("mode_url"), left, Config.options);
										//Glide.with(context).load(data.getString("mode_url"));
										ImageListActivity.setImageViewCanShowBig(left, data.getString("mode_url"));

										imageLoader.displayImage(data.getString("img_url"), right, Config.options);
										//Glide.with(context).load(data.getString("img_url")).crossFade();
										ImageListActivity.setImageViewCanShowBig(right, data.getString("img_url"));
										if (!dataRow.getString("type").equals("红豆任务")) {
											imageLoader.displayImage(data.getString("yc_url"), rrrrright, Config.options);
											//Glide.with(context).load(data.getString("yc_url")).crossFade();
											itemView.findViewById(R.id.image_confirm_container).setVisibility(View.VISIBLE);


											ImageListActivity.setImageViewCanShowBig(rrrrright, data.getString("yc_url"));
											if(data.getString("yc_url").equals("")) {
												itemView.findViewById(R.id.image_confirm_container).setVisibility(View.INVISIBLE);
											}


										} else {
											itemView.findViewById(R.id.image_confirm_container).setVisibility(View.GONE);
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
									;

									((LinearLayout) selfView.findViewById(R.id.user_upload_list)).addView(itemView);

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

			convertView.findViewById(R.id.report).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ReportConfirmActivity.class);
					try {
						intent.putExtra("finish_id",data.getString("finish_id"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					((Activity )context).startActivityForResult(intent,101);
				}
			});

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
}
