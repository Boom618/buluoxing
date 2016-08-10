package com.buluoxing.famous.index;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.BeansMissionListAdapter;
import com.adapter.CompletedUserListAdapter;
import com.buluoxing.famous.MainActivity;
import com.buluoxing.famous.MyApplication;
import com.buluoxing.famous.R;
import com.buluoxing.famous.mission.DoMissionActivity;
import com.buluoxing.famous.mission.MissionSortActivity;
import com.buluoxing.famous.tips.StepIndexActivity;
import com.buluoxing.famous.tips.StepMoneyActivity;
import com.detail.Mission;
import com.diy.MyListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoneyFragment extends Fragment {


	private int pageNum = 1;
	private int pageNumMoney = 1;
	private MyListView listViewBeans;
	private BeansMissionListAdapter mAdapterBeans;
	private JSONArray dataListBeans = new JSONArray();


	//private String orderTimeAsc = "1";
	//private String orderTimeDesc = "2";
	//private String orderBeansDesc = "3";
	//private String orderBeansAsc = "4";
	//private String orderMoneyDesc = "5";
	//private String orderMoneyAsc = "6";

	private String currentOrder = "";
	private MyListView listViewMoney;
	private JSONArray dataListMoney = new JSONArray();
	private BeansMissionListAdapter mAdapterMoney;
	private String currentTaskType = "1";
	private String keyword = "";
	private SwipeRefreshLayout swipeRefreshLayout;
	private SwipeRefreshLayout swipeRefreshLayout2;
	private UpdateUIBroadcastReceiver broadcastReceiver;


	// 控件
	private EditText search; // 搜索
	private Button canSearch; // 取消搜索
	private TextView showBeans; // 红豆任务
	private TextView showMoney; // 红豆任务

	public MoneyFragment() {
		// Required empty public constructor
	}

	public View view = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment



		if(view==null) {
			view = inflater.inflate(R.layout.fragment_money, container, false);
			Log.e("赚钱","MoneyFragment");

			initView(view);

			mAdapterBeans = new BeansMissionListAdapter(getContext());
			mAdapterBeans.setDataList(dataListBeans);
			listViewBeans.setAdapter(mAdapterBeans);
			listViewBeans.setMyPullUpListViewCallBack(new MyListView.MyPullUpListViewCallBack() {
				@Override
				public void scrollBottomState() {
					getDataBeansList();
				}
			});
			getDataBeansList();


			search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
						keyword = ((EditText) v).getText().toString();

						Log.i("fuck", keyword);
						if (currentTaskType.equals(Config.TaskBeans)) {
							reloadDataBeansList();
						} else {
							reloadDataMoneyList();
						}
						view.findViewById(R.id.tmp_focus).requestFocus();
						InputMethodManager imm = (InputMethodManager)
							getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
						return true;
					}
					return false;
				}
			});
			search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						canSearch.setVisibility(View.VISIBLE);
					} else {
						canSearch.setVisibility(View.GONE);
					}
				}
			});
			canSearch.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					keyword = "";
					((EditText)view.findViewById(R.id.search)).setText("");
					reloadDataBeansList();
					reloadDataMoneyList();
					view.findViewById(R.id.tmp_focus).requestFocus();
					InputMethodManager imm = (InputMethodManager)
						getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			});

			view.findViewById(R.id.sort).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), MissionSortActivity.class);
					intent.putExtra("sort", currentOrder);
					intent.putExtra("type", currentTaskType);
					startActivityForResult(intent, 1);
				}
			});




			//设置刷新时动画的颜色，可以设置4个
			swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

			swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					reloadDataBeansList();
				}
			});

			//设置刷新时动画的颜色，可以设置4个
			swipeRefreshLayout2.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

			swipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					reloadDataMoneyList();
				}
			});

		}
		return view;
	}

	private void initView(View view){
		search = (EditText) view.findViewById(R.id.search);
		canSearch = (Button) view.findViewById(R.id.cancel_search);
		listViewBeans = (MyListView)view.findViewById(R.id.mission_list);
		listViewMoney = (MyListView)view.findViewById(R.id.mission_money_list);
		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		swipeRefreshLayout2 = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container2);

		showBeans = (TextView) view.findViewById(R.id.show_beans);
		showMoney = (TextView) view.findViewById(R.id.show_money);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		showBeans.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				v.setBackgroundResource(R.drawable.tab_select);
				((TextView) v).setTextColor(Color.parseColor("#47dfe4"));
				swipeRefreshLayout.setVisibility(View.VISIBLE);

				showMoney.setBackgroundResource(0);
				showMoney.setTextColor(Color.parseColor("#919296"));
				swipeRefreshLayout2.setVisibility(View.GONE);

				currentTaskType = Config.TaskBeans;
			}
		});

		showMoney.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				v.setBackgroundResource(R.drawable.tab_select);
				((TextView) v).setTextColor(Color.parseColor("#47dfe4"));
				swipeRefreshLayout2.setVisibility(View.VISIBLE);

				showBeans.setBackgroundResource(0);
				showBeans.setTextColor(Color.parseColor("#919296"));
				swipeRefreshLayout.setVisibility(View.GONE);
				currentTaskType = Config.TaskMoney;
			}
		});


		mAdapterMoney = new BeansMissionListAdapter(getContext());
		mAdapterMoney.setDataList(dataListMoney);
		listViewMoney.setAdapter(mAdapterMoney);
		listViewMoney.setMyPullUpListViewCallBack(new MyListView.MyPullUpListViewCallBack() {
			@Override
			public void scrollBottomState() {
				getDataMoneyList();
			}
		});
		getDataMoneyList();


	}

	@Override
	public void onStart() {
		super.onStart();
		//动态注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction("update.task.list");
		broadcastReceiver = new UpdateUIBroadcastReceiver();
		getActivity().registerReceiver(broadcastReceiver, filter);

		if(Common.checkShowTips(getContext(),"moneymoney")) {
			Boolean showTips = Common.checkShowTips(getContext(), "moneymoney");
			Intent intent = new Intent(getActivity(), StepMoneyActivity.class);
			startActivityForResult(intent, 202);


			showMoney.setBackgroundResource(R.drawable.tab_select);
			showMoney.setTextColor(Color.parseColor("#47dfe4"));
			swipeRefreshLayout2.setVisibility(View.VISIBLE);
			showBeans.setBackgroundResource(0);
			showBeans.setTextColor(Color.parseColor("#919296"));
			swipeRefreshLayout.setVisibility(View.GONE);
			currentTaskType = Config.TaskMoney;
		}
	}

	/**
	 * 定义广播接收器（内部类）
	 *
	 * @author lenovo
	 *
	 */
	private class UpdateUIBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			//textView.setText(String.valueOf(intent.getExtras().getInt("count")));
			reloadDataBeansList();
			reloadDataMoneyList();
		}

	}
	private void reloadDataBeansList(){

		dataListBeans = new JSONArray();
		pageNum = 1;
		mAdapterBeans.setDataList(dataListBeans);
		mAdapterBeans.notifyDataSetChanged();
		listViewBeans.haveData();
		listViewBeans.scrollTo(0,0);
		Log.i("fuck", "reloadDataBeansList");
		getDataBeansList();

	}
	private void reloadDataMoneyList(){
		dataListMoney = new JSONArray();
		pageNumMoney = 1;
		mAdapterMoney.setDataList(dataListMoney);
		mAdapterMoney.notifyDataSetChanged();
		listViewMoney.haveData();
		listViewBeans.scrollTo(0, 0);
		getDataMoneyList();
	}
	private void getDataBeansList() {
		HashMap<String,String> params = new HashMap<>();
		params.put("action","taskList");
		params.put("type",Config.TaskBeans);
		params.put("filter",currentOrder);
		params.put("search", keyword);
		params.put("user_id", Common.getUserId(getContext()));
		params.put("page_num", pageNum + "");
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				Log.i("beans_task_list", result);
				swipeRefreshLayout.setRefreshing(false);
				try {
					JSONObject resultObject = new JSONObject(result);
					if (!resultObject.getString("status").equals(Config.HttpSuccessCode)) {
						if( pageNum==1) {
							listViewBeans.noMoreData("暂无数据");
						} else {
							listViewBeans.noMoreData();
						}
					} else {
						JSONArray newData = resultObject.getJSONArray("result");
						for (int i = 0; i < newData.length(); i++) {
							dataListBeans.put(newData.get(i));
						}
						mAdapterBeans.notifyDataSetChanged();
						pageNum++;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}


	private void getDataMoneyList() {
		HashMap<String,String> params = new HashMap<>();
		params.put("action","taskList");
		params.put("type",Config.TaskMoney);
		params.put("filter",currentOrder);
		params.put("search",keyword);
		params.put("user_id", Common.getUserId(getContext()));
		params.put("page_num", pageNumMoney + "");
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				swipeRefreshLayout2.setRefreshing(false);
				Log.i("user_list", result);
				try {
					JSONObject resultObject = new JSONObject(result);
					if (!resultObject.getString("status").equals(Config.HttpSuccessCode)) {
						if( pageNumMoney==1) {
							listViewMoney.noMoreData("暂无数据");
						} else {
							listViewMoney.noMoreData();
						}
					} else {
						JSONArray newData = resultObject.getJSONArray("result");
						for (int i = 0; i < newData.length(); i++) {
							dataListMoney.put(newData.get(i));
						}
						mAdapterMoney.notifyDataSetChanged();
						pageNumMoney++;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}


	public void onActivityResult( int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);


		Log.i("fuck", "onActivityResult");
		if(requestCode==301) {
			reloadDataMoneyList();
			reloadDataBeansList();
			return;
		}

		if(resultCode==202) {


			try {

				Intent intent  = new Intent(getActivity(),DoMissionActivity.class);
				intent.putExtra("task",dataListBeans.getJSONObject(0).toString());
				intent.putExtra("task_type",Config.TaskBeans);
				startActivity(intent);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(data==null) {
			return;
		}
		Log.i("fuck222", resultCode + "");

		if(resultCode==0) {
			currentOrder = "";
		} else {
			currentOrder = resultCode + "";
		}

		if(currentTaskType.equals(Config.TaskBeans)) {
			reloadDataBeansList();
		} else {
			reloadDataMoneyList();
		}
	}
	public void onResume() {
		super.onResume();

	}
	public void onDestroy() {
		super.onDestroy();
		if (broadcastReceiver != null){
			getActivity().unregisterReceiver(broadcastReceiver);
		}
	}


}
