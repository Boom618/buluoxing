package com.buluoxing.famous.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.PayResult;
import com.buluoxing.famous.R;
import com.buluoxing.famous.wxapi.WXPayEntryActivity;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.util.Common;
import com.util.Http;
import com.util.WxPayCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
// 个人中心 - 现金充值  现金充值
public class AddMoneyActivity extends MyActivity implements WxPayCallback {

	private int payType = 2;
	private IWXAPI api;
	@Override
	protected int getLayout() {
		return R.layout.activity_add_money;
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

		api = WXAPIFactory.createWXAPI(this, "wx36834b9528fac4d4");

		findViewById(R.id.add_money).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				EditText editText = (EditText) findViewById(R.id.money);
				String siller = editText.getText().toString();

				if( siller.isEmpty() ) {
					Toast.makeText(AddMoneyActivity.this,"请输入充值金额",Toast.LENGTH_SHORT).show();
				}else{
					float money = Float.parseFloat(siller);
					if ( money >= 0.01){

						HashMap<String,String> params = new HashMap<String, String>();
						params.put("action","getPaySign");
						params.put("pay_way",payType+"");
						params.put("pay_type","2");
						params.put("user_id", Common.getUserId(AddMoneyActivity.this));

						params.put("number",money+"");

						final Common.LoadingHandler handler = Common.loading(AddMoneyActivity.this);

						Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Trade/getPaySign"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
							@Override
							public void run(String result) {

							}

							@Override
							public void run(final JSONObject result) {
								Log.i("result",result.toString());
								handler.close();

								try {
									if(payType==2) {
										final String payInfo = result.getString("result");

										Log.i("payInfo", payInfo);

										Runnable payRunnable = new Runnable() {

											@Override
											public void run() {
												// 构造PayTask 对象
												PayTask alipay = new PayTask(AddMoneyActivity.this);
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
									} else if(payType==1) {
										WXPayEntryActivity.wxPayCallback = AddMoneyActivity.this;
										JSONObject payInfo = result.getJSONObject("result");
										PayReq req = new PayReq();
										req.appId			= "wx36834b9528fac4d4";// payInfo.getString("wxf8b4f85f3a794e77");
										req.partnerId		= payInfo.getString("partnerid");
										req.prepayId		= payInfo.getString("prepayid");
										req.nonceStr		= payInfo.getString("noncestr");
										req.timeStamp		= payInfo.getString("timestamp");
										req.packageValue	= "Sign=WXPay";//payInfo.getString("com.buluoxing.famous");
										req.sign			= payInfo.getString("sign");
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
					}else{
						Toast.makeText(AddMoneyActivity.this,"金额不能小于 0.01 ",Toast.LENGTH_SHORT).show();
					}

				}



			}
		});

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
						Toast.makeText(AddMoneyActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						// 判断resultStatus 为非"9000"则代表可能支付失败
						// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) {
							Toast.makeText(AddMoneyActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

						} else {
							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
							Toast.makeText(AddMoneyActivity.this, "支付失败", Toast.LENGTH_SHORT).show();

						}
					}
					break;
				}
				default:
					break;
			}
		};
	};

	@Override
	public void paySucces(Boolean success) {
		if(success) {
			goActivity(MyMoneyActivity.class);
		}
	}
}
