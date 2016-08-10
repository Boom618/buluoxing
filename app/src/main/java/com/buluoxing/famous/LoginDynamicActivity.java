package com.buluoxing.famous;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.message.UmengRegistrar;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class LoginDynamicActivity extends MyActivity {

	@Override
	protected int getLayout() {
		return R.layout.activity_login_dynamic;
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

		findViewById(R.id.get_code).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String api = Http.buildBaseApiUrl("/Home/Members/index");
				String action = "sendMessage";
				String token = Common.signAction(action);

				String phone = ((EditText) findViewById(R.id.phone)).getText().toString();

				if(phone.equals("") || !Common.checkPhone(phone)) {
					Toast.makeText(LoginDynamicActivity.this,"手机号码不正确",Toast.LENGTH_SHORT).show();
					return;
				}


				HashMap<String,String> params = new HashMap<String, String>();
				params.put("action", action);
				params.put("phone", phone);
				params.put("token", token);
				params.put("type", "2");
				params.put("facility_token", UmengRegistrar.getRegistrationId(LoginDynamicActivity.this));
				Http.postApi(api, params, new Http.HttpSuccessInterface() {
					@Override
					public void run(String result) {

						try {
							JSONObject resultObject = new JSONObject(result);
							Log.i("send", resultObject.getString("message"));

							if(resultObject.getString("status").equals(Config.HttpSuccessCode)) {
								startTimer();
								Toast.makeText(LoginDynamicActivity.this, "短信已经发送", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(LoginDynamicActivity.this, resultObject.getString("message"), Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

			}
		});
		findViewById(R.id.regist_fast).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent registIntend = new Intent(LoginDynamicActivity.this, com.buluoxing.famous.RegistActivity.class);
				startActivity(registIntend);
			}
		});
		findViewById(R.id.login_password).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginDynamicActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
		findViewById(R.id.go_login).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String phone = ((EditText) findViewById(R.id.phone)).getText().toString();
				String code = ((EditText) findViewById(R.id.code)).getText().toString();

				if(phone.equals("") || !Common.checkPhone(phone)) {
					Toast.makeText(LoginDynamicActivity.this,"手机号码不能为空",Toast.LENGTH_SHORT).show();
					return;
				}

				if(code.equals("")) {
					Toast.makeText(LoginDynamicActivity.this,"验证码不能为空",Toast.LENGTH_SHORT).show();
					return;
				}

				Http.enableCookie(getApplicationContext());

				HashMap<String, String> params = new HashMap<String, String>();
				params.put("action", "login");
				params.put("phone", phone);
				params.put("phone_code", code);
				params.put("login_type", "2");
				params.put("facility_token", UmengRegistrar.getRegistrationId(LoginDynamicActivity.this));


				params.put("facility_info", Common.getDeviceName() + ","
					+ android.os.Build.VERSION.RELEASE);
				params.put("token", Common.signAction("login"));
				final Common.LoadingHandler handler = Common.loading(LoginDynamicActivity.this,"登录中...");
				Http.postApi(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpSuccessInterface() {
					@Override
					public void run(String result) {
						Log.i("login-result", result);
						handler.close();
						try {
							JSONObject loginResult = new JSONObject(result);
							String status = loginResult.getString("status");

							if (status.equals("100")) {
								JSONObject userInfo = loginResult.getJSONObject("result");
								MyApplication application = (MyApplication) getApplication();
								application.setUserId(userInfo.getString("id"));

								Intent intent = new Intent(LoginDynamicActivity.this, MainActivity.class);
								startActivity(intent);
								finish();



								SharedPreferences sp = getSharedPreferences("user_info",
									MODE_PRIVATE);
								SharedPreferences.Editor edit = sp.edit();
								edit.putString("phone", phone);
								edit.apply();

							} else {
								Toast.makeText(LoginDynamicActivity.this, loginResult.getString("message"), Toast.LENGTH_SHORT).show();
							}


						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});

		String phone = ((MyApplication)getApplication()).phone;
		findTextViewById(R.id.phone).setText(phone);
	}
	private int times = 60;
	public void startTimer() {
		times = 60;
		findViewById(R.id.get_code).setEnabled(false);
		findViewById(R.id.get_code).setBackgroundResource(R.drawable.radius_gray_rect);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if(times==0) {
					findViewById(R.id.get_code).setEnabled(true);
					((TextView)findViewById(R.id.get_code)).setText(getResources().getString(R.string.get_code));
					((TextView)findViewById(R.id.get_code)).setBackgroundResource(R.drawable.radius_get_code);
					return;
				}
				((TextView)findViewById(R.id.get_code)).setText((times--)+"秒");
				super.handleMessage(msg);
			};
		};
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// 需要做的事:发送消息
				Message message = new Message();
				handler.sendMessage(message);
			}
		};
		timer.schedule(task, 0, 1000); // 1s后执行task,经过1s再次执行
	}
}
