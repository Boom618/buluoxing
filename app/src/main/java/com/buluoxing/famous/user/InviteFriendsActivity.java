package com.buluoxing.famous.user;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.buluoxing.famous.ShareMissionActivity;
import com.buluoxing.famous.WBShareActivity;
import com.buluoxing.famous.wxapi.WXEntryActivity;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
// 个人中心 邀请好友加入
public class InviteFriendsActivity extends MyActivity {

	private String inviteCode = "";	//邀请码

	@Override
	protected int getLayout() {
		return R.layout.activity_invite_friends;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View backView = findViewById(R.id.back);
		if(backView!=null ) {
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}




		HashMap<String,String> params = new HashMap<>();
		params.put("action","sysShare");
		params.put("user_id", Common.getUserId(this));


		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/System/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {
				try {
					final JSONObject inviteInfo = result.getJSONObject("result");

					Log.i("fuck", result.toString());
					inviteCode = inviteInfo.getString("invite_code");
					final String info = inviteInfo.getString("desc").replace("|", "\r\n");
					final String str = info + ", 邀请码:" + inviteCode;
					((TextView) findViewById(R.id.invite_desc)).setText(info);
					((TextView)findViewById(R.id.invite_code)).setText(String.format("邀请码:%s", inviteCode));

					final String inviteString = Config.sysConfig.getJSONObject("prompt_conf")
						.getString("yq_text").replace("|","\r\n").replace("#", inviteInfo.getString("invite_code"));
					findTextViewById(R.id.invite_string).setText(inviteString);

					findViewById(R.id.copy_invite_code).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							ClipboardManager myClipboard;
							myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

							ClipData myClip;
							myClip = ClipData.newPlainText("text",inviteString);
							myClipboard.setPrimaryClip(myClip);

							Toast.makeText(InviteFriendsActivity.this, "您已成功复制邀请语\n" +
								"点击【立即邀请】，转发分享邀请语\n" +
								"获更多好友注册，更多佣金哦~", Toast.LENGTH_LONG).show();

						}
					});
					findViewById(R.id.invite).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							try {


								Intent intent = new Intent(InviteFriendsActivity.this, ShareMissionActivity.class);
								intent.putExtra("title","分享部落星 \n邀请码：" + inviteCode );
								intent.putExtra("desc",info);		// 字符后 不带邀请码
								intent.putExtra("desc1",str);		// 字符后带邀请码
								intent.putExtra("image_url",inviteInfo.getString("icon"));
								intent.putExtra("url",inviteInfo.getString("share_url"));
								intent.putExtra("message","邀请好友");
								startActivity(intent);

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});

					WXEntryActivity.setWxCallbackShareListener(new WXEntryActivity.WxCallbackListener() {
						@Override
						public void success(IWXAPI iwxapi) {
							Toast.makeText(InviteFriendsActivity.this, "邀请成功!", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void failed(int code) {
							Toast.makeText(InviteFriendsActivity.this, "邀请失败!", Toast.LENGTH_SHORT).show();
						}
					});


					WBShareActivity.setWeiboCallbackShareListener(new WBShareActivity.WeiboCallbackListener() {
						@Override
						public void success(IWeiboShareAPI mWeiboShareAPI) {
							Toast.makeText(InviteFriendsActivity.this, "邀请成功!", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void failed(int code) {
							Toast.makeText(InviteFriendsActivity.this, "邀请失败!", Toast.LENGTH_SHORT).show();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void run(JSONArray result) {

			}
		});

	}

	private UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
			com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
			if(platform.name().equals("WEIXIN_FAVORITE")){
				Toast.makeText(InviteFriendsActivity.this, platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
			}else{
				//Toast.makeText(getActivity(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
			}
		}
		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			Toast.makeText(InviteFriendsActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			Toast.makeText(InviteFriendsActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
		}
	};
}
