package com.buluoxing.famous.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.util.Common;
import com.util.Config;
import com.util.Http;
import com.util.L;
import com.util.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
// 用户中心 - 红豆抽红包
public class BeansToMoneyActivity extends MyActivity {

	private int index = 1;

	private String integral ;  // 用户红豆

	@Override
	protected int getLayout() {
		return R.layout.activity_beans_to_money;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			String hb_jf =  Config.sysConfig.getJSONObject("prompt_conf").getString("hb_jf");
			L.d(" 红豆数量 = " + hb_jf);
			String tips = hb_jf+"红豆抽一次";
			findTextViewById(R.id.show_me_money).setText(tips);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Intent intent = getIntent();
		integral = intent.getStringExtra("integral");

		findViewById(R.id.show_me_money).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ( Integer.parseInt(integral) >= 50){
					Intent intent = new Intent(BeansToMoneyActivity.this,BeansGetMoneyResultActivity.class);
					startActivity(intent);
				}else {
					T.showOnce(BeansToMoneyActivity.this,"亲,红豆数量不够");
				}
			}
		});


		startTimer();
	}

	private void loadHistory() {
		HashMap<String,String> params = new HashMap<>();
		params.put("action","redPackegList");
		params.put("user_id", Common.getUserId(this));

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/System/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {
				((LinearLayout) findViewById(R.id.history_container)).removeAllViews();
				try {
					JSONArray history = result.getJSONArray("result");
					for (int i = 0; i < history.length(); i++) {
						TextView textView = new TextView(BeansToMoneyActivity.this);
						LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.px743),
							(int) getResources().getDimension(R.dimen.px137)
						);
						textView.setLayoutParams(params1);
						textView.setText(history.getJSONObject(i).getString("str"));
						textView.setTextColor(Color.parseColor("#5e5e5e"));
						textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.px40));
						textView.setGravity(Gravity.CENTER);
						((LinearLayout) findViewById(R.id.history_container)).addView(textView);
					}
					number = history.length();
					index = 0;
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void run(JSONArray result) {

			}
		});
	}


	private int number = 0;
	public android.os.Handler handler = new android.os.Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(number==0) {
				return;
			}
			int rowHeight = (int) getResources().getDimension(R.dimen.px137);
			ScrollView scrollView = (ScrollView) findViewById(R.id.history);
			if(index>0) {
				scrollView.smoothScrollTo(0, rowHeight * index);
			} else {
				scrollView.scrollTo(0, rowHeight * index);
			}
			index++;
			if(index==number-1) {
				index = 0;
			}
		}
	};
	public void startTimer() {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// 需要做的事:发送消息
				Message message = new Message();
				handler.sendMessage(message);
			}
		};
		timer.schedule(task, 0, 2000); // 1s后执行task,经过1s再次执行
	}

	public void onResume() {
		super.onResume();

		loadHistory();
	}
}
