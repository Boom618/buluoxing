package com.buluoxing.famous.mission;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.buluoxing.famous.R;
import com.umeng.analytics.MobclickAgent;

// 首页 - 发布任务
public class MissionTypeSheetActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mission_type_sheet);

		View backView = findViewById(R.id.back);
		if(backView!=null ) {
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		findViewById(R.id.pub_red_bean_misson).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MissionTypeSheetActivity.this,PubMissonBeanActivity.class);
				startActivity(intent);
				finish();
			}
		});

		findViewById(R.id.pub_money_mission).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MissionTypeSheetActivity.this,PubMissonMoneyActivity.class);
				startActivity(intent);
				finish();
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
