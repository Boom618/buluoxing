package com.buluoxing.famous.kol;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buluoxing.famous.BrowserActivity;
import com.buluoxing.famous.ImageListActivity;
import com.buluoxing.famous.R;
import com.buluoxing.famous.user.FollowActivity;
import com.buluoxing.famous.user.InviteKolActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class KolActivity extends Activity {
	public Boolean isFollow = false;
	private SwipeRefreshLayout swipeRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kol);

		View backView = findViewById(R.id.back);
		if(backView!=null ) {
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		final String kolId = getIntent().getStringExtra("kol_id");


		findViewById(R.id.follow_info).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(KolActivity.this, FollowActivity.class);
				intent.putExtra("type","follow");
				intent.putExtra("fuser_id",kolId);
				startActivity(intent);
			}
		});
		findViewById(R.id.fans_info).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(KolActivity.this, FollowActivity.class);
				intent.putExtra("type","fans");
				intent.putExtra("fuser_id",kolId);
				startActivity(intent);
			}
		});

		findViewById(R.id.invite_cooper).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(KolActivity.this, InviteKolActivity.class);
				intent.putExtra("kol_id", kolId);
				startActivity(intent);
			}
		});

		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		//设置刷新时动画的颜色，可以设置4个
		swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				swipeRefreshLayout.setRefreshing(false);
				loadData();
			}
		});

		loadData();


	}
	public void loadData() {
		final String kolId = getIntent().getStringExtra("kol_id");
		HashMap<String,String> params = new HashMap<>();

		params.put("action","findKolDetail");
		params.put("user_id", Common.getUserId(this));
		params.put("kol_id", kolId);


		final Common.LoadingHandler handler=   Common.loading(this);
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {

				Log.i("fuck", result);
				handler.close();
				try {
					JSONObject resultObject = new JSONObject(result);
					final JSONObject kolDetail = resultObject.getJSONObject("result");


					findViewById(R.id.his_weibo).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(KolActivity.this, BrowserActivity.class);
							try {

								String url = kolDetail.getString("weibo_url");

								if(!url.contains("http://")) {
									url = "http://"+url;
								}

								intent.putExtra("url", url);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							startActivity(intent);
						}
					});


					final ImageView userIcon = (ImageView) findViewById(R.id.kol_icon);


					((TextView) findViewById(R.id.kol_nickname)).setText(kolDetail.getString("nickname"));
					((TextView) findViewById(R.id.city)).setText(kolDetail.getString("city"));
					((TextView) findViewById(R.id.area)).setText(kolDetail.getString("domain_str"));
					((TextView) findViewById(R.id.follow)).setText(kolDetail.getString("count_follow"));
					((TextView) findViewById(R.id.followed)).setText(kolDetail.getString("count_followed"));
					((TextView) findViewById(R.id.kol_desc)).setText(kolDetail.getString("desc"));
					((TextView) findViewById(R.id.weibo)).setText(kolDetail.getString("weibo_name"));
					((TextView) findViewById(R.id.wx)).setText(kolDetail.getString("wechat_id"));
					((TextView) findViewById(R.id.kol_level)).setText(String.format("Lv.%s", kolDetail.getString("grade_id")));


					ImageLoader imageLoader = ImageLoader.getInstance();
					imageLoader.loadImage(kolDetail.getString("photo"), new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							userIcon.setImageBitmap(loadedImage);
						}
					});

					userIcon.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String imageUrlList[] = new String[0];
							try {
								imageUrlList = new String[]{kolDetail.getString("photo")};
							} catch (JSONException e) {
								e.printStackTrace();
							}
							Intent intent = new Intent(KolActivity.this, ImageListActivity.class);
							intent.putExtra("image_list", imageUrlList);
							intent.putExtra("current_index", 0);
							startActivity(intent);
						}
					});



					final String qcodeUrl = "http://open.weixin.qq.com/qr/code/?username=" + kolDetail.getString("wechat_id");
					ImageLoader imageLoader2 = ImageLoader.getInstance();
					ImageView qcodeImageView = (ImageView) findViewById(R.id.wx_qcode);
					imageLoader2.displayImage(qcodeUrl, qcodeImageView);


					qcodeImageView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(KolActivity.this, ImageListActivity.class);
							String[] urls = {qcodeUrl};
							intent.putExtra("image_list", urls);
							startActivity(intent);
						}
					});
					findViewById(R.id.copy_wx).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							ClipboardManager myClipboard;
							myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

							ClipData myClip;
							try {
								myClip = ClipData.newPlainText("text",  kolDetail.getString("wechat_id"));
								myClipboard.setPrimaryClip(myClip);

								Toast.makeText(KolActivity.this,"公众号已复制成功，请打开微信直接粘贴查找公众号",Toast.LENGTH_SHORT).show();
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					});
					if(kolDetail.getString("wechat_id").equals("")) {
						findViewById(R.id.copy_wx).setVisibility(View.GONE);
					} else {
						findViewById(R.id.copy_wx).setVisibility(View.VISIBLE);
					}


					JSONArray imageList = kolDetail.getJSONArray("photo_array");

					LinearLayout imageListContainer = (LinearLayout)findViewById(R.id.image_list);


					String hisTitle = getResources().getString(R.string.his_images);
					((TextView)findViewById(R.id.his_images_title)).setText(String.format(hisTitle,imageList.length()));


					final String[] imageUrlList = new String[imageList.length()];

					for(int i=0;i<imageList.length();i++) {
						imageUrlList[i] = imageList.getJSONObject(i).getString("big_url");
					}

					imageListContainer.removeAllViews();
					for(int i=0;i<imageList.length();i++) {
						ImageView imageView = new ImageView(KolActivity.this);
						int width = (int)getResources().getDimension(R.dimen.px216);
						LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(width,width);
						imageParams.rightMargin = (int)getResources().getDimension(R.dimen.px30);
						imageView.setLayoutParams(imageParams);
						imageListContainer.addView(imageView);
						imageView.setImageResource(R.drawable.loading_image);
						JSONObject imageInfo = imageList.getJSONObject(i);
						ImageLoader imageLoader1 = ImageLoader.getInstance();
						imageLoader1.displayImage(imageInfo.getString("small_url"),imageView);

						imageView.setTag(i);

						imageView.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {


								Intent intent = new Intent(KolActivity.this,ImageListActivity.class);
								intent.putExtra("image_list",imageUrlList);
								intent.putExtra("current_index",(int)v.getTag());
								startActivity(intent);
							}
						});
					}


					isFollow = kolDetail.getString("is_follow").equals("1");

					if(isFollow) {
						((Button)findViewById(R.id.follow_kol)).setText(getResources().getString(R.string.cancel_follow));
					} else {
						((Button)findViewById(R.id.follow_kol)).setText(getResources().getString(R.string.plus_follow));
					}
					if(kolDetail.getString("id").equals(Common.getUserId(KolActivity.this))) {
						findViewById(R.id.follow_kol).setVisibility(View.INVISIBLE);
					} else {
						findViewById(R.id.follow_kol).setVisibility(View.VISIBLE);
					}

					findViewById(R.id.follow_kol).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							HashMap<String,String>params = new HashMap<String, String>();
							params.put("action","handleFollowUser");
							params.put("user_id", Common.getUserId(KolActivity.this));
							try {
								params.put("follow_id", kolDetail.getString("id"));
							} catch (JSONException e) {
								e.printStackTrace();
							}


							if(isFollow) {
								isFollow = false;
								params.put("status","3");
								((Button)findViewById(R.id.follow_kol)).setText(getResources().getString(R.string.plus_follow));
							} else {
								isFollow = true;
								params.put("status","1");
								((Button)findViewById(R.id.follow_kol)).setText(getResources().getString(R.string.cancel_follow));
							}
							Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpSuccessInterface() {
								@Override
								public void run(String result) {
									Log.i("follow",result);
								}
							});
						}
					});

					if(kolDetail.getString("type").equals(Config.UserTypeKol) || kolDetail.getString("type").equals(Config.UserTypeSingleKol)) {

						if(kolDetail.getString("type").equals(Config.UserTypeSingleKol)) {
							findViewById(R.id.single_kol).setVisibility(View.VISIBLE);
							findViewById(R.id.kol).setVisibility(View.GONE);
						} else {
							findViewById(R.id.kol).setVisibility(View.VISIBLE);
							findViewById(R.id.single_kol).setVisibility(View.GONE);
						}
					} else {
						findViewById(R.id.single_kol).setVisibility(View.GONE);
						findViewById(R.id.kol).setVisibility(View.GONE);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		MobclickAgent.onPause(this);
	}
}
