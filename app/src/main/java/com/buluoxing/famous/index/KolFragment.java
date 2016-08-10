package com.buluoxing.famous.index;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.adapter.KolListAdapter;
import com.buluoxing.famous.kol.FilterActivity;
import com.buluoxing.famous.kol.KolActivity;
import com.buluoxing.famous.R;
import com.buluoxing.famous.kol.KolFilterActivity;
import com.bumptech.glide.Glide;
import com.diy.MyListView;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * 网红主页
 */
public class KolFragment extends Fragment {

	public View view = null;

	public JSONArray dataList = new JSONArray();
	public MyListView listView ;
	public KolListAdapter mAdapter ;
	private boolean loading = false;
	private String[] cityIdList = {};
	private ArrayList<String> domainIds = new ArrayList<>();
	private String keyword = "";
	private int pageNum = 1;
	private String[] cityNameList={};
	private SwipeRefreshLayout swipeRefreshLayout;

	public KolFragment() {
	}


	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		if(view==null) {
			view = inflater.inflate(R.layout.fragment_kol, container, false);

			listView = (MyListView)view.findViewById(R.id.kol_list);
			mAdapter = new KolListAdapter(getContext());//得到一个MyAdapter对象lv.setAdapter(mAdapter);
			mAdapter.setDataList(dataList);
			listView.setAdapter(mAdapter);

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					try {
						JSONObject data = dataList.getJSONObject(position);

						Intent intent = new Intent(getContext(), KolActivity.class);
						intent.putExtra("kol_id", data.getString("id"));
						startActivity(intent);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			listView.setMyPullUpListViewCallBack(new MyListView.MyPullUpListViewCallBack() {
				@Override
				public void scrollBottomState() {
					loadList();
				}
			});
			loadList();

			view.findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), FilterActivity.class);
					intent.putStringArrayListExtra("domain_ids", domainIds);
					intent.putExtra("city_id_list", cityIdList);
					intent.putExtra("city_name_list", cityNameList);
					startActivityForResult(intent, 101);
				}
			});

			((EditText)view.findViewById(R.id.search)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
						keyword = ((EditText) v).getText().toString();

						reloadDataList();
						return true;
					}
					return false;
				}
			});

			((EditText)view.findViewById(R.id.search)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						view.findViewById(R.id.cancel_search).setVisibility(View.VISIBLE);
					} else {
						view.findViewById(R.id.cancel_search).setVisibility(View.GONE);
					}
				}
			});
			view.findViewById(R.id.cancel_search).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					keyword = "";
					((EditText)view.findViewById(R.id.search)).setText("");
					reloadDataList();
					view.findViewById(R.id.tmp_focus).requestFocus();
					InputMethodManager imm = (InputMethodManager)
						getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			});





			swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
			//设置刷新时动画的颜色，可以设置4个
			swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

			swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					reloadDataList();
				}
			});
		}
		return view;
	}
	private void reloadDataList(){

		dataList = new JSONArray();
		pageNum = 1;
		mAdapter.setDataList(dataList);
		mAdapter.notifyDataSetChanged();
		listView.haveData();
		Log.i("fuck", "fuck");
		loadList();
	}

	public void onActivityResult(int requestCode,int resultCode,Intent data) {
		if(data==null) {
			return;
		}
		if(requestCode==101) {
			cityIdList = data.getStringArrayExtra("city_id_list");
			cityNameList = data.getStringArrayExtra("city_name_list");
			domainIds = data.getStringArrayListExtra("domain_ids");

			reloadDataList();
		}
	}

	private void loadList(){
		if(loading) {
			return;
		}
		loading = true;

		HashMap<String,String> params = new HashMap<>();
		params.put("action","findKolList");
		params.put("user_id", Common.getUserId(this.getContext()));
		params.put("page_num", pageNum+"");

		String cityName = StringUtils.join(cityNameList, ",");
		if(cityName.equals("全国")) {
			cityName ="";
		}

		params.put("city_name", cityName);
		params.put("domain_id", StringUtils.join(domainIds.toArray(),","));
		params.put("search", keyword);

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				swipeRefreshLayout.setRefreshing(false);
				Log.i("kol_list", result);
				try {
					JSONObject resultObject = new JSONObject(result);

					if (!resultObject.getString("status").equals(Config.HttpSuccessCode) ) {
						if( pageNum==1) {
							listView.noMoreData("暂无数据");
						} else {
							listView.noMoreData();
						}
					} else {
						JSONArray newData = resultObject.getJSONArray("result");
						for (int i = 0; i < newData.length(); i++) {
							dataList.put(newData.get(i));
						}
						mAdapter.notifyDataSetChanged();
						pageNum++;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				loading = false;
			}
		});
	}
}
