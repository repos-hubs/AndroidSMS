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
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.PrivacyContactsListAdapter;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.R;

import java.util.ArrayList;
import java.util.List;

public class PrivacyContactListItemClickDialog extends Dialog implements View.OnClickListener{
	private Context mContext;
	private PrivacyContactDataItem mDataItem;
	private PrivacyContactsListAdapter mAdapter;
	
	private TextView mTitleTv;
	private View mCallLinear;
	private View mSendMsgLinear;
	private View mModifyLinear;
	private View mDeleteLinear;
	private View mCloseIv;
	
	private static final String LAYOUT_FILE = "privacy_contact_click_dialog";

	public PrivacyContactListItemClickDialog(Context context, int theme, PrivacyContactsListAdapter mAdapter,
			PrivacyContactDataItem dataItem) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mDataItem = dataItem;
		this.mAdapter = mAdapter;
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
			setContentView(R.layout.privacy_contact_click_dialog);
		}else{
			setContentView(contentView);
		}		
		
		mCallLinear = findViewById(R.id.privacy_contact_click_dialog_call_linear);
		mSendMsgLinear = findViewById(R.id.privacy_contact_click_dialog_send_msg_linear);
		mModifyLinear = findViewById(R.id.privacy_contact_click_dialog_modify_linear);
		mDeleteLinear = findViewById(R.id.privacy_contact_click_dialog_delete_linear);
		mCloseIv = findViewById(R.id.privacy_contact_click_dialog_close_iv);
		
		mCallLinear.setOnClickListener(this);
		mSendMsgLinear.setOnClickListener(this);
		mModifyLinear.setOnClickListener(this);
		mDeleteLinear.setOnClickListener(this);
		mCloseIv.setOnClickListener(this);
		
		mTitleTv = (TextView)findViewById(R.id.privacy_contact_click_dialog_title_tv);
		if(!mDataItem.getContactName().equals("")){
			mTitleTv.setText(mDataItem.getContactName());
		}else{
			mTitleTv.setText(mDataItem.getPhoneNumber());
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.privacy_contact_click_dialog_call_linear:
			String scheme = Constant.SCHEME_TEL;
			Uri phoneUri = Uri.fromParts(scheme, mDataItem.getPhoneNumber(), null);
			Intent intent = new Intent(Intent.ACTION_CALL, phoneUri);
			mContext.startActivity(intent);
			break;
		case R.id.privacy_contact_click_dialog_send_msg_linear:
			intent = new Intent(mContext, PrivacyDialogueDetailActivity.class);
			intent.putExtra(PrivacyDialogueDetailActivity.ACTION_TYPE, PrivacyDialogueDetailActivity.ACTION_MMS_SEND);
			intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_TYPE, PrivacyDialogueDetailActivity.ACTION_FROM_PRIVACY);
			intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_NUMBER, mDataItem.getPhoneNumber());
			if(TextUtils.isEmpty(mDataItem.getContactName())){
				intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_NAME, mDataItem.getContactName());
			}else{
				intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_NAME, mDataItem.getPhoneNumber());
			}
			mContext.startActivity(intent);
			break;
		case R.id.privacy_contact_click_dialog_modify_linear:
			PrivacyContactAddDialog mPrivacyContactAddDialog = new PrivacyContactAddDialog(mContext, R.style.Theme_CustomDialog, mAdapter, mDataItem);
			mPrivacyContactAddDialog.show();
			break;
		case R.id.privacy_contact_click_dialog_delete_linear:
//			PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(mContext);
//			pdbUtils.delContact(mContext, mDataItem);
//			mAdapter.delItem(mDataItem);
//			mAdapter.notifyDataSetChanged();
			List<PrivacyContactDataItem> delList = new ArrayList<PrivacyContactDataItem>();
			delList.add(mDataItem);
			PrivacyDelContactPromptDialog promptDialog = new PrivacyDelContactPromptDialog(mContext, R.style.Theme_CustomDialog, delList, mAdapter);
			promptDialog.show();
			
			break;
		case R.id.privacy_contact_click_dialog_close_iv:
			
			break;
		}
		dismiss();
	}

}
