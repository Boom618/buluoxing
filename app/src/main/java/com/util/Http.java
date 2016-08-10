package com.util;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.buluoxing.famous.MyApplication;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Http {

	public static Context context;

	public interface HttpSuccessInterface {

		public void run(String result);
	}
	public interface HttpPostSuccessWithJsonResultInterface {
		public void run(String result);
		public void run(JSONObject result);
		public void run(JSONArray result);
	}

	static public void enableCookie(Context ctx) {
		context = ctx;
	}

	static public HttpGet getApi(final String api, final HttpSuccessInterface r) {
		final Handler loadedHandler = new Handler() {
			public void handleMessage(Message msg) {

				String result = msg.obj.toString();
				r.run(msg.obj.toString());



			}
		};
		final HttpClient httpClient = new DefaultHttpClient();
		final HttpGet request = new HttpGet(api);
		if (context != null) {
			request.addHeader("Cookie", buildCommonCookie());
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					HttpResponse response = httpClient.execute(request);
					String result = EntityUtils.toString(response.getEntity());
					Message msg = loadedHandler.obtainMessage();
					msg.obj = result;
					msg.sendToTarget();
				} catch (ClientProtocolException e) {


					e.printStackTrace();
					return;
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}).start();
		return request;
	}


	static public void postApiWithToken(final String api,  HashMap<String,String> params, final HttpPostSuccessWithJsonResultInterface r){
		String action = params.get("action");
		String userId = params.get("user_id");
		String token = "";
		if(userId!=null) {

			token = Common.signAction(action,userId);

			Log.i("fuckfuck",token);
		}else {
			token = Common.signAction(action);
		}
		params.put("token",token);
		postApi(api, params, new HttpSuccessInterface() {
			@Override
			public void run(String result) {
				r.run(result);
				try {
					JSONArray jsonArray = new JSONArray(result);
					r.run(jsonArray);
				} catch (JSONException e) {
					try {
						JSONObject jsonObject = new JSONObject(result);
						r.run(jsonObject);
					} catch (JSONException e1) {
					}
				}
			}
		});
	}
	static public void postApiWithToken(final String api,  HashMap<String,String> params, final HttpSuccessInterface r){
		String action = params.get("action");
		String userId = params.get("user_id");
		String token = "";
		if(userId!=null) {

			 token = Common.signAction(action,userId);

			Log.i("fuckfuck",token);
		}else {
			token = Common.signAction(action);
		}
		params.put("token",token);
		postApi(api, params, r);
	}

	@Deprecated //use postApiWithToken
	static public HttpPost postApi(final String api,  HashMap<String,String> params, final HttpSuccessInterface r) {
		final Handler loadedHandler = new Handler() {
			public void handleMessage(Message msg) {
				String result = msg.obj.toString();
				Log.i("postResult",result);
				r.run(msg.obj.toString());


			}
		};


		Log.i("post",api);
		Log.i("params",params.toString());
		final List<NameValuePair> pairs = new ArrayList<NameValuePair>() ;

		for (HashMap.Entry<String, String> entry : params.entrySet()) {

			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			pairs.add(new BasicNameValuePair(key,value));
		}



		final HttpClient httpClient = new DefaultHttpClient();
		final HttpPost request = new HttpPost(api);
		if (context != null) {
			request.addHeader("Cookie", buildCommonCookie());
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					request.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
 					HttpResponse response = httpClient.execute(request);
					String result = EntityUtils.toString(response.getEntity());
					Message msg = loadedHandler.obtainMessage();
					msg.obj = result;
					msg.sendToTarget();
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					return;
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}).start();
		return request;
	}

	static public String buildCommonCookie() {
		String packageName = "";
		packageName = context.getPackageName();
		//version
		String cookie = "";
		try {
			cookie = String.format("version=%d; channel=%s; device=%s; package=%s",
					Common.getVersion(context), Common.getChannel(context),
					URLEncoder.encode(Common.getDeviceName(), "utf-8"), packageName);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.i("cookie", cookie);
		return cookie;

	}

	static public String buildBaseApiUrl(String section) {
		String urlString = Config.baseApi + section;
		return urlString;
	}

	static public String buildApiUrl(String section, HashMap<String, String> params) {
		String urlString = buildBaseApiUrl(section);
		for (HashMap.Entry<String, String> entry : params.entrySet()) {

			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			try {
				urlString = urlString + key + "=" + URLEncoder.encode(value, "utf-8") + "&";
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return urlString;
	}

	static public void uploadFileByResultData(Intent data){
	}
}