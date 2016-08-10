package com.buluoxing.famous;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class RegistActivity extends Activity {

	private String facility_token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);

		//MyApplication application = (MyApplication) getApplication();
		//application.registActivity = this;

		findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.get_code).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String api = Http.buildBaseApiUrl("/Home/Members/index");
				String action = "sendMessage";
				String token = Common.signAction(action);

				String phone = ((EditText)findViewById(R.id.phone)).getText().toString();


				if(phone.equals("") || !Common.checkPhone(phone)) {
					Toast.makeText(RegistActivity.this,"手机号码不正确",Toast.LENGTH_SHORT).show();
					return;
				}


				HashMap<String,String> params = new HashMap<String, String>();
				params.put("action", action);
				params.put("phone", phone);
				params.put("token", token);
				params.put("type", "1");

				Http.postApi(api, params, new Http.HttpSuccessInterface() {
					@Override
					public void run(String result) {
						try {
							JSONObject resultObject = new JSONObject(result);

							if(resultObject.getString("status").equals(Config.HttpSuccessCode)) {
								startTimer();
								Toast.makeText(RegistActivity.this, "短信已经发送", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(RegistActivity.this, resultObject.getString("message"), Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

			}
		});

		findViewById(R.id.next_step).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				final String phone = ((TextView)findViewById(R.id.phone)).getText().toString();
				final String code = ((TextView)findViewById(R.id.code)).getText().toString();
				final String password1 = ((TextView)findViewById(R.id.password1)).getText().toString();
				final String password2 = ((TextView)findViewById(R.id.password2)).getText().toString();
				//密码的长度
				int psw1=password1.length();
				int psw2=password2.length();
				if(psw1<6){
					Toast.makeText(RegistActivity.this, "密码最少6位", Toast.LENGTH_SHORT).show();
					return;
				}
				if(phone.equals("")  || !Common.checkPhone(phone)) {
					Toast.makeText(RegistActivity.this,"手机号码不正确",Toast.LENGTH_SHORT).show();
					return;
				}

				if(code.equals("")) {
					Toast.makeText(RegistActivity.this,"验证码不能为空",Toast.LENGTH_SHORT).show();
					return;
				}





				if(password1.equals("")) {
					Toast.makeText(RegistActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
					return;
				}

				if(!password1.equals(password2)) {
					Toast.makeText(RegistActivity.this,"两个密码不一样",Toast.LENGTH_SHORT).show();
					return;
				}


				HashMap<String,String> params = new HashMap<String, String>();
				params.put("action", "checkMessage");
				params.put("phone", phone);
				params.put("phone_code", code);


				final Common.LoadingHandler handler = Common.loading(RegistActivity.this);
				Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
					@Override
					public void run(String result) {

					}

					@Override
					public void run(JSONObject result) {
						handler.close();
						try {
							if (result.getString("status").equals(Config.HttpSuccessCode)) {
								Intent registDetailIntend = new Intent(RegistActivity.this, RegistDetailActivity.class);
								registDetailIntend.putExtra("phone", phone);
								registDetailIntend.putExtra("code", code);
								registDetailIntend.putExtra("password1", password1);
								registDetailIntend.putExtra("password2", password2);
								startActivity(registDetailIntend);
								finish();
							} else {
								Toast.makeText(RegistActivity.this,"短信验证码不正确！",Toast.LENGTH_SHORT).show();
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

		findViewById(R.id.login_direct).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});


	}
	private int times = 60;
	public void startTimer() {
		times = 60;
		findViewById(R.id.get_code).setEnabled(false);
		findViewById(R.id.get_code).setBackgroundResource(R.drawable.radius_rect_white);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if(times==0) {
					findViewById(R.id.get_code).setEnabled(true);
					((TextView)findViewById(R.id.get_code)).setText(getResources().getString(R.string.get_code));
					((TextView)findViewById(R.id.get_code)).setBackgroundResource(R.drawable.radius_rect);
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
