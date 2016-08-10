package com.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buluoxing.famous.R;
import com.buluoxing.famous.ShareMissionActivity;
import com.buluoxing.famous.mission.DoMissionActivity;
import com.buluoxing.famous.user.MissionCompleteStatusActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.util.Common;
import com.util.Config;
import com.util.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2016/5/29.
 */
public class Mission {
	public  String finish_id;
	public  boolean getMoneyNow;
	public  long remainGeRewardTime;
	public  long finishdate;
	public  boolean money_for_get = false;
	public  int isFinished;
	public  int is_getred;
	public  String money_surplus;
	public  String user_type;
	public  String task_require;
	public  String remainMoney;
	public  String surplus;
	public  String share_img;
	public   String userDomain;
	public String is_task ;             // 0 不可做 1 可做
	public  String linkUrl;
	public  String taskPatternStr;
	public  String number;
	public  String userId;
	public  String title;
	public String taskId;
	public String taskType;
	public String price;
	public String integral;
	public Long starttime;
	public Long endtime;
	public String provincialCityStr;
	public String sex;
	public String startold;
	public String endold;
	public String userPhoto;
	public String username;
	public String userType;
	public String userCity;
	public String isfinish;
	public JSONArray photoArray;
	public JSONArray modeArray;
	public Activity activity;
	public JSONObject missionJsonObject;
	public Mission(JSONObject misson,Activity acitity) throws JSONException {


		missionJsonObject = misson;
		this.activity = acitity;
		taskId = misson.getString("id");
		title = misson.getString("task_title");
		userId = misson.getString("user_id");
		try {
			linkUrl = misson.getString("link_url");
		} catch (Exception e) {

		}
		taskPatternStr = misson.getString("task_pattern_str");
		number = misson.getString("number");
		price = misson.getString("price");
		integral = misson.getString("integral");
		starttime = misson.getLong("starttime");
		endtime = misson.getLong("endtime");
		provincialCityStr = misson.getString("user_city");
		try {
			sex = misson.getString("sex");
		}catch (Exception e) {

		}
		try {
			startold = misson.getString("startold");
			endold = misson.getString("endold");
			is_task = misson.getString("is_task");
		}catch (Exception e) {

		}
		userPhoto = misson.getString("user_photo");
		username = misson.getString("user_name");
		userDomain = misson.getString("user_domain");
		userType = misson.getString("user_type");
		userCity = misson.getString("user_city");
		is_getred = misson.getInt("is_getred");
		finish_id = misson.getString("finish_id");
		finishdate = misson.getLong("finishdate");
		share_img = misson.getString("share_img");
		isfinish = misson.getString("isfinish");
		isFinished = Integer.parseInt(isfinish);
		photoArray = misson.getJSONArray("photo_array");
		modeArray = misson.getJSONArray("mode_array");
		surplus = misson.getString("surplus");
		task_require = misson.getString("task_require");
		money_surplus = misson.getString("money_surplus");
		user_type = misson.getString("user_type");
		remainMoney =misson.getString("money_surplus");
		fomat();
	}
	public void fomat() {

		Log.e("ffffff",finishdate+"");
		finishdate = finishdate*1000;
		try {
			if(share_img.contains("png") || share_img.contains("jpg") || share_img.contains("bmp")) {

			} else {
				JSONObject prompt_conf = Config.sysConfig.getJSONObject("prompt_conf");
				if (prompt_conf != null){
					share_img = Config.sysConfig.getJSONObject("prompt_conf").getString("share_img");
				}
			}

			if(missionJsonObject.getString("task_type").equals("红豆任务") || missionJsonObject.getString("task_type").equals("1")) {
				taskType = "1";
			} else {
				taskType = "2";
			}
			if(is_getred==0 && taskType.equals(Config.TaskMoney) && isFinished==1) {
				money_for_get = true;
				long wait_seconds = 0;
				wait_seconds = Config.sysConfig.getJSONObject("prompt_conf").getLong("wait_seconds");
				Log.d("fuckff",wait_seconds+"----"+new Date().getTime()+"---"+finishdate);
				remainGeRewardTime = wait_seconds-(new Date().getTime() - finishdate)/1000;
				if(remainGeRewardTime>0) {
					getMoneyNow = false;
				} else {
					getMoneyNow = true;
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void resetRemainTime()  {
		long wait_seconds = 0;
		try {
			wait_seconds = Config.sysConfig.getJSONObject("prompt_conf").getLong("wait_seconds");
			remainGeRewardTime = wait_seconds-(new Date().getTime() - finishdate)/1000;
			if(remainGeRewardTime>0) {
				getMoneyNow = false;
			} else {
				getMoneyNow = true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void uploadMissionImage(){
		Intent intent = new Intent(activity,DoMissionActivity.class);
		intent.putExtra("task_id",taskId);
		intent.putExtra("task_type",taskType);
		intent.putExtra("task",missionJsonObject.toString());
		activity.startActivityForResult(intent, 301);
	}
	private UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
			com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
			if(platform.name().equals("WEIXIN_FAVORITE")){
				Toast.makeText(activity, platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
			}else{
				//Toast.makeText(getActivity(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
				uploadMissionImage();
			}
		}
		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			Toast.makeText(activity,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			Toast.makeText(activity,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
		}
	};
	public void doThis(){



//		UMImage image = new UMImage(activity, "http://file.reco.cn/img/3a19267fadea533239d2a2ac247ecdf6/180x180.png");
//		new ShareAction(activity).setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.WEIXIN_CIRCLE)
//			.setCallback(umShareListener)
//			.withText(taskPatternStr)
//			.withTitle(title)
//			.withTargetUrl(linkUrl)
//			.withMedia(image)
//			.open();



		Intent intent = new Intent(activity, ShareMissionActivity.class);
		intent.putExtra("title",title);
		intent.putExtra("desc",linkUrl);
		intent.putExtra("image_url",share_img);
		intent.putExtra("url",linkUrl);
		intent.putExtra("share_mission",true);
		intent.putExtra("message","请通过以下方式转发后截图\r\n回到此页面上传截图即可完成任务");
		activity.startActivity(intent);
	}
	public void displayInItemView(final  RelativeLayout itemView,final String isTask) {



		((TextView) itemView.findViewById(R.id.mission_title)).setText(title);
		((TextView) itemView.findViewById(R.id.mission_href)).setText(linkUrl);

		if(taskType.equals(Config.TaskBeans)) {
			((TextView) itemView.findViewById(R.id.awards_beans_number)).setText(integral);
			((TextView) itemView.findViewById(R.id.remian_number)).setText(surplus);
		} else {
			((TextView) itemView.findViewById(R.id.awards_beans_number)).setText(money_surplus);
			((TextView) itemView.findViewById(R.id.remian_number)).setText(surplus);
		}

		((TextView) itemView.findViewById(R.id.username)).setText(username);
		((TextView) itemView.findViewById(R.id.area)).setText(userDomain);
		((TextView) itemView.findViewById(R.id.city)).setText(provincialCityStr);
		((TextView) itemView.findViewById(R.id.mission_type)).setText(taskPatternStr);

		if(!task_require.equals("")) {
			((TextView) itemView.findViewById(R.id.mission_request)).setText(task_require);
			itemView.findViewById(R.id.row_mission_require).setVisibility(View.VISIBLE);
		} else {
			itemView.findViewById(R.id.row_mission_require).setVisibility(View.GONE);
		}


		int isFinished = Integer.parseInt(isfinish);
		Long currentTime = new Date().getTime();


		((ImageView) itemView.findViewById(R.id.isfinished)).setBackgroundResource(R.drawable.right_top_mission_on);
		itemView.findViewById(R.id.upload_image_now).setVisibility(View.VISIBLE);

		Log.i("Mission",isfinish+"---"+missionJsonObject.toString());
		TextView text;
		if(isFinished==0) {
			Log.i("currentTime:", currentTime + "" + endtime);
			if(currentTime<starttime*1000) {
				itemView.findViewById(R.id.upload_image_now).setVisibility(View.INVISIBLE);
				((ImageView) itemView.findViewById(R.id.isfinished)).setBackgroundResource(R.drawable.un_start);
			} else if(currentTime>endtime*1000) {
				//itemView.findViewById(R.id.upload_image_now).setVisibility(View.INVISIBLE);
				text = (TextView) itemView.findViewById(R.id.upload_image_now);
				text.setVisibility(View.INVISIBLE);
				((ImageView) itemView.findViewById(R.id.isfinished)).setBackgroundResource(R.drawable.right_top_mission);
				//System.out.println("======000==========");

				// 直接用itemView 设置监听不可用 （ 一直没明白 ）
				//itemView.findViewById(R.id.ll_over).setOnClickListener(new View.OnClickListener() {
				////itemView.setOnClickListener(new View.OnClickListener() {
				//	@Override
				//	public void onClick(View v) {
				//		T.showOnce(activity.getApplicationContext(),"该任务已结束");
				//	}
				//});
			} else if(surplus.equals("0")) {
				itemView.findViewById(R.id.upload_image_now).setVisibility(View.INVISIBLE);
				((ImageView) itemView.findViewById(R.id.isfinished)).setBackgroundResource(R.drawable.mission_complete);
			} else {
				((ImageView) itemView.findViewById(R.id.isfinished)).setBackgroundResource(R.drawable.right_top_mission_on);
			}
		}



		// 任务详情 的 Item 点击事件
		itemView.findViewById(R.id.upload_image_now).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.e("TAG", "onClick: is_task = " + is_task );
				if ("1".equals(isTask)){
					// 可做任务
					uploadMissionImage();
				}else {
					T.showOnce(activity,"您不符合任务目标人群");

				}
			}
		});

//		if (currentTime > endtime*1000){
//			System.out.println("======000==========");
			 //直接用itemView 设置监听不可用 （ 一直没明白 ）
//			itemView.findViewById(R.id.ll_over).setOnClickListener(new View.OnClickListener() {
////			itemView.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					T.showOnce(activity.getApplicationContext(),"该任务已结束");
//				}
//			});
//		}

		if(isFinished==2) {
			((ImageView) itemView.findViewById(R.id.isfinished)).setBackgroundResource(R.drawable.mission_complete);
			itemView.findViewById(R.id.upload_image_now).setVisibility(View.INVISIBLE);

			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (taskType.equals("红豆任务") || taskType.equals("1")) {
						taskType = "1";
					} else {
						taskType = "2";
					}
					Intent intent = new Intent(activity, MissionCompleteStatusActivity.class);
					intent.putExtra("task_type",taskType);
					intent.putExtra("task", missionJsonObject.toString());
					activity.startActivity(intent);
				}
			});

		}

		if(taskType.equals(Config.TaskMoney)) {
			((TextView)itemView.findViewById(R.id.upload_image_now)).setText("点我赚钱");
		}

		if(is_getred==0 && taskType.equals(Config.TaskMoney) && isFinished==1) {
			((ImageView) itemView.findViewById(R.id.isfinished)).setBackgroundResource(R.drawable.money_for_get);

			if(remainGeRewardTime<=0) {
				((TextView) itemView.findViewById(R.id.upload_image_now)).setText("可领取");
			} else {
				((TextView) itemView.findViewById(R.id.upload_image_now)).setText("待领取");
			}
		}

		if(is_getred==1) {
			((ImageView) itemView.findViewById(R.id.isfinished)).setBackgroundResource(R.drawable.mission_complete);
			itemView.findViewById(R.id.upload_image_now).setVisibility(View.INVISIBLE);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (taskType.equals("红豆任务") || taskType.equals("1")) {
						taskType = "1";
					} else {
						taskType = "2";
					}
					Intent intent = new Intent(activity, MissionCompleteStatusActivity.class);
					intent.putExtra("task_type", taskType);
					intent.putExtra("task", missionJsonObject.toString());
					activity.startActivity(intent);
				}
			});
		} else {
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
		}

		((ImageView) itemView.findViewById(R.id.user_icon)).setImageResource(R.drawable.default_avatar);

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(userPhoto,(ImageView) itemView.findViewById(R.id.user_icon),Config.options);

		Date date = new Date();
		date.setTime(starttime*1000);
		SimpleDateFormat sf = new SimpleDateFormat("MM月dd日");
		String startTime = sf.format(date);

		date.setTime(endtime*1000);
		String endTime = sf.format(date);
		((TextView) itemView.findViewById(R.id.complete_time)).setText(String.format("%s-%s",startTime,endTime));


		if(user_type.equals("0")){
			itemView.findViewById(R.id.kol).setVisibility(View.GONE);
			itemView.findViewById(R.id.kol_single).setVisibility(View.GONE);
		} else if(user_type.equals("1")) {
			itemView.findViewById(R.id.kol).setVisibility(View.VISIBLE);
			itemView.findViewById(R.id.kol_single).setVisibility(View.GONE);
		} else if(user_type.equals("2")){
			itemView.findViewById(R.id.kol).setVisibility(View.GONE);
			itemView.findViewById(R.id.kol_single).setVisibility(View.VISIBLE);
		}

	}
}
