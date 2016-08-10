package com.buluoxing.famous.user;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buluoxing.famous.ConfirmActivity;
import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.buluoxing.famous.wxapi.WXEntryActivity;
import com.detail.UserInfo;
import com.umeng.socialize.PlatformConfig;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetMoneyActivity extends MyActivity {

	private int payType = 2;
	private UserInfo userInfo;
	private String user_id;
	private String result1;

	@Override
	protected int getLayout() {
		return R.layout.activity_get_money;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences sharedPreferences = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
		user_id=sharedPreferences.getString("user_id","");

		HashMap<String,String> params=new HashMap<>();
		params.put("action","checkUserFollow");
		params.put("token", Common.md5("checkUserFollow"+user_id+"buluoxing"));
		params.put("user_id", user_id);
		Http.postApi(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
//				Log.e(",result，result，result", result + "    " + user_id);
				try {
					JSONObject JSON = new JSONObject(result);
					result1 = JSON.getString("result");
					if(result1.equals("1")){
						TextView textView= (TextView) findViewById(R.id.get_money_GuanZhu);
						textView.setText("已关注部落星微信公众号");

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});



//		Toast.makeText(GetMoneyActivity.this, "用户ID为"+user_id, Toast.LENGTH_SHORT).show();


		findViewById(R.id.select_ali).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				payType = 2;
				((ImageView) findViewById(R.id.select_status_ali)).setBackgroundResource(R.drawable.pay_selected);
				((ImageView) findViewById(R.id.select_status_wx)).setBackgroundResource(R.drawable.pay_diselect);
				findViewById(R.id.wx_bind_container).setVisibility(View.GONE);
				findViewById(R.id.alipay_account).setVisibility(View.VISIBLE);
				try {
					findTextViewById(R.id.get_money_count).setHint(Config.sysConfig.getJSONObject("prompt_conf").getString("min_money_ali"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		findViewById(R.id.select_wx).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				payType = 1;
				((ImageView)findViewById(R.id.select_status_wx)).setBackgroundResource(R.drawable.pay_selected);
				((ImageView)findViewById(R.id.select_status_ali)).setBackgroundResource(R.drawable.pay_diselect);
				findViewById(R.id.alipay_account).setVisibility(View.GONE);
				findViewById(R.id.wx_bind_container).setVisibility(View.VISIBLE);
				try {
					findTextViewById(R.id.get_money_count).setHint(Config.sysConfig.getJSONObject("prompt_conf").getString("min_money_wx"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		findTextViewById(R.id.wx_status).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(userInfo.wxBinding) {
					Intent intent = new Intent(GetMoneyActivity.this, ConfirmActivity.class);
					intent.putExtra("message","你确认解除微信绑定吗?");
					startActivityForResult(intent, 100);
				} else {
					userInfo.bindWx(GetMoneyActivity.this, new UserInfo.OnWxBindResultListener() {
						@Override
						public void success(WXEntryActivity.WxAuthResult result) {
							findTextViewById(R.id.wx_status).setText("解除绑定微信号");
							userInfo.wxBinding = true;
						}

						@Override
						public void failed() {

						}
					});
				}
			}
		});

		Common.getUserInfo(this, new Common.OnGetUserInfoListener() {
			@Override
			public void geted(UserInfo userInfo) {
				GetMoneyActivity.this.userInfo = userInfo;
				if (userInfo.wxBinding) {
					findTextViewById(R.id.wx_status).setText("已完成，点此解绑微信号");
				} else {
					findTextViewById(R.id.wx_status).setText("点我绑定微信");
				}
			}
		});


		findViewById(R.id.do_get_money).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


				HashMap<String, String> params = new HashMap<String, String>();
				params.put("action", "userWithdraw");


				if(findTextViewById(R.id.name).getText().toString().equals("")) {
					Toast.makeText(GetMoneyActivity.this,"姓名不能为空!",Toast.LENGTH_SHORT).show();
					return;
				}
				if(payType==2) {
					if(findTextViewById(R.id.ali_account).getText().toString().equals("")) {
						Toast.makeText(GetMoneyActivity.this,"支付宝账号不能为空!",Toast.LENGTH_SHORT).show();
						return;
					}
				}

				String money = findTextViewById(R.id.get_money_count).getText().toString();
				if(money.equals("")) {
					Toast.makeText(GetMoneyActivity.this,"提现金额不能为空!",Toast.LENGTH_SHORT).show();
					return;
				}

				params.put("money", findTextViewById(R.id.get_money_count).getText().toString() + "");
				params.put("type", payType + "");
				params.put("name", findTextViewById(R.id.name).getText().toString());
				params.put("user_id", Common.getUserId(GetMoneyActivity.this));
				params.put("ali_name", findTextViewById(R.id.ali_account).getText().toString());

				final Common.LoadingHandler handler = Common.loading(GetMoneyActivity.this, "正在提现...");

				Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
					@Override
					public void run(String result) {

					}

					@Override
					public void run(JSONObject result) {
						handler.close();


						try {
							if (result.getString("status").equals(Config.HttpSuccessCode)) {
								String message = Config.sysConfig.getJSONObject("prompt_conf").getString("transfer");
								if(payType==1) {
									 message = Config.sysConfig.getJSONObject("prompt_conf").getString("transfer").replace("支付宝","微信");
								}

								Toast.makeText(GetMoneyActivity.this, message, Toast.LENGTH_SHORT).show();
								finish();
							} else {
								Toast.makeText(GetMoneyActivity.this, result.getString("message"), Toast.LENGTH_SHORT).show();
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
		//PlatformConfig.setWeixin(Config.WxAppIdBind, Config.WxAppSecretBind);


		try {
			findTextViewById(R.id.get_money_count).setHint(Config.sysConfig.getJSONObject("prompt_conf").getString("min_money_ali"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		findViewById(R.id.copy_id).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ClipboardManager myClipboard;
				myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

				ClipData myClip;
				myClip = ClipData.newPlainText("text", "tribal-star");
				myClipboard.setPrimaryClip(myClip);
				if(result1.equals("0")){
					Toast.makeText(GetMoneyActivity.this, "复制成功", Toast.LENGTH_SHORT).show();

				}else{

				}
			}
		});
	}
	public void onActivityResult(int requestCode,int resultCode,Intent data) {
		if(requestCode==100) {
			if(resultCode==1) {
				HashMap<String,String> params = new HashMap<String, String>();
				params.put("action","userDelBinding");
				params.put("user_id", Common.getUserId(GetMoneyActivity.this));
				final Common.LoadingHandler handler = Common.loading(GetMoneyActivity.this,"解除绑定...");
				Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
					@Override
					public void run(String result) {

					}

					@Override
					public void run(JSONObject result) {
						handler.close();
						userInfo.wxBinding = false;
						findTextViewById(R.id.wx_status).setText("点我绑定微信");
						Toast.makeText(GetMoneyActivity.this,"解除绑定成功!",Toast.LENGTH_SHORT).show();
					}

					@Override
					public void run(JSONArray result) {

					}
				});
			}
		}
	}
}
