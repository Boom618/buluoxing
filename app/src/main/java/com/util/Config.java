package com.util;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.json.JSONObject;

/**
 * Created by admin on 2016/5/3.
 */
public class Config {

//	public static String baseApi = "http://www.buluoxing.com";		// 生产环境
	public static String baseApi = "http://192.168.10.152";			// 测试环境,

	public static String HttpSuccessCode = "100";
	public static String NeedMoreMoney = "403";
	public static String NeedMoreBeans = "402";

	//用户类型
	public static String UserTypeNormal = "0";
	public static String UserTypeKol = "1";
	public static String UserTypeSingleKol = "2";

	//任务类型
	public static String TaskBeans = "1";
	public static String TaskMoney = "2";

	//申请网红类型
	public static String RequestKolStatusNotRequest = "0";
	public static String RequestKolStatusRequesting = "1";
	public static String RequestKolStatusPass = "2";
	public static String RequestKolStatusRequestOnce = "3";

	public static JSONObject sysConfig = new JSONObject();
	public static JSONObject wechat_conf = new JSONObject();

	public static DisplayImageOptions options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)                               //启用内存缓存
		.cacheOnDisk(true)                                 //启用外存缓存
		.bitmapConfig(Bitmap.Config.ARGB_8888)			   //RGB_565 比 默认是ARGB_8888 降低2倍内存
		.build();

	public static String WxAppId = "";
	public static String WxAppSecret = "";

	public static String WxAppIdBind = "";
	public static String WxAppSecretBind = "";

	// 版本更新
	public static String Version_ID = "1";
	public static String Version_URL = "http://buluoxing.com/Downloads/";



	public static String WeiboAppId = "2323939127";
	public static String WeiBoAppSecret = "cdd7d3873055b3076ba882df4ec972db";


	// 友盟APPKey
	public final static String YM_APP_KEY = "574af33d67e58e99850020fb";
	// 渠道 ID  （默认）
	public final static String CHANNEL_DF_ID = "default";

}
