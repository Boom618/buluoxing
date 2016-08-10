package com.buluoxing.famous.tips;

import android.os.Bundle;

import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;

public class StepWaitActivity extends MyActivity {

	public static StepWaitActivity myself;
	@Override
	protected int getLayout() {
		return R.layout.activity_step_wait;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		myself = this;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (myself != null){
			myself = null;
		}
	}
}
