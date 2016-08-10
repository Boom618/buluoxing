package com.diy;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buluoxing.famous.R;

import java.util.ArrayList;


/**
 * 上拉刷新ListView
 *
 * @author xiejinxiong
 *
 */
public class AutoNewLineLayout extends LinearLayout {


	private int number;
	private ArrayList<View> viewArrayList = new ArrayList<>();
	private int index = 0;
	private LinearLayout currentRow;
	private int rowDistance;

	/** 存储上下文 */
	public AutoNewLineLayout(Context context) {
		super(context);
		initView();
	}

	public AutoNewLineLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	public void setOnelineNumber(int number) {
		this.number = number;
	}
	public void setRowDistance(int dis) {
		rowDistance = dis;
	}
	private void  initView(){
		setOrientation(VERTICAL);

	}
	public void addNewView(View view) {
		if(index%number==0) {
			LinearLayout row = new LinearLayout(getContext());

			LinearLayout.LayoutParams rowParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			rowParams.bottomMargin = rowDistance;
			row.setLayoutParams(rowParams);
			currentRow = row;
			addView(row);
		}
		currentRow.addView(view);
		index ++;
	}
	public void clearAllViews(){
		removeAllViews();
		index = 0;
	}
}