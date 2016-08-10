package com.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.buluoxing.famous.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by admin on 2016/5/8.
 */
public class ImageDetailListAdapter extends PagerAdapter {



	String[] imageUrlList = null;
	Context context;
	public int position = 0;

	public ImageDetailListAdapter(Context ctx) {
		context = ctx;
	}
	public void setImageUrlList(String[] list) {
		imageUrlList = list;
	}
	@Override
	public int getCount() {
		return imageUrlList.length;
	}
	public int getItemPosition(){
		return position;
	}
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view==object;
	}

	public void destroyItem(ViewGroup container, int position,
	                        Object object) {
		//Warning：不要在这里调用removeView
	}
	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		this.position = position;

		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		//对ViewPager页号求模取出View列表中要显示的项
		ImageView imageView =new ImageView(context);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setLayoutParams(params);

		imageView.setImageResource(R.drawable.loading_head_image);
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory(true)                               //启用内存缓存
			.cacheOnDisk(true)                                 //启用外存缓存
			.build();

		imageLoader.displayImage(imageUrlList[position], imageView,options);
		container.addView(imageView);
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((Activity)context).finish();
			}
		});


		return imageView;
	}
}
