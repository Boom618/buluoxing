package com.buluoxing.famous;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.umeng.message.UmengRegistrar;
import com.util.Common;
import com.util.Http;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends MyActivity {

	@Override
	protected int getLayout() {
		return R.layout.activity_login;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		Log.i("hh", getResources().getDimension(R.dimen.screen_width) + "");

		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();

//		Log.i("hh", width+ ":"+height);

		findViewById(R.id.go_login).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String phone = ((EditText) findViewById(R.id.phone)).getText().toString();
				String password = ((EditText) findViewById(R.id.password)).getText().toString();

				if (phone.equals("") || !Common.checkPhone(phone)) {
					Toast.makeText(LoginActivity.this, "手机号码不正确", Toast.LENGTH_SHORT).show();
					return;
				}

				if (password.equals("")) {
					Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}

				Http.enableCookie(getApplicationContext());

				HashMap<String, String> params = new HashMap<String, String>();
				params.put("action", "login");
				params.put("phone", phone);
				params.put("password", password);
				params.put("login_type", "1");
				params.put("facility_token", UmengRegistrar.getRegistrationId(LoginActivity.this));


				params.put("facility_info", Common.getDeviceName() + ","
					+ android.os.Build.VERSION.RELEASE);
				params.put("token", Common.signAction("login"));

//				final Common.LoadingHandler handler = Common.loading(LoginActivity.this, "登录中...");
//				final SpotsDialog dialog = new SpotsDialog(LoginActivity.this, "登录中");
//				dialog.show();
				final SweetAlertDialog dialog = showHttpDialog("登录中",true);
				dialog.show();
				Http.postApi(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpSuccessInterface() {
					@Override
					public void run(String result) {
//						Log.i("login-result", result);
//						handler.close();

						try {
							JSONObject loginResult = new JSONObject(result);
							String status = loginResult.getString("status");

							if (status.equals("100")) {
								dialog.dismiss();
								JSONObject userInfo = loginResult.getJSONObject("result");
								MyApplication application = (MyApplication) getApplication();
								application.setUserId(userInfo.getString("id"));

								Intent intent = new Intent(LoginActivity.this, MainActivity.class);
								startActivity(intent);
								finish();

								SharedPreferences sp = getSharedPreferences("user_info",
									MODE_PRIVATE);
								SharedPreferences.Editor edit = sp.edit();
								edit.putString("phone", phone);
								edit.apply();

							} else {
								if (dialog != null){
									dialog.dismiss();
								}
								Toast.makeText(LoginActivity.this, loginResult.getString("message"), Toast.LENGTH_SHORT).show();
							}


						} catch (JSONException e) {
							if (dialog != null){
								dialog.dismiss();
							}
							e.printStackTrace();
						}
					}
				});
			}
		});


		findViewById(R.id.regist_fast).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent registIntend = new Intent(LoginActivity.this, com.buluoxing.famous.RegistActivity.class);
				startActivity(registIntend);
			}
		});

		findViewById(R.id.foget_password).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent loginDynamicIntend = new Intent(LoginActivity.this, com.buluoxing.famous.LoginDynamicActivity.class);
				startActivity(loginDynamicIntend);
				finish();
			}
		});

		String phone = ((MyApplication)getApplication()).phone;
		findTextViewById(R.id.phone).setText(phone);

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
			MyApplication application = (MyApplication) getApplication();
			if(application.mainActivity!=null) {
				application.mainActivity.finish();
			}
		} else if(keyCode == KeyEvent.KEYCODE_MENU) {
			return false;
		} else if(keyCode == KeyEvent.KEYCODE_HOME) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
