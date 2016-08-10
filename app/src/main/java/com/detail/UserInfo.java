package com.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.buluoxing.famous.BrowserActivity;
import com.buluoxing.famous.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.message.UmengService;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2016/6/2.
 */
public class UserInfo {
	public   String weibo_url;
	public  String weibo_name;
	public  String wechat_id;
	public  String sex;
	public  String domian_str;
	public  String desc;
	public String type;
	public String birthday;
	public  Boolean wxBinding;
	public  String beans="";
	public String photo = "";
	public String username = "";
	public String city ="";
	public String domain = "";
	public String id="";
	public JSONObject user;
	public boolean is_report = false;
	public int is_kol = 0;
	public Boolean has_publish = false;
	private UMShareAPI mShareAPI;
	public Context context;
	private OnWxBindResultListener wxBindListener;

	public static int REQUEST_KOL_NO = 0;
	public static int REQUEST_KOL_ING = 1;
	public static int REQUEST_KOL_FAILED = 3;
	private IWXAPI mWeixinAPI;

	public UserInfo(JSONObject user) throws JSONException {
		this.user = user;
		id = user.getString("id");
		birthday = user.getString("birthday");
		photo = user.getString("photo");
		username = user.getString("nickname");
		city = user.getString("city");
		sex = user.getString("sex");
		wechat_id = user.getString("wechat_id");
		weibo_name = user.getString("weibo_name");
		weibo_url  = user.getString("weibo_url");
		domain = user.getString("domian_name");
		domian_str = user.getString("domian_str");
		beans = user.getString("integral");
		wxBinding = user.getString("is_binding").equals("1");
		is_kol = user.getInt("is_kol");
		has_publish = user.getInt("has_publish")==1;
		type = user.getString("type");
		desc = user.getString("desc");
		is_report = user.getString("is_report").equals("1");
	}
	public String toString(){
		return user.toString();
	}

	public interface OnWxBindResultListener{
		public void success(WXEntryActivity.WxAuthResult result);
		public void failed();
	}

	public void bindWx(Activity activity,OnWxBindResultListener listener) {

//		Intent intent = new Intent(activity, BrowserActivity.class);
//		intent.putExtra("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + Config.WxAppIdBind
//			+ "&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_userinfo&state=wxdemo#wechat_redirect");
//		activity.startActivity(intent);
//		UMServiceFactory
//
//		UMSocialService mController = UMServiceFactory .getUMSocialService("com.umeng.share", RequestType.SOCIAL);
//		return;

		context = activity;
		wxBindListener = listener;

		//Log.i("fuckfuckfuck", Config.WxAppIdBind + ":" + Config.WxAppSecretBind);
		PlatformConfig.setWeixin(Config.WxAppId, Config.WxAppSecret);
		mShareAPI = UMShareAPI.get(activity);

//		IWXAPI api = WXAPIFactory.createWXAPI(activity, Config.WxAppIdBind, true);
//		api.registerApp(Config.WxAppIdBind);
//		SendAuth.Req req = new SendAuth.Req();
//		req.scope = "snsapi_userinfo";
//		req.state = "wxdemo";
//		api.sendReq(req);


		mShareAPI.doOauthVerify(activity, SHARE_MEDIA.WEIXIN, umAuthListener);

		WXEntryActivity.setAuthListener(new WXEntryActivity.WxAuthCallbackListener() {
			@Override
			public void success(final WXEntryActivity.WxAuthResult authResult) {
				String openid = authResult.openid;
				String unionid = authResult.unionid;

				HashMap<String,String> params = new HashMap<>();
				params.put("action","userBinding");
				params.put("user_id", Common.getUserId(context));
				params.put("open_id", openid);
				params.put("union_id", unionid);

				final Common.LoadingHandler handler = Common.loading(context,"正在绑定");
				Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
					@Override
					public void run(String result) {

					}

					@Override
					public void run(JSONObject result) {
						handler.close();
						Toast.makeText(context, "您已成功绑定，继续完成步骤二，就能立即提现啦！", Toast.LENGTH_SHORT).show();
						wxBindListener.success(authResult);
					}

					@Override
					public void run(JSONArray result) {

					}
				});
			}

			@Override
			public void failed(int code) {

			}
		});
	}
	/** auth callback interface**/
	private UMAuthListener umAuthListener = new UMAuthListener() {
		@Override
		public void onComplete(SHARE_MEDIA platform, int action, final Map<String, String> data) {

		}

		@Override
		public void onError(SHARE_MEDIA platform, int action, Throwable t) {
			Toast.makeText( context, "Authorize fail", Toast.LENGTH_SHORT).show();
			wxBindListener.failed();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform, int action) {
			Toast.makeText( context, "Authorize cancel", Toast.LENGTH_SHORT).show();
			wxBindListener.failed();
		}

	};

	public HashMap<String,String> getSaveParams() {
		HashMap<String,String> params  = new HashMap<>();
		params.put("birthday",birthday);
		params.put("desc",desc);
		params.put("domian_str",domian_str);
		params.put("nickname",username);
		params.put("sex",sex);
		params.put("wechat_id",wechat_id);
		params.put("weibo_name",weibo_name);
		params.put("weibo_url", weibo_url);
		return params;
	}

}
