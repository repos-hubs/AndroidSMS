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
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.SafeUtils;
import com.kindroid.kincent.R;

public class PrivacyChangePwdDialog extends Dialog implements View.OnClickListener{
	private Context mContext;
	
	private EditText mInputEt;
	private EditText mConfirmEt;
	private CheckBox mShowPwdCb;
	
	private Button mButtonOk;
	private Button mButtonCancel;
	
	private static final String LAYOUT_FILE = "privacy_change_pwd_dialog";
	
	public PrivacyChangePwdDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		mContext = context;
		initContentViews();
	}
	private void initContentViews(){
		findViews();
		bindListeners();
	}
	private void findViews(){
		View contentView = null;
		try{
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		}catch(Exception e){
			contentView = null;
		}
		if(contentView == null){
			this.setContentView(R.layout.privacy_change_pwd_dialog);
		}else{
			setContentView(contentView);
		}	
		
		
		mInputEt = (EditText)findViewById(R.id.privacy_pwd_et);
		mConfirmEt = (EditText)findViewById(R.id.privacy_pwd_confirm_et);
		mShowPwdCb = (CheckBox)findViewById(R.id.privacy_show_pwd_cb);
		mButtonOk = (Button)findViewById(R.id.button_ok);
		mButtonCancel = (Button)findViewById(R.id.button_cancel);		
	}
	private void bindListeners(){
		mButtonOk.setOnClickListener(this);
		mButtonCancel.setOnClickListener(this);
		mShowPwdCb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(mShowPwdCb.isChecked()){
					mInputEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					mConfirmEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);					
				}else{
					mInputEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					mConfirmEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
			
		});
	}
	private void changePwd(){
		String mInputPwd = mInputEt.getText().toString().trim();
		String mConfirmPwd = mConfirmEt.getText().toString().trim();
		if(TextUtils.isEmpty(mInputPwd)){
			Toast.makeText(mContext, R.string.pwd_empty_prompt, Toast.LENGTH_LONG).show();
			return;
		}
		if(mInputPwd.length() != 4){
			Toast.makeText(mContext, R.string.privacy_pwd_length_error, Toast.LENGTH_LONG).show();
			return;
		}
		if(!mInputPwd.equals(mConfirmPwd)){
			Toast.makeText(mContext, R.string.pwd_confirm_not_same, Toast.LENGTH_LONG).show();
			return;
		}
		SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(mContext);
		Editor editor = sh.edit();
		editor.putString(Constant.SHARE_PREFS_PRIVACY_PWD,
				SafeUtils.getMD5(mInputPwd.getBytes()));
		editor.commit();
		dismiss();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.button_ok:
			changePwd();
			break;
		case R.id.button_cancel:
			dismiss();
			break;
		}
	}

}
