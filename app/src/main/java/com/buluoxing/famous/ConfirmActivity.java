package com.buluoxing.famous;

import android.os.Bundle;
import android.view.View;
// 退出 APP Activity/ 解除微信 绑定  -    DialogFragment
public class ConfirmActivity extends MyActivity {

	@Override
	protected int getLayout() {
		return R.layout.activity_confirm;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		findTextViewById(R.id.message).setText(getIntent().getStringExtra("message"));

		if(getIntent().getStringExtra("yes")!=null) {
			findTextViewById(R.id.yes).setText(getIntent().getStringExtra("yes"));
		}
		if(getIntent().getStringExtra("cancel")!=null) {
			findTextViewById(R.id.cancel).setText(getIntent().getStringExtra("cancel"));
		}

		findTextViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(1);
				finish();
			}
		});
		findTextViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(0);
				finish();
			}
		});
	}
}
