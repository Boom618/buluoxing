package com.buluoxing.famous.mission;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.CompletedUserListAdapter;
import com.buluoxing.famous.BrowserActivity;
import com.buluoxing.famous.ConfirmActivity;
import com.buluoxing.famous.ImageListActivity;
import com.buluoxing.famous.MoneyPackageOpendActivity;
import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.buluoxing.famous.tips.StepMissionActivity;
import com.buluoxing.famous.tips.StepWaitActivity;
import com.buluoxing.famous.user.BeansToMoneyActivity;
import com.detail.Mission;
import com.diy.MyListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.Common;
import com.util.Config;
import com.util.FileUpload;
import com.util.Http;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
// 赚钱	任务进行中的  任务详情
public class DoMissionActivity extends MyActivity {

	private MyListView listView;
	private CompletedUserListAdapter mAdapter;
	private JSONArray dataList = new JSONArray();
	private int pageNum = 1;
	private JSONArray sampleList;
	private boolean uploading = false;
	private ImageView currentUploadImageView;
	private ArrayList<ImageView> addImageBtn = new ArrayList<>();
	private JSONObject task;
	private String taskType;
	private String[] photoIds;
	private String[] photoUploadUrls;
	private Mission mission;
	private ArrayList<ImageView> addConfirmImageBtn =  new ArrayList<>();
	private ArrayList<LinearLayout> itemList = new ArrayList<>();
	private JSONObject taskDetail;
	private ScrollView scrollView;
	private boolean timerHasStart = false;

	private ImageView confirmImage; //做任务  再次确认截图

	private String surplusNum = "0";		// 任务剩余数量

	@Override
	protected int getLayout() {
		return R.layout.activity_do_mission;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		scrollView = (ScrollView) findViewById(R.id.scrollView);
		View backView = findViewById(R.id.back);
		if(backView!=null ) {
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		try {
			task = new JSONObject(getIntent().getStringExtra("task"));
			taskType = getIntent().getStringExtra("task_type");
			mission = new Mission(task,this);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		listView = (MyListView)findViewById(R.id.completed_user_list);
		listView.setFocusable(false);
		listView.setFocusableInTouchMode(false);


		loadMission();
	}
	public void loadMission(){
		HashMap<String,String> params = new HashMap<>();
		params.put("action", "taskDetail");
		try {
			params.put("task_id",task.getString("id"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.put("user_id", Common.getUserId(this));

		final Common.LoadingHandler handler = Common.loading(this,"加载任务...");

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {
				handler.close();
				try {
					taskDetail = result.getJSONObject("result");
					//mission.price = taskDetail.getJSONObject("task_array").getString("price");

					JSONObject taskBasic = taskDetail.getJSONObject("task_array");

					mission.title = taskBasic.getString("title");
					mission.taskType = taskBasic.getString("task_type");
					mission.taskPatternStr = taskBasic.getString("task_pattern_str");
					mission.integral = taskBasic.getString("integral");
					mission.share_img = taskBasic.getString("share_img");
					mission.money_surplus = taskBasic.getString("surplus_price");
					mission.remainMoney = taskBasic.getString("surplus_price");
					mission.task_require = taskBasic.getString("task_require");
					mission.finish_id = taskBasic.getString("finish_id");
					mission.isfinish = taskBasic.getString("isfinish");
					mission.is_getred = taskBasic.getInt("is_getred");
					mission.price = taskBasic.getString("price");

					mission.finishdate = taskBasic.getLong("finishdate");
					surplusNum = taskBasic.getString("surplus_num");
					mission.fomat();

				} catch (JSONException e) {
					e.printStackTrace();
				}
				init();
			}

			@Override
			public void run(JSONArray result) {

			}
		});
	}

	public void reloadInfo() {

		Log.i("fucl", "重新加载信息");
		HashMap<String,String> params = new HashMap<>();
		params.put("action", "taskDetail");
		try {
			params.put("task_id",task.getString("id"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.put("user_id", Common.getUserId(this));

		final Common.LoadingHandler handler = Common.loading(this,"加载任务...");

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {
				handler.close();
				try {
					taskDetail = result.getJSONObject("result");

					Log.i("fuckck", taskDetail.toString());

					JSONObject taskBasic = taskDetail.getJSONObject("task_array");
					mission.price = taskBasic.getString("price");

					mission.title = taskBasic.getString("title");
					mission.taskType = taskBasic.getString("task_type");
					mission.taskPatternStr = taskBasic.getString("task_pattern_str");
					mission.integral = taskBasic.getString("integral");
					mission.share_img = taskBasic.getString("share_img");
					mission.money_surplus = taskBasic.getString("surplus_price");
					mission.remainMoney = taskBasic.getString("surplus_price");
					mission.task_require = taskBasic.getString("task_require");
					mission.finish_id = taskBasic.getString("finish_id");
					mission.isfinish = taskBasic.getString("isfinish");
					mission.is_getred = taskBasic.getInt("is_getred");
					mission.price = taskBasic.getString("price");
					mission.finishdate = taskBasic.getLong("finishdate");
					mission.fomat();

					String getWhat = "";
					if (taskType.equals(Config.TaskBeans)) {
						getWhat = task.getString("integral");
						getWhat = getWhat += "红豆";
						findTextViewById(R.id.reward_type).setText("奖励红豆：");
					} else {
						getWhat = mission.remainMoney += "元";
						findTextViewById(R.id.reward_type).setText("红包还剩：");
					}

					((TextView) findViewById(R.id.get_what)).setText(getWhat);
					((TextView) findViewById(R.id.title)).setText(task.getString("task_title"));
					findTextViewById(R.id.mission_request).setText(mission.task_require);
					findTextViewById(R.id.mission_type).setText(mission.taskPatternStr);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void run(JSONArray result) {

			}
		});
	}

	public void setTitleAndRequireOneLine(){
		findTextViewById(R.id.title).setSingleLine(true);
		findTextViewById(R.id.mission_request).setSingleLine(true);

		findTextViewById(R.id.title).setEllipsize(TextUtils.TruncateAt.valueOf("END"));
		findTextViewById(R.id.mission_request).setEllipsize(TextUtils.TruncateAt.valueOf("END"));
		Log.i("guivk", "一行");
	}
	public void setTitleAndRequireNotOneLine(){
		findTextViewById(R.id.title).setSingleLine(false);
		findTextViewById(R.id.mission_request).setSingleLine(false);
	}
	public void init() {
		try {

			Log.i("task", task.toString());

			String getWhat = "";
			if(taskType.equals(Config.TaskBeans)) {
				getWhat = task.getString("integral");
				getWhat = getWhat += "红豆";
				findTextViewById(R.id.reward_type).setText("奖励红豆：");
			} else {
				getWhat = mission.remainMoney += "元";
				findTextViewById(R.id.reward_type).setText("红包还剩：");
			}

			((TextView)findViewById(R.id.get_what)).setText(getWhat);
			((TextView)findViewById(R.id.title)).setText(task.getString("task_title"));

			if(!mission.task_require.equals("")) {
				findTextViewById(R.id.mission_request).setText(mission.task_require);
			} else {
				findViewById(R.id.task_require_row).setVisibility(View.INVISIBLE);
			}
			findTextViewById(R.id.mission_type).setText(mission.taskPatternStr);

			// 剩余任务数量
			((TextView) findViewById(R.id.num_surplus)).setText(surplusNum + "个");

			findViewById(R.id.preview).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(DoMissionActivity.this, BrowserActivity.class);
					intent.putExtra("url",mission.linkUrl);
					startActivity(intent);
				}
			});


			JSONArray uploadedSampleList = taskDetail.getJSONArray("photo_array");

			HashMap<String,JSONObject> uploadedHashMap = new HashMap<>();

			if (uploadedSampleList.length() != 0 ){
				for (int i=0;i<uploadedSampleList.length();i++) {
					JSONObject sample = uploadedSampleList.getJSONObject(i);
					uploadedHashMap.put(sample.getString("pattern_id"),sample);
				}
			}

			sampleList = taskDetail.getJSONArray("mode_array");
			Log.d("Test", "init: sampleList : " + sampleList);

			for (int i=0;i<sampleList.length();i++) {
				JSONObject sample = sampleList.getJSONObject(i);

				if(!sample.getString("img_url").equals("")) {
					sample.put("mode_url", sample.getString("img_url"));
				}
				if(uploadedHashMap.containsKey(sample.getString("pattern_id"))) {
					//sample.put("mode_url",uploadedHashMap.get(sample.getString("pattern_id")).getString("mode_url"));
					sample.put("screen_short",uploadedHashMap.get(sample.getString("pattern_id")).getString("img_url"));
					sample.put("yc_url",uploadedHashMap.get(sample.getString("pattern_id")).getString("yc_url"));
				}
			}


			photoIds = new String[sampleList.length()];
			photoUploadUrls = new String[sampleList.length()];
			initSampleList();

			// 点击做任务 事件
			findViewById(R.id.do_mission).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
						mission.doThis();
				}
			});

			if(mission.taskType.equals(Config.TaskMoney)) {
				if(mission.money_for_get) {

					Log.i("remain",mission.remainGeRewardTime+"");
					if(mission.remainGeRewardTime>0) {
						startTimer();
						findViewById(R.id.complete_task).setBackgroundResource(R.drawable.radius_rect_disable);
					} else {
						findViewById(R.id.complete_task).setBackgroundResource(R.drawable.radius_login);
						setCanGetMoneyNow();
					}




				} else {
					if(Common.checkShowTips(this,"missionmission")) {
						Intent intent = new Intent(this, StepMissionActivity.class);
						startActivityForResult(intent, 203);
						setTitleAndRequireOneLine();
					}
				}
			} else {

				if(Common.checkShowTips(this,"missionmission")) {
					Intent intent = new Intent(this, StepMissionActivity.class);
					startActivityForResult(intent, 203);
					setTitleAndRequireOneLine();
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}


		mAdapter = new CompletedUserListAdapter(this);
		mAdapter.setDataList(dataList);
		listView.setAdapter(mAdapter);
		listView.setFocusable(false);

		listView.setMyPullUpListViewCallBack(new MyListView.MyPullUpListViewCallBack() {
			@Override
			public void scrollBottomState() {
				getDataList();
			}
		});

		getDataList();

		findViewById(R.id.complete_task).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				final AlertDialog dialog = new AlertDialog.Builder(DoMissionActivity.this).create();
				View view = View.inflate(DoMissionActivity.this, R.layout.dialogfragment_out_app, null);
				dialog.setView(view);

				TextView message = (TextView) view.findViewById(R.id.message);
				String string = "";
				try {
					string = Config.sysConfig.getJSONObject("prompt_conf").getString("confirm_upload");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				message.setText(string.isEmpty()?"请确保图片正确并符合要求,如不符合会被举报,3次以上将禁止接任务7天" : string);
				TextView yes = (TextView) view.findViewById(R.id.yes);
				TextView cancel = (TextView) view.findViewById(R.id.cancel);
				cancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				yes.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();

				if (!mission.money_for_get) {

					for (int i = 0; i < photoIds.length; i++) {
						if (photoIds[i] == null) {
							//Toast.makeText(DoMissionActivity.this, "请完成上传任务要求的截图", Toast.LENGTH_SHORT).show();
							return;
						}
					}

					HashMap<String, String> params = new HashMap<String, String>();
					params.put("action", "userFinish");
					params.put("type", taskType);
					params.put("photo_id", StringUtils.join(photoIds, ","));
					params.put("user_id", Common.getUserId(DoMissionActivity.this));
					try {
						params.put("task_id", task.getString("id"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					final Common.LoadingHandler handler = Common.loading(DoMissionActivity.this, "正在完成任务");
					Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
						@Override
						public void run(String result) {

						}

						@Override
						public void run(JSONObject result) {
							handler.close();
							Log.i("complete_mission", result.toString());


							Intent intentNoti = new Intent();
							intentNoti.setAction("update.task.list");
							sendBroadcast(intentNoti);

							reloadInfo();

							try {
								if (result.getString("status").equals(Config.HttpSuccessCode)) {
									if (taskType.equals(Config.TaskMoney)) {

										mission.price = result.getJSONObject("result").getString("reward");
										mission.finish_id = result.getJSONObject("result").getString("finish_id");
										setWait();

									} else {

										if (result.getJSONObject("result").getInt("reward") < 50) {
											Intent intent = new Intent(DoMissionActivity.this, ConfirmActivity.class);

											String hb_jf = Config.sysConfig.getJSONObject("prompt_conf").getString("hb_jf");

											intent.putExtra("message", "恭喜" + result.getJSONObject("result").getString("reward") + "红豆成功冲入豆包！\r\n累计" + hb_jf
												+ "红豆后就能抽取现金红包哦！");
											intent.putExtra("yes", "立即抽红包");
											intent.putExtra("no", " 继续赚豆");
											startActivityForResult(intent, 1001);
										} else {
											Intent intent = new Intent(DoMissionActivity.this, ConfirmActivity.class);
											intent.putExtra("message", "恭喜" + result.getJSONObject("result").getString("reward") + "红豆成功冲入豆包！");
											intent.putExtra("yes", "立即抽红包");
											intent.putExtra("no", "下次再抽");
											startActivityForResult(intent, 1002);
										}
									}
								} else {
									String message = result.getString("message");
									//Log.i("TAG", "run: message = " + message );

									Toast.makeText(DoMissionActivity.this, message , Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void run(JSONArray result) {

						}
					});
				} else if (mission.money_for_get) {
					if (mission.getMoneyNow) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("action", "userRewared");
						params.put("user_id", Common.getUserId(DoMissionActivity.this));
						params.put("task_id", mission.taskId);

						Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
							@Override
							public void run(String result) {

							}

							@Override
							public void run(JSONObject result) {
								Intent intentNoti = new Intent();
								intentNoti.setAction("update.task.list");
								sendBroadcast(intentNoti);

								reloadInfo();

								try {
									if (result.getString("status").equals(Config.HttpSuccessCode)) {
										Intent intent = new Intent(DoMissionActivity.this, MoneyPackageOpendActivity.class);
										try {
											intent.putExtra("rewards", result.getJSONObject("result").getString("reward"));
										} catch (JSONException e) {
											e.printStackTrace();
										}
										intent.putExtra("task_type", mission.taskType);
										startActivity(intent);
										finish();
									} else {
										// 同一张图片
										confirmImage.setImageResource(R.drawable.loading_image);
										Toast.makeText(DoMissionActivity.this, result.getString("message"), Toast.LENGTH_SHORT).show();
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
				}
					}
				});
				dialog.show();

			}
		});
	}

	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		Log.i("result", requestCode + "");

		if(resultCode==203) {
			mission.doThis();
			return;
		}


		if(requestCode==1001) {
			if(resultCode==1) {
				goActivity(BeansToMoneyActivity.class);
			} else {
			}
			finish();
			return;
		}

		if(requestCode==1002) {
			if(resultCode==1) {
				goActivity(BeansToMoneyActivity.class);
			} else {
			}
			finish();
			return;
		}
		if(data==null) {
			return;
		}

		if(requestCode<10000) {

			FileUpload fileUpload = new FileUpload(this);
			fileUpload.setImageShowHolder(addImageBtn.get(requestCode));

			HashMap<String, String> params = new HashMap<>();
			try {
				params.put("task_mode", sampleList.getJSONObject(requestCode).getString("pattern_id"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			params.put("type", "4");
			fileUpload.checkIsScreenShot = true;
			fileUpload.uploadByResult(data, params);
			fileUpload.setOnUploadStatusChangedListener(new FileUpload.OnUploadStatusChangedListener() {
				@Override
				public void success(JSONObject result) {
					try {
						photoIds[requestCode] = result.getJSONObject("result").getString("id");
						photoUploadUrls[requestCode] = result.getJSONObject("result").getString("img_url");
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			});
		} else {
			FileUpload fileUpload = new FileUpload(this);
			fileUpload.setImageShowHolder(addConfirmImageBtn.get(requestCode-10000));
			HashMap<String, String> params = new HashMap<>();
			try {
				params.put("task_mode", sampleList.getJSONObject(requestCode-10000).getString("pattern_id"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			params.put("type", "6");
			params.put("finish_id", mission.finish_id);
			fileUpload.checkIsScreenShot = true;
			fileUpload.uploadByResult(data, params);
			fileUpload.setOnUploadStatusChangedListener(new FileUpload.OnUploadStatusChangedListener() {
				@Override
				public void success(JSONObject result) {
				}
			});
		}
	}
	public void setWait(){
		mission.finishdate = new Date().getTime();
		mission.money_for_get = true;

		mission.resetRemainTime();

		if(mission.remainGeRewardTime>0) {
			startTimer();
			findViewById(R.id.complete_task).setBackgroundResource(R.drawable.radius_rect_disable);
			for (int i=0;i<itemList.size();i++) {
				ImageListActivity.setImageViewCanShowBig((ImageView) itemList.get(i).findViewById(R.id.add_sample_image), photoUploadUrls[i]);
			}

			if(Common.checkShowTips(this,"waitwait")) {
//				for (int i=0;i<itemList.size()-1;i++) {
//					 任务类型 微信群转发  不延迟 都显示图片
//					itemList.get(i).setVisibility(View.GONE);
//				}
				Intent intent = new Intent(this, StepWaitActivity.class);
				startActivity(intent);
				setTitleAndRequireOneLine();
			}

		} else {
			findViewById(R.id.complete_task).setBackgroundResource(R.drawable.radius_login);
			setCanGetMoneyNow();
		}
	}
	public void setCanGetMoneyNow() {

		if(StepWaitActivity.myself!=null) {
			try {
				StepWaitActivity.myself.finish();
				StepWaitActivity.myself = null;
			} catch (Exception e) {

			}
		}
		((TextView)findViewById(R.id.complete_task)).setText("立即领取"+mission.price+"元");
		((TextView)findViewById(R.id.complete_task)).setBackgroundResource(R.drawable.radius_login);

		for (int i=0;i<itemList.size();i++) {
			try {
				if(sampleList.getJSONObject(i).getString("is_yc").equals("2")) {
					itemList.get(i).findViewById(R.id.do_mission_confirm).setVisibility(View.VISIBLE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		mission.money_for_get = true;
		mission.getMoneyNow = true;
		showAddConfirmImage();

	}

	public void showAddConfirmImage() {
		for(int i=0;i<sampleList.length();i++) {
			JSONObject taskMode = null;
			try {
				taskMode = sampleList.getJSONObject(i);
				if(mission.taskType.equals(Config.TaskMoney) && mission.money_for_get && (taskMode.getInt("is_yc")==2)) {
					itemList.get(i).findViewById(R.id.do_mission_confirm).setVisibility(View.VISIBLE);
				} else {
//					itemList.get(i).setVisibility(View.GONE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}
	public void startTimer() {
		if(!timerHasStart) {
			timerHasStart = true;
		} else {
			return;
		}
		final android.os.Handler handler = new android.os.Handler() {
			public void handleMessage(Message msg) {
				if( mission.remainGeRewardTime==0) {
					setCanGetMoneyNow();
					return;
				}
				// 倒计时
				((TextView) findViewById(R.id.complete_task)).setText(Common.getMissionRemainGetMoneyText(mission.remainGeRewardTime--) + mission.price + "元");
				super.handleMessage(msg);
			};
		};
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// 需要做的事:发送消息
				Message message = new Message();
				handler.sendMessage(message);
			}
		};
		timer.schedule(task, 0, 1000); // 1s后执行task,经过1s再次执行
	}

	// 做任务 动态 代码加载 图片展示
	public void initSampleList(){
		// is_yc = 1 不延迟  is_yc = 2 延迟
		((LinearLayout) findViewById(R.id.sample_list)).removeAllViews();
		for(int i=0;i<sampleList.length();i++) {

			try {
				JSONObject taskMode = sampleList.getJSONObject(i);
				LayoutInflater mInflater = LayoutInflater.from(this);
				final LinearLayout itemView = (LinearLayout) mInflater.inflate(R.layout.sample_do_item, null);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				//params.bottomMargin = (int) getResources().getDimension(R.dimen.px55);
				itemView.setLayoutParams(params);
				((LinearLayout) findViewById(R.id.sample_list)).addView(itemView);

				if(mission.taskType.equals(Config.TaskMoney) && mission.money_for_get && (taskMode.getInt("is_yc")==2) && mission.remainGeRewardTime<=0) {
					itemView.findViewById(R.id.do_mission_confirm).setVisibility(View.VISIBLE);
				}
				if(mission.taskType.equals(Config.TaskMoney) && mission.money_for_get && (taskMode.getInt("is_yc")==1)) {
//					itemView.setVisibility(View.GONE);
				}

				((TextView) itemView.findViewById(R.id.sample_title)).setText(taskMode.getString("title") + "示例图");
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(taskMode.getString("mode_url"), (ImageView) itemView.findViewById(R.id.sample), Config.options);
				ImageListActivity.setImageViewCanShowBig((ImageView) itemView.findViewById(R.id.sample), taskMode.getString("mode_url"));

				Log.i("taskmode", taskMode.toString());
				String s = taskMode.toString();

				confirmImage = (ImageView) itemView.findViewById(R.id.add_image_confirm);
				try {
					ImageLoader imageLoader2 = ImageLoader.getInstance();
					imageLoader2.displayImage(taskMode.getString("screen_short"), (ImageView) itemView.findViewById(R.id.add_sample_image), Config.options);

					ImageLoader imageLoader3 = ImageLoader.getInstance();
					imageLoader3.displayImage(taskMode.getString("yc_url"), confirmImage, Config.options);
				} catch (JSONException e) {
					Log.i("fuck","没有上传任务图...");
				}
				itemView.setTag(i);
				itemView.findViewById(R.id.add_sample_image).setTag(i);
				confirmImage.setTag(i + 10000);

				addImageBtn.add((ImageView) itemView.findViewById(R.id.add_sample_image));
				//addConfirmImageBtn.add((ImageView) itemView.findViewById(R.id.add_image_confirm));
				addConfirmImageBtn.add(confirmImage);



				itemView.findViewById(R.id.add_sample_image).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(intent, (int) v.getTag());
					}
				});


				confirmImage.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(intent, (int) v.getTag());
					}
				});

				if(mission.taskType.equals(Config.TaskMoney)) {
					if(mission.money_for_get) {
						ImageListActivity.setImageViewCanShowBig((ImageView) itemView.findViewById(R.id.add_sample_image),taskMode.getString("screen_short"));
					}
				}

				itemList.add(itemView);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void getDataList() {
		final HashMap<String,String> params = new HashMap<>();
		params.put("action","finishTaskList");
		params.put("task_id", getIntent().getStringExtra("task_id"));
		params.put("page_num", pageNum+ "");
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				Log.i("user_list", "DoMission :" + result);
				try {
					JSONObject resultObject = new JSONObject(result);

					if (!resultObject.getString("status").equals(Config.HttpSuccessCode)) {
						if( pageNum == 1 ) {
							listView.noMoreData("暂无数据");
						} else {
							listView.noMoreData();
						}
						if ( pageNum == 1) {
							LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) listView.getLayoutParams();
							params1.height = (int) getResources().getDimension(R.dimen.px100);
							listView.setLayoutParams(params1);
						}
					} else {
						JSONArray newData = resultObject.getJSONArray("result");

						if (pageNum == 1) {
							LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) listView.getLayoutParams();
							if (newData.length() * (int) getResources().getDimension(R.dimen.px179) < getResources().getDimension(R.dimen.px1920)) {
								params1.height = newData.length() * (int) getResources().getDimension(R.dimen.px179);
							} else {
//								params1.height = (int) getResources().getDimension(R.dimen.px1920);
								params1.height = newData.length() * (int) getResources().getDimension(R.dimen.px179);

							}
							listView.setLayoutParams(params1);
						}
						for (int i = 0; i < newData.length(); i++) {
							dataList.put(newData.get(i));
						}
						mAdapter.notifyDataSetChanged();
						pageNum++;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void onResume() {
		super.onResume();
		setTitleAndRequireNotOneLine();
	}

}
