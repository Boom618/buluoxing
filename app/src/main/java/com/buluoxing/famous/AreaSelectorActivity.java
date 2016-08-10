package com.buluoxing.famous;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.diy.AutoNewLineLayout;
import com.umeng.analytics.MobclickAgent;
import com.util.Common;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AreaSelectorActivity extends Activity {

	public HashMap<String,City> citySelect = new HashMap<>();
	public HashMap<String,City> cityAllList = new HashMap<>();
	public ArrayList<JSONObject> hotCityList = new ArrayList<>();
	private String allCityId;
	private String[] cityIdList;

	private AutoNewLineLayout autoNewLineLayout; //热门城市

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		//去除title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//去掉Activity上面的状态栏
		setContentView(R.layout.activity_area_selector);


		View backView = findViewById(R.id.back);
		if(backView!=null ) {
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
		cityIdList = getIntent().getStringArrayExtra("city_id_list");
		Common.getProvinceData(this, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				try {
					JSONObject resultJson = new JSONObject(result);
					JSONArray provinceList = resultJson.getJSONArray("result");
					Log.i("result", provinceList.toString());
					showProvinceList(provinceList);


					JSONObject allCity = provinceList.getJSONObject(provinceList.length()-1).getJSONArray("city_array").getJSONObject(0);

					Log.i("all_city",allCity.toString());

					allCityId = allCity.getString("id");
					if(cityIdList.length==0) {
						cityIdList = new String[1];
						cityIdList[0] = allCityId;
					}

					if (cityIdList != null) {
						for (int i = 0; i < cityIdList.length; i++) {
							City city = cityAllList.get(cityIdList[i]);
							if (city != null) {
								citySelect(cityAllList.get(cityIdList[i]));
							}
						}
						showSelectCityList();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent();
				ArrayList<City> citySelectList = new ArrayList<City>();
				for (String key : citySelect.keySet()) {
					City city = citySelect.get(key);
					citySelectList.add(city);
				}

				String[] cityIdList = new String[citySelectList.size()];
				String[] cityNameList = new String[citySelectList.size()];

				for (int i = 0; i < citySelectList.size(); i++) {
					cityIdList[i] = citySelectList.get(i).id;
					cityNameList[i] = citySelectList.get(i).name;
				}
				Log.i("city_id_list",cityIdList[0]);
				mIntent.putExtra("city_id_list", cityIdList);
				mIntent.putExtra("city_name_list", cityNameList);
				// 设置结果，并进行传送
				setResult(102, mIntent);
				finish();
			}
		});

		// 重置 城市
		findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				City city = cityAllList.get(allCityId);
				citySelect(city);
				//autoNewLineLayout.setBackgroundColor(Color.parseColor("#ffffff"));

				//city.holder.setTextColor(Color.parseColor("#ffffff"));


			}
		});
	}

	private void showSelectCityList(){
		ArrayList<City> citySelectList = new ArrayList<City>();
		for (String key : citySelect.keySet()) {
			City city = citySelect.get(key);
			citySelectList.add(city);
		}
		int cityCount = citySelectList.size();
		Log.i("count", cityCount + "");
		int pagePerNumber = 6;
		Double pageNum = Math.ceil(cityCount / (float) pagePerNumber);

		((LinearLayout)findViewById(R.id.select_list)).removeAllViews();

		for (int j=0;j<pageNum;j++) {
			LinearLayout cityRow = new LinearLayout(this);
			cityRow.setOrientation(LinearLayout.HORIZONTAL);
			cityRow.setGravity(Gravity.CENTER_VERTICAL);
			LinearLayout.LayoutParams cityRowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)getResources().getDimension(R.dimen.px130));
			cityRow.setLayoutParams(cityRowParams);
			for (int k=0;k<pagePerNumber;k++) {
				double index = j*pagePerNumber+k;
				if(index<cityCount) {
					City city = citySelectList.get((int)index);
					TextView cityView = new TextView(this);
					cityView.setSingleLine(true);
					cityView.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
					cityView.setText(city.name);
					cityView.setSingleLine(true);
					cityView.setTextColor(getResources().getColor(R.color.blue_common));
					cityView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.px35));
					cityView.setGravity(Gravity.CENTER);
					LinearLayout.LayoutParams cityParams = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.px183),
						ViewGroup.LayoutParams.WRAP_CONTENT);
					cityView.setLayoutParams(cityParams);
					cityRow.addView(cityView);
				}
			}
			((LinearLayout)findViewById(R.id.select_list)).addView(cityRow);
		}
	}

	private void showProvinceList(JSONArray provinceList) {
		LinearLayout provinceListContainer = (LinearLayout)findViewById(R.id.province_list);
		LayoutInflater mInflater = LayoutInflater.from(this);
		for (int i=0;i<provinceList.length();i++) {
			try {
				JSONObject province = provinceList.getJSONObject(i);
				LinearLayout provinceRow = (LinearLayout)mInflater.inflate(R.layout.province_info, null);


				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
				provinceRow.setLayoutParams(params);
				((TextView)provinceRow.findViewById(R.id.province_name)).setText(province.getString("province"));
				provinceListContainer.addView(provinceRow);
				provinceRow.findViewById(R.id.title_bar).setTag(false);

				provinceRow.findViewById(R.id.title_bar).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						switchCityShow((RelativeLayout) v, (LinearLayout)((LinearLayout)v.getParent()).findViewById(R.id.city_list));
					}
				});


				JSONArray cityList = province.getJSONArray("city_array");

				int cityCount = cityList.length();

				int pagePerNumber = 4;
				Double pageNum = Math.ceil(cityCount/4.0);


				for (int j=0;j<pageNum;j++) {

					LinearLayout cityRow = new LinearLayout(this);
					cityRow.setOrientation(LinearLayout.HORIZONTAL);
					LinearLayout.LayoutParams cityRowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					cityRow.setLayoutParams(cityRowParams);

					for (int k=0;k<pagePerNumber;k++) {
						double index = j*4+k;

						if(index<cityCount) {

							final JSONObject cityInfo = cityList.getJSONObject((int) index);

							if (hotCityList.size()<8) {
								hotCityList.add(cityInfo);
							}



							final TextView cityView = new TextView(this);
							cityView.setSingleLine(true);
							cityView.setEllipsize(TextUtils.TruncateAt.valueOf("END"));

							cityView.setText(cityInfo.getString("city"));
							cityView.setSingleLine(true);
							cityView.setBackgroundResource(R.drawable.radius_rect_little);
							cityView.setTextColor(Color.parseColor("#5a5a5a"));
							cityView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.px35));
							cityView.setGravity(Gravity.CENTER);
							LinearLayout.LayoutParams cityParams = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.px216),
								(int)getResources().getDimension(R.dimen.px66));
							cityParams.leftMargin =(int) getResources().getDimension(R.dimen.px37);
							cityParams.bottomMargin = (int)getResources().getDimension(R.dimen.px50);
							cityView.setLayoutParams(cityParams);
							cityRow.addView(cityView);

							City city = new City(cityInfo.getString("id"),cityInfo.getString("city"),cityView);
							cityAllList.put(city.id,city);

							cityView.setTag(city);
							cityView.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									City city = (City)v.getTag();
									citySelect(city);
								}
							});
						}
					}
					((LinearLayout)provinceRow.findViewById(R.id.city_list)).addView(cityRow);
				}



			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		initHotCityView();
	}
	private void initHotCityView(){
		for (int i=0;i<hotCityList.size();i++) {
			JSONObject cityInfo = hotCityList.get(i);
			final TextView cityView = new TextView(this);
			try {
				cityView.setText(cityInfo.getString("city"));
				cityView.setBackgroundResource(R.drawable.radius_rect_little);
				cityView.setTextColor(Color.parseColor("#5a5a5a"));
				cityView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.px35));
				cityView.setGravity(Gravity.CENTER);
				cityView.setSingleLine(true);
				LinearLayout.LayoutParams cityParams = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.px216),
					(int)getResources().getDimension(R.dimen.px66));
				cityParams.leftMargin =(int) getResources().getDimension(R.dimen.px37);
				cityParams.bottomMargin = (int)getResources().getDimension(R.dimen.px50);
				cityView.setLayoutParams(cityParams);

				City city = new City(cityInfo.getString("id"),cityInfo.getString("city"),cityView);

				cityView.setTag(city);
				cityView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						City city = (City) v.getTag();
						citySelect(city);
					}
				});

				autoNewLineLayout = ((AutoNewLineLayout) findViewById(R.id.hot_city_list));
				autoNewLineLayout.setOnelineNumber(4);
				autoNewLineLayout.setRowDistance((int) getResources().getDimension(R.dimen.px52));
				autoNewLineLayout.addNewView(cityView);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void switchCityShow(RelativeLayout titleBar,LinearLayout cityList) {
		boolean isSelect = (boolean)titleBar.getTag();
		ImageView rightIcon = (ImageView)titleBar.findViewById(R.id.show_city_list);
		if(isSelect) {
			cityList.setVisibility(View.GONE);
			rightIcon.setBackgroundResource(R.drawable.province_down);
			titleBar.setTag(false);
		} else {
			cityList.setVisibility(View.VISIBLE);
			rightIcon.setBackgroundResource(R.drawable.province_up);
			titleBar.setTag(true);
		}
	}

	private void citySelect(City city) {

		boolean isSelect = city.selected;
		if(!isSelect) {
			selectCity(city);

			//选中了全国 清空
			if (city.id.equals(allCityId)) {
				//遍历
				for (String key : citySelect.keySet()) {
					City cityOld = citySelect.get(key);
					dselectCity(cityOld);

				}
				citySelect = new HashMap<>();

			}  else{
				City allCity = cityAllList.get(allCityId);
				if(allCity!=null) {
					dselectCity(allCity);
					citySelect.remove(allCityId);
				}
			}
			citySelect.put(city.id, city);
			showSelectCityList();


		} else {
			if(city.id.equals(allCityId)) {
				return;
			}
			dselectCity(city);
			citySelect.remove(city.id);
			showSelectCityList();
		}
	}
	private void selectCity(City city) {
		city.holder.setBackgroundResource(R.drawable.radius_rect_little_blue);
		city.holder.setTextColor(Color.parseColor("#ffffff"));
		city.selected = true;
		city.holder.setTag(city);
	}
	private void dselectCity(City city) {
		city.holder.setBackgroundResource(R.drawable.radius_rect_little);
		city.holder.setTextColor(Color.parseColor("#5a5a5a"));
		city.selected = false;
		city.holder.setTag(city);

	}

	private class City {
		public String id="";
		public String name = "";
		public TextView holder  = null;
		public Boolean selected = false;
		public City(String id,String name,TextView holder) {
			this.id = id;
			this.name = name;
			this.holder = holder;
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
