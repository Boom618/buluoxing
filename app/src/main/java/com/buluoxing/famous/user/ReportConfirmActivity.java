package com.buluoxing.famous.user;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.buluoxing.famous.R;
import com.buluoxing.famous.RegistDetailActivity;
import com.umeng.analytics.MobclickAgent;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ReportConfirmActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_confirm);

		findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String,String> params = new HashMap<String, String>();
				params.put("action","taskInform");
				params.put("user_id", Common.getUserId(ReportConfirmActivity.this));
				params.put("finish_id",getIntent().getStringExtra("finish_id"));

				final Common.LoadingHandler handler = Common.loading(ReportConfirmActivity.this,"举报中...");
				Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
					@Override
					public void run(String result) {

					}

					@Override
					public void run(JSONObject result) {
						handler.close();

						try {
							if(result.getString("status").equals(Config.HttpSuccessCode)) {
								Toast.makeText(ReportConfirmActivity.this, "举报成功", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(ReportConfirmActivity.this, result.getString("message"), Toast.LENGTH_SHORT).show();
							}
							setResult(101);
							finish();
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
