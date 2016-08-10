package com.buluoxing.famous;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by admin on 2016/6/11.
 */
public class LoadingDialog extends Dialog {
	int layoutRes;//布局文件
	Context context;
	private String title = "";
	private LayoutInflater mInflater;

	public LoadingDialog(Context context) {
		super(context,R.style.loadingDialog);
		this.context = context;
		this.layoutRes=R.layout.loading;
	}
	public LoadingDialog(Context context,String message) {
		super(context,R.style.loadingDialog);
		this.context = context;
		this.layoutRes=R.layout.loading;
		title = message;
	}
	/**
	 * 自定义布局的构造方法
	 * @param context
	 * @param resLayout
	 */
	public LoadingDialog(Context context,int resLayout){
		super(context);
		this.context = context;
		this.layoutRes=resLayout;
	}
	/**
	 * 自定义主题及布局的构造方法
	 * @param context
	 * @param theme
	 * @param resLayout
	 */
	public LoadingDialog(Context context, int theme,int resLayout){
		super(context, theme);
		this.context = context;
		this.layoutRes=resLayout;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInflater = LayoutInflater.from(context);
		View view = mInflater.inflate(layoutRes, null);
		((TextView)view.findViewById(R.id.text)).setText(title);
		this.setContentView(view);
	}
}