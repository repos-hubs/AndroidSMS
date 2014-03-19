/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author: heli.zhao
 * Date: 2011-09
 * Description:
 */
package com.kindroid.kincent.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kindroid.android.util.dialog.MessageMenuSignatureDialog;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.util.SmsUtils;
import com.kindroid.kincent.R;


/**
 * @author heli.zhao
 *
 */
public class SumSettingListActivity extends Activity implements View.OnClickListener {
	
	private TextView mTitleTv;
	private ImageView switchImageView;
	private ImageView dropDownImageView;
	private LinearLayout dropdownLayout;
	private TextView contentSigTextView;			//签名内容，用于设置字体颜色
	
	private boolean signatureSwitch = false;
	private MessageMenuSignatureDialog menuDialog;
	
	private static final String LAYOUT_FILE = "sum_setting_layout";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
			setContentView(R.layout.sum_setting_layout);
		}else{
			setContentView(contentView);
		}
		menuDialog = new MessageMenuSignatureDialog(SumSettingListActivity.this, R.style.Theme_CustomDialog);
		findViews();
	}
	
	private void findViews() {
		mTitleTv = (TextView) findViewById(R.id.title_tv);
		mTitleTv.getPaint().setFakeBoldText(true);
		mTitleTv.setText(R.string.menu_setting_item);
		
		switchImageView = (ImageView) findViewById(R.id.switchImageView);
		switchImageView.setOnClickListener(this);
		
		dropDownImageView = (ImageView) findViewById(R.id.dropdownImageView);
		dropdownLayout = (LinearLayout) findViewById(R.id.dropdownContentLayout);
		dropdownLayout.setOnClickListener(this);
		
		contentSigTextView = (TextView) findViewById(R.id.signatureContentTitleTextView);
		
		signatureSwitch = SmsUtils.getSignatureSwitch(SumSettingListActivity.this);
		
		if (signatureSwitch) {
//			switchImageView.setImageResource(R.drawable.sms_menu_switch_on);
			switchImageView.setSelected(true);
//			dropDownImageView.setImageResource(R.drawable.sms_dialogue_menu_drop_down_on);
			dropDownImageView.setSelected(true);
//			contentSigTextView.setTextColor(getResources().getColor(R.color.black));
			contentSigTextView.setSelected(true);
		} else {
//			switchImageView.setImageResource(R.drawable.sms_menu_switch_off);
			switchImageView.setSelected(false);
//			dropDownImageView.setImageResource(R.drawable.sms_dialogue_menu_drop_down_off);
			dropDownImageView.setSelected(false);
//			contentSigTextView.setTextColor(getResources().getColor(R.color.grey));
			contentSigTextView.setSelected(false);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.switchImageView:
			signatureSwitch = !signatureSwitch;
			if (signatureSwitch) {
//				switchImageView.setImageResource(R.drawable.sms_menu_switch_on);
				switchImageView.setSelected(true);
//				dropDownImageView.setImageResource(R.drawable.sms_dialogue_menu_drop_down_on);
				dropDownImageView.setSelected(true);
				contentSigTextView.setTextColor(getResources().getColor(R.color.black));
			} else {
//				switchImageView.setImageResource(R.drawable.sms_menu_switch_off);
				switchImageView.setSelected(false);
//				dropDownImageView.setImageResource(R.drawable.sms_dialogue_menu_drop_down_off);
				dropDownImageView.setSelected(false);
				contentSigTextView.setTextColor(getResources().getColor(R.color.grey));
			}
			SmsUtils.setSignatureSwitch(signatureSwitch, SumSettingListActivity.this);
			break;
		case R.id.dropdownContentLayout:
			if (signatureSwitch) {
				menuDialog.showDialog();
			}
			break;
		default:
			break;
		}
	}
	
}
