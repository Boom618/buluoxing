package com.buluoxing.famous.user;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adapter.ImageLoopAdapter;
import com.buluoxing.famous.ImageListActivity;
import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.Common;
import com.util.Config;
import com.util.FileUpload;
import com.util.Http;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
//个人中心 - 网红管理 - 相册管理
public class ManageGalleryActivity extends MyActivity {

	JSONArray photoArray = new JSONArray();
	public ImageView addImageView = null;
	public ArrayList<RelativeLayout> imageList;
	public boolean edit = false;
	public ArrayList<String> selectPids = new ArrayList<>();

	@Override
	protected int getLayout() {
		return R.layout.activity_manage_gallery;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		loadGallery();

		findTextViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(edit) {

					if(selectPids.size()>0) {
						//删除
						String pids = StringUtils.join(selectPids.toArray(), ",");

						HashMap<String,String> params =  new HashMap<String, String>();
						params.put("action", "userDeletePhoto");
						params.put("photo_id", pids);
						params.put("user_id", Common.getUserId(ManageGalleryActivity.this));

						final Common.LoadingHandler handler = Common.loading(ManageGalleryActivity.this,"删除中...");
						Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
							@Override
							public void run(String result) {

							}

							@Override
							public void run(JSONObject result) {
								edit = false;
								loadGallery();
								findTextViewById(R.id.edit).setText("编辑");
								handler.close();
							}

							@Override
							public void run(JSONArray result) {

							}
						});



					} else {
						Toast.makeText(ManageGalleryActivity.this,"请选择一张图片",Toast.LENGTH_SHORT).show();
					}
				} else {
					edit = true;
					findTextViewById(R.id.edit).setText("删除");

					for (RelativeLayout container:imageList) {
						container.findViewById(R.id.selected).setVisibility(View.VISIBLE);
					}
					// 删除操作 不可以选择图库
					if (addImageView !=null ){
						addImageView.setVisibility(View.GONE);
					}
				}
			}
		});

	}

	public void loadGallery(){


		HashMap<String,String> params = new HashMap<>();
		params.put("action","userPhotoList");
		params.put("page_num","1");
		params.put("user_id", Common.getUserId(this));

		imageList = new ArrayList<>();
		selectPids = new ArrayList<>();

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {
				((LinearLayout) findViewById(R.id.row_list)).removeAllViews();
				Log.i("kol", result.toString());
				try {
					try {
						photoArray = result.getJSONArray("result");
					} catch (JSONException e) {
						e.printStackTrace();
						photoArray = new JSONArray();
					}

					int rowCount = (int) Math.ceil(photoArray.length() / 3.0);


					LinearLayout lastRow = null;

					for (int i = 0; i < rowCount; i++) {
						LinearLayout row = new LinearLayout(ManageGalleryActivity.this);
						row.setOrientation(LinearLayout.HORIZONTAL);
						LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						rowParams.bottomMargin = (int) getResources().getDimension(R.dimen.px55);
						row.setLayoutParams(rowParams);

						for (int j = 0; j < 3; j++) {
							final int index = i * 3 + j;
							if (index < photoArray.length()) {
								final JSONObject photo = photoArray.getJSONObject(index);


								LayoutInflater mInflater = LayoutInflater.from(ManageGalleryActivity.this);

								final RelativeLayout container = (RelativeLayout) mInflater.inflate(R.layout.one_pic, null);
								imageList.add(container);
								int width = (int) getResources().getDimension(R.dimen.px302);

								LinearLayout.LayoutParams containerViewParams = new LinearLayout.LayoutParams(width, width);
								containerViewParams.leftMargin = (int) getResources().getDimension(R.dimen.px37);
								container.setLayoutParams(containerViewParams);


								ImageView imageView = (ImageView) container.findViewById(R.id.image);

								ImageLoader imageLoader = ImageLoader.getInstance();
								imageLoader.displayImage(photo.getString("small_url"), imageView, Config.options);
								row.addView(container);

								container.setTag(false);

								container.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {

										if(edit) {
											if((boolean)container.getTag()) {
												container.setTag(false);
												container.findViewById(R.id.selected).setBackgroundResource(R.drawable.pay_diselect);
												try {
													selectPids.remove(photo.getString("id"));
												} catch (JSONException e) {
													e.printStackTrace();
												}
											} else {
												container.setTag(true);
												container.findViewById(R.id.selected).setBackgroundResource(R.drawable.pay_selected);
												try {
													selectPids.add(photo.getString("id"));
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}

										} else {
											String[] photos = new String[photoArray.length()];
											for (int kk = 0; kk < photoArray.length(); kk++) {
												try {
													photos[kk] = photoArray.getJSONObject(kk).getString("big_url");
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}
											Intent intent = new Intent(ManageGalleryActivity.this, ImageListActivity.class);
											intent.putExtra("image_list", photos);
											intent.putExtra("current_index", index);
											startActivity(intent);
										}
									}
								});
							}
						}
						((LinearLayout) findViewById(R.id.row_list)).addView(row);
						lastRow = row;
					}

					//那加+
					addImageView = new ImageView(ManageGalleryActivity.this);
					addImageView.setScaleType(ImageView.ScaleType.FIT_XY);
					int width = (int) getResources().getDimension(R.dimen.px302);
					LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(width, width);
					imageViewParams.leftMargin = (int) getResources().getDimension(R.dimen.px37);
					addImageView.setLayoutParams(imageViewParams);
					addImageView.setBackgroundResource(R.drawable.add_image);

					addImageView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
                                                        /* 开启Pictures画面Type设定为image */
							intent.setType("image/*");
                                                         /* 使用Intent.ACTION_GET_CONTENT这个Action */
							intent.setAction(Intent.ACTION_GET_CONTENT);
                                                         /* 取得相片后返回本画面 */
							startActivityForResult(intent, 1);
						}
					});

					if (lastRow != null && photoArray.length()%3!=0) {
						lastRow.addView(addImageView);
					} else {
						LinearLayout row = new LinearLayout(ManageGalleryActivity.this);
						row.setOrientation(LinearLayout.HORIZONTAL);
						LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						rowParams.bottomMargin = (int) getResources().getDimension(R.dimen.px55);
						row.setLayoutParams(rowParams);
						row.addView(addImageView);
						((LinearLayout) findViewById(R.id.row_list)).addView(row);
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



	public class FileInfo {
		public String filename = "";
		public Bitmap bitmap;
		byte[] byteArray;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			final Uri uri = data.getData();
			Log.i("path", uri.toString());
			String path = uri.getPath();

			FileUpload fileUpload = new FileUpload(this);
			HashMap<String, String> params = new HashMap<>();
			params.put("type", "2");
			fileUpload.setMaxWidth(450);
			fileUpload.uploadByResult(data, params);
			fileUpload.setOnUploadStatusChangedListener(new FileUpload.OnUploadStatusChangedListener() {
				@Override
				public void success(JSONObject result) {
					loadGallery();
					addImageView.setBackgroundResource(R.drawable.add_image);
					Toast.makeText(ManageGalleryActivity.this, "上传图片成功！", Toast.LENGTH_SHORT).show();
				}
			});
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
