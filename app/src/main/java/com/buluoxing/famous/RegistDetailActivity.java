package com.buluoxing.famous;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.util.Common;
import com.util.Config;
import com.util.Http;
import com.util.T;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class RegistDetailActivity extends MyActivity {

	public int y = 1994;
	public int m = 9;
	public int d = 1;
	public String dateString = "1994-10-01";
	public int sex = 1;
	private String facility_token="";
	private boolean check = true;

	private TextView 		mTextData;		// 生日文本
	private Button			mButtonRegist;	// 注册
	private EditText		mCode;			// 邀请码
	private ImageView		mCheckBox;		// 选中条款
	private ImageView		mCheckBoy;		// 选中男
	private ImageView		mCheckGril;		// 选中女
	private LinearLayout 	mLayout;

	@Override
	protected int getLayout() {
		return R.layout.activity_regist_detail;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_regist_detail);

		initView();

		mTextData.setText(dateString);


//		Calendar calendar = Calendar.getInstance();


//		mLayout.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				new DatePickerDialog(RegistDetailActivity.this,
//					new DatePickerDialog.OnDateSetListener() {
//						@Override
//						public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//							y = year;
//							m = monthOfYear;
//							d = dayOfMonth;
//
//							monthOfYear++;
//							String mm = monthOfYear >= 10 ? monthOfYear + "" : "0" + monthOfYear;
//							String dd = dayOfMonth >= 10 ? dayOfMonth + "" : "0" + dayOfMonth;
//							dateString = year + "-" + mm + "-" + dd;
//							mTextData.setText(dateString);
//						}
//					},
//					y, m, d).show();
//			}
//		});


		mButtonRegist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!check) {
					T.showOnce(getApplicationContext(), "你还没接受服务条款");
					return;
				}
				String birthday = mTextData.getText().toString();
				String subOld = birthday.substring(0, 4);
				String nowTime = get3Time(new Date());
				String subNow = nowTime.substring(0, 4);
				String minAge = "10";
				String minAgeStr = "生日将关系到可接任务的类型,请选择正确的生日";
				try {
					minAge = Config.sysConfig.getJSONObject("prompt_conf").getString("min_age");
					minAgeStr = Config.sysConfig.getJSONObject("prompt_conf").getString("min_age_alert");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				int age = Integer.parseInt(minAge);
				if ( Integer.parseInt(subNow) - Integer.parseInt(subOld ) < age ){
					T.showOnce(getApplicationContext(), minAgeStr);
					return;
				}

				Intent intent = getIntent();
				final String phone = intent.getStringExtra("phone");
				String code = intent.getStringExtra("code");
				String password1 = intent.getStringExtra("password1");
				//String password2 = intent.getStringExtra("password2");

				//String birthday = mTextData.getText().toString();

				String inviteCode = mCode.getText().toString();


				HashMap<String, String> params = new HashMap<String, String>();

				params.put("action", "register");
				params.put("phone", phone);
				params.put("phone_code", code);
				params.put("invitation_code", inviteCode);
				params.put("password", password1);
				params.put("birthday", birthday);
				params.put("sex", sex + "");
				params.put("facility_token", UmengRegistrar.getRegistrationId(RegistDetailActivity.this));
				params.put("facility_type", "1");
				params.put("token", Common.signAction("register"));


				String api = Http.buildBaseApiUrl("/Home/Members/index");

				Log.i("params", params.toString());

				Http.postApi(api, params, new Http.HttpSuccessInterface() {
					@Override
					public void run(String result) {
						try {
							JSONObject resultJson = new JSONObject(result);

							if (resultJson.getString("status").equals(Config.HttpSuccessCode)) {
								Toast.makeText(RegistDetailActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();

								MyApplication application = (MyApplication) getApplication();
//								if (application.registActivity != null) {
//									application.registActivity.finish();
//								}


								JSONObject userInfo = resultJson.getJSONObject("result");
								application.setUserId(userInfo.getString("id"));

								SharedPreferences sp = getSharedPreferences("user_info",
									MODE_PRIVATE);
								SharedPreferences.Editor edit = sp.edit();
								edit.putString("phone", phone);
								edit.apply();

								showDialog();



							} else {
								Toast.makeText(RegistDetailActivity.this, resultJson.getString("message"), Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				});

			}
		});


		findViewById(R.id.radio_boy).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sex = 1;

				mCheckBoy.setImageResource(R.drawable.radio_select);
				mCheckGril.setImageResource(R.drawable.radio_unselect);
			}
		});
		findViewById(R.id.radio_girl).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sex = 2;
				mCheckBoy.setImageResource(R.drawable.radio_unselect);
				mCheckGril.setImageResource(R.drawable.radio_select);
			}
		});

		findViewById(R.id.is_agree).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegistDetailActivity.this,TermsInfoActivity.class);
				startActivity(intent);
			}
		});

		mCheckBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				check = !check;
				if (check) {
					mCheckBox.setBackgroundResource(R.drawable.check);
				} else {
					mCheckBox.setBackgroundResource(R.drawable.uncheck);
				}
			}
		});
	}

	private void initView(){
		mTextData = ((TextView)findViewById(R.id.date_view));
		mCode = (EditText) findViewById(R.id.invit_code);
		mLayout = (LinearLayout) findViewById(R.id.select_birthday);
		mCheckBox = (ImageView)findViewById(R.id.checkbox);
		mCheckBoy = (ImageView) findViewById(R.id.image_boy);
		mCheckGril = (ImageView)findViewById(R.id.image_girl);
		mButtonRegist = (Button) findViewById(R.id.go_regist);
	}

	@Override
	protected void onStart() {
		super.onStart();

		mLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showControlTime(mTextData,false,100,"YMD", TimePickerView.Type.YEAR_MONTH_DAY);
			}
		});

	}

	private void showDialog(){

		new SweetAlertDialog(RegistDetailActivity.this, SweetAlertDialog.SUCCESS_TYPE)
				.setTitleText("")
				.setContentText("恭喜您注册成功,可前往个人中心修改昵称和头像")
				.setConfirmText("确认")
				.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						Intent intent = new Intent(RegistDetailActivity.this, MainActivity.class);
						startActivity(intent);
						sweetAlertDialog.dismiss();
						finish();
					}
				})
				.show();

	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//		MobclickAgent.onResume(this);
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//
//		MobclickAgent.onPause(this);
//	}
}
