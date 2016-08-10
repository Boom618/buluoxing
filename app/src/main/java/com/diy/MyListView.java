package com.diy;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.buluoxing.famous.R;

import java.security.acl.Group;


/**
 * 上拉刷新ListView
 *
 * @author xiejinxiong
 *
 */
public class MyListView extends ListView implements OnScrollListener {

	/** 底部显示正在加载的页面 */
	private View footerView = null;
	/** 存储上下文 */
	private Context context;
	/** 上拉刷新的ListView的回调监听 */
	private MyPullUpListViewCallBack myPullUpListViewCallBack;
	/** 记录第一行Item的数值 */
	private int firstVisibleItem;
	boolean isLastRow = false;
	public MyListView(Context context) {
		super(context);
		this.context = context;
		initListView();
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initListView();

	}

	/**
	 * 初始化ListView
	 */
	private void initListView() {

		// 为ListView设置滑动监听
		setOnScrollListener(this);
		// 去掉底部分割线
		setFooterDividersEnabled(false);
		initBottomView();
	}

	/**
	 * 初始化话底部页面
	 */
	public void initBottomView() {

		if (footerView == null) {
			footerView = LayoutInflater.from(this.context).inflate(
				R.layout.list_footerview, null);
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
			footerView.setLayoutParams(params);
		}
		addFooterView(footerView);
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			//加载元素

			if(myPullUpListViewCallBack!=null) {
				myPullUpListViewCallBack.scrollBottomState();
			}
			isLastRow = false;
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
	                     int visibleItemCount, int totalItemCount) {
		Log.i("scroll",String.format("%d-%d-%d",firstVisibleItem,visibleItemCount,totalItemCount));
		this.firstVisibleItem = firstVisibleItem;
		if (footerView != null) {
			//判断可视Item是否能在当前页面完全显示
			if ((visibleItemCount == totalItemCount) && (totalItemCount != 1) ) {
				footerView.setVisibility(View.GONE);//隐藏底部布局
			} else {
				footerView.setVisibility(View.VISIBLE);//显示底部布局
			}
		}
		if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
			isLastRow = true;
		}
	}

	public void haveData() {
		((TextView)this.footerView.findViewById(R.id.more_text)).setText("加载中...");
	}

	public void noMoreData(String message) {
		((TextView)this.footerView.findViewById(R.id.more_text)).setText(message);
	}
	public void noMoreData() {
		((TextView)this.footerView.findViewById(R.id.more_text)).setText("已经全部加载完毕");
	}

	public void setMyPullUpListViewCallBack(
		MyPullUpListViewCallBack myPullUpListViewCallBack) {
		this.myPullUpListViewCallBack = myPullUpListViewCallBack;
	}

	/**
	 * 上拉刷新的ListView的回调监听
	 *
	 * @author xiejinxiong
	 *
	 */
	public interface MyPullUpListViewCallBack {
		void scrollBottomState();
	}
}