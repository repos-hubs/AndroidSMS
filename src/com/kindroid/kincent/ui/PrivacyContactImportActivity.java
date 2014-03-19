/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.telephony.PhoneNumberUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.kindroid.android.util.dialog.GenealDailog;
import com.kindroid.kincent.AutoLockActivityInterface;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.PrivacyContactImportAdapter;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.PrivacyContactImportDataThread;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrivacyContactImportActivity extends ListActivity implements
		OnClickListener, OnCheckedChangeListener, AutoLockActivityInterface {
	private PrivacyContactImportAdapter mAdapter;

	private Button mButtonOk;
	private Button mButtonCancel;

	private CheckBox mSelectAll;
	private boolean mOnlyChangeSelectAllState;
	
	private GenealDailog mGeneralDailog;
	
	private static final String LAYOUT_FILE = "privacy_contact_import_list_activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initContentViews();
		//初始化自动锁时间
		KindroidMessengerPrivacyActivity.registLockedActivity(this);
		KindroidMessengerPrivacyActivity.timeForLock = System.currentTimeMillis();		
		KindroidMessengerPrivacyActivity.updateLockTask();
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		KindroidMessengerPrivacyActivity.timeForLock = System.currentTimeMillis();
		KindroidMessengerPrivacyActivity.updateLockTask();
		return super.dispatchTouchEvent(ev);
	}
	private void initContentViews() {
		View contentView = null;
		try{
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		}catch(Exception e){
			contentView = null;
		}
		if(contentView == null){
			setContentView(R.layout.privacy_contact_import_list_activity);
		}else{
			setContentView(contentView);
		}	
		
		mButtonOk = (Button) findViewById(R.id.button_ok);
		mButtonCancel = (Button) findViewById(R.id.button_cancel);
		mSelectAll = (CheckBox) findViewById(R.id.select_al_cb);
		mSelectAll.setOnCheckedChangeListener(this);
		mButtonOk.setOnClickListener(this);
		mButtonCancel.setOnClickListener(this);

		int importType = getIntent().getIntExtra(
				Constant.PRIVACY_CONTACT_IMPORT_SOURCE,
				Constant.IMPORT_FROM_CONTACTS);
		fillData(importType);
	}

	private void fillData(final int importType) {
		mAdapter = new PrivacyContactImportAdapter(this);
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getContactsForAdapter(importType);
			}

		});
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		PrivacyContactDataItem item = (PrivacyContactDataItem) mAdapter
				.getItem(position);
		item.setSelected(!item.isSelected());
		CheckBox cb = (CheckBox) v
				.findViewById(R.id.privacy_contact_import_item_cb);
		if (cb != null) {
			cb.setChecked(item.isSelected());
		}
		if(!cb.isChecked() && mSelectAll.isChecked()){
			mOnlyChangeSelectAllState = true;
			mSelectAll.setChecked(false);
		}
		super.onListItemClick(l, v, position, id);
	}

	private void getContactsForAdapter(int importType) {
		switch (importType) {
		case Constant.IMPORT_FROM_CONTACTS:
			importFromContacts();
			break;
		case Constant.IMPORT_FROM_CALLOG:
			importFromCallog();
			break;
		case Constant.IMPORT_FROM_SMS:
			importFromSms();
			break;
		}
		setListAdapter(mAdapter);
	}

	private void importFromContacts() {
		Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;
		Cursor contactsCursor = getContentResolver().query(
				contactsUri,
				new String[] { ContactsContract.Contacts.HAS_PHONE_NUMBER,
						ContactsContract.Contacts.IN_VISIBLE_GROUP,
						ContactsContract.Contacts._ID,
						ContactsContract.Contacts.DISPLAY_NAME }, null, null,
				ContactsContract.Contacts.DISPLAY_NAME + " ASC");
		if (contactsCursor == null) {
			return;
		}
		while (contactsCursor.moveToNext()) {
			int hasPhoneNum = contactsCursor
					.getInt(contactsCursor
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
			if (hasPhoneNum != 1) {
				continue;
			}
			int inVisibleGroup = contactsCursor
					.getInt(contactsCursor
							.getColumnIndex(ContactsContract.Contacts.IN_VISIBLE_GROUP));
			if (inVisibleGroup == 0) {
				continue;
			}
			long cId = contactsCursor.getLong(contactsCursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			String displayName = contactsCursor.getString(contactsCursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			Uri lookUri = ContactsContract.Data.CONTENT_URI;
			String querySelection = Data.CONTACT_ID + "=? AND "
					+ ContactsContract.CommonDataKinds.Phone.MIMETYPE + "='"
					+ ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
					+ "'";
			Cursor localCursor = getContentResolver()
					.query(lookUri,
							new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
							querySelection,
							new String[] { String.valueOf(cId) }, null);

			if (localCursor != null) {
				while (localCursor.moveToNext()) {
					String phoneNum = localCursor
							.getString(localCursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					if (displayName == null || displayName.equals("")) {
						displayName = phoneNum;
					}
					PrivacyContactDataItem item = new PrivacyContactDataItem(
							displayName, "", phoneNum, 0);
					item.setDesp(phoneNum);
					mAdapter.addItem(item);
				}
				try {
					localCursor.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		try {
			contactsCursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mAdapter != null && mAdapter.getCount() > 0) {
			mAdapter.sortItemsByContactName(true);
		}
	}

	private void importFromCallog() {
		Cursor mCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,
				null, null, null, "date DESC");
		
		if (mCursor == null) {
			return;
		}
		while (mCursor.moveToNext()) {
			String displayName = mCursor.getString(mCursor
					.getColumnIndex("name"));
			String phoneNum = mCursor.getString(mCursor
					.getColumnIndex("number"));
			long callDate = mCursor.getLong(mCursor.getColumnIndex("date"));

			if (null == displayName || !"".equals(displayName.trim())) {
				displayName = phoneNum;
			}
			PrivacyContactDataItem item = new PrivacyContactDataItem(
					displayName, "", phoneNum, 0);
			Date date = new Date(callDate);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			item.setDesp(df.format(date));
			mAdapter.addItem(item);
		}
		try {
			mCursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void importFromSms() {
		String strUriInbox = "content://sms/inbox";
		Uri uriSms = Uri.parse(strUriInbox);
		Cursor smsCursor = getContentResolver().query(uriSms,
				new String[] { "person", "body", "address" }, null, null,
				"date DESC");
		if (smsCursor == null) {
			return;
		}
		while (smsCursor.moveToNext()) {
			// int person =
			// smsCursor.getInt(smsCursor.getColumnIndex("person"));
			String body = smsCursor.getString(smsCursor.getColumnIndex("body"));
			String address = smsCursor.getString(smsCursor
					.getColumnIndex("address"));

			// get contact info for number
			String selection = Constant.CALLER_ID_SELECTION.replace("+",
					PhoneNumberUtils.toCallerIDMinMatch(address));
			Cursor cursor = getContentResolver().query(
					Constant.PHONES_WITH_PRESENCE_URI,
					Constant.CALLER_ID_PROJECTION, selection,
					new String[] { address }, null);
			PrivacyContactDataItem item = new PrivacyContactDataItem(address,
					"", address, 0);
			if (cursor != null) {
				try {
					if (cursor.moveToFirst()) {
						String cName = cursor
								.getString(Constant.CONTACT_NAME_COLUMN);
						if (!cName.equals("")) {
							item.setContactName(cName);
						}
					}
				} finally {
					cursor.close();
				}
			}
			item.setDesp(body);
			mAdapter.addItem(item);
		}
		try {
			smsCursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_cancel:
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;
		case R.id.button_ok:
			PrivacyContactImportDataDialog importDataDialog = new PrivacyContactImportDataDialog(
					this, R.style.Theme_CustomDialog, mAdapter);
			importDataDialog.show();

			break;
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if(mOnlyChangeSelectAllState){
			mOnlyChangeSelectAllState = false;
			return;
		}
		for (int i = 0; i < mAdapter.getCount(); i++) {
			PrivacyContactDataItem item = (PrivacyContactDataItem) mAdapter
					.getItem(i);
			item.setSelected(isChecked);
		}
		mAdapter.notifyDataSetChanged();
	}
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case PrivacyContactImportDataThread.PRIVACY_CONTACT_IMPORT_FINISH:
				if(mGeneralDailog != null){
					mGeneralDailog.disMisDialog();
				}
				finish();
				break;
			}
		}
		
	};
	private void saveContacts(boolean importData) {
		mGeneralDailog=new GenealDailog(this, R.style.Theme_CustomDialog);
		mGeneralDailog.showDialog();
		PrivacyContactImportDataThread importThread = new PrivacyContactImportDataThread(this, mHandler, mAdapter, importData);
		importThread.start();
		
	}

	private class PrivacyContactImportDataDialog extends Dialog implements
			View.OnClickListener {
		private Context mContext;
		private PrivacyContactImportAdapter mAdapter;
		private Button mButtonOk;
		private Button mButtonCancel;
		private View mCloseIv;
		
		private static final String DIALOG_LAYOUT_FILE = "privacy_contact_import_data_dialog";

		public PrivacyContactImportDataDialog(Context context, int theme,
				PrivacyContactImportAdapter mAdapter) {
			super(context, theme);
			// TODO Auto-generated constructor stub
			this.mContext = context;
			this.mAdapter = mAdapter;
			initContentViews();
		}

		private void initContentViews() {
			View contentView = null;
			try{
				contentView = KindroidMessengerApplication.mThemeRegistry.inflate(DIALOG_LAYOUT_FILE);
			}catch(Exception e){
				contentView = null;
			}
			if(contentView == null){
				setContentView(R.layout.privacy_contact_import_data_dialog);
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
				saveContacts(false);
				break;
			case R.id.button_ok:
				saveContacts(true);
				break;
			}
			
		}

	}

	@Override
	public void onLock() {
		// TODO Auto-generated method stub
		finish();
	}

}
