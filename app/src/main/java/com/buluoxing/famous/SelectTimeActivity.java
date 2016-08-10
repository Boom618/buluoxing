package com.buluoxing.famous;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
// 发布任务 - 开始时间  选择时间
public class SelectTimeActivity extends MyActivity {

	public  DatePicker datePicker;
	public TimePicker timePicker;

	@Override
	protected int getLayout() {
		return R.layout.activity_select_time;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		datePicker = (DatePicker) findViewById(R.id.datePicker);
		timePicker = (TimePicker) findViewById(R.id.timePicker);

		Intent intent = getIntent();
		long defaultTime = intent.getLongExtra("default_time", 0);


		timePicker.setIs24HourView(true);
		Calendar calendar = Calendar.getInstance();
		if(defaultTime>0) {
			calendar.setTime(new Date(defaultTime));
		} else {
			calendar.setTime(new Date());
		}

		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		datePicker.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));


		findViewById(R.id.select_time).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent();

				int y = datePicker.getYear();
				int month = datePicker.getMonth();
				int d = datePicker.getDayOfMonth();

				int h = timePicker.getCurrentHour();
				int i = timePicker.getCurrentMinute();

				Calendar calendar = Calendar.getInstance();
				calendar.set(y, month, d, h, i, 0);

				if (calendar.getTime().getTime() < new Date().getTime()) {
					Toast.makeText(SelectTimeActivity.this, "不可以选择已经过去的时间", Toast.LENGTH_SHORT).show();
					return;
				}

				mIntent.putExtra("time", calendar.getTime().getTime());
				// 设置结果，并进行传送
				setResult(getIntent().getIntExtra("code", 0), mIntent);
				finish();
			}
		});

	}
}
