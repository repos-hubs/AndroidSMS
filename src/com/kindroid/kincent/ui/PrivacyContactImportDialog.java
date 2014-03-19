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
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.R;

public class PrivacyContactImportDialog extends Dialog implements View.OnClickListener{
	private Context mContext;
	private Activity mPrivacyContactActivity;
	
	private View mImportFromContactsLinear;
	private View mImportFromCallogLinear;
	private View mImportFromSmsLinear;
	private View mCloseIv;
	
	private static final String LAYOUT_FILE = "privacy_contact_import_dialog";

	public PrivacyContactImportDialog(Context context, int theme, Activity activity) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mPrivacyContactActivity = activity;
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
			setContentView(R.layout.privacy_contact_import_dialog);
		}else{
			setContentView(contentView);
		}	
		
		mImportFromContactsLinear = findViewById(R.id.privacy_contact_import_from_contacts_linear);
		mImportFromCallogLinear = findViewById(R.id.privacy_contact_import_from_callog_linear);
		mImportFromSmsLinear = findViewById(R.id.privacy_contact_import_from_sms_linear);
		mCloseIv = findViewById(R.id.privacy_contact_import_dialog_close_iv);
		
		mImportFromContactsLinear.setOnClickListener(this);
		mImportFromCallogLinear.setOnClickListener(this);
		mImportFromSmsLinear.setOnClickListener(this);
		mCloseIv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		dismiss();
		switch(v.getId()){
		case R.id.privacy_contact_import_from_contacts_linear:
			Intent intent = new Intent(mPrivacyContactActivity, PrivacyContactImportActivity.class);			
			intent.putExtra(Constant.PRIVACY_CONTACT_IMPORT_SOURCE, Constant.IMPORT_FROM_CONTACTS);
			mPrivacyContactActivity.startActivityForResult(intent, Constant.REQUEST_CODE_FOR_PRIVACY_CONTACT_IMPORT_ACTIVITY);
			break;
		case R.id.privacy_contact_import_from_callog_linear:
			intent = new Intent(mPrivacyContactActivity, PrivacyContactImportActivity.class);
			intent.putExtra(Constant.PRIVACY_CONTACT_IMPORT_SOURCE, Constant.IMPORT_FROM_CALLOG);
			mPrivacyContactActivity.startActivityForResult(intent, Constant.REQUEST_CODE_FOR_PRIVACY_CONTACT_IMPORT_ACTIVITY);
			break;
		case R.id.privacy_contact_import_from_sms_linear:
			intent = new Intent(mPrivacyContactActivity, PrivacyContactImportActivity.class);
			intent.putExtra(Constant.PRIVACY_CONTACT_IMPORT_SOURCE, Constant.IMPORT_FROM_SMS);
			mPrivacyContactActivity.startActivityForResult(intent, Constant.REQUEST_CODE_FOR_PRIVACY_CONTACT_IMPORT_ACTIVITY);
			break;
		case R.id.privacy_contact_import_dialog_close_iv:
			dismiss();
			break;
		}
		
	}

}
