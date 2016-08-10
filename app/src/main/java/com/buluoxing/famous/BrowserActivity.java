package com.buluoxing.famous;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BrowserActivity extends MyActivity {


	private class MyWebChromeClient extends WebChromeClient
	{
		@Override
		public void onReceivedTitle(WebView view, String title) {
			// TODO Auto-generated method stub
			super.onReceivedTitle(view, title);

			findTextViewById(R.id.title).setText(title);

		}

	}

	@Override
	protected int getLayout() {
		return R.layout.activity_browser;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String url = getIntent().getStringExtra("url");
		Log.i("url", url);
		WebView webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new MyWebChromeClient());
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url){
				return false;
			}
		});
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.requestFocus();
		webView.loadUrl(url);
	}
}
