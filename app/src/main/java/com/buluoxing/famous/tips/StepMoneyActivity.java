package com.buluoxing.famous.tips;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.umeng.analytics.MobclickAgent;

public class StepMoneyActivity extends Activity {

//	@Override
//	protected int getLayout() {
//		return R.layout.activity_step_money;
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step_money);
		findViewById(R.id.do_mission).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(202);
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
