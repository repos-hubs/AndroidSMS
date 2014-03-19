/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.PrivacyMessageListAdapter;
import com.kindroid.kincent.data.PrivacyMessagesItem;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.R;

public class PrivacyMessageItemClickDialog extends Dialog implements View.OnClickListener{
	private Context mContext;
	private PrivacyMessagesItem mItem;
	private PrivacyMessageListAdapter mAdapter;
	
	private TextView mTitleTv;
	private View mCloseIv;
	private View mReplyLinear;
	private View mDeleteLinear;
	private View mRestoreLinear;

	private static final String LAYOUT_FILE = "privacy_messages_item_click_dialog";
	
	public PrivacyMessageItemClickDialog(Context context, int theme, PrivacyMessageListAdapter adapter,
			PrivacyMessagesItem item) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mAdapter = adapter;
		this.mItem = item;
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
			setContentView(R.layout.privacy_messages_item_click_dialog);
		}else{
			setContentView(contentView);
		}
		
		mTitleTv = (TextView)findViewById(R.id.privacy_messages_item_click_dialog_title_tv);
		mCloseIv = findViewById(R.id.privacy_messages_item_click_dialog_close_iv);
		mReplyLinear = findViewById(R.id.privacy_messages_item_click_dialog_reply_linear);
		mDeleteLinear = findViewById(R.id.privacy_messages_item_click_dialog_delete_linear);
		mRestoreLinear = findViewById(R.id.privacy_messages_item_click_dialog_call_linear);
		if(mItem.getContactName() != null){
			mTitleTv.setText(mItem.getContactName());
		}else{
			mTitleTv.setText(mItem.getPhoneNumber());
		}
		
		mCloseIv.setOnClickListener(this);
		mReplyLinear.setOnClickListener(this);
		mDeleteLinear.setOnClickListener(this);
		mRestoreLinear.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.privacy_messages_item_click_dialog_close_iv:
			dismiss();
			break;
		case R.id.privacy_messages_item_click_dialog_reply_linear:
//			String scheme = Constant.SCHEME_SMSTO;
//			Uri phoneUri = Uri.fromParts(scheme, mItem.getPhoneNumber(), null);
//			Intent intent = new Intent(Intent.ACTION_SENDTO, phoneUri);
//			mContext.startActivity(intent);
			Intent intent = new Intent(mContext, PrivacyDialogueDetailActivity.class);
			intent.putExtra(PrivacyDialogueDetailActivity.ACTION_TYPE, PrivacyDialogueDetailActivity.ACTION_MMS_REPLY);
			intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_TYPE, PrivacyDialogueDetailActivity.ACTION_FROM_PRIVACY);
			intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_NUMBER, mItem.getPhoneNumber());
			intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_NAME, mItem.getPhoneNumber());
			mContext.startActivity(intent);
			dismiss();
			break;
		case R.id.privacy_messages_item_click_dialog_delete_linear:
//			PrivacyDBUtils.getInstance(mContext).delMessages(mContext, mItem);
//			mAdapter.delItem(mItem);
//			mAdapter.notifyDataSetChanged();
			DelConfirmDialog confirmDialog = new DelConfirmDialog(mContext, R.style.Theme_CustomDialog);
			confirmDialog.show();
			dismiss();
			break;
		case R.id.privacy_messages_item_click_dialog_call_linear:
//			PrivacyDBUtils.getInstance(mContext).restoreMessages(mContext, mItem);
//			mAdapter.delItem(mItem);
//			mAdapter.notifyDataSetChanged();
			String scheme = Constant.SCHEME_TEL;
			Uri phoneUri = Uri.fromParts(scheme, mItem.getPhoneNumber(), null);
			intent = new Intent(Intent.ACTION_CALL, phoneUri);
			mContext.startActivity(intent);
			dismiss();
			break;
		}
		
	}
	private class DelConfirmDialog extends Dialog implements View.OnClickListener{
		private Context mContext;
		private Button buttonOk;
		private Button buttonCancel;
		private View closeIv;

		public DelConfirmDialog(Context context, int theme) {
			super(context, theme);
			// TODO Auto-generated constructor stub
			initContentViews();
		}
		private void initContentViews(){
			setContentView(R.layout.confirm_dialog);
			buttonOk = (Button)findViewById(R.id.button_ok);
			buttonCancel = (Button)findViewById(R.id.button_cancel);
			closeIv = findViewById(R.id.dialog_close_iv);
			buttonOk.setOnClickListener(this);
			buttonCancel.setOnClickListener(this);
			closeIv.setOnClickListener(this);
			
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.button_ok:
				PrivacyDBUtils.getInstance(mContext).delMessages(mContext, mItem);
				mAdapter.delItem(mItem);
				mAdapter.notifyDataSetChanged();
				dismiss();
				break;
			case R.id.button_cancel:
				dismiss();
				break;
			case R.id.dialog_close_iv:
				dismiss();
				break;
			}
		}
		
	}

}
