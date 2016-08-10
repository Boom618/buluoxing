package com.buluoxing.famous;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.buluoxing.famous.user.AddMoneyActivity;
import com.buluoxing.famous.user.GetMoneyActivity;
import com.buluoxing.famous.user.GoOnlineService;
import com.buluoxing.famous.user.InviteFriendsActivity;
import com.buluoxing.famous.user.PasswordActivity;
import com.buluoxing.famous.user.UserSetUpActivity;
import com.buluoxing.famous.user.UserSettingActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.util.Common;
import com.util.Http;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LeftActivity extends MyActivity {

	@Override
	protected int getLayout() {
		return R.layout.activity_left;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);



		findViewById(R.id.sign_in).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Common.userSign(LeftActivity.this);
			}
		});
		findViewById(R.id.add_money).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goActivity(AddMoneyActivity.class);
			}
		});
		findViewById(R.id.get_money).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goActivity(GetMoneyActivity.class);
			}
		});
		findViewById(R.id.change_password).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LeftActivity.this, PasswordActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.invit_friends).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LeftActivity.this, InviteFriendsActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.user_set_up).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent userSetUpIntent = new Intent(LeftActivity.this, UserSetUpActivity.class);
//				Intent userSetUpIntent = new Intent(LeftActivity.this, UserSettingActivity.class);
				startActivity(userSetUpIntent);
				finish();
			}
		});
		findViewById(R.id.Left_OnclientService).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(LeftActivity.this, GoOnlineService.class);
				startActivity(intent);
			}
		});

//		findViewById(R.id.right_blank).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});

		loadData();
	}


	private void loadData() {
		String userId = Common.getUserId(LeftActivity.this);
		HashMap<String, String> params = new HashMap<>();
		params.put("action", "userRefreshInfo");
		params.put("user_id", userId);
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				Log.i("user", result);
				ImageLoader imageLoader = ImageLoader.getInstance();
				final ImageView userIcon = (ImageView) findViewById(R.id.profile_image);

				try {
					JSONObject resultObject = new JSONObject(result);
					JSONObject userInfo = resultObject.getJSONObject("result");
					String photo = userInfo.getString("photo");

					imageLoader.loadImage(photo, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							userIcon.setImageBitmap(loadedImage);
						}
					});

					((TextView) findViewById(R.id.username)).setText(userInfo.getString("nickname"));
					((TextView) findViewById(R.id.city_area)).setText(userInfo.getString("city") + " " + userInfo.getString("domian_name"));
					((TextView) findViewById(R.id.kol_level)).setText(String.format("Lv.%s", userInfo.getString("grade_id")));


				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}


	@Override
	protected void onPause() {
		super.onPause();
//		MyApplication application = (MyApplication) getApplication();
//		((MainActivity)application.mainActivity).hideCover();
	}

	public void onDestroy() {
		super.onDestroy();


	}

}
