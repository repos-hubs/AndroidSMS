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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.AutoLockSettingAdapter;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.R;

public class PrivacyChangeAutoLockSettingDialog extends Dialog {
	private Context mContext;

	private Button mButtonCancel;
	private ListView mListView;
	
	private static final String LAYOUT_FILE = "privacy_change_auto_lock_dialog";

	public PrivacyChangeAutoLockSettingDialog(Context context, int theme) {
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
			setContentView(R.layout.privacy_change_auto_lock_dialog);
		}else{
			setContentView(contentView);
		}
		
		mButtonCancel = (Button)findViewById(R.id.button_cancel);
		mListView = (ListView)findViewById(R.id.privacy_auto_lock_list);
		SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(mContext);
		int autoLockSelected = sh.getInt(Constant.SHARE_PREFS_PRIVACY_AUTO_LOCK_TIME, 0);
		AutoLockSettingAdapter mAdapter = new AutoLockSettingAdapter(mContext, autoLockSelected);
		mListView.setAdapter(mAdapter);
	}
	private void bindListeners(){
		mButtonCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(mContext);
				Editor editor = sh.edit();
				editor.putInt(Constant.SHARE_PREFS_PRIVACY_AUTO_LOCK_TIME, position);
				editor.commit();
				dismiss();
			}
			
		});
	}

}
