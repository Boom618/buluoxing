package com.buluoxing.famous;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

public class GetMoneyPackageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_money_package);
		View backView = findViewById(R.id.back);
		if(backView!=null ) {
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
		findViewById(R.id.open_money_package).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GetMoneyPackageActivity.this,MoneyPackageOpendActivity.class);
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
