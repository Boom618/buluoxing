package com.buluoxing.famous.wxapi;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.util.Config;
import com.util.Http;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ntop on 15/9/4.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	private IWXAPI api;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, Config.WxAppId, false);
		api.handleIntent(getIntent(), this);
	}
	@Override
	public void onReq(BaseReq baseReq) {

	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onResp(BaseResp baseResp) {
		Log.i("return code", baseResp.getType() + "");



		if(baseResp.getType()==ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
			if(baseResp.errCode==0) {
				if(listenerShare!=null) {
					listenerShare.success(api);
				}
			} else {
				if(listenerShare!=null) {
					listenerShare.failed(baseResp.errCode);
				}
			}
		} else if(baseResp.getType()==ConstantsAPI.COMMAND_SENDAUTH) {
			if(baseResp.errCode==0) {
				String code = ((SendAuth.Resp) baseResp).code;
				Http.getApi("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + Config.WxAppId + "&secret=" + Config.WxAppSecret + "&code=" + code + "&grant_type=authorization_code", new Http.HttpSuccessInterface() {
					@Override
					public void run(String result) {
						Log.i("auth_result", result);
						if (authLisener != null) {
							try {
								authLisener.success(new WxAuthResult(result));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
			} else {
				if(authLisener!=null) {
					authLisener.failed(baseResp.errCode);
				}
				Toast.makeText(this,"登录失败!",Toast.LENGTH_SHORT).show();
			}
		}
		finish();
	}
	public interface  WxCallbackListener{
		public void success(IWXAPI iwxapi);
		public void failed(int code);
	}
	public class WxAuthResult {
		public  String access_token;
		public  String refresh_token;
		public  String openid;
		public  String unionid;
		public  String scope;
		public  int expires_in;

		public WxAuthResult(String resultString) throws JSONException {

			JSONObject result = new JSONObject(resultString);
			access_token = result.getString("access_token");
			refresh_token = result.getString("refresh_token");
			expires_in = result.getInt("expires_in");
			openid = result.getString("openid");
			unionid = result.getString("unionid");
			scope = result.getString("scope");
		}
	}
	public interface  WxAuthCallbackListener{
		public void success(WxAuthResult result);
		public void failed(int code);
	}
	private  static WxCallbackListener listenerShare;
	private static WxAuthCallbackListener authLisener;

	public static void setWxCallbackShareListener(WxCallbackListener listener) {
		listenerShare = listener;
	}
	public  static void setAuthListener(WxAuthCallbackListener listener) {
		authLisener = listener;
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		MobclickAgent.onPause(this);
	}
}