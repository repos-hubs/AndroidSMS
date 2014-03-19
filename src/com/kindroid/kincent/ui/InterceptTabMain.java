/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:zili.chen
 * Date:2011.0907
 * Description:
 */
package com.kindroid.kincent.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost.TabSpec;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.service.UpdateProbService;
import com.kindroid.kincent.R;
public class InterceptTabMain extends TabActivity implements View.OnClickListener {

	
	private LinearLayout mInterceptHistoryLinear,mBlackWListLinear,mInterceptSettingLinear;
	private TabSpec mTab1,mTab2,mTab3;
	private Intent mInterceptHistoryI,mInterceptBlackWhiteListI,mInterceptSettingI;
	private ImageView mInterceptHistoryIv,mBlackWListIv,mInterceptSettingIv;
	
	

	private static final String LAYOUT_FILE = "intercept_tab_main";
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
			setContentView(R.layout.intercept_tab_main);
		}else{
			setContentView(contentView);
		}
		
		findView();
		bindListenerToView();
	}

	void findView() {
		mInterceptHistoryLinear = (LinearLayout)findViewById(R.id.intercept_history_linear);
		mBlackWListLinear =  (LinearLayout)findViewById(R.id.black_white_list_linear);
		mInterceptSettingLinear = (LinearLayout)findViewById(R.id.intercept_setting_linear);
		mInterceptHistoryIv=(ImageView) findViewById(R.id.intercept_history_iv);
		mBlackWListIv=(ImageView) findViewById(R.id.black_white_list_iv);
		mInterceptSettingIv=(ImageView) findViewById(R.id.intercept_setting_iv);
		
	
		mInterceptHistoryI = new Intent(this, InterceptHistory.class);
		mInterceptBlackWhiteListI = new Intent(this,
				InterceptBlackWhiteList.class);
		mInterceptSettingI = new Intent(this, InterceptSetting.class);

		mTab1 = getTabHost().newTabSpec("tab1").setIndicator("tab1")
				.setContent(mInterceptHistoryI);
		mTab2 =getTabHost().newTabSpec("tab2").setIndicator("tab2")
				.setContent(mInterceptBlackWhiteListI);
		mTab3 = getTabHost().newTabSpec("tab3").setIndicator("tab3")
				.setContent(mInterceptSettingI);
		mInterceptHistoryLinear.setSelected(true);
		mInterceptHistoryIv.setSelected(true);
	}
	void bindListenerToView() {
		getTabHost().addTab(mTab1);
		getTabHost().addTab(mTab2);
		getTabHost().addTab(mTab3);
		mInterceptHistoryLinear.setOnClickListener(this);
		mBlackWListLinear.setOnClickListener(this);
		mInterceptSettingLinear.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
//		mInterceptHistoryLinear.setBackgroundResource(R.drawable.intercept_log_bg_off);
//		mBlackWListLinear.setBackgroundResource(R.drawable.black_white_list_bg_off);
//		mInterceptSettingLinear.setBackgroundResource(R.drawable.intercept_setting_off);
		
		mInterceptHistoryLinear.setSelected(false);
		mBlackWListLinear.setSelected(false);
		mInterceptSettingLinear.setSelected(false);
		
		mInterceptHistoryIv.setSelected(false);
		mBlackWListIv.setSelected(false);
		mInterceptSettingIv.setSelected(false);
		
		
		switch (v.getId()) {
		case R.id.intercept_history_linear:
			//mInterceptHistoryLinear.setBackgroundResource(R.drawable.intercept_log_bg_on);
			mInterceptHistoryLinear.setSelected(true);
			mInterceptHistoryIv.setSelected(true);
			getTabHost().setCurrentTab(0);

			break;
		case R.id.black_white_list_linear:
		//	mBlackWListLinear.setBackgroundResource(R.drawable.black_white_list_bg_on);
			mBlackWListLinear.setSelected(true);
			mBlackWListIv.setSelected(true);
			getTabHost().setCurrentTab(1);
			break;
		case R.id.intercept_setting_linear:
		//	mInterceptSettingLinear.setBackgroundResource(R.drawable.intercept_setting_on);
			mInterceptSettingLinear.setSelected(true);
			mInterceptSettingIv.setSelected(true);
			getTabHost().setCurrentTab(2);
			break;
		}

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		Intent intent=new Intent(InterceptTabMain.this,UpdateProbService.class);
		startService(intent);
	}
	
	
	
	
}