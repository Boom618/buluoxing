package com.buluoxing.famous;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.alipay.euler.andfix.patch.PatchManager;
import com.buluoxing.famous.index.KolFragment;
import com.buluoxing.famous.index.MoneyFragment;
import com.detail.UserInfo;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.util.Config;
import com.util.Http;
import com.util.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * Created by admin on 2016/5/14.
 */
public class MyApplication extends Application {
	public String userId;
	public UserInfo userInfo;
	public Activity mainActivity;
	public Activity registActivity;

	public MoneyFragment money;
	public KolFragment kol;
	public String phone="";
	public Point size;
	//补丁文件名
	private static final String APATCH_PATH = "/out.apatch";

	// 内存泄露
	private RefWatcher refWatcher;

	@Override
	public void onCreate()
	{
		MultiDex.install(this); // 解决友盟 NoClassDefFoundError
		super.onCreate();
		initImageloader();

		//初始化登录
		SharedPreferences sp = getSharedPreferences("user_info",
			MODE_PRIVATE);
		 userId = sp.getString("user_id", null);
		phone = sp.getString("phone","");
		Http.context = getApplicationContext();

		PlatformConfig.setWeixin("wx36834b9528fac4d4", "e9b8604ff1b43b205654f175c3f62717");
		PlatformConfig.setSinaWeibo("2323939127", "cdd7d3873055b3076ba882df4ec972db");
		initSysConfig();
		if(userId!=null) {
			initUserInfo();
		}
		//  腾讯BugCar是
		CrashReport.initCrashReport(getApplicationContext(),"900038809",false);

		//  内存泄露 检查
		refWatcher = LeakCanary.install(this);

		// 友盟 使用 集成测试服务
		MobclickAgent.setDebugMode( true );
		// 友盟统计
		MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(this,
				Config.YM_APP_KEY, Config.CHANNEL_DF_ID, MobclickAgent.EScenarioType.E_UM_GAME,true);
		MobclickAgent.startWithConfigure(config);

		// Android Fix
//		AndFix();

		// 配置OkhttpClient
//		OkHttpClient okHttpClient = new OkHttpClient.Builder()
////                .addInterceptor(new LoggerInterceptor("TAG"))
//				.connectTimeout(10000L, TimeUnit.MILLISECONDS)
//				.readTimeout(10000L, TimeUnit.MILLISECONDS)
//				//其他配置
//				.build();
//
//		OkHttpUtils.initClient(okHttpClient);

		// EventBus 只在DEBUG模式下，抛出异常，便于自测，同时又不会导致release环境的app崩溃
//		EventBus.builder().throwSubscriberException(BuildConfig.DEBUG).installDefaultEventBus();

	}

	// 检测 Activity 泄露
	public static RefWatcher getRefwatcher(Context context){
		MyApplication app = (MyApplication) context.getApplicationContext();
		return app.refWatcher;
	}


	public void initUserInfo() {
		HashMap<String, String> params = new HashMap<>();
		params.put("action", "userRefreshInfo");
		params.put("user_id", userId);
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {
				try {
					userInfo = new UserInfo(result.getJSONObject("result"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void run(JSONArray result) {

			}
		});
	}

	public void initSysConfig(){
		HashMap<String,String> params = new HashMap<>();
		params.put("action","configList");
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/System/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {
				try {
					JSONObject sysConfig = result.getJSONObject("result");

					Log.i("sys",sysConfig.toString());

					Config.sysConfig = sysConfig;
					Config.wechat_conf = sysConfig.getJSONObject("wechat_conf");

					Config.WxAppId = sysConfig.getJSONObject("wechat_conf").getJSONObject("pay").getString("app_id");
					Config.WxAppSecret = sysConfig.getJSONObject("wechat_conf").getJSONObject("pay").getString("app_secret");

					Config.WxAppIdBind = sysConfig.getJSONObject("wechat_conf").getJSONObject("bind").getString("app_id");
					Config.WxAppSecretBind = sysConfig.getJSONObject("wechat_conf").getJSONObject("bind").getString("app_secret");

					Config.Version_ID = sysConfig.getJSONObject("prompt_conf").getString("version_id");
					Config.Version_URL = sysConfig.getJSONObject("prompt_conf").getString("version_url");


				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void run(JSONArray result) {

			}
		});
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
		SharedPreferences sp = getSharedPreferences("user_info",
			MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		edit.putString("user_id",userId);
		edit.commit();

	}

	public void logout(){
		this.userId = null;
		SharedPreferences sp = getSharedPreferences("user_info",
			MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		edit.remove("user_id");
		edit.commit();
	}
	public boolean checkLogin() {
		if(userId!=null) {
			return true;
		} else {
			return false;
		}
	}
	// 原 private
	public void initImageloader(){
		File cacheDir = StorageUtils.getOwnCacheDirectory(this, "imageloader/Cache");
		//初始化imageloader
		ImageLoaderConfiguration config = new ImageLoaderConfiguration
			.Builder(getApplicationContext())
			.memoryCacheExtraOptions(1080, 1920) // max width, max height，即保存的每个缓存文件的最大长宽
			.threadPoolSize(3)//线程池内加载的数量
			.diskCache(new UnlimitedDiskCache(cacheDir))
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.denyCacheImageMultipleSizesInMemory()
			.memoryCache(new UsingFreqLimitedMemoryCache(5 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
			.memoryCacheSize(5 * 1024 * 1024)
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
			.imageDownloader(new BaseImageDownloader(getApplicationContext(), 60 * 1000, 120 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
			//.writeDebugLogs() // Remove for release app
			.build();
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}

	// Android Fix 热修复
	private  void AndFix(){

		try {
			String appHome = Environment.getExternalStorageDirectory().getAbsolutePath()+"/blxApp";
			L.d("appHome:" + appHome);
			// 创建目录文件 存放 patch 补丁文件
			boolean mkdirs = new File(appHome).mkdirs();
			PatchManager mPatchManager = new PatchManager(this);
			mPatchManager.init(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
			mPatchManager.loadPatch();
			//补丁文件路径
			String patchFileString = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + APATCH_PATH;
			Log.d("APP", "onCreate: patchFileString :" + patchFileString);
			mPatchManager.addPatch(patchFileString);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
