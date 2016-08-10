package com.buluoxing.famous.mission;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.buluoxing.famous.user.AddBeansActivity;
import com.buluoxing.famous.user.AddMoneyActivity;

public class NeedMoreMoneyActivity extends MyActivity {

	@Override
	protected int getLayout() {
		return R.layout.activity_need_more_money;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_need_more_money);

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

		findViewById(R.id.add_money).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(getIntent().getStringExtra("type").equals("1")) {
					goActivity(AddBeansActivity.class);
				}else {
					goActivity(AddMoneyActivity.class);
				}
			}
		});
	}
}
