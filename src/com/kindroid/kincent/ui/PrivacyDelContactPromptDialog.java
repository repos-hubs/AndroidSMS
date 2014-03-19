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
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kindroid.android.util.dialog.GenealDailog;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.PrivacyContactsListAdapter;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.util.PrivacyContactImportDataThread;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.R;

import java.util.ArrayList;
import java.util.List;

public class PrivacyDelContactPromptDialog extends Dialog implements View.OnClickListener{
	private Context mContext;
	private List<PrivacyContactDataItem> mItems;
	private Button mButtonOk;
	private Button mButtonCancel;
	private View mCloseIv;
	private PrivacyContactsListAdapter mAdapter;
	
	private static final String LAYOUT_FILE = "privacy_delcontact_restore_prompt_dialog";
	
	private GenealDailog mGeneralDailog;
	private static final int DEL_CONTACT_FINISH = 0;

	public PrivacyDelContactPromptDialog(Context context, int theme, List<PrivacyContactDataItem> mItems
			, PrivacyContactsListAdapter mAdapter) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mItems = mItems;
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
			setContentView(R.layout.privacy_delcontact_restore_prompt_dialog);
		}else{
			setContentView(contentView);
		}
		
		mButtonOk = (Button) findViewById(R.id.button_ok);
		mButtonCancel = (Button) findViewById(R.id.button_cancel);
		mCloseIv = findViewById(R.id.privacy_contact_import_data_dialog_close_iv);
		
		mCloseIv.setOnClickListener(this);
		mButtonOk.setOnClickListener(this);
		mButtonCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.dismiss();
		switch (v.getId()) {
		case R.id.privacy_contact_import_data_dialog_close_iv:

			break;
		case R.id.button_cancel:
			delContacts(false);
//			PrivacyContactsActivity.menuShowedLinear = 0;
			break;
		case R.id.button_ok:
			delContacts(true);
//			PrivacyContactsActivity.menuShowedLinear = 0;
			break;
		}
		
	}
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case DEL_CONTACT_FINISH:
				if (mGeneralDailog != null) {
					mGeneralDailog.disMisDialog();
				}
				mAdapter.notifyDataSetChanged();
				break;
			
			}
		}

	};
	private class DelContactsThread extends Thread{
		private boolean restoreFlag;
		public DelContactsThread(boolean restoreFlag){
			this.restoreFlag = restoreFlag;
		}
		public void run(){
			List<PrivacyContactDataItem> listForDelete = new ArrayList<PrivacyContactDataItem>();
			if(mItems.size() == 1){
				PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(mContext);
				if(restoreFlag){
					pdbUtils.delContact(mContext, mItems.get(0), false);
				}else{
					pdbUtils.delContact(mContext, mItems.get(0));
				}
				listForDelete.add(mItems.get(0));
				mAdapter.delItem(mItems.get(0));
			}else{				
				for(int i = 0; i < mAdapter.getCount(); i++){
					PrivacyContactDataItem item = (PrivacyContactDataItem)mAdapter.getItem(i);
					if(item.isSelected()){
						PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(mContext);
						if(restoreFlag){
							pdbUtils.delContact(mContext, item, false);
						}else{
							pdbUtils.delContact(mContext, item);
						}					
						listForDelete.add(item);
					}
					item.setShowSelect(false);
				}
				for(int i = 0; i < listForDelete.size(); i++){
					mAdapter.delItem(listForDelete.get(i));
				}				
			}
			
			if(restoreFlag){
				for(int i = 0; i < listForDelete.size(); i++){
					try{
						PrivacyDBUtils.getInstance(mContext).restoreMessages(mContext, listForDelete.get(i).getPhoneNumber());
					}catch(Exception e){
						
					}
				}
			}
			mHandler.sendEmptyMessage(DEL_CONTACT_FINISH);
		}
	}
	private void delContacts(boolean restoreFlag){
		if(mItems == null || mItems.size() == 0){
			return;
		}
		mGeneralDailog = new GenealDailog(mContext, R.style.Theme_CustomDialog);
		mGeneralDailog.showDialog();
		new DelContactsThread(restoreFlag).start();
		
	}
	
}
