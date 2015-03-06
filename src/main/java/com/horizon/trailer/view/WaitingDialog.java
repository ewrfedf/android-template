package com.horizon.trailer.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.horizon.trailer.R;


public class WaitingDialog extends Dialog{

	private TextView tv;
//	private AnimationDrawable mAnimationDrawable;
	private String tip; 
	
	public WaitingDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	protected WaitingDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public WaitingDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		View view = LayoutInflater.from(getContext()).inflate(R.layout.waiting, null);
		tv = (TextView) view.findViewById(R.id.waiting_tv);
		if(tip!=null&&!"".equals(tip)){
			tv.setText(tip);
		}
		setContentView(view);
	}
	public void setString(String tip){
		tv.setText(tip);
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
//		mAnimationDrawable = (AnimationDrawable) iv.getDrawable();
//		mAnimationDrawable.start();
	}
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
//		mAnimationDrawable = (AnimationDrawable) iv.getDrawable();
//		mAnimationDrawable.stop();
	}
	
	public void setMsg(String txt) {
		tv.setText(txt);
	}
	
}