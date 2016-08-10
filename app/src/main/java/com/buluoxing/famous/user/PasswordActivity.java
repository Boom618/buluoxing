package com.buluoxing.famous.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.buluoxing.famous.LoginActivity;
import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.MyApplication;
import com.buluoxing.famous.R;
import com.util.Common;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class PasswordActivity extends MyActivity {

	@Override
	protected int getLayout() {
		return R.layout.activity_password;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		findViewById(R.id.save_pass).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


				String password1 = findTextViewById(R.id.password1).getText().toString();
				String password2 = findTextViewById(R.id.password2).getText().toString();
				int psw1=password1.length();
				int psw2=password2.length();
				if(psw1<6){
					Toast.makeText(PasswordActivity.this, "密码最少6位", Toast.LENGTH_SHORT).show();
					return;
				}
				final Common.LoadingHandler handler  = Common.loading(PasswordActivity.this, "保存...");
				if(!password1.equals(password2)) {
					Toast.makeText(PasswordActivity.this,"两次密码不同!",Toast.LENGTH_SHORT).show();
					handler.close();
					return;
				}

				HashMap<String,String> params = new HashMap<String, String>();
				params.put("action","modifyPassword");
				params.put("user_id",Common.getUserId(PasswordActivity.this));
				params.put("new_password",findTextViewById(R.id.password1).getText().toString());

				Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
					@Override
					public void run(String result) {

					}

					@Override
					public void run(JSONObject result) {
						handler.close();
						Toast.makeText(PasswordActivity.this,"密码修改成功，请重新登录!",Toast.LENGTH_SHORT).show();
						finish();

						MyApplication application = (MyApplication)getApplication();
						application.logout();
						Intent intent = new Intent(PasswordActivity.this,LoginActivity.class);
						startActivity(intent);

						application.mainActivity.finish();
					}

					@Override
					public void run(JSONArray result) {

					}
				});
			}
		});
	}
}
