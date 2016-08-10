package com.buluoxing.famous;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.buluoxing.famous.fileload.FileApi;
import com.buluoxing.famous.fileload.FileCallback;
import com.diy.HDialogBuilder;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.util.Common;
import com.util.Config;
import com.util.DataManager;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class SplashActivity extends Activity {

	private TextView txtProgress;
	private HDialogBuilder hDialogBuilder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		getSysConfig();

		//加载用户信息

	}



	/**
	 *  版本更新
	 * @param context
     */
	private void checkUpDate(Context context ,String id){

		int code = Integer.parseInt(id);
		if (isUpDate(context,code)){
			//更新
			String baseUrl = Config.Version_URL;
			String fileName = "buluoxing.apk";
			String fileStoreDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File
					.separator + "buluoxing";
			String fileStoreName = fileName;

			showLoadingDialog();

			FileApi.getInstance(baseUrl)
					.loadFileByName(fileName, new FileCallback(fileStoreDir, fileStoreName) {
						@Override
						public void onSuccess(File file) {
							super.onSuccess(file);
							hDialogBuilder.dismiss();
							openFile(file);
						}

						@Override
						public void progress(long progress, long total) {
							updateProgress(progress, total);
						}

						@Override
						public void onFailure(Call<ResponseBody> call, Throwable t) {
							hDialogBuilder.dismiss();
							call.cancel();
						}
					});

		}else {

			Common.getProvinceData(this, new Http.HttpSuccessInterface() {
				@Override
				public void run(String result) {
					Log.i("cache", "cache,province data");
				}
			});
			new Handler().postDelayed(new Runnable() {
				public void run() {

					MyApplication application = (MyApplication) getApplication();

					Intent mainIntent = null;
					if (application.checkLogin()) {
						mainIntent = new Intent(SplashActivity.this, MainActivity.class);
					} else {
						mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
					}
					startActivity(mainIntent);
					finish();
				}
			}, 2000);

			PushAgent mPushAgent = PushAgent.getInstance(this);
			mPushAgent.enable();


		}

	}

	private void getSysConfig(){
		HashMap<String,String> params = new HashMap<>();
		params.put("action","configList");
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/System/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {
				try {
					JSONObject sysConfig = result.getJSONObject("result");

					Log.e("sys", "getSysConfig = " + sysConfig.toString());

					String ID = sysConfig.getJSONObject("prompt_conf").getString("version_id");
					Log.e("sys ID","ID = " + ID);

					checkUpDate(SplashActivity.this,ID);

					Config.Version_URL = sysConfig.getJSONObject("prompt_conf").getString("version_url");

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void run(JSONArray result) {

			}
		});
	}

	// 安装 APK
	private void openFile(File file) {
		// TODO Auto-generated method stub
		Log.e("OpenFile", file.getName());
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	/**
	 * 更新下载进度
	 *
	 * @param progress
	 * @param total
	 */
	private void updateProgress(long progress, long total) {
		txtProgress.setText(String.format("正在下载：(%s/%s)",
				DataManager.getFormatSize(progress),
				DataManager.getFormatSize(total)));
	}

	/**
	 * 显示下载对话框
	 */
	private void showLoadingDialog() {
		txtProgress = (TextView) View.inflate(this, R.layout
				.layout_hd_dialog_custom_tv, null);
		//txtProgress.setText("确认下载？");
		hDialogBuilder = new HDialogBuilder(this);
		hDialogBuilder.setCustomView(txtProgress)
				.title("下载")
				.nBtnText("取消")
				.nBtnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						hDialogBuilder.dismiss();
						FileApi.cancelLoading();
					}
				})
				.outsideCancelable(false)
				.show();
	}

	private boolean isUpDate(Context context ,int code){
		String versionName = "";
		int versioncode = 0;
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			versioncode = pi.versionCode;
			Log.e("VersionInfo", "versionName = " + versionName);
			Log.e("VersionInfo", "versioncode = " + versioncode);
			if ( versioncode < code ){
				// 更新
				return true;
			}
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
			return false;
		}

		// 其他情况 一律 不更新
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 检查更新


		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		MobclickAgent.onPause(this);
	}
}
