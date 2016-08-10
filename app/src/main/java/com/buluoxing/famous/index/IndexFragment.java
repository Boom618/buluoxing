package com.buluoxing.famous.index;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapter.ImageLoopAdapter;
import com.buluoxing.famous.kol.KolActivity;
import com.buluoxing.famous.LeftActivity;
import com.buluoxing.famous.MainActivity;
import com.buluoxing.famous.R;
import com.buluoxing.famous.tips.StepIndexActivity;
import com.buluoxing.famous.user.InviteFriendsActivity;
import com.detail.Kol;
import com.detail.Mission;
import com.detail.UserInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.util.Common;
import com.util.Config;
import com.util.Http;
import com.util.ScreenUtils;
import com.util.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class IndexFragment extends Fragment  {
	public View view = null;
	public ArrayList<ImageView> dotImageList = new ArrayList<ImageView>();
	public int currentSelectPosition = 0;
	private String currentTaskId;
	private String currentTask;
	private SwipeRefreshLayout swipeRefreshLayout;
	private UpdateUIBroadcastReceiver broadcastReceiver;

	public IndexFragment() {
		// Required empty public constructor
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		if(view==null) {
			view = inflater.inflate(R.layout.fragment_index, container, false);

			swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
			//设置刷新时动画的颜色，可以设置4个
			swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

			swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					loadData();
				}
			});


			//签到
			view.findViewById(R.id.sign_in).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Common.userSign(getContext());
				}
			});

			view.findViewById(R.id.more_kol).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MainActivity activity = (MainActivity)getActivity();
					activity.mTabHost.setCurrentTab(3);
				}
			});
			view.findViewById(R.id.find_more_mission).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MainActivity activity = (MainActivity)getActivity();
					activity.mTabHost.setCurrentTab(1);
				}
			});

			view.findViewById(R.id.show_left_list).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {


					//((MainActivity)getActivity()).showCover();

					Intent intent = new Intent(getActivity(), LeftActivity.class);
					startActivity(intent);
				}
			});

			view.findViewById(R.id.invite_friends).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), InviteFriendsActivity.class);
					startActivity(intent);
				}
			});
			loadData();

			if(Common.checkShowTips(getContext(),"indexindex")) {
				Intent intent = new Intent(getActivity(), StepIndexActivity.class);
				startActivityForResult(intent, 201);
			}

			//动态注册广播
			IntentFilter filter = new IntentFilter();
			filter.addAction("update.task.list");
			broadcastReceiver = new UpdateUIBroadcastReceiver();
			getActivity().registerReceiver(broadcastReceiver, filter);

		}

		return view;
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
			loadData();
		}

	}
	public void onDestroy() {
		super.onDestroy();
		if (broadcastReceiver != null) {
			getActivity().unregisterReceiver(broadcastReceiver);
		}
	}
	private void loadData() {
		//获取banner
		initBanner();
		//网红
		initKol();
		initMissionBeans();

		//签到信息
		getSignInfo();
	}
	private void getSignInfo() {
		HashMap<String,String> params = new HashMap<>();

		params.put("action", "signRecord");
		params.put("user_id", Common.getUserId(getContext()));

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/System/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {
				try {
					((TextView) view.findViewById(R.id.sign_info)).setText(result.getString("result"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void run(JSONArray result) {

			}
		});
	}
	private  void initMissionBeans(){


		//红豆
		HashMap<String,String> params = new HashMap<>();

		params.put("action","recommendTaskList");
		params.put("user_id",Common.getUserId(getContext()));

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {

				Log.i("红豆", result);
				swipeRefreshLayout.setRefreshing(false);
				try {

					JSONObject resultObject = new JSONObject(result);


					final JSONArray missionDataList = resultObject.getJSONArray("result");
					LayoutInflater mInflater = LayoutInflater.from(getContext());
					LinearLayout beansContainer = (LinearLayout) view.findViewById(R.id.beans_mission_container);
					beansContainer.removeAllViews();
					for (int i = 0; i < missionDataList.length(); i++) {
						final JSONObject mission = missionDataList.getJSONObject(i);
						Log.e("TAG", "run: mission = " + mission );
						Log.e("TAG", "run: getActivity = " + getActivity() );
						final Mission missionDetail = new Mission(mission,getActivity());
						RelativeLayout itemView = null;
						if(missionDetail.taskType == Config.TaskBeans) {
							 itemView = (RelativeLayout) mInflater.inflate(R.layout.mission_beans_item, null);
						}  else {
							 itemView = (RelativeLayout) mInflater.inflate(R.layout.mission_money_item, null);
						}
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						itemView.setLayoutParams(params);
						beansContainer.addView(itemView);
						Log.e("TAG", "run: itemView = " + itemView );
						Log.e("TAG", "run: missionDetail = " + missionDetail );

						String is_task = mission.getString("is_task"); //0 不可做 1 可做
						missionDetail.displayInItemView(itemView,is_task);
					}

					view.findViewById(R.id.loading_text).setVisibility(View.GONE);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}


	private void setUserIcon(String url,final  RelativeLayout itemView ) {
		//头像
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.loadImage(url, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {


				((ImageView) itemView.findViewById(R.id.user_icon)).setImageBitmap(loadedImage);
			}
		});
	}
	private void initBanner() {
		HashMap<String,String> bannerParams = new HashMap<>();
		bannerParams.put("action", "bannerList");
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), bannerParams, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				Log.i("banner", result);
				try {
					JSONObject resultObject = new JSONObject(result);
					JSONArray imageList = resultObject.getJSONArray("result");
					ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
					for (int i = 0; i < imageList.length(); i++) {
						HashMap<String, String> data = new HashMap<String, String>();
						data.put("link_url", imageList.getJSONObject(i).getString("link_url"));
						data.put("img_url", imageList.getJSONObject(i).getString("img_url"));
						dataList.add(data);
					}

					ViewPager imageLoop = (ViewPager) view.findViewById(R.id.top_image_loop);
					ImageLoopAdapter adapter = new ImageLoopAdapter(getContext());
					adapter.setImageUrlList(dataList);
					imageLoop.setAdapter(adapter);


					//初始化点点点
					LinearLayout dotList = (LinearLayout) view.findViewById(R.id.dot_list);
					dotList.removeAllViews();
					dotImageList = new ArrayList<ImageView>();
					for (int i = 0; i < dataList.size(); i++) {
						ImageView imageView = new ImageView(getContext());
						imageView.setImageResource(R.drawable.image_common_dot);
						int width = (int) getResources().getDimension(R.dimen.px23);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
						params.leftMargin = (int) getResources().getDimension(R.dimen.px26);
						imageView.setLayoutParams(params);
						dotList.addView(imageView);
						dotImageList.add(imageView);
					}
					dotImageList.get(currentSelectPosition).setImageResource(R.drawable.image_current_dot);

					imageLoop.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
						@Override
						public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

						}

						@Override
						public void onPageSelected(int position) {
							dotImageList.get(currentSelectPosition).setImageResource(R.drawable.image_common_dot);
							dotImageList.get(position).setImageResource(R.drawable.image_current_dot);
							currentSelectPosition = position;
						}

						@Override
						public void onPageScrollStateChanged(int state) {

						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
					return;
				}


			}
		});
	}

	private void initKol() {
		HashMap<String,String> params = new HashMap<>();
		params.put("action", "recommendMemberList");

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {

				Log.i("kol",result);
				//红人
				try {
					JSONObject resultObject = new JSONObject(result);
					JSONArray kolList = resultObject.getJSONArray("result");

					LayoutInflater mInflater = LayoutInflater.from(getContext());
					DisplayImageOptions options = new DisplayImageOptions.Builder()
						.cacheInMemory(true)                               //启用内存缓存
						.cacheOnDisk(true)                                 //启用外存缓存
						.build();
					LinearLayout hotContainer = (LinearLayout) view.findViewById(R.id.hot_container);
					hotContainer.removeAllViews();
					for(int i=0;i<kolList.length();i++) {
						LinearLayout hotView = (LinearLayout) mInflater.inflate(R.layout.hot_kol_item,null);


						JSONObject user = kolList.getJSONObject(i);
						((TextView)hotView.findViewById(R.id.username)).setText(user.getString("nickname"));
						((TextView)hotView.findViewById(R.id.area)).setText(user.getString("domain"));

						ImageLoader imageLoader = ImageLoader.getInstance();
						imageLoader.displayImage(user.getString("photo"), (ImageView) hotView.findViewById(R.id.user_icon), options);
						hotContainer.addView(hotView);

						LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)hotView.getLayoutParams();
						params.width = 0;
						params.weight = 1;
						hotView.setLayoutParams(params);
						hotView.setTag(user.getString("id"));
						hotView.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(getContext(), KolActivity.class);
								intent.putExtra("kol_id", v.getTag().toString());
								startActivity(intent);
							}
						});


						if(user.getString("type").equals("0")){
							hotView.findViewById(R.id.kol).setVisibility(View.GONE);
							hotView.findViewById(R.id.kol_single).setVisibility(View.GONE);
						} else if(user.getString("type").equals("1")) {
							hotView.findViewById(R.id.kol).setVisibility(View.VISIBLE);
							hotView.findViewById(R.id.kol_single).setVisibility(View.GONE);
						} else if(user.getString("type").equals("2")){
							hotView.findViewById(R.id.kol).setVisibility(View.GONE);
							hotView.findViewById(R.id.kol_single).setVisibility(View.VISIBLE);
						}

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void onActivityResult(int requestCode,int resultCode,Intent data) {

		Log.i("tag",requestCode+":"+resultCode);

		if(resultCode==201) {
			MainActivity activity = (MainActivity)getActivity();
			activity.mTabHost.setCurrentTab(1);
		}
	}

}
