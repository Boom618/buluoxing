package com.buluoxing.famous;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.adapter.ImageDetailListAdapter;
import com.adapter.ImageLoopAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.util.Common;
import com.util.Config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImageListActivity extends Activity {
	public int position = 0;
	public static void setImageViewCanShowBig(final ImageView imageView, final String imageUrl){
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
				public void onClick(View v) {
					showOneImage(imageView.getContext(),imageUrl);
			}
		});
	}
	public static void showOneImage(Context context,String imageUrl) {
		Intent intent = new Intent(context,ImageListActivity.class);
		intent.putExtra("image_list",new String[]{imageUrl});
		intent.putExtra("current_index",0);
		context.startActivity(intent);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_list);
		View backView = findViewById(R.id.back);

		int currentIndex = getIntent().getIntExtra("current_index",0);
		position = currentIndex;

		if(backView!=null ) {
			backView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
		final String[] imageUrllist = getIntent().getStringArrayExtra("image_list");
		Log.i("图片放大", "onCreate: imageUrllist = " + imageUrllist);
		final ViewPager imageLoop = (ViewPager) findViewById(R.id.image_list);

		final ImageDetailListAdapter adapter = new ImageDetailListAdapter(this);

		imageLoop.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				ImageListActivity.this.position = position;
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});


		adapter.setImageUrlList(imageUrllist);
		imageLoop.setAdapter(adapter);
		imageLoop.setCurrentItem(currentIndex);

		findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				final String imageUrl = imageUrllist[position];
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.loadImage(imageUrl, Config.options, new SimpleImageLoadingListener(){
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						loadedImage.compress(Bitmap.CompressFormat.PNG , 100 , stream);


						byte[] byteArray = stream.toByteArray();
						File dir=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/picture" );
						if(!dir.isFile()){
							dir.mkdir();
						}
						File file=new File(dir, Common.md5(imageUrl) +".png" );
						try {
							FileOutputStream fos=new FileOutputStream(file);
							fos.write(byteArray, 0, byteArray.length);
							fos.flush();

							Uri uri = Uri.fromFile(file);


							// 其次把文件插入到系统图库
							try {
								MediaStore.Images.Media.insertImage(getContentResolver(),
									file.getAbsolutePath(), "部落星_"+Common.md5(imageUrl), null);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
							sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

							Toast.makeText(ImageListActivity.this,"保存成功!",Toast.LENGTH_SHORT).show();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();

						}
					}
				});
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
