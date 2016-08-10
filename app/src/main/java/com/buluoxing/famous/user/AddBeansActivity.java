package com.buluoxing.famous.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.PayResult;
import com.buluoxing.famous.R;
import com.buluoxing.famous.wxapi.WXPayEntryActivity;
import com.detail.UserInfo;
import com.diy.AutoNewLineLayout;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.util.Common;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.util.WxPayCallback;

public class AddBeansActivity extends MyActivity implements WxPayCallback{


	private JSONArray typeList;
	private ArrayList<TextView> typeViewList = new ArrayList<>();
	private int currentIndex = -1;
	private String partner;
	private String seller_id;
	private String out_trade_no;
	private int payType  = 2;
	private IWXAPI api;

	@Override
	protected int getLayout() {
		return R.layout.activity_add_beans;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		findViewById(R.id.select_ali).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				payType = 2;
				((ImageView) findViewById(R.id.select_status_ali)).setBackgroundResource(R.drawable.pay_selected);
				((ImageView) findViewById(R.id.select_status_wx)).setBackgroundResource(R.drawable.pay_diselect);
			}
		});

		findViewById(R.id.select_wx).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				payType = 1;
				((ImageView) findViewById(R.id.select_status_wx)).setBackgroundResource(R.drawable.pay_selected);
				((ImageView) findViewById(R.id.select_status_ali)).setBackgroundResource(R.drawable.pay_diselect);
			}
		});


		Common.getUserInfo(this, new Common.OnGetUserInfoListener() {
			@Override
			public void geted(UserInfo userInfo) {

				Log.i("userinfo", userInfo.toString());

				((TextView) findViewById(R.id.remain_number)).setText(userInfo.beans);
			}
		});
		api = WXAPIFactory.createWXAPI(this, "wx36834b9528fac4d4");
		findViewById(R.id.pay).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentIndex < 0) {
					Toast.makeText(AddBeansActivity.this, "请选择充值额度！", Toast.LENGTH_SHORT).show();
					return;
				}


				HashMap<String, String> params = new HashMap<String, String>();
				params.put("action", "getPaySign");
				params.put("pay_way", payType + "");
				params.put("pay_type", "1");
				params.put("user_id", Common.getUserId(AddBeansActivity.this));
				try {
					params.put("number", typeList.getJSONObject(currentIndex).getString("money"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				final Common.LoadingHandler handler = Common.loading(AddBeansActivity.this);

				Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Trade/getPaySign"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
					@Override
					public void run(String result) {

					}

					@Override
					public void run(final JSONObject result) {
						handler.close();

						try {

							if (payType == 2) {
								final String payInfo = result.getString("result");

								Log.i("payInfo", payInfo);

								Runnable payRunnable = new Runnable() {

									@Override
									public void run() {
										// 构造PayTask 对象
										PayTask alipay = new PayTask(AddBeansActivity.this);
										// 调用支付接口，获取支付结果
										String result = alipay.pay(payInfo, true);

										Message msg = new Message();
										msg.what = 1;
										msg.obj = result;
										mHandler.sendMessage(msg);
									}
								};

								// 必须异步调用
								Thread payThread = new Thread(payRunnable);
								payThread.start();
							} else if (payType == 1) {

								WXPayEntryActivity.wxPayCallback = AddBeansActivity.this;

								JSONObject payInfo = result.getJSONObject("result");


								PayReq req = new PayReq();
								req.appId = "wx36834b9528fac4d4";// payInfo.getString("wxf8b4f85f3a794e77");
								req.partnerId = payInfo.getString("partnerid");
								req.prepayId = payInfo.getString("prepayid");
								req.nonceStr = payInfo.getString("noncestr");
								req.timeStamp = payInfo.getString("timestamp");
								req.packageValue = "Sign=WXPay";//payInfo.getString("com.buluoxing.famous");
								req.sign = payInfo.getString("sign");
								api.sendReq(req);
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

		loadAvalibleBeansType();
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1: {
					PayResult payResult = new PayResult((String) msg.obj);
					/**
					 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
					 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
					 * docType=1) 建议商户依赖异步通知
					 */
					String resultInfo = payResult.getResult();// 同步返回需要验证的信息

					String resultStatus = payResult.getResultStatus();
					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
					if (TextUtils.equals(resultStatus, "9000")) {
						Toast.makeText(AddBeansActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						// 判断resultStatus 为非"9000"则代表可能支付失败
						// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) {
							Toast.makeText(AddBeansActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

						} else {
							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
							Toast.makeText(AddBeansActivity.this, "支付失败", Toast.LENGTH_SHORT).show();

						}
					}
					break;
				}
				default:
					break;
			}
		};
	};
	private void  loadAvalibleBeansType(){
		HashMap<String,String> params = new HashMap<>();
		params.put("action", "redConfigList");

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/System/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {
				try {
					typeList = result.getJSONArray("result");


					initTypeList();

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void run(JSONArray result) {

			}
		});
	}

	public void initTypeList(){
		AutoNewLineLayout autoNewLineLayout = (AutoNewLineLayout) findViewById(R.id.auto_new_line);
		autoNewLineLayout.setOnelineNumber(3);
		autoNewLineLayout.setRowDistance((int) getResources().getDimension(R.dimen.px32));
		autoNewLineLayout.clearAllViews();

		for (int i=0;i<typeList.length();i++) {
			try {
				JSONObject typeInfo = typeList.getJSONObject(i);

				TextView typeView = new TextView(this);
				typeView.setText(typeInfo.getString("number"));
				typeView.setTag(i);
				typeView.setGravity(Gravity.CENTER);
				typeView.setTextColor(Color.parseColor("#4b4b4b"));
				typeView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.px46));
				typeView.setBackgroundResource(R.drawable.add_beans_type_bg);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.px289),(int)getResources().getDimension(R.dimen.px101));
				params.rightMargin = (int) getResources().getDimension(R.dimen.px39);
				typeView.setLayoutParams(params);
				autoNewLineLayout.addNewView(typeView);
				typeView.setTag(i);
				typeView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						selectType((Integer) v.getTag());
					}
				});
				typeViewList.add(typeView);
			} catch (JSONException e) {
				e.printStackTrace();
			}


		}

	}
	public void selectType(int index) {

		if(currentIndex>=0) {
			typeViewList.get(currentIndex).setBackgroundResource(R.drawable.add_beans_type_bg);
			typeViewList.get(currentIndex).setTextColor(Color.parseColor("#4b4b4b"));
		}
		typeViewList.get(index).setBackgroundResource(R.drawable.add_beans_type_bg_select);
		typeViewList.get(index).setTextColor(Color.parseColor("#ffffff"));

		currentIndex = index;

		try {
			((Button)findViewById(R.id.pay)).setText(String.format("支付   %s元",typeList.getJSONObject(index).getString("money")));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}


	@Override
	public void paySucces(Boolean success) {
		if(success) {
			goActivity(MyBeansActivity.class);
		}
	}
}
