package com.kindroid.android.util.dialog;



import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class GenealDailog extends Dialog {

	private TextView mTitleTv;
	private TextView mContentTv;
	private LinearLayout mContentLinear;
	private int initStarNum = 8;

	private boolean showStarByOrder = false;

	private Thread mThread;
	
	private boolean mShowContentTv=true;
	private String mContentStr,mTitleStr;
	
	
	private static final String ITEM_BG_LAYOUT_FILE = "loading_focus_item_bg";
	private static final String LAYOUT_FILE = "general_dialog_layout";

	public GenealDailog(Context context, int theme) {
		super(context, theme);

		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initContentViews();
	}
	private void initContentViews(){
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry
					.inflate(LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			setContentView(R.layout.general_dialog_layout);
		} else {
			setContentView(contentView);
		}
		
		mTitleTv = (TextView) findViewById(R.id.title_tv);
		mTitleTv.getPaint().setFakeBoldText(true);
		mTitleTv.setText(R.string.app_name);
		mContentTv = (TextView) findViewById(R.id.content_tv);
		mContentLinear = (LinearLayout) findViewById(R.id.linear);
		for (int i = 0; i < initStarNum; i++) {
			View view = initContentLinear();
			mContentLinear.addView(view, i, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}
	}
	private View initView(){
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(ITEM_BG_LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			contentView = LayoutInflater.from(getContext()).inflate(R.layout.loading_focus_item_bg, null);
		} 
		return contentView;
	}
	RelativeLayout initContentLinear() {
		
		RelativeLayout rl =(RelativeLayout)initView();
		
//		
//		RelativeLayout rl = new RelativeLayout(getContext());
//		ImageView off_iv = new ImageView(getContext());
//		off_iv.setBackgroundResource(R.drawable.loading_unfocus);
//		// off_iv.setTag(i+"off");
//		ImageView on_iv = new ImageView(getContext());
//		on_iv.setBackgroundResource(R.drawable.loading_focus);
//		// off_iv.setTag(i+"on");
//		on_iv.setVisibility(View.INVISIBLE);
//		rl.setGravity(Gravity.CENTER_VERTICAL);
//	
//	
//		rl.addView(off_iv,0, new RelativeLayout.LayoutParams(
//				RelativeLayout.LayoutParams.WRAP_CONTENT,
//				RelativeLayout.LayoutParams.WRAP_CONTENT));
//		rl.addView(on_iv, 1, new RelativeLayout.LayoutParams(
//				RelativeLayout.LayoutParams.WRAP_CONTENT,
//				RelativeLayout.LayoutParams.WRAP_CONTENT));
	
		return rl;
	}

	void restoreInitStatus() {
		for (int i = 0; i < initStarNum; i++) {
			RelativeLayout rl = (RelativeLayout) mContentLinear.getChildAt(i);
		
			rl.getChildAt(0).setVisibility(View.VISIBLE);
			rl.getChildAt(1).setVisibility(View.INVISIBLE);
		}
	}

	public void showDialog() {
		this.show();
		restoreInitStatus();
		
		if(mShowContentTv){
			if(mContentStr!=null){
				mContentTv.setText(mContentStr);
			}
			
		}else{
			mContentTv.setVisibility(View.GONE);
		}
		if(mTitleStr!=null){
			mTitleTv.setText(mTitleStr);
		}
		
		showStarByOrder = true;
		mThread=new WhileThread();
		mThread.start();
		
	}

	public void disMisDialog() {
		showStarByOrder = false;
		this.dismiss();
	}

	@Override
	public void onBackPressed() {
		showStarByOrder = false;
		this.dismiss();
	}

	class WhileThread extends Thread {
		@Override
		public void run() {
			while (showStarByOrder) {
				for (int i = 0; i < initStarNum; i++) {
					if(!showStarByOrder){
						break;
					}
					handler.sendEmptyMessage(i);
					try {
						Thread.sleep(350);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			mThread=null;
		}
	}
	private Handler handler=new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			int index=msg.what;
			RelativeLayout rl=(RelativeLayout) mContentLinear.getChildAt(index);
			rl.getChildAt(0).setVisibility(View.INVISIBLE);
			rl.getChildAt(1).setVisibility(View.VISIBLE);
			int preIndex=0;
			if(index==0){
				preIndex=initStarNum-1;
			}else{
				preIndex=index-1;
			}
			RelativeLayout rl2=(RelativeLayout) mContentLinear.getChildAt(preIndex);
			rl2.getChildAt(0).setVisibility(View.VISIBLE);
			rl2.getChildAt(1).setVisibility(View.INVISIBLE);
		

		};
		
	};
	public void setmInitContentStr(String mContentStr) {
		this.mContentStr = mContentStr;
	}

	public void setmInitTitleStr(String mTitleStr) {
		this.mTitleStr = mTitleStr;
	}
	
	public void setContentStr(String mContentStr) {
		mContentTv.setText(mContentStr);
	}
	
	
	

}
