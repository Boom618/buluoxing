package com.buluoxing.famous.tips;

import android.os.Bundle;
import android.view.View;

import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;

public class StepMissionActivity extends MyActivity {

	@Override
	protected int getLayout() {
		return R.layout.activity_step_mission;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.step_share).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(203);
				finish();
			}
		});
	}
}
