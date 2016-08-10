package com.buluoxing.famous.mission;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buluoxing.famous.ImageListActivity;
import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.buluoxing.famous.SelectTimeActivity;
import com.buluoxing.famous.user.AdnNameLengthFilter;
import com.diy.AutoNewLineLayout;
import com.diy.ContainsEmojiEditText;
import com.diy.MyCheckBox;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.Common;
import com.util.Config;
import com.util.FileUpload;
import com.util.Http;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PubMissonBeanActivity extends MyActivity {

	public JSONArray taskModelList = new JSONArray();
	public long startTime;
	public long endTime;

	private ArrayList<LinearLayout> sampleViewList = new ArrayList<>();
	private ArrayList<MyCheckBox> checkBoxList = new ArrayList<>();
	private String[] photoIds;
	private String shareImageId;
	private String currentSection = "wx";
	private int lowSpend = 0;
//	private EditText mEditTextTask;
//	private EditText mEditTextTitle;
	// 用户标题
	private ContainsEmojiEditText mEditTextTitle;
	// 用户 要求
	private ContainsEmojiEditText mEditTextTask;


	@Override
	protected int getLayout() {
		return R.layout.activity_pub_misson_bean;
	}


	private MyCheckBox initOneCheckbox(final JSONObject taskMode,int index) throws JSONException {
		MyCheckBox checkBox = new MyCheckBox(this);
		checkBox.setText(taskMode.getString("title"));
		int width = (int) getResources().getDimension(R.dimen.px423);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.bottomMargin = (int)getResources().getDimension(R.dimen.px43);
//		params.gravity = Gravity.CENTER;
		checkBox.setLayoutParams(params);
		checkBox.setTag(index);
		LinearLayout sampleView = createSample(index);
		sampleViewList.add(sampleView);
		((LinearLayout)findViewById(R.id.sample_list)).addView(sampleView);
		sampleView.setVisibility(View.GONE);


		checkBox.setOnCheckStatusChangedListener(new MyCheckBox.OnCheckStatusChangedListener() {
			@Override
			public void check(MyCheckBox cb, Boolean checked) {
				int index = (int) cb.getTag();

				if(!checked && checkIsDependentBySomeBody(index)) {
					cb.check(true);
					return;
				}


				try {
					if(taskMode.getString("is_hb").equals("2")) {
						Toast.makeText(PubMissonBeanActivity.this,"该任务模式只能发在现金任务",Toast.LENGTH_SHORT).show();
					}
					taskModelList.getJSONObject(index).put("checked", checked);

					if(!taskModelList.getJSONObject(index).getString("cat").equals(currentSection)) {
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (checked) {
					sampleViewList.get(index).setVisibility(View.VISIBLE);
					checkIsDependSomeBody(index);
				} else {
					sampleViewList.get(index).setVisibility(View.GONE);
				}
				calLowSpend();
			}
		});
		return checkBox;
	}
	private void freshSampleList() {
		for (int i=0;i<sampleViewList.size();i++) {
			try {
				if(!taskModelList.getJSONObject(i).getString("cat").equals(currentSection) ){
					sampleViewList.get(i).setVisibility(View.GONE);
				} else {
					if(taskModelList.getJSONObject(i).getBoolean("checked")){
						sampleViewList.get(i).setVisibility(View.VISIBLE);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		calLowSpend();
	}
	private void calLowSpend() {
		lowSpend = 0;
		for (int i = 0; i < taskModelList.length(); i++) {
			try {
				Boolean checked = taskModelList.getJSONObject(i).getBoolean("checked");
				if (checked) {
					if(taskModelList.getJSONObject(i).getString("cat").equals(currentSection)) {
						lowSpend += taskModelList.getJSONObject(i).getInt("inlow");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		findTextViewById(R.id.low_spend).setText(String.format("每任务最低消耗%d红豆", lowSpend));
	}


	public boolean checkIsDependentBySomeBody(int index) {

		try {
			JSONObject taskMode = taskModelList.getJSONObject(index);

			for(int i=0;i<taskModelList.length();i++) {
				JSONObject t = taskModelList.getJSONObject(i);
				if((t.getInt("is_zf")==taskMode.getInt("id")) && t.getBoolean("checked")) {
					Toast.makeText(PubMissonBeanActivity.this,"选择"+t.getString("title")+"的时候，必须选择"+taskMode.getString("title"),Toast.LENGTH_SHORT).show();
					return true;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	public void checkIsDependSomeBody(int index) {

		try {
			JSONObject taskMode = taskModelList.getJSONObject(index);
			int d = taskMode.getInt("is_zf");
			if(d>0) {
				for(int i=0;i<taskModelList.length();i++) {
					JSONObject t = taskModelList.getJSONObject(i);
					if(t.getInt("id")==d) {
						if(!checkBoxList.get(i).checked) {
							checkBoxList.get(i).check(true);
							Toast.makeText(PubMissonBeanActivity.this, taskMode.getString("title") + "模式必须选中" + taskModelList.getJSONObject(i).getString("title"), Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	private void doUnormalCheckbox(final JSONObject taskMode,MyCheckBox checkbox) throws JSONException {
		if(taskMode.getString("ischeck").equals("1")) {
			checkbox.check(true);
		}
		if(taskMode.getString("is_hb").equals("2")) {
			checkbox.setDisabled(true);
			checkbox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Toast.makeText(PubMissonBeanActivity.this,taskMode.getString("title")+"仅支持现金任务",Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//限制任务要求输入的字数
		mEditTextTask = (ContainsEmojiEditText) findViewById(R.id.mission_require);
		mEditTextTask.setFilters(new InputFilter[]{new AdnNameLengthFilter(this,45)});
		mEditTextTitle= (ContainsEmojiEditText) findViewById(R.id.mission_title);

//		if (!findTextViewById(R.id.mission_href).getText().toString().isEmpty()){
//			mEditTextTitle.setFilters(new InputFilter[]{new AdnNameLengthFilter(this,48)});
//        }

		JSONObject sysConfig = Config.sysConfig;
		try {
			JSONObject taskModelListMap = sysConfig.getJSONObject("task_mode_conf");
			JSONArray weChatList = taskModelListMap.getJSONArray("wechat");
			JSONArray weiBoList = taskModelListMap.getJSONArray("weibo");
			JSONArray otherList = taskModelListMap.getJSONArray("other");

			int index = 0;
			AutoNewLineLayout autoNewLineLayout = (AutoNewLineLayout) findViewById(R.id.mission_type_list_wx);
			autoNewLineLayout.setOnelineNumber(2);
			autoNewLineLayout.setRowDistance(0);
			for (int i=0;i<weChatList.length();i++) {
				JSONObject taskMode = weChatList.getJSONObject(i);
				taskMode.put("cat","wx");
				taskModelList.put(taskMode);
				taskMode.put("checked", false);
				MyCheckBox checkbox = initOneCheckbox(taskMode,index);
				checkBoxList.add(checkbox);
				autoNewLineLayout.addNewView(checkbox);
				index++;
				doUnormalCheckbox(taskMode,checkbox);
			}


			AutoNewLineLayout autoNewLineLayoutWeibo = (AutoNewLineLayout) findViewById(R.id.mission_type_list_weibo);
			autoNewLineLayoutWeibo.setOnelineNumber(2);
			autoNewLineLayoutWeibo.setRowDistance(0);
			for (int i=0;i<weiBoList.length();i++) {
				JSONObject taskMode = weiBoList.getJSONObject(i);
				taskMode.put("cat","weibo");
				taskModelList.put(taskMode);
				taskMode.put("checked", false);
				MyCheckBox checkbox = initOneCheckbox(taskMode,index);
				checkBoxList.add(checkbox);
				autoNewLineLayoutWeibo.addNewView(checkbox);
				index++;
				doUnormalCheckbox(taskMode,checkbox);
			}


			AutoNewLineLayout autoNewLineLayoutOther = (AutoNewLineLayout) findViewById(R.id.mission_type_list_other);
			autoNewLineLayoutOther.setOnelineNumber(2);
			autoNewLineLayoutOther.setRowDistance(0);
			for (int i=0;i<otherList.length();i++) {
				JSONObject taskMode = otherList.getJSONObject(i);
				taskMode.put("cat","other");
				taskModelList.put(taskMode);
				taskMode.put("checked", false);
				MyCheckBox checkbox = initOneCheckbox(taskMode,index);
				checkBoxList.add(checkbox);
				autoNewLineLayoutOther.addNewView(checkbox);
				index++;
				doUnormalCheckbox(taskMode,checkbox);
			}
			photoIds = new String[taskModelList.length()];

			calLowSpend();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		findViewById(R.id.start_time).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PubMissonBeanActivity.this, SelectTimeActivity.class);
				intent.putExtra("code", 100);
				intent.putExtra("default_time",startTime);
				startActivityForResult(intent, 100);
			}
		});
		findViewById(R.id.end_time).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PubMissonBeanActivity.this, SelectTimeActivity.class);
				intent.putExtra("code", 101);
				intent.putExtra("default_time",endTime);
				startActivityForResult(intent, 101);
			}
		});
		findViewById(R.id.pub_mission).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				ArrayList<String> tids = new ArrayList<String>();

				for (int i = 0; i < taskModelList.length(); i++) {
					try {
						Boolean checked = taskModelList.getJSONObject(i).getBoolean("checked");
						if (checked) {
							if (taskModelList.getJSONObject(i).getString("cat").equals(currentSection)) {
								tids.add(taskModelList.getJSONObject(i).getString("id"));
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				String tidsString = StringUtils.join(tids.toArray(), ",");
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("action", "userRelease");
				params.put("user_id", Common.getUserId(PubMissonBeanActivity.this));
				params.put("link_url", ((EditText) findViewById(R.id.mission_href)).getText().toString());
				params.put("title", ((EditText) findViewById(R.id.mission_title)).getText().toString());
				params.put("task_require", ((EditText) findViewById(R.id.mission_require)).getText().toString());



				params.put("task_type", "1");
				params.put("start_time", Common.getDateStrFromTime(startTime, " yyyy-MM-dd HH:mm:ss"));
				params.put("end_time", Common.getDateStrFromTime(endTime, " yyyy-MM-dd HH:mm:ss"));
				params.put("task_pattern", tidsString);

				ArrayList<String> uploadIds = new ArrayList<String>();
				for (int i = 0; i < photoIds.length; i++) {
					if (photoIds[i] != null) {
						try {
							if (taskModelList.getJSONObject(i).getString("cat").equals(currentSection)) {
								uploadIds.add(photoIds[i]);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				//检测输入的字数是否低于5个字符
				int lengthTask=mEditTextTask.getText().length();

				if(lengthTask>0&&lengthTask<=5){
                    Toast.makeText(PubMissonBeanActivity.this, "任务要求字数不低于5个或不写", Toast.LENGTH_SHORT).show();
                    return;
                }



				if (shareImageId != null) {
					params.put("share_img", shareImageId);
				}

				params.put("photo_id", StringUtils.join(uploadIds.toArray(), ","));
				params.put("number", ((EditText) findViewById(R.id.mission_number)).getText().toString());
				params.put("integral", ((EditText) findViewById(R.id.number_of_beans)).getText().toString());


				if (params.get("link_url").equals("")) {
					Toast.makeText(PubMissonBeanActivity.this, "链接不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				if (params.get("title").equals("")) {
					Toast.makeText(PubMissonBeanActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
//				if(params.get("task_require").equals("")) {
//					Toast.makeText(PubMissonBeanActivity.this,"任务要求不能为空",Toast.LENGTH_SHORT).show();
//					return;
//				}
				if (params.get("number").equals("")) {
					Toast.makeText(PubMissonBeanActivity.this, "任务数量不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				if (params.get("integral").equals("")) {
					Toast.makeText(PubMissonBeanActivity.this, "奖励红豆数量不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				if (Integer.parseInt(params.get("integral")) < lowSpend) {
					Toast.makeText(PubMissonBeanActivity.this, "奖励红豆不能小于最低消耗红豆数量", Toast.LENGTH_SHORT).show();
					return;
				}


				final Common.LoadingHandler handler = Common.loading(PubMissonBeanActivity.this, "发布任务中...");

				Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Task/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
					@Override
					public void run(String result) {

					}

					@Override
					public void run(JSONObject result) {
						try {
							handler.close();
							String resultCode = result.getString("status");
							Log.i("result", result.toString());
							if (resultCode.equals(Config.NeedMoreBeans)) {
								Intent intent = new Intent(PubMissonBeanActivity.this, NeedMoreMoneyActivity.class);
								intent.putExtra("type", "1");
								startActivity(intent);
							} else {
								if (resultCode.equals(Config.HttpSuccessCode)) {
									Toast.makeText(PubMissonBeanActivity.this, "任务时间结束后两天内未追加任务系统自动返还现金或红豆", Toast.LENGTH_LONG).show();
									Intent intentNoti = new Intent();
									intentNoti.setAction("update.task.list");
									sendBroadcast(intentNoti);
									finish();
								} else {
									// String message = result.getString("message");  // 没选中类型 message 为空
									Toast.makeText(PubMissonBeanActivity.this,  "请选择发布任务类型", Toast.LENGTH_SHORT).show();
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
		});

		//开始修改版
		findViewById(R.id.mission_title).setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					getUrlTitle();
				}
			}
		});


		findViewById(R.id.wx_section).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				findViewById(R.id.mission_type_list_wx).setVisibility(View.VISIBLE);
				findViewById(R.id.mission_type_list_weibo).setVisibility(View.GONE);
				findViewById(R.id.mission_type_list_other).setVisibility(View.GONE);

				findViewById(R.id.weibo_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
				findViewById(R.id.other_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
				findViewById(R.id.wx_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
				v.setBackgroundResource(R.drawable.section_bg);
				currentSection = "wx";

				freshSampleList();


			}
		});
		findViewById(R.id.weibo_section).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				findViewById(R.id.mission_type_list_wx).setVisibility(View.GONE);
				findViewById(R.id.mission_type_list_weibo).setVisibility(View.VISIBLE);
				findViewById(R.id.mission_type_list_other).setVisibility(View.GONE);

				findViewById(R.id.weibo_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
				findViewById(R.id.other_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
				findViewById(R.id.wx_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
				v.setBackgroundResource(R.drawable.section_bg);
				currentSection = "weibo";

				freshSampleList();
			}
		});
		findViewById(R.id.other_section).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				findViewById(R.id.mission_type_list_wx).setVisibility(View.GONE);
				findViewById(R.id.mission_type_list_weibo).setVisibility(View.GONE);
				findViewById(R.id.mission_type_list_other).setVisibility(View.VISIBLE);

				findViewById(R.id.weibo_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
				findViewById(R.id.other_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
				findViewById(R.id.wx_section).setBackgroundColor(Color.parseColor("#f1f1f1"));
				v.setBackgroundResource(R.drawable.section_bg);
				currentSection = "other";

				freshSampleList();
			}
		});


		((EditText)findViewById(R.id.mission_number)).addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					int missionNumber = Integer.parseInt(findTextViewById(R.id.mission_number).getText().toString());
					int number_of_beans = Integer.parseInt(findTextViewById(R.id.number_of_beans).getText().toString());
					findTextViewById(R.id.total_spend).setText(missionNumber * number_of_beans + "");
				} catch (Exception e) {

				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		((EditText)findViewById(R.id.number_of_beans)).addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					int missionNumber = Integer.parseInt(findTextViewById(R.id.mission_number).getText().toString());
					int number_of_beans = Integer.parseInt(findTextViewById(R.id.number_of_beans).getText().toString());
					findTextViewById(R.id.total_spend).setText(missionNumber * number_of_beans + "");
				} catch (Exception e) {

				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		findViewById(R.id.upload_mission_avatar).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
                                 /* 开启Pictures画面Type设定为image */
				intent.setType("image/*");
                                /* 使用Intent.ACTION_GET_CONTENT这个Action */
				intent.setAction(Intent.ACTION_GET_CONTENT);
                                /* 取得相片后返回本画面 */
				startActivityForResult(intent, 103);
			}
		});


		Long time = new Date().getTime();
		startTime = time;
		SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分",
			Locale.CHINA);
		String dateString = sdr.format(new Date(startTime));
		((TextView)findViewById(R.id.start_time)).setText(dateString);
		endTime = time+10*86400*1000;
		String dateString2 = sdr.format(new Date(endTime));
		((TextView)findViewById(R.id.end_time)).setText(dateString2);
	}

	public void getUrlTitle() {
		final Common.LoadingHandler handler  = Common.loading(this,"获取标题...");
		final Handler titleHandler = new Handler() {
			public void handleMessage(Message msg) {
				if(msg.what==2) {
					findTextViewById(R.id.mission_href).setText(msg.obj.toString());
				} else {
					String result = msg.obj.toString();
					if (!result.isEmpty()){
						findTextViewById(R.id.mission_title).setText(result);
					}
					handler.close();
				}
			}
		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = findTextViewById(R.id.mission_href).getText().toString();
//				if(!url.contains("http://")) {
				if(!url.contains("http")) {
					url = "http://"+url;
					Message msg = titleHandler.obtainMessage();
					msg.obj = url;
					msg.what = 2;
					msg.sendToTarget();

				}

				Document doc = null;
				try {

					doc = Jsoup.connect(url).get();
					String title = doc.title();

					Message msg = titleHandler.obtainMessage();
					msg.obj = title;
					msg.sendToTarget();
				} catch (IOException e) {
					Message msg = titleHandler.obtainMessage();
					msg.obj = "";
					msg.sendToTarget();
					e.printStackTrace();
				} catch (Exception e) {
					Message msg = titleHandler.obtainMessage();
					msg.obj = "";
					msg.sendToTarget();
					e.printStackTrace();
				}
			}
		}).start();
	}
	public LinearLayout createSample(final int taskIndex){
		LinearLayout itemView = null;
		try {
			JSONObject taskMode =  taskModelList.getJSONObject(taskIndex);
			LayoutInflater mInflater = LayoutInflater.from(this);
			itemView = (LinearLayout) mInflater.inflate(R.layout.sample_item, null);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.topMargin = (int) getResources().getDimension(R.dimen.px55);
			params.rightMargin = (int) getResources().getDimension(R.dimen.px55);
			itemView.setLayoutParams(params);
			((TextView) itemView.findViewById(R.id.sample_title)).setText("默认" + taskMode.getString("title") + "示例图");
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(taskMode.getString("img"), (ImageView) itemView.findViewById(R.id.sample), Config.options);

			ImageListActivity.setImageViewCanShowBig((ImageView) itemView.findViewById(R.id.sample),taskMode.getString("img"));


			itemView.findViewById(R.id.add_sample_image).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
                                        /* 开启Pictures画面Type设定为image */
					intent.setType("image/*");
                                        /* 使用Intent.ACTION_GET_CONTENT这个Action */
					intent.setAction(Intent.ACTION_GET_CONTENT);
                                         /* 取得相片后返回本画面 */
					startActivityForResult(intent, taskIndex);
				}
			});

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return itemView;
	}



	@Override
	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {

		if(resultCode==100) {
			Long time = data.getLongExtra("time",0);
			startTime = time;
			SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分",
				Locale.CHINA);
			String dateString = sdr.format(new Date(startTime));

			((TextView)findViewById(R.id.start_time)).setText(dateString);

		} else if(resultCode==101) {
			Long time = data.getLongExtra("time",0);
			endTime = time;
			SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分",
				Locale.CHINA);
			String dateString = sdr.format(new Date(endTime));

			((TextView)findViewById(R.id.end_time)).setText(dateString);
		} else if(requestCode==103){
			if(data!=null) {
				FileUpload fileUpload = new FileUpload(this);
				fileUpload.setMaxWidth(100);
				fileUpload.setImageShowHolder(findImageViewById(R.id.upload_mission_avatar));
				HashMap<String, String> params = new HashMap<>();
				params.put("type", "5");

				fileUpload.uploadByResult(data, params);

				fileUpload.setOnUploadStatusChangedListener(new FileUpload.OnUploadStatusChangedListener() {
					@Override
					public void success(JSONObject result) {
						try {
							shareImageId = result.getJSONObject("result").getString("img_short_url");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

			}
		}else {
			if(data!=null) {
				FileUpload fileUpload = new FileUpload(this);
				fileUpload.setImageShowHolder((ImageView) sampleViewList.get(requestCode).findViewById(R.id.add_sample_image));
				HashMap<String, String> params = new HashMap<>();
				params.put("task_mode", Config.TaskBeans);
				params.put("type", "3");
				try {
					params.put("task_mode", taskModelList.getJSONObject(requestCode).getString("id"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				fileUpload.uploadByResult(data, params);

				fileUpload.setOnUploadStatusChangedListener(new FileUpload.OnUploadStatusChangedListener() {
					@Override
					public void success(JSONObject result) {
						try {
							photoIds[requestCode] = result.getJSONObject("result").getString("id");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
