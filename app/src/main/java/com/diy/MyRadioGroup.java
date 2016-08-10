package com.diy;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buluoxing.famous.R;

import java.util.ArrayList;


/**
 * 上拉刷新ListView
 *
 * @author xiejinxiong
 *
 */
public class MyRadioGroup extends LinearLayout {

	private OnCheckStatusChangedListener listener;
	private ArrayList<LinearLayout> radioList = new ArrayList<>();

	public interface OnCheckStatusChangedListener{
		public void check(MyRadioGroup cb, int position);
	}

	/** 存储上下文 */
	public MyRadioGroup(Context context) {
		super(context);
		initView();
	}

	public MyRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void  initView(){
		setOrientation(LinearLayout.HORIZONTAL);
	}

	public void check(Boolean checked){

	}
	public void setOnCheckStatusChangedListener(OnCheckStatusChangedListener listener) {
		this.listener = listener;
	}
	public void addRadio(String text) {
		LayoutInflater mInflater = LayoutInflater.from(getContext());
		LinearLayout radio = (LinearLayout) mInflater.inflate(R.layout.radio, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		radio.setLayoutParams(params);
		params.rightMargin = (int) getResources().getDimension(R.dimen.px70);
		((TextView)radio.findViewById(R.id.radio_text)).setText(text);
		radio.setTag(radioList.size());
		radio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setRadioCheckByIndex((int)v.getTag());
			}
		});
		radioList.add(radio);
		this.addView(radio);
	}
	public void setRadioCheckByIndex(int index) {

		LinearLayout radio = radioList.get(index);
		for(int i=0;i<radioList.size();i++) {
			((ImageView)radioList.get(i).findViewById(R.id.radio_image)).setImageResource(R.drawable.radio_unselect);
		}
		((ImageView )radio.findViewById(R.id.radio_image)).setImageResource(R.drawable.radio_select);
		listener.check(this,index);
	}

}