package com.buluoxing.famous;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.buluoxing.famous.user.MyBeansActivity;
import com.buluoxing.famous.user.MyMoneyActivity;
import com.umeng.analytics.MobclickAgent;
import com.util.Config;

public class MoneyPackageOpendActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money_package_opend);

		String rewards = getIntent().getStringExtra("rewards");
		final String type = getIntent().getStringExtra("task_type");


		String text ="";
		String ctext ="";
		if(type.equals(Config.TaskBeans)) {
			text = "红豆";
			ctext = String.format("恭喜获得%s红豆红包",rewards);
		} else {
			text = "元";
			ctext = String.format("恭喜获得%s元现金红包",rewards);
		}

		((TextView)findViewById(R.id.danwei)).setText(text);
		((TextView)findViewById(R.id.congratulations_money)).setText(ctext);
		((TextView)findViewById(R.id.get_money)).setText(rewards);

		findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.look_now).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(type.equals(Config.TaskBeans)) {
					Intent intent = new Intent(MoneyPackageOpendActivity.this, MyBeansActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(MoneyPackageOpendActivity.this, MyMoneyActivity.class);
					startActivity(intent);
				}
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
