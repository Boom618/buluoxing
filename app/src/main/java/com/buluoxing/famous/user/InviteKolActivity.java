package com.buluoxing.famous.user;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.buluoxing.famous.R;
import com.umeng.analytics.MobclickAgent;
import com.util.Common;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class InviteKolActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_kol);


		View backView = findViewById(R.id.back);
		if(backView!=null ) {
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
		findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String text  = ((EditText)findViewById(R.id.detail)).getText().toString();

				HashMap<String,String> params = new HashMap<String, String>();
				params.put("action","inviteKol");
				params.put("user_id", Common.getUserId(InviteKolActivity.this));
				params.put("kol_id", getIntent().getStringExtra("kol_id"));
				params.put("content", text);

				final Common.LoadingHandler handler = Common.loading(InviteKolActivity.this,"正在提交...");

				Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
					@Override
					public void run(String result) {

					}

					@Override
					public void run(JSONObject result) {
						handler.close();
						Toast.makeText(InviteKolActivity.this,"提交成功请等待客服联系您",Toast.LENGTH_SHORT).show();
						finish();
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
