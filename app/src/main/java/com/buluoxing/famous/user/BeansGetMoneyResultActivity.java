package com.buluoxing.famous.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
// 红豆抽红包 - 抽红包结果
public class BeansGetMoneyResultActivity extends MyActivity {

	private static final String TAG = "BeansMoney";

	@Override
	protected int getLayout() {
		return R.layout.activity_beans_get_money_result;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});


		HashMap<String,String> params = new HashMap<>();

		params.put("action","pumpRedPackeg");
		params.put("user_id", Common.getUserId(this));
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/System/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {

				Log.i(TAG,result.toString());
				try {
					if(result.getString("status").equals(Config.HttpSuccessCode)) {
						findTextViewById(R.id.how_much).setText(result.getString("result"));
						findTextViewById(R.id.message).setText("恭喜获得" + result.getString("result") + "元现金红包");
					} else {
						// result.getString("message")
						Toast.makeText(BeansGetMoneyResultActivity.this,"亲,红豆数量不够",Toast.LENGTH_SHORT).show();
						finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void run(JSONArray result) {

			}
		});

		findTextViewById(R.id.find_detail).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BeansGetMoneyResultActivity.this,MyMoneyActivity.class);
				startActivity(intent);
			}
		});
	}
}
