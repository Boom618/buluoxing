package com.buluoxing.famous;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class TermsInfoActivity extends MyActivity {

	@Override
	protected int getLayout() {
		return R.layout.activity_terms_info;
	}
	private class MyWebChromeClient extends WebChromeClient
	{
		@Override
		public void onReceivedTitle(WebView view, String title) {
			// TODO Auto-generated method stub
			super.onReceivedTitle(view, "服务条款");

			findTextViewById(R.id.title).setText( "服务条款");

		}

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final WebView webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new MyWebChromeClient());
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.requestFocus();


		HashMap<String,String> params = new HashMap<>();
		params.put("action","termsInfo");

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/System/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {
				try {
					webView.loadData(result.getString("result"), "text/html; charset=UTF-8", null);
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
