/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:zili.chen
 * Date:2011.09
 * Description:
 */
package com.kindroid.kincent.ui;




import com.kindroid.android.model.CategorySmsListItem;
import com.kindroid.android.model.NativeCursor;
import com.kindroid.android.util.dialog.AddBlackWhiteDailog;
import com.kindroid.android.util.dialog.SureDelCollectSmsItemDailog;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;
public class CollectListDetailActivity extends Activity implements
		View.OnClickListener {

	private TextView mDelTv, mForwardTv,mSmsContentTv;
	
	private CategorySmsListItem mSmsItem;
	
	private TextView mTitleTv;	
	
	private static final String LAYOUT_FILE = "collect_detail_layout";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentViews();
	}
	private void initContentViews(){
		View contentView = null;
		try{
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		}catch(Exception e){
			contentView = null;
		}
		if(contentView == null){
			setContentView(R.layout.collect_detail_layout);
		}else{
			setContentView(contentView);
		}
		
		mSmsItem=(CategorySmsListItem) getIntent().getSerializableExtra(CollectListActivity.COLLET_LIST_ITEM);
		
		findView();
		bindListenerToView();
		
		mSmsContentTv.setText(mSmsItem.getmBody()+"");
	}

	private void findView() {
		mDelTv = (TextView) findViewById(R.id.del_tv);
		mForwardTv = (TextView) findViewById(R.id.forward_tv);
		mSmsContentTv=(TextView) findViewById(R.id.sms_content_tv);
		mTitleTv=(TextView) findViewById(R.id.activity_title_tv);
		
		TextPaint tp = mDelTv.getPaint();
		tp.setFakeBoldText(true);
		tp = mForwardTv.getPaint();
		tp.setFakeBoldText(true);
		tp=mTitleTv.getPaint();
		tp.setFakeBoldText(true);
		
		mTitleTv.setText(R.string.collect_detail);
		
		mDelTv.setText(R.string.sms_dialogue_delete);
		mForwardTv.setText(R.string.forward);

	}

	private void bindListenerToView() {

		mDelTv.setOnClickListener(this);
		mForwardTv.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.del_tv:

			SureDelCollectSmsItemDailog dialog = new SureDelCollectSmsItemDailog(CollectListDetailActivity.this,
					R.style.Theme_CustomDialog);
			dialog.setItem(mSmsItem);
			dialog.showDialog();

			break;
		case R.id.forward_tv:
			Intent intent =new Intent(CollectListDetailActivity.this,KindroidMessengerWriteMessageActivity.class);
			intent.putExtra(KindroidMessengerWriteMessageActivity.FORWARD_MESSAGE_KEY, mSmsItem.getmBody()+"");
			startActivity(intent);

			break;

		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

}