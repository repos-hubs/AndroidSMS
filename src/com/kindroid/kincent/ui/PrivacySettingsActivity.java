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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kindroid.kincent.AutoLockActivityInterface;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.R;

public class PrivacySettingsActivity extends Activity implements
		View.OnClickListener, AutoLockActivityInterface {
	private View mChangePwdLinear;
	private View mChangeDisplayLinear;
	private View mChangeEmailLinear;
	private View mChangeAutoLockLinear;
	private View mChangeNotifyForNewLinear;
	private View mChangeNotifyTextLinear;
	private View mChangeMsgRingLinear;
	private View mChangeMsgNotifyIconLinear;
	private View mChangeCallNotifyIconLinear;
	private View mChangeVibrateNotifyLinear;
	private View mChangeAutoSendMsgLinear;
	private View mChangeAutoMsgLinear;

	private CheckBox mChangeNotifyForNewCb;
	private CheckBox mChangeVibrateNotifyCb;
	private CheckBox mChangeAutoSendMsgCb;

	private TextView mChangeAutoMsgTv;

	public static final int REQUEST_CODE_RINGTONE = 90;

	public static final String DEFAULT_MSG_NOTIFY_RINGTONE = "content://settings/system/ringtone";

	private static final String LAYOUT_FILE = "privacy_settings";

	private Dialog mCurrentDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initContentView();
	}

	private void initContentView() {
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry
					.inflate(LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			setContentView(R.layout.privacy_settings);
		} else {
			setContentView(contentView);
		}
		findViews();
		bindListeners();
	}

	private void findViews() {
		mChangePwdLinear = findViewById(R.id.privacy_settings_change_pwd);
		mChangeDisplayLinear = findViewById(R.id.privacy_settings_change_display);
		mChangeEmailLinear = findViewById(R.id.privacy_settings_change_pwd_email);
		mChangeAutoLockLinear = findViewById(R.id.privacy_settings_change_auto_lock);
		mChangeNotifyForNewLinear = findViewById(R.id.privacy_settings_change_notify_for_new);
		mChangeNotifyTextLinear = findViewById(R.id.privacy_settings_change_notify_text);
		mChangeMsgRingLinear = findViewById(R.id.privacy_settings_change_message_ring);
		mChangeMsgNotifyIconLinear = findViewById(R.id.privacy_settings_change_message_notify_icon);
		mChangeCallNotifyIconLinear = findViewById(R.id.privacy_settings_change_call_in_notify_icon);
		mChangeVibrateNotifyLinear = findViewById(R.id.privacy_settings_change_vibrate_notify_for_new);
		mChangeAutoSendMsgLinear = findViewById(R.id.privacy_settings_change_hangup_auto_send_msg);
		mChangeAutoMsgLinear = findViewById(R.id.privacy_settings_change_auto_msg);

		mChangeNotifyForNewCb = (CheckBox) findViewById(R.id.privacy_settings_change_notify_for_new_cb);
		mChangeVibrateNotifyCb = (CheckBox) findViewById(R.id.private_settings_change_vibrate_notify_for_new_cb);
		mChangeAutoSendMsgCb = (CheckBox) findViewById(R.id.privacy_settings_change_hangup_auto_send_msg_cb);

		mChangeAutoMsgTv = (TextView) findViewById(R.id.privacy_change_auto_msg_item_title_tv);

		SharedPreferences sh = KindroidMessengerApplication
				.getSharedPrefs(this);
		boolean notifyForNew = sh.getBoolean(
				Constant.SHARE_PREFS_PRIVACY_NOTIFY_FOR_NEW, true);
		mChangeNotifyForNewCb.setChecked(notifyForNew);

		boolean vibrateNotify = sh.getBoolean(
				Constant.SHARE_PREFS_PRIVACY_VIBRATE_NOTIFY, true);
		mChangeVibrateNotifyCb.setChecked(vibrateNotify);

		boolean autoSendMsg = sh.getBoolean(
				Constant.SHARE_PREFS_PRIVACY_AUTO_SEND_MSG, false);
		mChangeAutoSendMsgCb.setChecked(autoSendMsg);
		if (!autoSendMsg) {
			mChangeAutoMsgLinear.setEnabled(false);
			mChangeAutoMsgTv
					.setTextColor(getResources().getColor(R.color.grey));
		}
	}

	private void bindListeners() {
		mChangePwdLinear.setOnClickListener(this);
		mChangeDisplayLinear.setOnClickListener(this);
		mChangeEmailLinear.setOnClickListener(this);
		mChangeAutoLockLinear.setOnClickListener(this);
		mChangeNotifyForNewLinear.setOnClickListener(this);
		mChangeNotifyTextLinear.setOnClickListener(this);
		mChangeMsgRingLinear.setOnClickListener(this);
		mChangeMsgNotifyIconLinear.setOnClickListener(this);
		mChangeCallNotifyIconLinear.setOnClickListener(this);
		mChangeVibrateNotifyLinear.setOnClickListener(this);
		mChangeAutoSendMsgLinear.setOnClickListener(this);
		mChangeAutoMsgLinear.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.privacy_settings_change_pwd:
			PrivacyChangePwdDialog changePwdDialog = new PrivacyChangePwdDialog(
					this.getParent(), R.style.Theme_CustomDialog);
			changePwdDialog.show();
			mCurrentDialog = changePwdDialog;
			break;
		case R.id.privacy_settings_change_display:
			PrivacyChangeDisplayDialog changeDislpayDialog = new PrivacyChangeDisplayDialog(
					this.getParent(), R.style.Theme_CustomDialog);
			changeDislpayDialog.show();
			mCurrentDialog = changeDislpayDialog;
			break;
		case R.id.privacy_settings_change_pwd_email:
			PrivacyChangeEmailDialog changePrivacyEmailDialog = new PrivacyChangeEmailDialog(
					this.getParent(), R.style.Theme_CustomDialog);
			changePrivacyEmailDialog.show();
			mCurrentDialog = changePrivacyEmailDialog;
			break;
		case R.id.privacy_settings_change_auto_lock:
			PrivacyChangeAutoLockSettingDialog changeAutoLockSettingDialog = new PrivacyChangeAutoLockSettingDialog(
					this.getParent(), R.style.Theme_CustomDialog);
			changeAutoLockSettingDialog.show();
			mCurrentDialog = changeAutoLockSettingDialog;
			break;
		case R.id.privacy_settings_change_notify_for_new:
			if (mChangeNotifyForNewCb.isChecked()) {
				mChangeNotifyForNewCb.setChecked(false);
			} else {
				mChangeNotifyForNewCb.setChecked(true);
			}

			SharedPreferences sh = KindroidMessengerApplication
					.getSharedPrefs(this);
			Editor editor = sh.edit();
			editor.putBoolean(Constant.SHARE_PREFS_PRIVACY_NOTIFY_FOR_NEW,
					mChangeNotifyForNewCb.isChecked());
			editor.commit();
			break;
		case R.id.privacy_settings_change_notify_text:
			PrivacyChangeNotifyTextDialog changePrivacyNotifyTextDialog = new PrivacyChangeNotifyTextDialog(
					this.getParent(), R.style.Theme_CustomDialog);
			changePrivacyNotifyTextDialog.show();
			mCurrentDialog = changePrivacyNotifyTextDialog;
			break;
		case R.id.privacy_settings_change_message_ring:
			// 打开系统铃声设置
			Intent intent = new Intent(
					android.media.RingtoneManager.ACTION_RINGTONE_PICKER);
			// Allow user to pick 'Default'
//			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
			// 设置类型为来电
			intent.putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_TYPE,
					android.media.RingtoneManager.TYPE_NOTIFICATION);
			// 设置显示的标题
			intent.putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_TITLE,
					getString(R.string.privacy_settings_message_ring_title));
			// last ringtone
			sh = KindroidMessengerApplication.getSharedPrefs(this);
			String lastRringtone = sh.getString(
					Constant.SHARE_PREFS_PRIVACY_MSG_RINGTONE,
					DEFAULT_MSG_NOTIFY_RINGTONE);
			try {
//				intent.putExtra(
//						android.media.RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
//						Uri.parse(lastRringtone));
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(lastRringtone));
			} catch (Exception e) {

			}
			getParent().startActivityForResult(intent, REQUEST_CODE_RINGTONE);
			break;
		case R.id.privacy_settings_change_message_notify_icon:
			PrivacyChangeMsgNotifyIconDialog changePrivacyMsgNotifyIconDialog = new PrivacyChangeMsgNotifyIconDialog(
					this.getParent(), R.style.Theme_CustomDialog);
			changePrivacyMsgNotifyIconDialog.show();
			mCurrentDialog = changePrivacyMsgNotifyIconDialog;
			break;
		case R.id.privacy_settings_change_call_in_notify_icon:
			PrivacyChangeCallInNotifyIconDialog changePrivacyCallInNotifyIconDialog = new PrivacyChangeCallInNotifyIconDialog(
					this.getParent(), R.style.Theme_CustomDialog);
			changePrivacyCallInNotifyIconDialog.show();
			mCurrentDialog = changePrivacyCallInNotifyIconDialog;
			break;
		case R.id.privacy_settings_change_vibrate_notify_for_new:
			if (mChangeVibrateNotifyCb.isChecked()) {
				mChangeVibrateNotifyCb.setChecked(false);
			} else {
				mChangeVibrateNotifyCb.setChecked(true);
			}
			sh = KindroidMessengerApplication.getSharedPrefs(this);
			editor = sh.edit();
			editor.putBoolean(Constant.SHARE_PREFS_PRIVACY_VIBRATE_NOTIFY,
					mChangeVibrateNotifyCb.isChecked());
			editor.commit();
			break;
		case R.id.privacy_settings_change_hangup_auto_send_msg:
			if (mChangeAutoSendMsgCb.isChecked()) {
				mChangeAutoSendMsgCb.setChecked(false);
				mChangeAutoMsgLinear.setEnabled(false);
				mChangeAutoMsgTv.setTextColor(getResources().getColor(
						R.color.grey));
			} else {
				mChangeAutoSendMsgCb.setChecked(true);
				mChangeAutoMsgLinear.setEnabled(true);
				mChangeAutoMsgTv.setTextColor(getResources().getColor(
						R.color.black));
			}
			sh = KindroidMessengerApplication.getSharedPrefs(this);
			editor = sh.edit();
			editor.putBoolean(Constant.SHARE_PREFS_PRIVACY_AUTO_SEND_MSG,
					mChangeAutoSendMsgCb.isChecked());
			editor.commit();
			break;
		case R.id.privacy_settings_change_auto_msg:
			intent = new Intent(this, PrivacyChangeAutoMsgListActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		System.out.println("onActiivtyResult, requestCode :" + requestCode);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onLock() {
		// TODO Auto-generated method stub
		if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
			mCurrentDialog.dismiss();
		}
	}

}
