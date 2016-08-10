package com.diy;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.buluoxing.famous.R;

import org.w3c.dom.Text;


/**
 * 上拉刷新ListView
 *
 * @author xiejinxiong
 *
 */
public class MyCheckBox extends LinearLayout {

	public interface OnCheckStatusChangedListener{
		public void check(MyCheckBox cb,Boolean checked);
	}
	public boolean checked = false;
	public ImageView checkBox;
	public TextView checkText;
	public OnCheckStatusChangedListener listener = null;
	public boolean disabled = false;
	/** 存储上下文 */
	public MyCheckBox(Context context) {
		super(context);
		initView();
	}

	public MyCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void  initView(){
		setOrientation(LinearLayout.HORIZONTAL);
		int width = (int)getResources().getDimension(R.dimen.px49);
		checkBox = new ImageView(getContext());
		LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(width,width);
		checkBox.setImageResource(R.drawable.uncheck);
		checkBox.setLayoutParams(imageParams);


		checkText = new TextView(getContext());
		checkText.setTextColor(Color.parseColor("#46474a"));
		checkText.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.px40));
		LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

		textParams.leftMargin = (int)getResources().getDimension(R.dimen.px29);
		checkText.setLayoutParams(textParams);

		this.addView(checkBox);
		this.addView(checkText);

		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				check(!checked);
			}
		});
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public void check(Boolean checked){

		if(disabled) {
			return;
		}

		this.checked = checked;
		if(checked) {
			checkBox.setImageResource(R.drawable.check);
		} else {
			checkBox.setImageResource(R.drawable.uncheck);
		}
		if(listener!=null) {
			listener.check(this,checked);
		}
	}
	public void setOnCheckStatusChangedListener(OnCheckStatusChangedListener listener) {
		this.listener = listener;
	}
	public void setText(String text){
		checkText.setText(text);
	}


}