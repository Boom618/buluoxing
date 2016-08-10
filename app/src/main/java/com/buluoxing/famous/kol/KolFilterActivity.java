package com.buluoxing.famous.kol;

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

import com.buluoxing.famous.AreaSelectorActivity;
import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.diy.AutoNewLineLayout;
import com.util.Common;
import com.util.Http;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class KolFilterActivity extends MyActivity {
	private HashMap<String,Boolean> initSelectArea = new HashMap<>();
	private ArrayList<TextView> areaForSelectList = new ArrayList<>();
	private String[] cityIdList = {};
	private String allCityId;
	private String[] cityNameList = {};
	private ArrayList<String> domainIds = new ArrayList<>();

	@Override
	protected int getLayout() {
		return R.layout.activity_kol_filter;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadAreaList();


		cityIdList = getIntent().getStringArrayExtra("city_id_list");
		cityNameList = getIntent().getStringArrayExtra("city_name_list");
		domainIds = getIntent().getStringArrayListExtra("domain_ids");

		for (int i=0;i<domainIds.size();i++) {
			initSelectArea.put(domainIds.get(i),true) ;
		}

		if(cityNameList!=null) {
			String citySelect = StringUtils.join(cityNameList, ",");
			((TextView)findViewById(R.id.area)).setText(citySelect);
		}

		Common.getProvinceData(this, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				try {
					JSONObject resultJson = new JSONObject(result);
					JSONArray provinceList = resultJson.getJSONArray("result");

					JSONObject allCity = provinceList.getJSONObject(provinceList.length() - 1).getJSONArray("city_array").getJSONObject(0);

					Log.i("all_city", allCity.toString());
					allCityId = allCity.getString("id");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		findViewById(R.id.select_city).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(KolFilterActivity.this, AreaSelectorActivity.class);
				intent.putExtra("city_id_list", cityIdList);
				startActivityForResult(intent, 101);
			}
		});

		findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i=0;i<areaForSelectList.size();i++) {
					SelectInfo info = (SelectInfo) areaForSelectList.get(i).getTag();
					unSelectArea(areaForSelectList.get(i));
				}
				initSelectView();
				String[] lll =  {allCityId};
				cityIdList = lll;
				((TextView)findViewById(R.id.area)).setText("全国");
			}
		});
		findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.putExtra("city_name_list",cityNameList);
				intent.putExtra("city_id_list",cityIdList);

				ArrayList<String> selectArea = new ArrayList<String>();
				for (int i=0;i<areaForSelectList.size();i++) {
					SelectInfo info = (SelectInfo) areaForSelectList.get(i).getTag();
					if(info.select) {
						try {
							selectArea.add(allDomainList.getJSONObject(info.index).getString("id"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				intent.putStringArrayListExtra("domain_ids",selectArea);
				setResult(101, intent);
				finish();
			}
		});

	}

	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		if(requestCode==101) {
			if(data==null) {
				return;
			}
			cityIdList = data.getStringArrayExtra("city_id_list");
			cityNameList = data.getStringArrayExtra("city_name_list");
			String citySelect = StringUtils.join(cityNameList, ",");
			((TextView)findViewById(R.id.area)).setText(citySelect);
		}
	}
	private JSONArray allDomainList;
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

					AutoNewLineLayout autoNewLineLayout = (AutoNewLineLayout) findViewById(R.id.all_domain_list);
					autoNewLineLayout.setOnelineNumber(4);
					autoNewLineLayout.setRowDistance((int) getResources().getDimension(R.dimen.px33));
					for (int i=0;i<allDomainList.length();i++) {

						JSONObject area = allDomainList.getJSONObject(i);
						TextView textView = makeAreaForSelect(area.getString("name"), i);
						autoNewLineLayout.addNewView(textView);
						areaForSelectList.add(textView);
						Log.i("dddddd",area.toString());

						if(initSelectArea.get(area.getString("id"))!=null) {
							selectArea(textView);
						}

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

	public class SelectInfo{
		int index = 0;
		Boolean select = false;
		public SelectInfo(int i,boolean s) {
			index = i;
			select = s;
		}
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



		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					JSONObject area = allDomainList.getJSONObject(index);

					SelectInfo info = (SelectInfo) v.getTag();
					if (info.select) {
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
	private void initSelectView(){
		((LinearLayout)findViewById(R.id.area_list)).removeAllViews();
		for (int i=0;i<areaForSelectList.size();i++) {
			TextView textView = areaForSelectList.get(i);
			SelectInfo info = (SelectInfo) textView.getTag();
			if (info.select) {
				((LinearLayout) findViewById(R.id.area_list)).addView(makeSelectArea(textView.getText().toString()));
			}
		}
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
}
