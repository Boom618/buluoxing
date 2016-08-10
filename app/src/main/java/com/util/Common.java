package com.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buluoxing.famous.LoadingActivity;
import com.buluoxing.famous.LoadingDialog;
import com.buluoxing.famous.LoginActivity;
import com.buluoxing.famous.MyApplication;
import com.buluoxing.famous.R;
import com.detail.UserInfo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by admin on 2016/5/3.
 */
public class Common {

	public static int getVersion(Context context) {

		if (context != null) {
			String pkName = context.getPackageName();
			int versionCode = 0;
			try {
				versionCode = context.getPackageManager().getPackageInfo(
					pkName, 0).versionCode;
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
			return versionCode;
		}
		return 0;
	}

	public static String getChannel(Context context) {
		if (context != null) {
			ApplicationInfo appInfo;
			try {
				appInfo = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
				String channel = appInfo.metaData.getString("UMENG_CHANNEL");
				return channel;
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
				return "";
			}
		}
		return "";
	}

	public static String getDeviceName() {
		return android.os.Build.PRODUCT + "-" + android.os.Build.MODEL;
	}


	public static String signAction(String action) {
		return signAction(action, "");
	}

	public static String signAction(String action, String userid) {
		return md5(action + userid + "buluoxing");
	}

	public static String md5(String plainText) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}


	public static void getProvinceData(Context context, final Http.HttpSuccessInterface callback) {
		final SharedPreferences sp = context.getSharedPreferences("sys_config",
			context.MODE_PRIVATE);
		String result = sp.getString("city", "");
		long time = sp.getLong("city_expire", 0);
		long currentTime = new Date().getTime();

		if(currentTime>time) {
			result = "";
		}
		if(!result.equals("")) {
			Log.i("cache", "hit");
			callback.run(result);
		}  else  {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("action", "sysCityList");
			params.put("token", Common.signAction("sysCityList"));
			Http.postApi(Http.buildBaseApiUrl("/Home/System/index"), params, new Http.HttpSuccessInterface() {
				@Override
				public void run(String result) {
					callback.run(result);
					Log.i("cache", "miss");
					SharedPreferences.Editor editor = sp.edit();
					editor.putLong("city_expire",new Date().getTime()+1000*3600*24*3);
					editor.putString("city",result);
					editor.commit();
				}
			});
		}
	}


	public static void userSign(final Context context) {
		MyApplication application  = (MyApplication)context.getApplicationContext();
		String userId =  application.getUserId();
		HashMap<String ,String> params = new HashMap<String,String>();
		params.put("action","signIn");
		params.put("user_id",userId);
		params.put("token", signAction("signIn", userId));

		final LoadingHandler handler = loading(context, "签到中...");
		Http.postApi(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {

				try {
					JSONObject signResult = new JSONObject(result);
					Log.i("sign", result);

					if(signResult.getString("status").equals(Config.HttpSuccessCode)) {
						Toast.makeText(context, "恭喜你，签到成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, signResult.getString("message"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}


				handler.close();
			}
		});

	}
	public static String getUserId(Context context) {
		MyApplication application = (MyApplication)context.getApplicationContext();
		return application.getUserId();
	}
	public static UserInfo getUserInfo(Context context) {
		MyApplication application = (MyApplication)context.getApplicationContext();
		return application.userInfo;
	}
	public static class LoadingHandler {
		public LoadingDialog dialog;
		public void close(){
			try {
			if(dialog!=null) {
				Log.i("fuck","销毁loading");
				dialog.dismiss();
			}} catch (Exception e) {

			}
		}
	}
	public static LoadingHandler loading(Context context,String tips){
		LoadingDialog dialog = new LoadingDialog(context,tips);
		LoadingHandler handler = new LoadingHandler();
		handler.dialog = dialog;
		dialog.show();
		return handler;
	}

	public static LoadingHandler loading(Context context){
		String tips = context.getResources().getString(R.string.loading);
		return loading(context,tips);
	}


	public static String getDateStrFromTime(long time,String formate) {
		SimpleDateFormat sdr  = new SimpleDateFormat(formate, Locale.CHINA);
		return sdr.format(new Date(time));
	}

	public static String getDateStrFromPhpTime(String time,String formate) {

		SimpleDateFormat sdr  = new SimpleDateFormat(formate, Locale.CHINA);

		Long s = Long.parseLong(time)*1000;

		return sdr.format(new Date(s));
	}
	public static String getDanwei(String taskType) {
		return taskType.equals(Config.TaskBeans)?"红豆":"元";
	}

	public interface OnGetUserInfoListener {
		void geted(UserInfo userInfo) ;
	}

	public static void getUserInfo(Context context, final OnGetUserInfoListener listener) {
		String userId = Common.getUserId(context);
		HashMap<String, String> params = new HashMap<>();
		params.put("action", "userRefreshInfo");
		params.put("user_id", userId);
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				Log.i("user", result);
				try {
					JSONObject resultObject = new JSONObject(result);
					listener.geted(new UserInfo(resultObject.getJSONObject("result")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}
	public static Boolean checkEmpty(Context context,String value,String message) {

		if(value.equals("")) {
			Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
			return true;
		} else {
			return false;
		}
	}
	public static void controlUserType(View itemView,String type) {
		if(type.equals("0")){
			itemView.findViewById(R.id.kol).setVisibility(View.GONE);
			itemView.findViewById(R.id.kol_single).setVisibility(View.GONE);
		} else if(type.equals("1")) {
			itemView.findViewById(R.id.kol).setVisibility(View.VISIBLE);
			itemView.findViewById(R.id.kol_single).setVisibility(View.GONE);
		} else if(type.equals("2")) {
			itemView.findViewById(R.id.kol).setVisibility(View.GONE);
			itemView.findViewById(R.id.kol_single).setVisibility(View.VISIBLE);
		}
	}
	public static String getMissionRemainGetMoneyText(long t) {
		if(t<=60) {
			String s = t <= 0?"立即领取":t + "秒后可领取奖励";
			return s;
//			return t+"秒后可领取奖励";
		} else {
			return t/60+"分"+t%60+"秒后可领取奖励";
		}
	}
	public static Boolean checkShowTips(Context context,String tag) {

		String userId = getUserId(context);
		SharedPreferences sp = context.getSharedPreferences("user_info",
			context.MODE_PRIVATE);
		String isShow = sp.getString(userId + tag, null);


		SharedPreferences.Editor edit = sp.edit();
		edit.putString(userId + tag, "1");
		edit.commit();
		//return true;
		return  isShow==null ;
	}
	public static Boolean checkPhone(String phone) {
		if(phone.length()!=11) {
			return false;
		}
		if(!phone.substring(0,1).equals("1")) {
			return false;
		}
		return true;
	}


	/**
	 * 检查 用户 是否安装该应用
	 * @param mContext
	 * @param packageName
     * @return
     */
	public boolean checkAppIsInstalled(Context mContext,String packageName) {
		boolean isInstalled = false;
		PackageManager pm = mContext.getPackageManager();
		List<ApplicationInfo> m_appList = pm.getInstalledApplications(0);
		for (ApplicationInfo ai : m_appList) {
			if (ai.packageName.equals(packageName)) {
				isInstalled = true;
				break;
			}
		}
		return isInstalled;
	}
}
