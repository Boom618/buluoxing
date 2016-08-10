package com.buluoxing.famous.kol;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buluoxing.famous.R;
import com.buluoxing.famous.view.FlowLayout;
import com.diy.AutoNewLineLayout;
import com.umeng.analytics.MobclickAgent;
import com.util.Common;
import com.util.Http;
import com.util.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
//  网红-> 筛选
public class FilterActivity extends Activity {

	public HashMap<String,City> citySelect = new HashMap<>();
	public HashMap<String,City> cityAllList = new HashMap<>();
	private String allCityId;
	private String[] cityIdList;
	private HashMap<String,Boolean> initSelectArea = new HashMap<>();
	private ArrayList<TextView> areaForSelectList = new ArrayList<>();
	private ArrayList<String> domainIds = new ArrayList<>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter);

		View backView = findViewById(R.id.back);
		if(backView!=null ) {
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		loadAreaList();
		cityIdList = getIntent().getStringArrayExtra("city_id_list");


		domainIds = getIntent().getStringArrayListExtra("domain_ids");

		for (int i=0;i<domainIds.size();i++) {
			initSelectArea.put(domainIds.get(i),true) ;
		}

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
				// Intent mIntent = new Intent();
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
				intent.putStringArrayListExtra("domain_ids", selectArea);
				setResult(101, intent);
				finish();
			}
		});
		findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				citySelect(cityAllList.get(allCityId));
				for (int i=0;i<areaForSelectList.size();i++) {
					SelectInfo info = (SelectInfo) areaForSelectList.get(i).getTag();
					unSelectArea(areaForSelectList.get(i));
				}
				initSelectView();
			}
		});
	}

	// 显示 选中的 城市 列表
	private void showSelectCityList(){
		ArrayList<City> citySelectList = new ArrayList<City>();
		for (String key : citySelect.keySet()) {
			City city = citySelect.get(key);
			citySelectList.add(city);
		}
		int cityCount = citySelectList.size();
		Log.i("count", cityCount + "");
		int pagePerNumber = 4;
		Double pageNum = Math.ceil(cityCount / (float) pagePerNumber);

		((LinearLayout)findViewById(R.id.select_list)).removeAllViews();

		for (int j=0;j<pageNum;j++) {
			LinearLayout cityRow = new LinearLayout(this);
			cityRow.setOrientation(LinearLayout.HORIZONTAL);
			cityRow.setGravity(Gravity.CENTER_VERTICAL);
			LinearLayout.LayoutParams cityRowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			cityRow.setLayoutParams(cityRowParams);
			for (int k=0;k<pagePerNumber;k++) {
				double index = j*pagePerNumber+k;
				if(index<cityCount) {
					City city = citySelectList.get((int)index);
					TextView cityView = new TextView(this);
					cityView.setText(city.name);

					cityView.setSingleLine(true);
					cityView.setEllipsize(TextUtils.TruncateAt.END);

					cityView.setTextColor(getResources().getColor(R.color.blue_common));
					cityView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.px35));
					cityView.setGravity(Gravity.CENTER);
					// 原来 字体显示不全
					LinearLayout.LayoutParams cityParams = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.px183),
//					LinearLayout.LayoutParams cityParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
					cityParams.setMargins(5,0,5,0);
					cityView.setLayoutParams(cityParams);
					cityRow.addView(cityView);
				}
			}
			((LinearLayout)findViewById(R.id.select_list)).addView(cityRow);
		}
	}

	//  显示 list 的 （ 省 - 市 ） 列表
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



							final TextView cityView = new TextView(this);

							cityView.setText(cityInfo.getString("city"));
							cityView.setSingleLine(true);
							cityView.setEllipsize(TextUtils.TruncateAt.END);
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
					for (int i = 0; i < allDomainList.length(); i++) {

						JSONObject area = allDomainList.getJSONObject(i);
						TextView textView = makeAreaForSelect(area.getString("name"), i);
						autoNewLineLayout.addNewView(textView);
						areaForSelectList.add(textView);
						Log.i("dddddd", area.toString());

						if (initSelectArea.get(area.getString("id")) != null) {
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
	//  显示 领域 列表
	private void initSelectView(){
		FlowLayout Layout = (FlowLayout) findViewById(R.id.area_list);
		Layout.removeAllViews();
		for (int i=0;i<areaForSelectList.size();i++) {
			TextView textView = areaForSelectList.get(i);
			SelectInfo info = (SelectInfo) textView.getTag();
			if (info.select) {
					Layout.addView(makeSelectArea(textView.getText().toString()));
//				int childCount = Layout.getChildCount();
//				if (childCount < 2) {
//				}else {
//					T.showOnce(FilterActivity.this,"最多只能选择两个领域");
//				}
			}
		}
	}
	private TextView makeSelectArea(String text) {
		TextView textView = new TextView(this);
		textView.setText(text);
		textView.setTextColor(getResources().getColor(R.color.blue_common));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.px40));
		//int width = (int) getResources().getDimension(R.dimen.px160);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		params.rightMargin = (int) getResources().getDimension(R.dimen.px10);
		textView.setLayoutParams(params);
		return textView;
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
