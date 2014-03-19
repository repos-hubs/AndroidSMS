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
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kindroid.android.util.dialog.GenealDailog;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.PrivacyContactImportAdapter;
import com.kindroid.kincent.adapter.PrivacyContactsListAdapter;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.PrivacyContactImportDataThread;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.util.SafeUtils;
import com.kindroid.kincent.R;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.PhoneType;

import java.util.ArrayList;
import java.util.List;

public class PrivacyContactAddDialog extends Dialog implements
		View.OnClickListener, Spinner.OnItemSelectedListener {
	private Context mContext;
	private Button mButtonSave;
	private Button mButtonCancel;
	private Spinner mSpinner;
	private EditText mNumberEt;
	private EditText mContactNameEt;
	private TextView mTitleTv;
	private PrivacyContactsListAdapter mAdapter;
	private PrivacyContactDataItem mItem;
	
	private LinearLayout mSpinnerLinear;

	private GenealDailog mGeneralDailog;
	
	private static final String LAYOUT_FILE = "privacy_contact_add_dialog";

	public PrivacyContactAddDialog(Context context, int theme,
			PrivacyContactsListAdapter mAdapter, PrivacyContactDataItem mItem) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mAdapter = mAdapter;
		this.mItem = mItem;
		initContentViews();
	}

	private void initContentViews() {
		View contentView = null;
		try{
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		}catch(Exception e){
			contentView = null;
		}
		if(contentView == null){
			setContentView(R.layout.privacy_contact_add_dialog);
		}else{
			setContentView(contentView);
		}	
		
		mTitleTv = (TextView)findViewById(R.id.title_text_tv);
		if(mItem != null){
			mTitleTv.setText(R.string.privacy_contact_modify_dialog_title);
		}
		
		mButtonSave = (Button) findViewById(R.id.button_ok);
		mButtonCancel = (Button) findViewById(R.id.button_cancel);		
		mButtonSave.setOnClickListener(this);
		mButtonCancel.setOnClickListener(this);
		
		mSpinnerLinear = (LinearLayout)findViewById(R.id.spinner_linear);
//		mSpinner = (Spinner) findViewById(R.id.privacy_contact_add_blocked_type_spinner);
		mSpinner = new Spinner(mContext);
		mSpinnerLinear.addView(mSpinner, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		mSpinner.setOnItemSelectedListener(this);
		String[] spinnerArray = mContext.getResources().getStringArray(
				R.array.privacy_contact_blocked_type);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
				android.R.layout.simple_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(adapter);
		mSpinner.onClick(this, BUTTON_POSITIVE);
		
		mNumberEt = (EditText) findViewById(R.id.privacy_contact_add_number_et);
		mContactNameEt = (EditText) findViewById(R.id.privacy_contact_add_contact_name_et);
		if (mItem != null) {
			if (!mItem.getContactName().equals("")) {
				mContactNameEt.setText(mItem.getContactName());
			}
			mNumberEt.setText(mItem.getPhoneNumber());
			mSpinner.setSelection(mItem.getBlockedType());
		}
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			dismiss();
			switch (msg.what) {
			case PrivacyContactImportDataThread.PRIVACY_CONTACT_IMPORT_FINISH:
				if (mGeneralDailog != null) {
					mGeneralDailog.disMisDialog();
				}				
				mAdapter.notifyDataSetChanged();
				break;
			case PrivacyContactImportDataThread.PRIVACY_CONTACT_ADD_EXISTED:
				Toast.makeText(mContext, R.string.privacy_add_contact_existed, Toast.LENGTH_SHORT).show();
				if (mGeneralDailog != null) {
					mGeneralDailog.disMisDialog();
				}
				break;
			}
		}

	};

	private void saveContacts(boolean importData) {
		String mContactName = mContactNameEt.getText().toString().trim();
		String mNumber = mNumberEt.getText().toString().trim();
		int blockedType = mSpinner.getSelectedItemPosition();
		
		if (mItem == null) {
			PrivacyContactDataItem item = new PrivacyContactDataItem(mContactName, "", mNumber,
					blockedType);
//			PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(mContext);
//			long ret = pdbUtils.addContact(mContext, item);
//			if (ret == 0) {
//				Toast.makeText(mContext, R.string.privacy_contact_add_existed,
//						Toast.LENGTH_LONG).show();
//				return;
//			}
//			if (ret == -1) {
//				Toast.makeText(mContext, R.string.privacy_contact_add_failed,
//						Toast.LENGTH_LONG).show();
//			} else {
//				mAdapter.addItem(item);
//				mGeneralDailog = new GenealDailog(mContext, R.style.Theme_CustomDialog);
//				mGeneralDailog.showDialog();
//				List<PrivacyContactDataItem> mItems = new ArrayList<PrivacyContactDataItem>();
//				item.setSelected(true);
//				mItems.add(item);
//				PrivacyContactImportAdapter adapter = new PrivacyContactImportAdapter(mContext, mItems);
//				PrivacyContactImportDataThread importThread = new PrivacyContactImportDataThread(
//						mContext, mHandler, adapter, importData);
//				importThread.start();
//			}
//			mAdapter.addItem(item);
			mGeneralDailog = new GenealDailog(mContext, R.style.Theme_CustomDialog);
			mGeneralDailog.showDialog();
			List<PrivacyContactDataItem> mItems = new ArrayList<PrivacyContactDataItem>();
			item.setSelected(true);
			mItems.add(item);
			PrivacyContactImportAdapter adapter = new PrivacyContactImportAdapter(mContext, mItems);
			PrivacyContactImportDataThread importThread = new PrivacyContactImportDataThread(
					mContext, mHandler, adapter, importData);
			importThread.start();
		} 

		mAdapter.notifyDataSetChanged();

	}

	private void saveContacts() {
		String mContactName = mContactNameEt.getText().toString().trim();
		String mNumber = mNumberEt.getText().toString().trim();
		int blockedType = mSpinner.getSelectedItemPosition();
		if (mItem == null) {
			PrivacyContactDataItem item = new PrivacyContactDataItem(mContactName, "", mNumber,
					blockedType);
			PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(mContext);
			long ret = pdbUtils.addContact(mContext, item);
			if (ret == 0) {
				Toast.makeText(mContext, R.string.privacy_contact_add_existed,
						Toast.LENGTH_LONG).show();
				return;
			}
			if (ret == -1) {
				Toast.makeText(mContext, R.string.privacy_contact_add_failed,
						Toast.LENGTH_LONG).show();
			} else {
				mAdapter.addItem(item);
			}
		} 

		mAdapter.notifyDataSetChanged();

		dismiss();
	}

	private void editContact() {
		String mContactName = mContactNameEt.getText().toString().trim();
		String mNumber = mNumberEt.getText().toString().trim();
		int blockedType = mSpinner.getSelectedItemPosition();

		mItem.setBlockedType(blockedType);
		mItem.setContactName(mContactName);
		mItem.setPhoneNumber(mNumber);
		
		PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(mContext);
		pdbUtils.updateContact(mContext, mItem);
		mAdapter.notifyDataSetChanged();

		dismiss();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_cancel:
			dismiss();
			break;
		case R.id.button_ok:
			String mNumber = mNumberEt.getText().toString().trim();
			if (mNumber.equals("")) {
				Toast.makeText(mContext,
						R.string.privacy_change_text_empty_prompt,
						Toast.LENGTH_LONG).show();
				return;
			} else if (!PhoneNumberUtils.isGlobalPhoneNumber(mNumber)) {
				Toast.makeText(mContext, R.string.input_valid_number,
						Toast.LENGTH_SHORT).show();
				return;
			}else if(mNumber.length() < 5 || mNumber.length() > 16){
				Toast.makeText(mContext, R.string.input_valid_number,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (mItem == null) {
				saveContacts(true);
			} else {
				editContact();
			}
			break;		
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	private class PrivacyContactImportDataDialog extends Dialog implements
			View.OnClickListener {
		private Context mContext;
		private Button mButtonOk;
		private Button mButtonCancel;
		private View mCloseIv;

		public PrivacyContactImportDataDialog(Context context, int theme) {
			super(context, theme);
			// TODO Auto-generated constructor stub
			this.mContext = context;
			initContentViews();
		}

		private void initContentViews() {
			setContentView(R.layout.privacy_contact_import_data_dialog);
			mButtonOk = (Button) findViewById(R.id.button_ok);
			mButtonCancel = (Button) findViewById(R.id.button_cancel);
			mCloseIv = findViewById(R.id.privacy_contact_import_data_dialog_close_iv);
			
			mButtonOk.setOnClickListener(this);
			mButtonCancel.setOnClickListener(this);
			mCloseIv.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
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
			dismiss();
		}

	}

}
