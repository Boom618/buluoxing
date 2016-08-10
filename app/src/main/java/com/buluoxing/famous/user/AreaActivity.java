package com.buluoxing.famous.user;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buluoxing.famous.R;
import com.detail.UserInfo;
import com.umeng.analytics.MobclickAgent;
import com.util.Common;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
// 网红档案管理 领域    改为 KolAreaActivity 替代
public class AreaActivity extends Activity {
	private String[] domainList;
	private JSONArray allDomainList;
	private HashMap<String,Boolean> initSelectArea = new HashMap<>();
	private ArrayList<TextView> areaForSelectList = new ArrayList<>();
	private UserInfo userDetail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_area);

		View backView = findViewById(R.id.back);
		if(backView!=null ) {
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
		String userId = Common.getUserId(this);
		HashMap<String, String> params = new HashMap<>();
		params.put("action", "userRefreshInfo");
		params.put("user_id", userId);
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {
			}

			@Override
			public void run(JSONObject result) {

				Log.i("result",result.toString());

				try {
					JSONObject userInfo = result.getJSONObject("result");
					userDetail = new UserInfo(userInfo);
					String domainStr = userDetail.domain;
					domainList = domainStr.split(",");

					for (int i = 0; i < domainList.length; i++) {
						initSelectArea.put(domainList[i], true);
						((LinearLayout) findViewById(R.id.area_selected)).addView(makeSelectArea(domainList[i]));
					}
					loadAreaList();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void run(JSONArray result) {

			}
		});

		findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


				HashMap<String,String> params = userDetail.getSaveParams();
				params.put("action","modifyUserInfo");
				params.put("user_id",Common.getUserId(AreaActivity.this));

				String area = "";

				ArrayList<String > fortest = new ArrayList<String>();

				for (int i=0;i<areaForSelectList.size();i++) {
					SelectInfo info = (SelectInfo) areaForSelectList.get(i).getTag();
					if(info.select) {
						fortest.add("1");
						try {
							area +=allDomainList.getJSONObject(info.index).getString("id")+",";
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				if(fortest.size()>2) {
					Toast.makeText(AreaActivity.this,"最多只能选择两个领域!",Toast.LENGTH_SHORT).show();
					return;
				}
				if(area.length()>0) {
					area  = area.substring(0,area.length()-1);
				}


				params.put("domian_str", area);


				final Common.LoadingHandler handler = Common.loading(AreaActivity.this,"正在保存...");
				Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
					@Override
					public void run(String result) {

					}

					@Override
					public void run(JSONObject result) {
						Log.i("save", result.toString());
						handler.close();

						Toast.makeText(AreaActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
						finish();
					}

					@Override
					public void run(JSONArray result) {

					}
				});
			}
		});
	}

	private TextView makeSelectArea(String text) {
		TextView textView = new TextView(this);
		textView.setText(text);
		textView.setTextColor(getResources().getColor(R.color.blue_common));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.px40));
		int width = (int) getResources().getDimension(R.dimen.px100);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;


		textView.setLayoutParams(params);
		return textView;


	}

	public class SelectInfo{
		int index = 0;
		Boolean select = false;
		public SelectInfo(int i,boolean s) {
			index = i;
			select = s;
		}
	}
	private void initSelectView(){
		((LinearLayout)findViewById(R.id.area_selected)).removeAllViews();
		for (int i=0;i<areaForSelectList.size();i++) {
			TextView textView = areaForSelectList.get(i);
			SelectInfo info = (SelectInfo) textView.getTag();
			if (info.select) {
				((LinearLayout)findViewById(R.id.area_selected)).addView(makeSelectArea(textView.getText().toString()));
			}
		}
	}
	private void selectArea(TextView textView){
		textView.setBackgroundResource(R.drawable.radius_rect_little_blue);
		textView.setTextColor(Color.parseColor("#ffffff"));

		SelectInfo info = (SelectInfo) textView.getTag();
		info.select = true;
		textView.setTag(info);
		initSelectView();
	}

	private void unSelectArea(TextView textView){
		textView.setBackgroundResource(R.drawable.radius_rect_little);
		textView.setTextColor(Color.parseColor("#5a5a5a"));

		SelectInfo info = (SelectInfo) textView.getTag();
		info.select = false;
		textView.setTag(info);
		initSelectView();
	}

	private TextView makeAreaForSelect(String text,final int index) {
		TextView textView = new TextView(this);
		textView.setText(text);
		textView.setBackgroundResource(R.drawable.radius_rect_little);
		textView.setTextColor(Color.parseColor("#5a5a5a"));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.px35));
		textView.setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.px216),
			(int)getResources().getDimension(R.dimen.px66));
		params.leftMargin =(int) getResources().getDimension(R.dimen.px46);
		textView.setLayoutParams(params);
		textView.setTag(new SelectInfo(index, false));

		if(initSelectArea.get(text)!=null) {
			textView.setBackgroundResource(R.drawable.radius_rect_little_blue);
			textView.setTextColor(Color.parseColor("#ffffff"));
			SelectInfo info = (SelectInfo) textView.getTag();
			info.select = true;
			textView.setTag(info);
		}

		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					JSONObject area = allDomainList.getJSONObject(index);

					SelectInfo info = (SelectInfo) v.getTag();
					if(info.select) {
						unSelectArea((TextView) v);
					} else {
						selectArea((TextView) v);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});


		return textView;
	}


	private void loadAreaList() {
		String userId = Common.getUserId(this);
		HashMap<String, String> params = new HashMap<>();
		params.put("action", "sysDomainList");

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/System/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {
			}

			@Override
			public void run(JSONObject result) {
				try {
					allDomainList = result.getJSONArray("result");

					int pageNum = (int) Math.ceil(allDomainList.length()/4.0);
					for(int i=0;i<pageNum;i++) {

						LinearLayout row = new LinearLayout(AreaActivity.this);
						row.setOrientation(LinearLayout.HORIZONTAL);
						LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						rowParams.topMargin = (int) getResources().getDimension(R.dimen.px52);
						row.setLayoutParams(rowParams);

						for(int j=0;j<4;j++) {
							int index = i*4+j;
							if(index<allDomainList.length()) {
								JSONObject area = allDomainList.getJSONObject(index);
								TextView textView = makeAreaForSelect(area.getString("name"),index);
								areaForSelectList.add(textView);
								row.addView(textView,index);
							}
						}

						((LinearLayout)findViewById(R.id.area_list)).addView(row);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void run(JSONArray result) {

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
