package com.buluoxing.famous.user;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.buluoxing.famous.R;
import com.detail.UserInfo;
import com.umeng.analytics.MobclickAgent;
import com.util.Common;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
// 个人中心 - 申请成为网红  网红档案
public class KolSetupActivity extends Activity {

	JSONObject kolInfo = null;

	private String request_kol;
	private UserInfo userInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kol_setup);
		request_kol = getIntent().getStringExtra("request_kol");



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
		params.put("action", "userRefreshInfo");
		params.put("user_id", Common.getUserId(KolSetupActivity.this));

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {
				Log.i("kol", result.toString());
				try {
					kolInfo = result.getJSONObject("result");
					userInfo = new UserInfo(kolInfo);

					((EditText) findViewById(R.id.wx_id_edit)).setText(kolInfo.getString("wechat_id"));
					((EditText) findViewById(R.id.weibo_id_edit)).setText(kolInfo.getString("weibo_name"));
					((EditText) findViewById(R.id.weibo_url_edit)).setText(kolInfo.getString("weibo_url"));
					((EditText) findViewById(R.id.desc)).setText(kolInfo.getString("desc"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void run(JSONArray result) {

			}
		});

		HashMap<String,String> params22 = new HashMap<>();
		params22.put("action","findKolDetail");
		params22.put("kol_id",Common.getUserId(KolSetupActivity.this));
		params22.put("user_id",Common.getUserId(KolSetupActivity.this));
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params22, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {
				try {
					JSONObject user = result.getJSONObject("result");

					((EditText) findViewById(R.id.wx_id_edit)).setText(user.getString("wechat_id"));
					((EditText) findViewById(R.id.weibo_id_edit)).setText(user.getString("weibo_name"));
					((EditText) findViewById(R.id.weibo_url_edit)).setText(user.getString("weibo_url"));
					((EditText) findViewById(R.id.desc)).setText(user.getString("desc"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void run(JSONArray result) {

			}
		});



		findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String, String> params = userInfo.getSaveParams();

				if (request_kol == null) {

					params.put("action", "modifyUserInfo");
				} else {
					params.put("action", "applicationKol");
				}
				params.put("user_id", Common.getUserId(KolSetupActivity.this));

				params.put("wechat_id", ((EditText) findViewById(R.id.wx_id_edit)).getText().toString());
				params.put("weibo_name", ((EditText) findViewById(R.id.weibo_id_edit)).getText().toString());
				params.put("weibo_url", ((EditText) findViewById(R.id.weibo_url_edit)).getText().toString());
				params.put("desc", ((EditText) findViewById(R.id.desc)).getText().toString());
				final Common.LoadingHandler handler = Common.loading(KolSetupActivity.this, "正在保存...");
				Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
					@Override
					public void run(String result) {

					}

					@Override
					public void run(JSONObject result) {
						Log.i("save", result.toString());
						handler.close();
						if (request_kol == null) {
							Toast.makeText(KolSetupActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
							finish();
						} else {
							Toast.makeText(KolSetupActivity.this, "申请网红成功", Toast.LENGTH_SHORT).show();
							finish();
						}
					}

					@Override
					public void run(JSONArray result) {

					}
				});
			}
		});

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
