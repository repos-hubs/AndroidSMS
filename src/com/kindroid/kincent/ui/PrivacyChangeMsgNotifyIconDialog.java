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
import android.widget.ImageView;
import android.widget.Toast;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.R;

public class PrivacyChangeMsgNotifyIconDialog extends Dialog implements View.OnClickListener{
	public static final int PRIVACY_MSG_NOTIFY_ICONS_SUM = 2;
	public static final String PRIVACY_MSG_NOTIFY_ICON_NAME_PREF = "privacy_msg_notify_icon";

	private Context mContext;

	private Button mButtonOk;
	private Button mButtonCancel;
	private View mChangeMsgNotifyIconLinear;
	private ImageView mChangeMsgNotifyIconIv;
	private int iconIndex = 0;
	
	private static final String LAYOUT_FILE = "privacy_change_msg_notify_icon_dialog";

	public PrivacyChangeMsgNotifyIconDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		mContext = context;
		initContentViews();
		bindListeners();
	}
	private void initContentViews() {
		View contentView = null;
		try{
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		}catch(Exception e){
			contentView = null;
		}
		if(contentView == null){
			setContentView(R.layout.privacy_change_msg_notify_icon_dialog);
		}else{
			setContentView(contentView);
		}	
		
		mChangeMsgNotifyIconLinear = findViewById(R.id.privacy_change_msg_notify_icon_linear);
		mButtonOk = (Button) findViewById(R.id.button_ok);
		mButtonCancel = (Button) findViewById(R.id.button_cancel);
		mChangeMsgNotifyIconIv = (ImageView) findViewById(R.id.privacy_msg_notify_icon_iv);

		SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(mContext);
		iconIndex = sh.getInt(Constant.SHARE_PREFS_PRIVACY_MSG_NOTIFY_ICON, 0);
		try {
			int iconId = mContext.getResources().getIdentifier(
					PRIVACY_MSG_NOTIFY_ICON_NAME_PREF + iconIndex, "drawable",
					mContext.getPackageName());
			if (iconId > 0) {
				mChangeMsgNotifyIconIv.setImageResource(iconId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void bindListeners() {
		mChangeMsgNotifyIconLinear.setOnClickListener(this);
		mButtonOk.setOnClickListener(this);
		mButtonCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.privacy_change_msg_notify_icon_linear:
			if (iconIndex == PRIVACY_MSG_NOTIFY_ICONS_SUM) {
				iconIndex = 0;
			} else {
				iconIndex = iconIndex + 1;
			}
			try {
				int iconId = mContext.getResources().getIdentifier(
						PRIVACY_MSG_NOTIFY_ICON_NAME_PREF + iconIndex, "drawable",
						mContext.getPackageName());
				if (iconId > 0) {
					mChangeMsgNotifyIconIv.setImageResource(iconId);					
				} else {
					iconIndex = 0;
					iconId = mContext.getResources().getIdentifier(
							PRIVACY_MSG_NOTIFY_ICON_NAME_PREF + iconIndex, "drawable",
							mContext.getPackageName());
					if (iconId > 0) {
						mChangeMsgNotifyIconIv.setImageResource(iconId);						
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		case R.id.button_ok:
			try {				
				SharedPreferences sh = KindroidMessengerApplication
						.getSharedPrefs(mContext);
				Editor editor = sh.edit();
				editor.putInt(Constant.SHARE_PREFS_PRIVACY_MSG_NOTIFY_ICON, iconIndex);
				editor.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			dismiss();
			break;
		case R.id.button_cancel:
			dismiss();
			break;
		}
	}

}
