package com.buluoxing.famous;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.media.WBShareCallBackActivity;
import com.util.Config;

/**
 * Created by wangfei on 15/12/3.
 */
public class WBShareActivity extends Activity implements  IWeiboHandler.Response {
	private IWeiboShareAPI mWeiboShareAPI;

	@Override
	public void onResponse(BaseResponse baseResponse) {
		switch (baseResponse.errCode) {
			case WBConstants.ErrorCode.ERR_OK:
				Log.i("weibo share", "success");
				if(listenerShare!=null) {
					listenerShare.success(mWeiboShareAPI);
				}
				break;
			case WBConstants.ErrorCode.ERR_CANCEL:
				Log.i("weibo share", "分享取消了");
				if(listenerShare!=null) {
					listenerShare.failed(baseResponse.errCode);
				}
				break;
			case WBConstants.ErrorCode.ERR_FAIL:
				if(listenerShare!=null) {
					listenerShare.failed(baseResponse.errCode);
				}
				Log.i("weibo share",baseResponse.errMsg);
				break;
		}
		finish();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 创建微博分享接口实例
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Config.WeiboAppId);

		// 如果未安装微博客户端，设置下载微博对应的回调
		if (!mWeiboShareAPI.isWeiboAppInstalled()) {
			Toast.makeText(this,"请安装微博客户端！",Toast.LENGTH_SHORT).show();
		}
		mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
		// 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
		// 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
		// 失败返回 false，不调用上述回调
		if (savedInstanceState != null) {
			mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
		}
	}
	/**
	 * @see {@link Activity#onNewIntent}
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
		// 来接收微博客户端返回的数据；执行成功，返回 true，并调用
		// {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
		mWeiboShareAPI.handleWeiboResponse(intent, this);
	}


	public interface  WeiboCallbackListener{
		public void success(IWeiboShareAPI mWeiboShareAPI);
		public void failed(int code);
	}
	private  static WeiboCallbackListener listenerShare;
	public static void setWeiboCallbackShareListener(WeiboCallbackListener listener) {
		listenerShare = listener;
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
