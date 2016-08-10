package com.buluoxing.famous;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.buluoxing.famous.wxapi.WXEntryActivity;
import com.detail.Mission;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.PlatformConfig;
import com.util.Common;
import com.util.Config;
import com.util.T;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
// 个人中心 - 邀请好友 - 立即邀请
public class ShareMissionActivity extends MyActivity {

	private IWXAPI api;
	private IWeiboShareAPI mWeiboShareAPI;
	private Common.LoadingHandler handler;

	public String title = "";
	public String desc = "";
	public String desc1 = "";
	public String image_url = "";
	public String url = "";
	private String message = "";
	private boolean shareMission = false;
	private boolean isFriends = false;

	@Override
	protected int getLayout() {
		return R.layout.activity_share_mission;
	}
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		title = getIntent().getStringExtra("title");
		desc = getIntent().getStringExtra("desc");		// 字符后 不带邀请码（微信单个好友）
		desc1 = getIntent().getStringExtra("desc1");	// 字符后 带邀请码 （微信朋友圈）
		image_url = getIntent().getStringExtra("image_url");
		url = getIntent().getStringExtra("url");
		message = getIntent().getStringExtra("message");
		shareMission = getIntent().getBooleanExtra("share_mission", false);

		if(shareMission) {
			if(Common.checkShowTips(this,"shareshare")) {
				findViewById(R.id.cover).setVisibility(View.VISIBLE);
			}
		}

		findTextViewById(R.id.message).setText(message);

		Log.i("fuck0", Config.WxAppId + ':' + Config.WxAppSecret);
		PlatformConfig.setWeixin(Config.WxAppId, Config.WxAppSecret);


		api = WXAPIFactory.createWXAPI(this, Config.WxAppId, true);
		api.registerApp(Config.WxAppId);

		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Config.WeiboAppId);
		mWeiboShareAPI.registerApp();



		findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 微信朋友圈
		findViewById(R.id.wx_circle).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (api.isWXAppInstalled()){
					isFriends = false;
					desc = desc1; // (分享朋友圈 在描述中添加邀请码)
					sendToWx(SendMessageToWX.Req.WXSceneTimeline);
				}else {
					// 没有安装微信
					T.showOnce(getApplicationContext(),"请检测是否安装微信");
				}

			}
		});
		// 微信好友
		findViewById(R.id.wx_friends).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (api.isWXAppInstalled()){
					isFriends = true;
					sendToWx(SendMessageToWX.Req.WXSceneSession);
				}else {
					// 没有安装微信
					T.showOnce(getApplicationContext(),"请检测是否安装微信");
				}
			}
		});
		findViewById(R.id.browser).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(url);
				intent.setData(content_url);
				startActivity(intent);
				finish();
			}
		});

		findViewById(R.id.weibo).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isFriends = false;
				Log.i("fuck", "点微博");

				// 设置 Bitmap 类型的图片到视频对象里
				WBShareActivity.setWeiboCallbackShareListener(new WBShareActivity.WeiboCallbackListener() {
					@Override
					public void success(IWeiboShareAPI iWeiboShareAPI) {
						iWeiboShareAPI.launchWeibo(ShareMissionActivity.this);
						Toast.makeText(ShareMissionActivity.this, "分享成功！", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void failed(int code) {
						Toast.makeText(ShareMissionActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
					}
				});

				final ImageLoader imageLoader = ImageLoader.getInstance();

				handler = Common.loading(ShareMissionActivity.this, "获取封面图...");
				imageLoader.loadImage(image_url, Config.options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						handler.close();
						TextObject textObject = new TextObject();
						textObject.text = title;

						WebpageObject mediaObject = new WebpageObject();
						mediaObject.identify = Utility.generateGUID();
						if (shareMission) {
							mediaObject.title = url;
							mediaObject.description = "";
						} else {
							mediaObject.title = desc;
							mediaObject.description = "";
						}
						mediaObject.setThumbImage(loadedImage);
						mediaObject.actionUrl = url;
						mediaObject.defaultText = "buluoxing";

						WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
						weiboMessage.mediaObject = mediaObject;
						weiboMessage.textObject = textObject;

						// 2. 初始化从第三方到微博的消息请求
						SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
						// 用transaction唯一标识一个请求
						request.transaction = String.valueOf(System.currentTimeMillis());
						request.multiMessage = weiboMessage;
						// 3. 发送请求消息到微博，唤起微博分享界面
						mWeiboShareAPI.sendRequest(ShareMissionActivity.this, request);
						finish();
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						// Empty implementation
						handler.close();
						Toast.makeText(ShareMissionActivity.this, "服务端返回缩略图不正确", Toast.LENGTH_SHORT).show();
					}
				});

			}
		});
	}
	public void sendToWx(final int type) {
		WXEntryActivity.setWxCallbackShareListener(new WXEntryActivity.WxCallbackListener() {
			@Override
			public void success(IWXAPI iwxapi) {
				if(!isFriends){
					iwxapi.openWXApp();
				}

				Toast.makeText(ShareMissionActivity.this, "分享成功!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void failed(int code) {
				Toast.makeText(ShareMissionActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
			}
		});

		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = url;
		// 用WXTextObject对象初始化一个WXMediaMessage对象
		final WXMediaMessage msg = new WXMediaMessage(webpage);
		if(shareMission) {
			msg.title = title;
			// 发送文本类型的消息时，title字段不起作用
			// msg.title = "Will be ignored";
			msg.description = url;
		} else {
			msg.title = desc;
			// 发送文本类型的消息时，title字段不起作用
			// msg.title = "Will be ignored";
			msg.description = title;
		}
		ImageLoader imageLoader = ImageLoader.getInstance();
		handler = Common.loading(ShareMissionActivity.this,"获取封面图...");



		imageLoader.loadImage(image_url, Config.options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				msg.setThumbImage(loadedImage);
				// 构造一个Req
				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.transaction = buildTransaction("urlshare"); // transaction字段用于唯一标识一个请求
				req.message = msg;
				req.scene = type;
				//;
				// 调用api接口发送数据到微信
				api.sendReq(req);

				handler.close();

			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				// Empty implementation
				try {
					handler.close();
				} catch (Exception e) {

				}
				Toast.makeText(ShareMissionActivity.this, "服务端返回缩略图不正确", Toast.LENGTH_SHORT).show();
			}
		});
		finish();
	}
	private void doStartApplicationWithPackageName(String packagename) {

		// 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
		PackageInfo packageinfo = null;
		try {
			packageinfo = getPackageManager().getPackageInfo(packagename, 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return;
		}

		// 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);

		// 通过getPackageManager()的queryIntentActivities方法遍历
		List<ResolveInfo> resolveinfoList = getPackageManager()
			.queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			// packagename = 参数packname
			String packageName = resolveinfo.activityInfo.packageName;
			// 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
			String className = resolveinfo.activityInfo.name;
			// LAUNCHER Intent
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			// 设置ComponentName参数1:packagename参数2:MainActivity路径
			ComponentName cn = new ComponentName(packageName, className);

			intent.setComponent(cn);
			startActivity(intent);
		}
	}
}
