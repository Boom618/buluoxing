package com.buluoxing.famous.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.buluoxing.famous.kol.KolActivity;
import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.util.Common;
//个人中心 - 网红管理  网红档案管理
public class KolManageActivity extends MyActivity {

	@Override
	protected int getLayout() {
		return R.layout.activity_kol_manage;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		findViewById(R.id.kol_manage).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(KolManageActivity.this, KolSetupActivity.class);
				startActivity(intent);
			}
		});

		findViewById(R.id.gallary_manage).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(KolManageActivity.this,ManageGalleryActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.area).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Intent intent = new Intent(KolManageActivity.this,AreaActivity.class);
				Intent intent = new Intent(KolManageActivity.this,KolAreaActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.my_zone).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(KolManageActivity.this,KolActivity.class);
				intent.putExtra("kol_id", Common.getUserId(getApplicationContext()));
				startActivity(intent);
			}
		});
	}
}
