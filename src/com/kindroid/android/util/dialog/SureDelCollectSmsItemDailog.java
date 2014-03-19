package com.kindroid.android.util.dialog;




import com.kindroid.android.model.CategorySmsListItem;
import com.kindroid.android.model.NativeCursor;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;
import com.kindroid.security.util.CollectionDataBase;
import com.kindroid.security.util.Constant;
import com.kindroid.security.util.InterceptDataBase;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;

import android.widget.TextView;


public class SureDelCollectSmsItemDailog extends Dialog implements View.OnClickListener {
	private TextView mSureTv,mCancelTv,mTitleTv,mContentTv;
	private CategorySmsListItem item;
	
	private static final String LAYOUT_FILE = "sure_del_dialog";
	public CategorySmsListItem getItem() {
		return item;
	}
	public void setItem(CategorySmsListItem item) {
		this.item = item;
	}


	private int type;
	private Context context;

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
			setContentView(R.layout.sure_del_dialog);
		} else {
			setContentView(contentView);
		}
		
		findView();
	}
	private void findView(){
		
		mTitleTv=(TextView) findViewById(R.id.title_tv);
		mContentTv=(TextView) findViewById(R.id.content_tv);
		mSureTv=(TextView) findViewById(R.id.sure_tv);
		mCancelTv=(TextView) findViewById(R.id.cancel_tv);
		mSureTv.setOnClickListener(this);
		mCancelTv.setOnClickListener(this);
	    TextPaint tp=mSureTv.getPaint();
	    tp.setFakeBoldText(true);
	    tp=mCancelTv.getPaint();
	    tp.setFakeBoldText(true);
	    tp=mTitleTv.getPaint();
	    tp.setFakeBoldText(true);
	    mSureTv.setText(getContext().getString(R.string.sms_contact_confirm));
	    mCancelTv.setText(getContext().getString(R.string.sms_contact_cancel));
	    mTitleTv.setText(getContext().getString(R.string.sms_remind));
	    mContentTv.setText(getContext().getString(R.string.sure_del_the_collect));
	    
	    
	}
	

	


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sure_tv:
			CollectionDataBase.get(getContext()).delCollectSms(item.getmId());
			((Activity)context).finish();
			dismiss();
			break;
		case R.id.cancel_tv:
			dismiss();
			break;
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	
	/**
	 * nc 必填选项
	 * */
	
	public SureDelCollectSmsItemDailog(Context context, int theme) {
		super(context, theme);
		this.context=context;
	}

	
	public void showDialog(){
		this.show();
		
	}
	
	
	
	
	
	
	

}
