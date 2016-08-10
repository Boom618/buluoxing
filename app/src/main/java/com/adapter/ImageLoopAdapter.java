package com.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buluoxing.famous.BrowserActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 2016/5/8.
 */
public class ImageLoopAdapter extends PagerAdapter {



	ArrayList<HashMap<String,String>>imageUrlList = null;
	Context context;

	public ImageLoopAdapter(Context ctx) {
		context = ctx;
	}
	public void setImageUrlList(ArrayList<HashMap<String,String>> list) {
		imageUrlList = list;
	}
	@Override
	public int getCount() {
		return imageUrlList.size();
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
	public Object instantiateItem(final ViewGroup container, final int position) {

		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		//对ViewPager页号求模取出View列表中要显示的项
		ImageView imageView =new ImageView(context);
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setLayoutParams(params);


		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory(true)                               //启用内存缓存
			.cacheOnDisk(true)                                 //启用外存缓存
			.build();

		imageLoader.displayImage(imageUrlList.get(position).get("img_url"), imageView,options);
		container.addView(imageView);

		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, BrowserActivity.class);
				intent.putExtra("url",imageUrlList.get(position).get("link_url"));
				context.startActivity(intent);
			}
		});
		return imageView;
	}
}
