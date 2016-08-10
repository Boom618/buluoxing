package com.buluoxing.famous.mission;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buluoxing.famous.MyApplication;
import com.buluoxing.famous.R;
import com.umeng.analytics.MobclickAgent;
import com.util.Config;

import java.util.ArrayList;
import java.util.HashMap;

public class MissionSortActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mission_sort);

		findViewById(R.id.shadow).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		String[] sortList = {"默认排序", "时间降序","时间升序","红豆从高到低","红豆从低到高","红包从高到低 ","红包从低到高"};


		String type = getIntent().getStringExtra("type");

		for (int i=0;i<sortList.length;i++) {
			if(type.equals(Config.TaskBeans) && ( i==5 || i==6)) {
				continue;
			}
			if(type.equals(Config.TaskMoney) && ( i==3 || i==4)) {
				continue;
			}


			TextView textView = new TextView(this);
			textView.setText(sortList[i]);
			textView.setTag(i);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			textView.setTextColor(Color.parseColor("#474747"));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.px40));
			textView.setBackgroundResource(R.drawable.title_bar_line);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.px111));
			params.leftMargin =  0;
			params.rightMargin = 0;
			params.topMargin  = 0;
			params.bottomMargin = 0;
			textView.setLayoutParams(params);

			((LinearLayout)findViewById(R.id.sort_list)).addView(textView);


			textView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.i("fuck", v.getTag().toString());
					Intent data = new Intent();
					setResult((Integer) v.getTag(),data);

					finish();
				}
			});
		}

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
