package com.buluoxing.famous.mission;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.adapter.CompletedUserListAdapter;
import com.buluoxing.famous.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;

public class MissionStatusActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mission_status);

		View backView = findViewById(R.id.back);
		if(backView!=null ) {
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		ListView listView = (ListView)findViewById(R.id.completed_user_list);
		CompletedUserListAdapter mAdapter = new CompletedUserListAdapter(this);//得到一个MyAdapter对象lv.setAdapter(mAdapter);
		JSONArray dataList = null;
		try {
			dataList = new JSONArray("[{\"day\":\"呵呵\"},{\"day\":\"呵呵2\"},{\"day\":\"呵呵3\"},{\"day\":\"呵呵4\"},{\"day\":\"呵呵5\"},{\"day\":\"呵呵\"},{\"day\":\"呵呵2\"},{\"day\":\"呵呵3\"},{\"day\":\"呵呵4\"},{\"day\":\"呵呵5\"}]");
			mAdapter.setDataList(dataList);
			listView.setAdapter(mAdapter);
		} catch (JSONException e) {
			e.printStackTrace();
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
