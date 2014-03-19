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
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.SafeUtils;
import com.kindroid.kincent.R;

public class PrivacyChangeEmailDialog extends Dialog implements View.OnClickListener{
	
	private Context mContext;
	
	private EditText mPrivacyEmailEt;
	private Button mButtonOk;
	private Button mButtonCancel;
	
	private static final String LAYOUT_FILE = "privacy_change_email_dialog";

	public PrivacyChangeEmailDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		mContext = context;
		initContentViews();
		bindListeners();
	}
	private void initContentViews(){
		View contentView = null;
		try{
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		}catch(Exception e){
			contentView = null;
		}
		if(contentView == null){
			setContentView(R.layout.privacy_change_email_dialog);
		}else{
			setContentView(contentView);
		}	
		
		mPrivacyEmailEt = (EditText)findViewById(R.id.privacy_email_et);
		mButtonOk = (Button)findViewById(R.id.button_ok);
		mButtonCancel = (Button)findViewById(R.id.button_cancel);
		
		SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(mContext);
		String defaultEmail = sh.getString(Constant.SHARE_PREFS_PRIVACY_EMAIL, "");
		if(!defaultEmail.equals("")){
			mPrivacyEmailEt.setText(defaultEmail);
		}
	}
	private void bindListeners(){
		mButtonOk.setOnClickListener(this);
		mButtonCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.button_cancel:
			dismiss();
			break;
		case R.id.button_ok:
			try{
				String emailText = mPrivacyEmailEt.getText().toString().trim();
				if(emailText.equals("")){
					Toast.makeText(mContext, R.string.privacy_email_empty_prompt, Toast.LENGTH_LONG).show();
					return;
				}
				if(!SafeUtils.validEmail(emailText)){
					Toast.makeText(mContext, R.string.privacy_email_invalid_prompt, Toast.LENGTH_LONG).show();
					return;
				}
				SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(mContext);
				Editor editor = sh.edit();
				editor.putString(Constant.SHARE_PREFS_PRIVACY_EMAIL,emailText);
				editor.commit();
			}catch(Exception e){
				e.printStackTrace();
			}
			dismiss();
			break;
		}
	}

}
